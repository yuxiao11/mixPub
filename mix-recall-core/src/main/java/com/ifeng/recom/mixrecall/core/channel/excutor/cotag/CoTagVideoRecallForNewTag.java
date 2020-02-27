package com.ifeng.recom.mixrecall.core.channel.excutor.cotag;

import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.config.ApplicationConfig;
import com.ifeng.recom.mixrecall.common.constant.ApolloConstant;
import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.TagLackLogUtils;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import com.ifeng.recom.mixrecall.common.util.UserUtils;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.preload.CotagVideoNewCache;
import com.ifeng.recom.mixrecall.core.util.MathUtil;
import com.ifeng.recom.mixrecall.core.util.RecallUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.ifeng.recom.mixrecall.common.service.BloomFilter.filterSimIdByBloomFilter;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getRecallTagAndRecallResultListMapFromDocList;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getTagAndWeightMap;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getTagPositionMap;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getTagRecallNumberWithNegativeBase;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getTagRecallNumberWithNegativeTest1;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getTagRecallNumberWithNegativeTest2;

/**
 * Created by geyl on 2017/11/13.
 */
public class CoTagVideoRecallForNewTag implements Callable<List<RecallResult>> {
    private static final Logger logger = LoggerFactory.getLogger(CoTagVideoRecallForNewTag.class);

    private static Gson gson = new Gson();
    private static Document.HotBoostComparator hotBoostComparator = new Document.HotBoostComparator();

    private MixRequestInfo mixRequestInfo;
    private TagPeriod tagPeriod;
    private UserModel userModel;
    private List<RecordInfo> recordInfoList;
    private int number;




    public CoTagVideoRecallForNewTag(MixRequestInfo mixRequestInfo, List<RecordInfo> recordInfoList, TagPeriod tagPeriod, int number) {
        this.mixRequestInfo = mixRequestInfo;
        this.recordInfoList = recordInfoList;
        this.number = number;
        this.userModel = mixRequestInfo.getUserModel();
        this.tagPeriod = tagPeriod;

    }

    @Override
    public List<RecallResult> call() throws Exception {
        if (CollectionUtils.isEmpty(recordInfoList)) {
            return Collections.EMPTY_LIST;
        }
        //此处添加负反馈
        int originSize = recordInfoList.size();
        Map<String,Double> negativeList = mixRequestInfo.getNegativeMap().get("featureWord");
        if(negativeList != null && negativeList.size() >= 1){
            List<RecordInfo> finalRecordInfo = recordInfoList.stream().filter(item -> !negativeList.keySet().contains(item.getRecordName().split("-")[1])).collect(Collectors.toList());

            recordInfoList = finalRecordInfo;
            logger.info("{} Channel:{} __________Negative_________ OriginSize:{} FilteredSize:{} RemoveSize:{} CotagWeightMap:{}",mixRequestInfo.getUid(),CoTagVideoRecallForNewTag.class.getSimpleName(), originSize,recordInfoList.size(),originSize-recordInfoList.size(),mixRequestInfo.getNegativeMap().get("cotag").toString());

        }


        if (recordInfoList == null || recordInfoList.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return doRecall();

        } catch (Exception e) {
            logger.error("{} cotag video for New Tag recall executor",mixRequestInfo.getUid(), e);
            return Collections.emptyList();
        }
    }

