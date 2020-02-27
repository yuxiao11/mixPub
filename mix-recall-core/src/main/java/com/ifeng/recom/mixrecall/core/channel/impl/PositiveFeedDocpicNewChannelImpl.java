package com.ifeng.recom.mixrecall.core.channel.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.item.Index4User;
import com.ifeng.recom.mixrecall.common.model.item.LastDocBean;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.itemUtil.index4User.MixReasonUtils;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.feedback.PositiveFeedDataCache;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by jibin on 2018/1/17.
 */
@Service
public class PositiveFeedDocpicNewChannelImpl {
    private static final Logger logger = LoggerFactory.getLogger(PositiveFeedDocpicNewChannelImpl.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    /**
     * 触发图文正反馈
     *
     * @param mixRequestInfo
     * @return
     */
    public List<RecallResult> doDocpicRecom(MixRequestInfo mixRequestInfo) {
        Map<String, List<RecallResult>> result = getDocpicRecomMap(mixRequestInfo);

        List<RecallResult> recallResults = new ArrayList<>();
        result.values().forEach(recallResults::addAll);
        return recallResults;
    }

    /**
     * 供头条直接调用使用
     * 获取召回结果，key为每个触发的SimId
     *
     * @param mixRequestInfo
     * @return
     */
    public Map<String, List<RecallResult>> getDocpicRecomMap(MixRequestInfo mixRequestInfo) {
        Map<String, List<RecallResult>> result = Maps.newHashMap();

        List<LastDocBean> lastDocBeans = mixRequestInfo.getLastDocBeans();
        if (CollectionUtils.isEmpty(lastDocBeans)) {
            if (mixRequestInfo.isDebugUser()) {
                logger.info("{} getFb lastDocBeans isNull", mixRequestInfo.getUid());
            }
            return result;
        }

        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("posTotal-d");

        getPositiveByServer(mixRequestInfo, result);


        timer.addEndTime("posTotal-d");
        timeLogger.info("ChannelLog PosFeedDocpic {} uid:{}", timer.getStaticsInfo(), mixRequestInfo.getUid());

        return result;
    }


    /**
     * 查询正反馈服务，或者正反馈结果
     *
     * @param mixRequestInfo
     * @param result
     * @return
     */
    private void getPositiveByServer(MixRequestInfo mixRequestInfo, Map<String, List<RecallResult>> result) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("docpicPosi");

        //第一步，从cache中获取数据

        Map<String, List<Index4User>> simId2Itemcf = PositiveFeedDataCache.checkUpdateDocpic(mixRequestInfo);
        if (MapUtils.isEmpty(simId2Itemcf)) {
            timer.addEndTime("docpicPosi");
            return;
        }

        Set<String> docIds = Sets.newHashSet();
        simId2Itemcf.values().forEach((list -> list.forEach(itemcfIndex -> docIds.add(itemcfIndex.getI()))));

        Map<String, Document> idDocs = DocPreloadCache.getBatchDocsWithQueryNoClone(docIds);

        //这里要保证原始有序，正反馈结果顺序不能乱，怕影响点击率
        //正反馈通过和用户无关，不用过布隆过滤
        for (Map.Entry<String, List<Index4User>> entry : simId2Itemcf.entrySet()) {
            String originSimId = entry.getKey();
            List<Index4User> recomIndexList = entry.getValue();

            List<RecallResult> recomDocs = result.computeIfAbsent(originSimId, k -> Lists.newArrayList());

            for (Index4User itemcfIndex : recomIndexList) {
                Document doc = idDocs.get(itemcfIndex.getI());
                String recallTag = originSimId;
                RecallResult recallResult = new RecallResult(doc, recallTag, MixReasonUtils.getWhyReasonPosi(itemcfIndex.getR()));
                recomDocs.add(recallResult);
            }
        }
        //删除simid=空列表
        Iterator<Map.Entry<String, List<RecallResult>>> iterator = result.entrySet().iterator();
        while (iterator.hasNext()){
            if (iterator.next().getValue().size()==0){
                iterator.remove();
            }
        }
        timer.addEndTime("docpicPosi");
    }


    public static void main(String[] args) {
        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        LogicParams logicParams = new LogicParams();
        mixRequestInfo.setLogicParams(logicParams);

        List<LastDocBean> lastDocBeans = Lists.newArrayList();
        LastDocBean a = new LastDocBean();
        a.setDocId("47012529");
        a.setSimId("clusterId_30062242");

        LastDocBean b = new LastDocBean();
        b.setDocId("65770776");
        b.setSimId("clusterId_18229366");
        lastDocBeans.add(a);
        lastDocBeans.add(b);
        mixRequestInfo.setLastDocBeans(lastDocBeans);

        PositiveFeedDocpicNewChannelImpl positiveFeedDocpicChannel = new PositiveFeedDocpicNewChannelImpl();
        List<RecallResult> results = positiveFeedDocpicChannel.doDocpicRecom(mixRequestInfo);

        results.forEach(recallResult -> {
            System.out.println(recallResult.getDocument().getDocId() + " " + recallResult.getDocument().getTitle() + " " + recallResult.getWhyReason().getValue() + " " + recallResult.getDocument().getDocType() + "  " + recallResult.getRecallTag());
        });
    }

}
