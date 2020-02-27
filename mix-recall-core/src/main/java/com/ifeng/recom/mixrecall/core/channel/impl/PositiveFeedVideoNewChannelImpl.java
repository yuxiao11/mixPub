package com.ifeng.recom.mixrecall.core.channel.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.item.CdmlVideoItem;
import com.ifeng.recom.mixrecall.common.model.item.Index4User;
import com.ifeng.recom.mixrecall.common.model.item.LastDocBean;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.itemUtil.index4User.MixReasonUtils;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.DocidGuidCache;
import com.ifeng.recom.mixrecall.core.cache.feedback.CdmlVideoCache;
import com.ifeng.recom.mixrecall.core.cache.feedback.PositiveFeedDataCache;
import com.ifeng.recom.mixrecall.core.util.MathUtil;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by geyl on 2018/1/20.
 */
@Service
public class PositiveFeedVideoNewChannelImpl {
    private static final Logger logger = LoggerFactory.getLogger(PositiveFeedVideoNewChannelImpl.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    public static final String Abtest_VideoPositiveFeed_test_cdml = "VideoPositiveFeed_test_cdml";

    private static Gson gson = new Gson();

    /**
     * 视频正反馈通道
     *
     * @param mixRequestInfo
     * @return
     */
    public List<RecallResult> doVideoRecom(MixRequestInfo mixRequestInfo) {

        List<RecallResult> recallResults = new ArrayList<>();

        List<String> debugUids = new ArrayList<>();
        debugUids.add("99001008030221");
        debugUids.add("zhoukang");
        debugUids.add("7c239f2dc23c46049aa6117da7e45e7e");

        Map<String, Boolean> userType = mixRequestInfo.getUserTypeMap();
        if ((userType != null && userType.containsKey(Abtest_VideoPositiveFeed_test_cdml) && userType.get(Abtest_VideoPositiveFeed_test_cdml))
                || debugUids.contains(mixRequestInfo.getUid())) {
            logger.info("uid:{} before cdml size:{}", mixRequestInfo.getUid(), recallResults.size());
            LogicParams logicParams = mixRequestInfo.getLogicParams();
            int positiveFeedVideoNum = logicParams.getPositiveFeedVideoNum();
            Map<String, List<RecallResult>> result = new HashMap<>();
            getCdmlVideo(mixRequestInfo, positiveFeedVideoNum, result);
            result.values().forEach(recallResults::addAll);
            logger.info("uid:{} after cdml size:{}", mixRequestInfo.getUid(), recallResults.size());
        } else {
            Map<String, List<RecallResult>> result = getVideoRecomMap(mixRequestInfo);
            result.values().forEach(recallResults::addAll);
        }

        return recallResults;
    }

    /**
     * 供头条直接调用使用
     * 获取召回结果，key为每个触发的SimiId
     *
     * @param mixRequestInfo
     * @return
     */
    private Map<String, List<RecallResult>> getVideoRecomMap(MixRequestInfo mixRequestInfo) {
        Map<String, List<RecallResult>> result = Maps.newHashMap();

        List<LastDocBean> lastDocBeans = mixRequestInfo.getLastDocBeans();
        if (CollectionUtils.isEmpty(lastDocBeans)) {
            return result;
        }

        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("posTotal-v");
        long start = System.currentTimeMillis();

        getPositiveByServer(mixRequestInfo, result);

        timer.addEndTime("posTotal-v");
        timeLogger.info("ChannelLog PosFeedVideo {} uid:{}", timer.getStaticsInfo(), mixRequestInfo.getUid());

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
        timer.addStartTime("videoPosi");

        List<LastDocBean> lastDocBeans = mixRequestInfo.getLastDocBeans();

        //第一步，从cache中获取数据

        Map<String, List<Index4User>> simId2Itemcf = PositiveFeedDataCache.checkUpdateVideo(mixRequestInfo);
        if (MapUtils.isEmpty(simId2Itemcf)) {
            timer.addEndTime("videoPosi");
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
                RecallResult recallResult = new RecallResult(doc, itemcfIndex.getRT(), MixReasonUtils.getWhyReasonPosi(itemcfIndex.getR()));
                recomDocs.add(recallResult);
            }

        }
        //删除simid=空列表
        Iterator<Map.Entry<String, List<RecallResult>>> iterator = result.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().size() == 0) {
                iterator.remove();
            }
        }
        //lamada