    private List<RecallResult> doRecall() {
        List<String> result = new ArrayList<>();

        boolean needReport = false;
        if(MathUtil.getNum(100)<3){
            needReport = true;
        }
        Map<String,Integer> needNumMap = new HashMap<>();
        Map<String,Integer> lackNumMap = new HashMap<>();
        Map<String,Integer> recallNumMap = new HashMap<>();


        //画像标签和权重
        Map<String, Double> tagWeight = getTagAndWeightMap(recordInfoList);

        //画像标签和位置
        Map<String, Double> tagPosition = getTagPositionMap(recordInfoList);


        Map<String, Integer> tagRecallNum = new HashMap<>();
        /**
         * 此处进行ABtest
         */

        String abtestFlag = mixRequestInfo.getDevMap().getOrDefault("CateFilter","base");

        String flag = "Video";

        /**
         * 此处进行ABtest
         */

//        tagRecallNum = getTagRecallNumberWithNegativeTest2(mixRequestInfo, number,recordInfoList, tagWeight,flag);

        if(abtestFlag.equals("test1") || mixRequestInfo.getUid().equals("334791b3ebfd4e1ca97e28a4b16cf784") ||ApplicationConfig.getProperty(ApolloConstant.MixRecall_DebugUsers).contains(mixRequestInfo.getUid())){
            tagRecallNum = getTagRecallNumberWithNegativeTest1(mixRequestInfo, number,recordInfoList, tagWeight,flag);

        }else if(abtestFlag.equals("test2")){

            tagRecallNum = getTagRecallNumberWithNegativeTest2(mixRequestInfo, number,recordInfoList, tagWeight,flag);

        }else{
            tagRecallNum = getTagRecallNumberWithNegativeBase(mixRequestInfo, number,recordInfoList, tagWeight);

        }

        RecallUtils.yxdebugLog(tagPeriod, mixRequestInfo, recordInfoList, tagRecallNum);

        /**
         * 此处用于测试
         */

        //记录需要召回文章数
        if(needReport){
            needNumMap = new HashMap<>(tagRecallNum);
        }

        //待召回文章的标签
        Set<String> recallTagSet = tagRecallNum.keySet();

        //tag 和 id map, id List 按hotboost 有序
        Map<String, List<String>> tagIdsImmutable;
        tagIdsImmutable = CotagVideoNewCache.getFromCache(recallTagSet);
        Map<String, List<String>> tagIds = new HashMap<>(tagIdsImmutable);

        //id 和 tag map
        Map<String, String> idTags = new HashMap<>();

        //串起整个召回结果
        List<RecallResult> resultDocs = new ArrayList<>();

        List<String> totalId = new ArrayList<>();

        int loopTime = 0;
        while (tagIds.keySet().size() >= 2 && loopTime < 8) {
            loopTime++;

            for (Iterator<Map.Entry<String, List<String>>> it = tagIds.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, List<String>> entry = it.next();

                String tag = entry.getKey();
                List<String> docIds = entry.getValue();

                int recallSize = loopTime * tagRecallNum.get(tag) * 3;

                List<String> recalledIds = docIds.subList(0, Math.min(recallSize, docIds.size()));
                List<String> alternativeIds = docIds.subList(Math.min(recallSize, docIds.size()), docIds.size());

                totalId.addAll(recalledIds);

                for (String id : recalledIds) {
                    idTags.put(id, tag);
                }

                if (alternativeIds.size() == 0) {
                    it.remove();
                } else {
                    tagIds.put(tag, alternativeIds);
                }
            }

            Set<String> totalIdSet = new HashSet<>(totalId);
            totalId.clear();

            Map<String, Document> idDocs = DocPreloadCache.getBatchDocsNoClone(totalIdSet);
            List<Document> docs = new ArrayList<>(idDocs.values());

            //需要根据Document对象获取simId后才可进行布隆过滤;
            List<Document> filteredDocs = filterSimIdByBloomFilter(mixRequestInfo.getUid(), docs);

            filteredDocs.sort(RecallUtils.hotBoostComparator);

            Map<String, List<RecallResult>> filteredTagDocs = getRecallTagAndRecallResultListMapFromDocList(filteredDocs, idTags);


            for (Map.Entry<String, List<RecallResult>> entry : filteredTagDocs.entrySet()) {
                String tag = entry.getKey();
                List<RecallResult> docs4tag = entry.getValue();

                int number = tagRecallNum.get(tag);
                int subSize = Math.min(number, docs4tag.size());

                resultDocs.addAll(docs4tag.subList(0, subSize));

                int needNum = number - subSize;

                if (needNum <= 0) {
                    tagIds.remove(tag);
                    tagRecallNum.remove(tag);
                } else {
                    tagRecallNum.put(tag, needNum);
                }
            }
        }


        if(needReport){
            lackNumMap = new HashMap<>(tagRecallNum);
        }

        //记录标签缺失信息
        TagLackLogUtils.recordLackInfo(mixRequestInfo, DocType.VIDEO, needNumMap, lackNumMap, recallNumMap);
        setPositionForDocs(resultDocs, tagPosition);
        setWhyInfo(resultDocs, tagPeriod, mixRequestInfo);
        DocUtils.setRealPositionForDocs(resultDocs,tagIdsImmutable);


        return resultDocs;
    }


