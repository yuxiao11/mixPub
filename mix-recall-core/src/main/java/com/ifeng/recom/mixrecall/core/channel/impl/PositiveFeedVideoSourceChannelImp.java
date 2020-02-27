package com.ifeng.recom.mixrecall.core.channel.impl;

import com.google.common.collect.Sets;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.item.LastDocBean;
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
 * Created by tangsc on 2018/10/17.
 */
@Service
public class PositiveFeedVideoSourceChannelImp {
    private static final Logger logger = LoggerFactory.getLogger(PositiveFeedVideoSourceChannelImp.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);
    private static final String key_Source = "source=";
    /**
     * 触发视频正反馈
     *
     * @param mixRequestInfo
     * @return
     */
    public List<RecallResult> doVideoRecom(MixRequestInfo mixRequestInfo,List<LastDocBean> remainBeanlist) {
        List<RecallResult> result = getVideoRecomMap(mixRequestInfo,remainBeanlist);
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
    public  List<RecallResult> getVideoRecomMap(MixRequestInfo mixRequestInfo,List<LastDocBean> remainBeanlist) {
        List<RecallResult> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(remainBeanlist)) {
            return result;
        }
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("posTotal-videoSource");

        getPositiveByCache(mixRequestInfo, result,remainBeanlist);

        timer.addEndTime("posTotal-videoSource");
        timeLogger.info("SourceChannelLog PosFeedVideo {} uid:{}", timer.getStaticsInfo(), mixRequestInfo.getUid());
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

            Set<String> sourceKeySet = SourceSet.stream().map(x -> key_Source + x).collect(Collectors.toSet());
            Map<String, List<String>> SourceIdsImmutable = CacheManager.getPreloadDocId(sourceKeySet, CacheManager.PreloadDocType.DOC);
            Map<String, List<String>> SourceIds = new HashMap<>(SourceIdsImmutable);

            List<String> totalId = new ArrayList<>();

            if (SourceIds.keySet().size() >= 1 ) {
                for (Map.Entry<String,List<String>> entry : SourceIds.entrySet()) {
                    List<String> docIds = entry.getValue();
                    totalId.addAll(docIds);
                }
            }
            Set<String> totalIdSet = new HashSet<>(totalId);
            totalId.clear();

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
                    if(!DocUtils.isVideo(doc)){
                        continue;
                    } else if(!simIdSet.add(doc.getSimId())){
                        continue;
                    }else if(StringUtils.isNotEmpty(doc.getSource()) && source.equals(doc.getSource())){
                        if(resultCount >= 10){
                            break;
                        }
                        String recallTag = docBean.getSimId();
                        RecallResult recallResult = new RecallResult(doc, recallTag, MixReasonUtils.getWhyReasonPosi(WhyReason.POS_FEED_VIDEO_SOURCE.getValue()));
                        resultCount++;
                        result.add(recallResult);
                    }
                }
                if(resultCount == 0){
                    logger.info("uid:{},docid:{},simid:{},source={} get video recall is null",mixRequestInfo.getUid(),docid,simid,source);
                }
            }
        }catch (Exception e){
            logger.error("uid: {}, getVideoPositiveByCache error {}",mixRequestInfo.getUid(),e);
            e.printStackTrace();
        }
    }
}
