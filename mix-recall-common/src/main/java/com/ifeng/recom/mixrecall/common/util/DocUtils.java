package com.ifeng.recom.mixrecall.common.util;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.MixStrategyConstant;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.item.Index4User;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import com.ifeng.recom.tools.recallInfo.util.RecallInfoUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ifeng.recom.mixrecall.common.dao.hbase.HBaseUtils.getHashedID;
import static com.ifeng.recom.mixrecall.common.dao.hbase.HBaseUtils.getResultByColumn;

/**
 * 查询文章特征
 */

@Service
public class DocUtils {
    private static final Logger logger = LoggerFactory.getLogger(DocUtils.class);

    private static final String familyName = "info";
    private static final String columnName = "jsonItemf";
    private static final int HASH_CODE = 499;
    private static final String CONTENT_TableName = "news_itemf";
    private static final String INDEX_TableName = "news_itemf_index";
    private static final String CMPP_PREFIX = "cmpp_";

    private static Gson gson = new Gson();




    /**
     * 查询Item index
     *
     * @param docId
     * @return
     */
    public static String getItemIndex(String docId) {
        try {
            String rowkey = getHashedID(docId);
            Map<String, String> itemMap = getResultByColumn(INDEX_TableName, rowkey, familyName, columnName);

            String indexId;
            if (itemMap != null) {
                indexId = itemMap.get(columnName).replace("\"", "");
                return indexId;
            } else {
                return docId;
            }
        } catch (Exception e) {
            logger.error("query hbase item index failed:" + docId);
            return "";
        }
    }


    /**
     * 把召回结果转换为index4user
     *
     * @param mixRequestInfo
     * @param recallResult
     * @return
     */
    public Index4User getIndex4User(MixRequestInfo mixRequestInfo, RecallResult recallResult,boolean isNewResult) {
        Index4User index4User = new Index4User();
        try {
            Document doc = recallResult.getDocument();
            index4User.setI(doc.getDocId());

            //设置ctr默认值
            if (doc.getHotBoost() != null) {
                try {
                    double hotBoostDouble = doc.getHotBoost();
                    double hotBoostCtr = hotBoostDouble * GyConstant.CTRQ_hotBoost_threshold;
                    String ctr = String.format(GyConstant.HotBoostCtr_Format, hotBoostCtr);
                    index4User.setC(ctr);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("{} dealCtrDefault Error:{},{}", mixRequestInfo.getUid(), index4User.getI(), e);
                }
            }


            if (isNewResult) {
                String reason = "";
                if (recallResult.getWhyReason() != null) {
                    reason = recallResult.getWhyReason().getValue();
                }
                index4User.setS(StringUtils.defaultString(recallResult.getStrategy(), MixStrategyConstant.mixStrategy));
                index4User.setR(reason);
                index4User.setH(doc.getHotBoost());

                //多通道信息
                if(CollectionUtils.isNotEmpty(recallResult.getChannels())){
                    List<String> channels = recallResult.getChannels().stream().map(x->x.getValue()).collect(Collectors.toList());
                    index4User.setCh(channels);
                }

                if(recallResult.getPosition()>0 && mixRequestInfo.isDebugUser()){
                    Map<String,String> debugInfo = recallResult.getDebugInfo();
                    if(debugInfo == null) {
                        debugInfo = new HashMap<>();
                    }
                    debugInfo.put("position",recallResult.getPosition()+"");
                    index4User.setD(gson.toJson(debugInfo));
                }

                //多召回标签
                if(CollectionUtils.isNotEmpty(recallResult.getTags())){
                    List<String> tags = recallResult.getTags().stream().collect(Collectors.toList());
                    index4User.setTags(tags);
                }

                if (mixRequestInfo.isDebugUser() && recallResult.getDebugInfo() != null) {
                    index4User.setD(gson.toJson(recallResult.getDebugInfo()));
                }
                if (StringUtils.isNotBlank(recallResult.getRecallTag())) {
                    index4User.setRT(recallResult.getRecallTag());
                }

            } else {
                //待流量迁移完成后将删除
                com.ifeng.recom.tools.recallInfo.model.RecallInfo recallInfo = new com.ifeng.recom.tools.recallInfo.model.RecallInfo();

                recallInfo.setStrategy(StringUtils.defaultString(recallResult.getStrategy(), MixStrategyConstant.mixStrategy));

                String reason = "";
                if (recallResult.getWhyReason() != null) {
                    reason = recallResult.getWhyReason().getValue();
                }

                recallInfo.setReason(reason);
                recallInfo.setHotBoost(doc.getHotBoost());

                if (mixRequestInfo.isDebugUser() && recallResult.getDebugInfo() != null) {
                    recallInfo.setDebugInfo(gson.toJson(recallResult.getDebugInfo()));
                }

                if (StringUtils.isNotBlank(recallResult.getRecallTag())) {
                    recallInfo.setRecallTag(recallResult.getRecallTag());
                }

                if (recallResult.getPosition() != 0) {
                    recallInfo.setPosition(String.valueOf(recallResult.getPosition()));
                }

                String recallInfoStr = RecallInfoUtils.getReasonProbuf(recallInfo);
                index4User.setP(recallInfoStr);
            }



        } catch (Exception e) {
            logger.error("getMixResult from doc error:{}", e);
        }
        return index4User;
    }