//        Map<String, List<RecallResult>> collect = result.entrySet().stream().filter(x -> x.getValue().size() == 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        timer.addEndTime("videoPosi");
    }


    private void getCdmlVideo(MixRequestInfo mixRequestInfo, int numToadd, Map<String, List<RecallResult>> result) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("cdmlVideo");
        List<LastDocBean> lastDocBeans = mixRequestInfo.getLastDocBeans();
        List<String> id2Query = new ArrayList<>();

        Map<String, String> guidSimIdMapping = new HashMap<>();

        logger.info("uid:{} cdml docIds:{}, lastDocBeans:{}", mixRequestInfo.getUid(), gson.toJson(lastDocBeans));
        for (LastDocBean lastDocBean : lastDocBeans) {
            String docId = lastDocBean.getDocId();
            String simId = lastDocBean.getSimId();
            if (StringUtils.isBlank(lastDocBean.getDocId()) || StringUtils.isBlank(lastDocBean.getSimId())) {
                continue;
            }
            if (docId.contains("-")) {
                id2Query.add(docId);
            } else {
                if (docId.startsWith("ucms_")) {
                    docId = docId.replace("ucms_", "");
                }
                String guid = DocidGuidCache.getGuidFromCache(docId);
                id2Query.add(guid);

                //查redis用guid查，返回结果guid得转回simId，引擎这边只认simId
                guidSimIdMapping.put(guid, simId);
            }
        }

        logger.info("uid:{} cdml id:{}", mixRequestInfo.getUid(), id2Query);
        Map<String, List<CdmlVideoItem>> cdmlVideoResult = CdmlVideoCache.batchQueryCdmlItem(id2Query, guidSimIdMapping);

        Set<String> cdmlGuids = new HashSet<>();
        for (Map.Entry<String, List<CdmlVideoItem>> entry : cdmlVideoResult.entrySet()) {
            List<CdmlVideoItem> list = entry.getValue();
            if (list == null || list.isEmpty()) {
                continue;
            }
            for (CdmlVideoItem item : list) {
                if (StringUtils.isNotBlank(item.getGuid())) {
                    cdmlGuids.add(item.getGuid());
                }
            }
        }

        logger.info("uid:{} cdml guid:{}", mixRequestInfo.getUid(), cdmlGuids);
        Map<String, Document> idDocs = DocPreloadCache.getBatchDocsWithQueryNoClone(cdmlGuids);

        for (Map.Entry<String, List<CdmlVideoItem>> entry : cdmlVideoResult.entrySet()) {
            String originGuid = entry.getKey();
            List<CdmlVideoItem> recomItemList = entry.getValue();

            List<RecallResult> recomDocs = result.computeIfAbsent(originGuid, k -> Lists.newArrayList());

            int count = 0;
            for (CdmlVideoItem item : recomItemList) {
                if (count++ > numToadd) {
                    break;
                }

                Document doc = idDocs.get(item.getGuid());
                String recallTag = originGuid;

                RecallResult recallResult = new RecallResult(doc, recallTag, WhyReason.POS_FEED_VIDEO_ITEMCF_CDML);
                recomDocs.add(recallResult);
            }
        }


        timer.addEndTime("cdmlVideo");
    }


    public static void main(String[] args) {
        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        LogicParams logicParams = new LogicParams();
        mixRequestInfo.setLogicParams(logicParams);
        mixRequestInfo.setUid("99001008030221");


        List<LastDocBean> lastDocBeans = Lists.newArrayList();
        LastDocBean a = new LastDocBean();
        a.setDocId("b79ec5cf-dbcf-4ea2-8a22-0e868371d2d4");
        a.setSimId("clusterId_87033383");

        lastDocBeans.add(a);

        LastDocBean b = new LastDocBean();
        b.setDocId("e544ab35-da7f-4218-9682-553c99f97da8");
        b.setSimId("clusterId_117873817");
        lastDocBeans.add(b);
        mixRequestInfo.setLastDocBeans(lastDocBeans);


        PositiveFeedVideoNewChannelImpl positiveFeedVideoChannel = new PositiveFeedVideoNewChannelImpl();
        List<RecallResult> results = positiveFeedVideoChannel.doVideoRecom(mixRequestInfo);

        results.forEach(recallResult -> {
            System.out.println(recallResult.getDocument().getDocId() + " " + recallResult.getDocument().getTitle() + " " + recallResult.getWhyReason().getValue() + " " + recallResult.getDocument().getDocType());
        });
    }

}
