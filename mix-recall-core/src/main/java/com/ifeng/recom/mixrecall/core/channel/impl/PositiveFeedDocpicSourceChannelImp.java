package com.ifeng.recom.mixrecall.core.channel.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.item.LastDocBean;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import com.ifeng.recom.mixrecall.common.util.itemUtil.index4User.MixReasonUtils;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import joptsimple.internal.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tangsc on 2018/09/20.
 */
@Service
public class PositiveFeedDocpicSourceChannelImp {
    private static final Logger logger = LoggerFactory.getLogger(PositiveFeedDocpicSourceChannelImp.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);
    private static final String key_Source = "source=";
    /**
     * 触发图文正反馈
     *
     * @param mixRequestInfo
     * @return
     */
    public List<RecallResult> doDocpicRecom(MixRequestInfo mixRequestInfo,List<LastDocBean> remainBeanlist) {
        List<RecallResult> result = getDocpicRecomMap(mixRequestInfo,remainBeanlist);
        List<RecallResult> recallResults = new ArrayList<>();
        recallResults.addAll(result);
        return recallResults;
    }

    /**
     * 供头条直接调用使用
     * 获取召回结果，key为每个触发的source
     *
     * @param mixRequestInfo
     * @return
     */
    public  List<RecallResult> getDocpicRecomMap(MixRequestInfo mixRequestInfo,List<LastDocBean> remainBeanlist) {
        List<RecallResult> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(remainBeanlist)) {
            return result;
        }
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("posTotal-docpicSource");

        getPositiveByCache(mixRequestInfo, result,remainBeanlist);

        timer.addEndTime("posTotal-docpicSource");
        timeLogger.info("SourceChannelLog PosFeedDocpic {} uid:{}", timer.getStaticsInfo(), mixRequestInfo.getUid());
        return result;
    }

    private void getPositiveByCache(MixRequestInfo mixRequestInfo, List<RecallResult> result, List<LastDocBean> remainBeanlist){
        Set<String> SourceSet = Sets.newHashSet();
        try{
            remainBeanlist.forEach((bean)->{
                if(!Strings.isNullOrEmpty(bean.getSource())){
                    SourceSet.add(bean.getSource());
                }
            });
            if(CollectionUtils.isEmpty(SourceSet)){
                return;
            }
            // key: source=
            Set<String> sourceKeySet = SourceSet.stream().map(x -> key_Source + x).collect(Collectors.toSet());
            //source      docids  媒体--对应的文章列表
            Map<String, List<String>> SourceIdsImmutable = CacheManager.getPreloadDocId(sourceKeySet, CacheManager.PreloadDocType.DOC);
            Map<String, List<String>> SourceIds = new HashMap<>(SourceIdsImmutable);
            List<String> totalId = new ArrayList<>();

            if (SourceIds.keySet().size() >= 1 ) {  //tagIds中存在数据的
                for (Map.Entry<String,List<String>> entry : SourceIds.entrySet()) {
                    List<String> docIds = entry.getValue();
                    totalId.addAll(docIds);
                }
            }
            Set<String> totalIdSet = new HashSet<>(totalId);
            totalId.clear();
            // key: id  value: document
            Map<String, Document> idDocs = DocPreloadCache.getBatchDocsWithQueryNoClone(totalIdSet);
            for(LastDocBean docBean : remainBeanlist){
                int resultCount = 0;
                String docid = docBean.getDocId();
                String simid = docBean.getSimId();
                String source = docBean.getSource();
                if(StringUtils.isEmpty(source) || StringUtils.isEmpty(docid) || StringUtils.isEmpty(simid)){
                    continue;
                }
                Set<String> simIdSet = new HashSet<>();
                for(Map.Entry<String, Document> entry : idDocs.entrySet()){
                    Document doc = entry.getValue();
                    if(!simIdSet.add(doc.getSimId())){
                        continue;
                    }else if(!DocUtils.isDocpic(doc)){
                        continue;
                    }else if(StringUtils.isNotEmpty(doc.getSource()) && source.equals(doc.getSource())){
                        if(resultCount >= 10){
                            break;
                        }
                        String recallTag = docBean.getSimId();
                        RecallResult recallResult = new RecallResult(doc, recallTag, MixReasonUtils.getWhyReasonPosi(WhyReason.POS_FEED_DOCPIC_SOURCE.getValue()));
                        resultCount++;
                        result.add(recallResult);
                    }
                }
                if(resultCount == 0){
                    logger.info("uid:{},lastbeans docid:{},simid:{},source={} get docpic recall is null",mixRequestInfo.getUid(),docid,simid,source);
                }
            }
        }catch (Exception e){
            logger.error("uid: {}, getDocpicPositiveByCache error {}",mixRequestInfo.getUid(),e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        LogicParams logicParams = new LogicParams();
        mixRequestInfo.setLogicParams(logicParams);
        List<RecallResult> result = new ArrayList<>();

        List<LastDocBean> remainBeanlist = Lists.newArrayList();
        LastDocBean a = new LastDocBean();
        a.setDocId("47012529");
        a.setSimId("clusterId_30062242");
        a.setSource("体育风暴");

        LastDocBean b = new LastDocBean();
        b.setDocId("65770776");
        b.setSimId("clusterId_18229366");
        b.setSource("观察者网");
        remainBeanlist.add(a);
        remainBeanlist.add(b);


      //  mixRequestInfo.setLastDocBeans(lastDocBeans);

        PositiveFeedDocpicSourceChannelImp positiveFeedDocpicChannel = new PositiveFeedDocpicSourceChannelImp();
        positiveFeedDocpicChannel.getPositiveByCache(mixRequestInfo,result,remainBeanlist);

//        results.forEach(recallResult -> {
//            System.out.println(recallResult.getDocument().getDocId() + " " + recallResult.getDocument().getTitle() + " " + recallResult.getWhyReason().getValue() + " " + recallResult.getDocument().getDocType() + "  " + recallResult.getRecallTag());
//        });
    }
}