    public static void setPositionForDocs(List<RecallResult> recallResultList, Map<String, Double> tagPosition) {
        Map<String, List<RecallResult>> tagRecallResults = getRecallTagAndResultListMap(recallResultList);

        for (Map.Entry<String, List<RecallResult>> entry : tagRecallResults.entrySet()) {
            String tag = entry.getKey();
            List<RecallResult> recallResults = entry.getValue();

            Map<String, RecallResult> idResultMap = new HashMap<>();
            List<Document> documents = new ArrayList<>();
            for (RecallResult recallResult : recallResults) {
                documents.add(recallResult.getDocument());
                idResultMap.put(recallResult.getDocument().getDocId(), recallResult);
            }

            documents.sort(hotBoostComparator);

            int i = 1;
            for (Document doc : documents) {
                double profileTagPosition = tagPosition.get(tag);
                idResultMap.get(doc.getDocId()).setPreloadPosition(i);
                idResultMap.get(doc.getDocId()).setPositionWeight(profileTagPosition);
                i++;
            }
        }
    }

    private static Map<String, List<RecallResult>> getRecallTagAndResultListMap(List<RecallResult> recallResultList) {
        Map<String, List<RecallResult>> tagAndResultListMap = new HashMap<>();
        for (RecallResult recallResult : recallResultList) {
            try {
                if (tagAndResultListMap.containsKey(recallResult.getRecallTag())) {
                    List<RecallResult> docList = tagAndResultListMap.get(recallResult.getRecallTag());
                    docList.add(recallResult);
                    tagAndResultListMap.put(recallResult.getRecallTag(), docList);
                } else {
                    List<RecallResult> docList = new ArrayList<>();
                    docList.add(recallResult);
                    tagAndResultListMap.put(recallResult.getRecallTag(), docList);
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return tagAndResultListMap;
    }

    private static void setWhyInfo(List<RecallResult> recallResults, TagPeriod tagPeriod, MixRequestInfo mixRequestInfo) {
        WhyReason whyReasonTrue = null;
        if (tagPeriod.equals(TagPeriod.LONG)) {
            whyReasonTrue = WhyReason.COTAG_V_LONG_N;
            if(UserUtils.isUserInterestDecayNew(mixRequestInfo)){
                whyReasonTrue = WhyReason.COTAG_V_LONG_N_decay_n;
            }
        } else if (tagPeriod.equals(TagPeriod.RECENT)) {
            whyReasonTrue = WhyReason.COTAG_V_RECENT_N;
        } else if (tagPeriod.equals(TagPeriod.LAST)) {
            whyReasonTrue = WhyReason.COTAG_V_LAST_N;
        }

        for (RecallResult recallResult : recallResults) {
            Document document=recallResult.getDocument();
            recallResult.setWhyReason(whyReasonTrue);
            if(document!=null&&StringUtils.isNotBlank(document.getDistype())){
                String disType=document.getDistype();
                if(disType.equals("jppool")){
                    recallResult.setWhyReason(WhyReason.COTAG_V_JP);
                }
            }
        }
    }
}