    /**
     * 获取召回的通用结果
     *
     * @param mixRequestInfo
     * @param recallResultList
     * @return
     */
    public List<Index4User> getIndex4UserList(MixRequestInfo mixRequestInfo, List<RecallResult> recallResultList,boolean isNewResult) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("getIndexResult");
        List<Index4User> index4Users = Lists.newArrayList();
        try {
            for (RecallResult recallResult : recallResultList) {
                Index4User index4User = getIndex4User(mixRequestInfo, recallResult,isNewResult);
                index4Users.add(index4User);
            }
        } catch (Exception e) {
            logger.error("getMixResult from doc error:{}", e);
        }
        timer.addEndTime("getIndexResult");
        return index4Users;
    }

    public List<Index4User> getIndex4UserForLastTopic(MixRequestInfo mixRequestInfo, List<RecallResult> recallResultList,boolean isNewResult) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("getIndexResult");
        List<Index4User> index4Users = Lists.newArrayList();
        try {
            for (RecallResult recallResult : recallResultList) {
                Index4User index4User = getIndex4User(mixRequestInfo, recallResult,isNewResult);
                List<RecordInfo> lastCombineList=null;
                List<RecordInfo> lastVideoList=null;

                try{
                    if(UserUtils.isLastSim0Model(mixRequestInfo)){
                        lastCombineList=mixRequestInfo.getUserModel().getLastLdaTopicList();
                    }else {
                        lastCombineList= UserProfileUtils.profileTagWeightFilter(mixRequestInfo.getUserModel().getLastLdaDocpicTopicList(),RecallConstant.PROFILE_CUT_OFF_WEIGHT);
                    }
                    if(CollectionUtils.isEmpty(lastCombineList)){
                        lastCombineList=new ArrayList<>();
                    }
                    lastVideoList=mixRequestInfo.getUserModel().getLastVideoCotag();
                    if(CollectionUtils.isNotEmpty(lastVideoList)){
                        lastCombineList.addAll(lastVideoList);
                    }
                    String rt=index4User.getRT();
                    if(CollectionUtils.isNotEmpty(lastCombineList)&&rt!=null){
                        for(RecordInfo recordInfo:lastCombineList){
                            String recordName=recordInfo.getRecordName();
                            if(recordName!=null&&recordName.equals(rt)){
                                index4User.setU(recordInfo.getWeight());
                                break;
                            }
                        }
                    }
                }catch (Exception e){
                    logger.error("{} LastTopic deal IndexUser error:{}",mixRequestInfo.getUid(),e);
                    logger.error("{} LastTopic index4User:{} lastCombineList:{}",mixRequestInfo.getUid(),new Gson().toJson(index4User),new Gson().toJson(lastCombineList));
                }
                index4Users.add(index4User);
            }
        } catch (Exception e) {
            logger.error("getMixResult from doc error:{}", e);
        }
        timer.addEndTime("getIndexResult");
        return index4Users;
    }
    public static void setRealPositionForDocs(List<RecallResult> recallResultList, Map<String, List<String>> tagIds) {
        for (RecallResult recallResult : recallResultList) {
            if(recallResult.getDocument()==null){
                continue;
            }
            List<String> list = tagIds.get(recallResult.getRecallTag());
            if(CollectionUtils.isNotEmpty(list) && StringUtils.isNotEmpty(recallResult.getDocument().getDocId()) ) {
                int pos = list.indexOf(recallResult.getDocument().getDocId());
                recallResult.setPosition(pos + 1);
            }
        }
    }

    public static Document initDocument(Document document) {
        if (document == null || (StringUtils.isBlank(document.getTimeSensitive())&&StringUtils.isBlank(document.getExpireTime()))) {
            return null;
        }

        try {
            String time="";
            if(StringUtils.isNotBlank(document.getExpireTime())){
                time = document.getExpireTime().replace("\r\n", "");
            }else if(StringUtils.isNotBlank(document.getTimeSensitive())){
                time = document.getTimeSensitive().replace("\r\n", "");
            }

            //统一处理下时间格式 如是时间戳不处理 如不是转变为时间戳
            time=convertTime(time);
            document.setTimeSensitive(time);
        } catch (Exception e) {
            logger.error("", e);
        }
        return document;
    }

    /**
     * 判断doc是视频
     *
     * @param doc
     * @return
     */
    public static boolean isVideo(Document doc) {
        if (doc == null) {
            return false;
        }
        return (DocType.VIDEO.getValue().equals(doc.getDocType()));
    }

    /**
     * 判断doc是图文
     *
     * @param doc
     * @return
     */
    public static boolean isDocpic(Document doc) {
        if (doc == null) {
            return false;
        }
        return (DocType.DOCPIC.getValue().equals(doc.getDocType()));
    }


    public static String convertTime(String time ){
        if(StringUtils.isBlank(time)){
            return null;
        }
        String dateStr="";
        try{
            if(time.contains("T")||time.contains("Z")){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
                Long milliSecond = dateTime.toInstant(ZoneOffset.of("+0")).toEpochMilli();
                dateStr = milliSecond + "";
            }else{
                dateStr=time;
            }
        }catch (Exception e){
            logger.error("convertTime error:{}",e);
        }
        return dateStr;

    }



}


