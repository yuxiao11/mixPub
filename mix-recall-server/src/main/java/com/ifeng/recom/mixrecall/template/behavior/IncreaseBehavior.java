package com.ifeng.recom.mixrecall.template.behavior;

import com.google.common.collect.Lists;
import com.ifeng.recom.mixrecall.common.config.ApplicationConfig;
import com.ifeng.recom.mixrecall.common.config.TestUserConfig;
import com.ifeng.recom.mixrecall.common.constant.ApolloConstant;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.IncreaseRecallNum;
import com.ifeng.recom.mixrecall.common.constant.MonitorKey;
import com.ifeng.recom.mixrecall.common.constant.MonitorType;
import com.ifeng.recom.mixrecall.common.constant.RecallChannelBeanName;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant.CHANNEL;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.item.MixResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.filter.BeijingFilter;
import com.ifeng.recom.mixrecall.common.service.handler.remove.IItemRemoveHandler;
import com.ifeng.recom.mixrecall.common.service.handler.remove.RemoverHandlerService;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.UserUtils;
import com.ifeng.recom.mixrecall.core.util.MathUtil;
import com.ifeng.recom.mixrecall.model.RecallChannelResult;
import com.ifeng.recom.mixrecall.model.RecallConfig;
import com.ifeng.recom.mixrecall.negative.NegativeSupport;
import com.ifeng.recom.mixrecall.template.BaseTemplate;
import com.ifeng.recom.mixrecall.threadpool.RecallExecutor;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import com.ifeng.recom.tools.log.MonitorLog;
import com.ifeng.recom.tools.log.MonitorLogEntity;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.ifeng.recom.mixrecall.common.config.constant.ApolloConstant.DSSM_TEST_USERS;
import static com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory.MapStringMapStringString;


/**
 * 召回的增量和全量公用一个逻辑，只是调用量的size不同
 * Created by jibin on 2018/1/19.
 */
@Service
public class IncreaseBehavior extends BaseTemplate<MixResult> {
    private static final Logger logger = LoggerFactory.getLogger(IncreaseBehavior.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);


    @Autowired
    private NegativeSupport negativeSupport;

    @Autowired
    private RemoverHandlerService removerHandlerService;

    @Autowired
    private RecallExecutor recallThreadPool;

    @Resource(name = "recallThreadPool")
    private ThreadPoolExecutor poolExecutor;

    @Override
    public MixResult doRecom(MixRequestInfo mixRequestInfo) {
        long start = System.currentTimeMillis();
        TimerEntity timer = TimerEntityUtil.getInstance();
        if (mixRequestInfo.isDebugUser()) {
            logger.info("uid:{} debugTest increaseBehavior start");
        }
        String uid = mixRequestInfo.getUid();
        Map<String, Boolean> userTypeMap = mixRequestInfo.getUserTypeMap();
        /**
         * 最近50条曝光的信息 获取负反馈信息 其中缓存时间为30mins
         * 输入参数：MixrequestInfo
         * 输出参数：Map<String,List<String>> key:通道名{cotag,c,sc,topic} value:对应通道需要过滤的tag
         */
        Map<String, Map<String, Double>> negtiveTag = negativeSupport.getNegativeTag(mixRequestInfo);
        mixRequestInfo.setNegativeMap(negtiveTag);
        List<RecallConfig> recallConfigs = Lists.newArrayList();
        List<IItemRemoveHandler<Document>> removeHandlers = removerHandlerService.buildCommonHandlers(mixRequestInfo);
        /**
         * 此处添加图谱召回相关通道 by yx 20191128
         */
        if (mixRequestInfo.getDevMap().getOrDefault("GraphCotag", "base").equals("true")
                || ApplicationConfig.getProperty(ApolloConstant.MixRecall_DebugUsers).contains(mixRequestInfo.getUid())) {
            recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_D_GRAPH).setRemoverList(removeHandlers));
            recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_V_GRAPH).setRemoverList(removeHandlers));
        }
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.USER_SEARCH).setRemoverList(removeHandlers));
        if (!UserUtils.isSubTestModel(mixRequestInfo)) {
            // 过滤条件不同
            recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.USER_SUB).
                    setRemoverList(removerHandlerService.buildSubHandlers(mixRequestInfo)));
        }
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.LDA_TOPIC).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.SOURCE).setRemoverList(removeHandlers));

        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_DOC).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_D_N).setRemoverList(removeHandlers));
        //新通道ffm试验
        if (UserUtils.isFFMTest(mixRequestInfo)) {
            recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.FFM).setRemoverList(removeHandlers));
        }
        //新通道ffmv试验
        if (UserUtils.isFFMVTest(mixRequestInfo)) {
            recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.FFMV).setRemoverList(removeHandlers));
        }
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_V_N).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_V).setRemoverList(removeHandlers));
        if (!userTypeMap.getOrDefault("isWxb", true)) {
            long hashNum = MathUtil.getNumByUid(uid, "usercf_dssm");
            if (TestUserConfig.getTestUser(DSSM_TEST_USERS).contains(mixRequestInfo.getUid()) ||
                    (hashNum < 1500 && !userTypeMap.getOrDefault("isDevTestUser", false))) {
                recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.USER_CF_DSSM).setRemoverList(removeHandlers));
            } else {
                recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.USER_CF_ALS).setRemoverList(removeHandlers));
            }
        }
        if (!BeijingFilter.isBJOrWXB(mixRequestInfo)) {
            recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.EXCELLENT_V).setRemoverList(removeHandlers));
        }
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.EXCELLENT_D).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_V_SIM).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_D_SIM).setRemoverList(removeHandlers));
        //添加user_media通道
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.MEDIA_D).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.MEDIA_V).setRemoverList(removeHandlers));
        //添加单独c sc图文+视频通道
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.DOCPIC_C).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.VIDEO_C).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.DOCPIC_SC).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.VIDEO_SC).setRemoverList(removeHandlers));
        List<RecallResult> resultDocs = Lists.newArrayList();
        try {

            timer.addStartTime("getAsyncExecutorResult");
            List<RecallChannelResult> channelResults = recallThreadPool.recall(mixRequestInfo, recallConfigs, 500, poolExecutor);
            logger.info("recall channel statistics result:{}, uid:{}", recallThreadPool.storeLoggerInfo(channelResults), uid);

            MonitorLogEntity[] channelCost = BaseTemplate.channelCost(channelResults, mixRequestInfo.getUid());
            if (channelCost != null && channelCost.length > 0) {
                MonitorLog.storeLogger(MonitorType.CHANNEL_COST, MonitorType.INCR, channelCost);
            }
            Map<CHANNEL, List<RecallResult>> channelDocsMap = recallThreadPool.change(channelResults);
            timer.addEndTime("getAsyncExecutorResult");
            List<RecallResult> userCfAls = channelDocsMap.getOrDefault(CHANNEL.USER_CF_ALS, new ArrayList<>());
            List<RecallResult> userCfDssm = channelDocsMap.getOrDefault(CHANNEL.USER_CF_DSSM, new ArrayList<>());
            List<RecallResult> cotagDoc = channelDocsMap.getOrDefault(CHANNEL.COTAG_DOC, new ArrayList<>());
            List<RecallResult> cotagNewDoc = channelDocsMap.getOrDefault(CHANNEL.COTAG_D_N, new ArrayList<>());
            List<RecallResult> ldaTopic = channelDocsMap.getOrDefault(CHANNEL.LDA_TOPIC, new ArrayList<>());
            List<RecallResult> ffm = channelDocsMap.getOrDefault(CHANNEL.FFM, new ArrayList<>());
            List<RecallResult> userSearch = channelDocsMap.getOrDefault(CHANNEL.USER_SEARCH, new ArrayList<>());
            List<RecallResult> userSub = channelDocsMap.getOrDefault(CHANNEL.USER_SUB, new ArrayList<>());
            List<RecallResult> cotagVideoNew = channelDocsMap.getOrDefault(CHANNEL.COTAG_V, new ArrayList<>());
            List<RecallResult> cotagVideoN = channelDocsMap.getOrDefault(CHANNEL.COTAG_V_N, new ArrayList<>());
            List<RecallResult> sourceNews = channelDocsMap.getOrDefault(CHANNEL.SOURCE, new ArrayList<>());
            List<RecallResult> excellentVideo = channelDocsMap.getOrDefault(CHANNEL.EXCELLENT_V, new ArrayList<>());
            List<RecallResult> excellentDocpic = channelDocsMap.getOrDefault(CHANNEL.EXCELLENT_D, new ArrayList<>());
            List<RecallResult> cotagVSim = channelDocsMap.getOrDefault(CHANNEL.COTAG_V_SIM, new ArrayList<>());
            List<RecallResult> cotagDSim = channelDocsMap.getOrDefault(CHANNEL.COTAG_D_SIM, new ArrayList<>());
            List<RecallResult> cotagVGraph = channelDocsMap.getOrDefault(CHANNEL.COTAG_V_GRAPH, new ArrayList<>());
            List<RecallResult> cotagDGraph = channelDocsMap.getOrDefault(CHANNEL.COTAG_D_GRAPH, new ArrayList<>());
            List<RecallResult> docMedia = channelDocsMap.getOrDefault(CHANNEL.MEDIA_D, new ArrayList<>());
            List<RecallResult> videoMedia = channelDocsMap.getOrDefault(CHANNEL.MEDIA_V, new ArrayList<>());
            List<RecallResult> docpicC = channelDocsMap.getOrDefault(CHANNEL.DOCPIC_C, new ArrayList<>());
            List<RecallResult> docpicSc = channelDocsMap.getOrDefault(CHANNEL.DOCPIC_SC, new ArrayList<>());
            List<RecallResult> videoC = channelDocsMap.getOrDefault(CHANNEL.VIDEO_C, new ArrayList<>());
            List<RecallResult> videoSc = channelDocsMap.getOrDefault(CHANNEL.VIDEO_SC, new ArrayList<>());

            Map<String, Double> channelRatio = new HashMap<>();
            resultDocs = outputChannelNumControl(mixRequestInfo, cotagDoc, userSearch, userSub, userCfAls, cotagVideoNew, cotagVideoN, sourceNews, ldaTopic, excellentVideo, excellentDocpic, cotagNewDoc, ffm, userCfDssm,
                    cotagVSim, cotagDSim, cotagDGraph, cotagVGraph, docMedia, videoMedia, docpicC, docpicSc, videoC, videoSc, channelRatio);
            logger.info("uid:{} before remove dup:{}", uid, resultDocs.size());
            resultDocs = mergeMultiChannelDocs(mixRequestInfo, resultDocs, channelRatio);
            logger.info("uid:{} after remove dup:{}", uid, resultDocs.size());
            //debug 日志
            if (mixRequestInfo.isDebugUser() || "3619988e26104ce99bb230d2d0335a91".equals(mixRequestInfo.getUid())) {
                for (RecallResult recallResult : resultDocs) {
                    logger.info("uid:{} docId:{} simId:{} docType:{} available:{} disType:{} times:{} hotboost:{} title:{} channel:{} debug:{}", uid, recallResult.getDocument().getDocId(), recallResult.getDocument().getSimId(), recallResult.getDocument().getDocType(), recallResult.getDocument().isAvailable(), recallResult.getDocument().getDistype(), recallResult.getDocument().getTimeSensitive().replace("\r\n", ""), recallResult.getDocument().getHotBoost(), recallResult.getDocument().getTitle(), recallResult.getWhyReason().getValue(), recallResult.getRecallTag());
                }
            }
        } catch (Exception e) {
            logger.error("{} getMixResult ERROR:{}", mixRequestInfo.getUid(), e);
        }

        int recallSize = CollectionUtils.size(resultDocs);
        logger.info("{} result is size:{}", mixRequestInfo.getUid(), recallSize);

        MonitorLogEntity[] channelCount = BaseTemplate.monitorChannel(resultDocs, mixRequestInfo.getUid());
        if (channelCount != null && channelCount.length > 0) {
            MonitorLog.storeLogger(MonitorType.CHANNEL_SIZE, MonitorType.INCR, channelCount);
        }
        MonitorLog.storeLogger(MonitorType.RECALL_BASIC, MonitorType.INCR,
                MonitorLogEntity.intEntity(MonitorKey.SIZE, recallSize),
                MonitorLogEntity.longEntity(MonitorKey.RECOM_COST, System.currentTimeMillis() - start),
                MonitorLogEntity.stringEntity(MonitorKey.UID, mixRequestInfo.getUid())
        );

        return buildMixResult(mixRequestInfo, resultDocs, GyConstant.isNewResult);
    }


    private static List<RecallResult> outputChannelNumControl(MixRequestInfo mixRequestInfo, List<RecallResult> cotagDoc, List<RecallResult> search, List<RecallResult> sub, List<RecallResult> userCfAls, List<RecallResult> cotagVideoNew, List<RecallResult> cotagVideoN, List<RecallResult> sourceNews,
                                                              List<RecallResult> ldaTopic, List<RecallResult> excellentVideo, List<RecallResult> excellentDocpic, List<RecallResult> cotagNewDoc, List<RecallResult> ffm, List<RecallResult> userCfDssm, List<RecallResult> cotagVSim, List<RecallResult> cotagDSim,
                                                              List<RecallResult> cotagDGraph, List<RecallResult> cotagVGraph, List<RecallResult> docMedia, List<RecallResult> videoMedia, List<RecallResult> docpicC, List<RecallResult> docpicSc, List<RecallResult> videoC, List<RecallResult> videoSc, Map<String, Double> channelRatio) {

        int needNum = 700;
        List<RecallResult> results = new ArrayList<>();
//        List<RecallResult> finalResults = new ArrayList<>();
        Map<String, String> devMap = mixRequestInfo.getDevMap();
        Map<String, Integer> expectMap = new HashMap<>();


        int cotagDocSize = Math.min(IncreaseRecallNum.COTAG_DOC, cotagDoc.size());
        int searchSize = Math.min(IncreaseRecallNum.SEARCH, search.size());
        int subSize = Math.min(IncreaseRecallNum.SUB, sub.size());
        int userCfAlsSize = Math.min(IncreaseRecallNum.USER_CF, userCfAls.size());
        int cotagVideoNewSize = Math.min(IncreaseRecallNum.COTAG_VIDEO, cotagVideoNew.size());
        int cotagVideoNSize = Math.min(IncreaseRecallNum.COTAG_VIDEO, cotagVideoN.size());
        int sourceNewsSize = Math.min(IncreaseRecallNum.SOURCE, sourceNews.size());
        int ldaTopicSize = Math.min(IncreaseRecallNum.LDA_TOPIC, ldaTopic.size());
        int excellentVideoSize = Math.min(IncreaseRecallNum.EXCELLENT_VIDEO, excellentVideo.size());
        int excellentDocpicSize = Math.min(IncreaseRecallNum.EXCELLENT_DOCPIC, excellentDocpic.size());

        int expDocSize = 0;
        int expVideoSize = 0;
        int expSupplyNum = 0;


        int cotagNewDocSize = Math.min(IncreaseRecallNum.COTAG_N_DOC, cotagNewDoc.size());
        int FFMSize = IncreaseRecallNum.FFM;
        if (mixRequestInfo.getDevMap().getOrDefault("ffmNumTest", "base").equals("true")) {
            FFMSize = 500;
        }
        FFMSize = Math.min(FFMSize, ffm.size());

        int userCfDssmSize = Math.min(IncreaseRecallNum.USER_CF, userCfDssm.size());
        int cotagVSimSize = Math.min(IncreaseRecallNum.COTAG_V_SIM, cotagVSim.size());
        int cotagDSimSize = Math.min(IncreaseRecallNum.COTAG_D_SIM, cotagDSim.size());
        int docMediaSize = Math.min(IncreaseRecallNum.MEDIA_D, docMedia.size());
        int videoMediaSize = Math.min(IncreaseRecallNum.MEDIA_V, videoMedia.size());

        int cotagNum = cotagVideoNSize + cotagDocSize + cotagNewDocSize + cotagVideoNewSize;
        int cotagSimNum = cotagDSimSize + cotagVSimSize;
        int exploreNum = expDocSize + expVideoSize;
//        int cotagGraphNum = cotagDGraphSize + cotagVGraphSize;

        int excellentNum = excellentVideoSize + excellentDocpicSize;
        int ldaNum = ldaTopicSize;
        int mediaNum = docMediaSize + videoMediaSize;
        int subNum = subSize;
        int ucfNum = userCfAlsSize + userCfDssmSize;
        int totalNum = cotagNum + cotagSimNum + exploreNum + excellentNum + ldaNum + mediaNum + subNum + ucfNum;


        int expectTotal = 0;

        logger.info("dynamic Num : uid:{} num:cotagDoc:{}  search:{} sub:{} userCfAls:{},cotagVideoNew:{}" +
                        ",cotagVideoN:{},sourceNews:{},ldaTopic:{},excellentVideo:{},excellentDocpic:{},EXP_D:{}, EXP_V:{}," +
                        "cotagNewDoc:{},userCfDssm:{},cotagVSim:{},cotagDSim:{},docMedia:{},videoMedia:{}"
                , mixRequestInfo.getUid(), cotagDocSize, searchSize, subSize, userCfAlsSize, cotagVideoNewSize
                , cotagVideoNSize, sourceNewsSize, ldaTopicSize, excellentVideoSize, excellentDocpicSize, expDocSize, expVideoSize
                , cotagNewDocSize, userCfDssmSize, cotagVSimSize, cotagDSimSize, docMediaSize, videoMediaSize);


        /**
         * 上面需要改
         */
//        Map<String, Map<String, String>> ratioMap = new HashMap<>();//NumRatioConfig.getRatioNum(ApolloConstant.pullnumRatio);
        String exploreDefault = ApplicationConfig.getProperty(ApolloConstant.pullnumRatio);
        Map<String, Map<String, String>> ratioMap = GsonUtil.json2Object(exploreDefault, MapStringMapStringString);


//            Collections.shuffle(exploreList);

        Map<String, Double> numMap = getNumMap(mixRequestInfo, cotagNum, cotagSimNum, exploreNum, excellentNum, ldaNum, mediaNum, subNum, ucfNum, totalNum, ratioMap);


        cotagVideoNSize = (int) (cotagVideoNSize * numMap.get("cotagRatio"));
        cotagDocSize = (int) (cotagDocSize * numMap.get("cotagRatio"));
        cotagNewDocSize = (int) (cotagNewDocSize * numMap.get("cotagRatio"));
        cotagVideoNewSize = (int) (cotagVideoNewSize * numMap.get("cotagRatio"));
        cotagDSimSize = (int) (cotagDSimSize * numMap.get("cotagSimRatio"));
        cotagVSimSize = (int) (cotagVSimSize * numMap.get("cotagSimRatio"));
        expDocSize = (int) (expDocSize * numMap.get("exploreRatio"));
        expVideoSize = (int) (expVideoSize * numMap.get("exploreRatio"));
        excellentVideoSize = (int) (excellentVideoSize * numMap.get("excellentRatio"));
        excellentDocpicSize = (int) (excellentDocpicSize * numMap.get("excellentRatio"));
        ldaTopicSize = (int) (ldaTopicSize * numMap.get("ldaRatio"));
        docMediaSize = (int) (docMediaSize * numMap.get("mediaRatio"));
        videoMediaSize = (int) (videoMediaSize * numMap.get("mediaRatio"));
        subSize = (int) (subSize * numMap.get("subRatio"));
        userCfAlsSize = (int) (userCfAlsSize * numMap.get("ucfRatio"));
        userCfDssmSize = (int) (userCfDssmSize * numMap.get("ucfRatio"));


        /**
         * cotag内部各个通道占比
         */

//            if (mixRequestInfo.getUid().equals("3619988e26104ce99bb230d2d0335a91")) {
//                logger.info("CotagInner 3619988e26104ce99bb230d2d0335a91 is coming...");
//                logger.info("CotagInner devMap:{}", devMap.get("recallDynamic"));
//        Map<String, Map<String, String>> cotagInnerRatioMap = new HashMap<>();
        Map<String, Double> cotagInnerNumMap = null;
        Map<String, Map<String, String>> cotagInnerRatioMap = GsonUtil.json2Object(ApplicationConfig.getProperty(ApolloConstant.cotagInnerRatio), MapStringMapStringString);

        try {
            cotagInnerNumMap = getCotagInnerNumMap(mixRequestInfo, cotagDocSize, cotagNewDocSize, cotagVideoNewSize, cotagVideoNSize, cotagInnerRatioMap);
        } catch (Throwable e) {
            logger.error(e.getMessage() + "CotagInner uid: {}", mixRequestInfo.getUid());
        }

        int cotagTotalNum = cotagDocSize + cotagNewDocSize + cotagVideoNewSize + cotagVideoNSize;
//        logger.info("CotagInner Before: uid:{} cotagTotalNum:{} cotagNewDocSize:{} cotagDocSize:{} cotagVideoNewSize:{} cotagVideoNSize:{}",
//                mixRequestInfo.getUid(), cotagTotalNum, cotagNewDocSize, cotagDocSize, cotagVideoNewSize, cotagVideoNSize);
//

        cotagVideoNewSize = (int) (cotagTotalNum * (cotagInnerNumMap.getOrDefault("co_v_l_Ratio", 0.0)
                + cotagInnerNumMap.getOrDefault("co_v_r_Ratio", 0.0)));

        cotagVideoNSize = (int) (cotagTotalNum * (cotagInnerNumMap.getOrDefault("co_v_l_n_base_Ratio", 0.0)
                + cotagInnerNumMap.getOrDefault("co_v_r_n_Ratio", 0.0)));

        cotagDocSize = (int) (cotagTotalNum * (cotagInnerNumMap.getOrDefault("co_d_l_base_Ratio", 0.0)
                + cotagInnerNumMap.getOrDefault("co_d_r_Ratio", 0.0)));
        cotagNewDocSize = (int) (cotagTotalNum * (cotagInnerNumMap.getOrDefault("co_d_l_n_Ratio", 0.0)
                + cotagInnerNumMap.getOrDefault("co_d_r_n_Ratio", 0.0)));

        cotagTotalNum = cotagDocSize + cotagNewDocSize + cotagVideoNewSize + cotagVideoNSize;

//        logger.info("CotagInner After: uid:{} cotagTotalNum:{} cotagNewDocSize:{} cotagDocSize:{} cotagVideoNewSize:{} cotagVideoNSize:{}",
//                mixRequestInfo.getUid(), cotagTotalNum, cotagNewDocSize, cotagDocSize, cotagVideoNewSize, cotagVideoNSize);
//

        expectMap.put("cotagVideoNSize", cotagVideoNSize);
        expectMap.put("cotagDocSize", cotagDocSize);
        expectMap.put("cotagNewDocSize", cotagNewDocSize);
        expectMap.put("cotagVideoNewSize", cotagVideoNewSize);
        expectMap.put("cotagDSimSize", cotagDSimSize);
        expectMap.put("cotagVSimSize", cotagVSimSize);
        expectMap.put("expDocSize", expDocSize);
        expectMap.put("expVideoSize", expVideoSize);
        expectMap.put("excellentVideoSize", excellentVideoSize);
        expectMap.put("excellentDocpicSize", excellentDocpicSize);
        expectMap.put("ldaTopicSize", ldaTopicSize);
        expectMap.put("docMediaSize", docMediaSize);
        expectMap.put("videoMediaSize", videoMediaSize);
        expectMap.put("subSize", subSize);
        expectMap.put("userCfAlsSize", userCfAlsSize);
        expectMap.put("userCfDssmSize", userCfDssmSize);
        expectMap.put("totalNum", numMap.get("totalNum").intValue());
        expectMap.put("pullCount", numMap.get("pullCount").intValue());


        /**
         * 以下三条results中添加的结果（sourceNews,search,ffm）在动态策略中并未修改（505,506,507行） 故不必重复添加
         */


        try {
            results.addAll(sub.subList(0, Math.min(sub.size(), expectMap.getOrDefault("subSize", sub.size()))));

            results.addAll(excellentVideo.subList(0, Math.min(excellentVideo.size(), expectMap.getOrDefault("excellentVideoSize", excellentVideo.size()))));
            results.addAll(excellentDocpic.subList(0, Math.min(excellentDocpic.size(), expectMap.getOrDefault("excellentDocpicSize", excellentDocpic.size()))));

            results.addAll(sourceNews.subList(0, Math.min(IncreaseRecallNum.SOURCE, sourceNews.size())));
            results.addAll(search.subList(0, Math.min(IncreaseRecallNum.SEARCH, search.size())));
            results.addAll(ffm.subList(0, FFMSize));

            results.addAll(docpicC.subList(0, Math.min(IncreaseRecallNum.DOCPIC_C, docpicC.size())));
            results.addAll(docpicSc.subList(0, Math.min(IncreaseRecallNum.DOCPIC_SC, docpicSc.size())));
            results.addAll(videoC.subList(0, Math.min(IncreaseRecallNum.VIDEO_C, videoC.size())));
            results.addAll(videoSc.subList(0, Math.min(IncreaseRecallNum.VIDEO_SC, videoSc.size())));

            /**
             * 添加图谱召回数据
             */

            results.addAll(cotagDGraph.subList(0, Math.min(IncreaseRecallNum.COTAG_D_GRAPH, cotagDGraph.size())));
            results.addAll(cotagVGraph.subList(0, Math.min(IncreaseRecallNum.COTAG_V_GRAPH, cotagVGraph.size())));

            results.addAll(cotagVideoN.subList(0, Math.min(cotagVideoN.size(), expectMap.getOrDefault("cotagVideoNSize", cotagVideoN.size()))));
            results.addAll(cotagDoc.subList(0, Math.min(cotagDoc.size(), expectMap.getOrDefault("cotagDocSize", cotagDoc.size()))));
            results.addAll(cotagNewDoc.subList(0, Math.min(cotagNewDoc.size(), expectMap.getOrDefault("cotagNewDocSize", cotagNewDoc.size()))));
            results.addAll(cotagVideoNew.subList(0, Math.min(cotagVideoNew.size(), expectMap.getOrDefault("cotagVideoNewSize", cotagVideoNew.size()))));
            results.addAll(ldaTopic.subList(0, Math.min(ldaTopic.size(), expectMap.getOrDefault("ldaTopicSize", ldaTopic.size()))));


            results.addAll(userCfAls.subList(0, Math.min(userCfAls.size(), expectMap.getOrDefault("userCfAlsSize", userCfAls.size()))));
            results.addAll(userCfDssm.subList(0, Math.min(userCfDssm.size(), expectMap.getOrDefault("userCfDssmSize", userCfDssm.size()))));


            results.addAll(cotagDSim.subList(0, Math.min(cotagDSim.size(), expectMap.getOrDefault("cotagDSimSize", cotagDSim.size()))));
            results.addAll(cotagVSim.subList(0, Math.min(cotagVSim.size(), expectMap.getOrDefault("cotagVSimSize", cotagVSim.size()))));

            results.addAll(docMedia.subList(0, Math.min(docMedia.size(), expectMap.getOrDefault("docMediaSize", docMedia.size()))));
            results.addAll(videoMedia.subList(0, Math.min(videoMedia.size(), expectMap.getOrDefault("videoMediaSize", videoMedia.size()))));
        } catch (Exception e) {
            logger.error("{} increase control ERROR:{}", mixRequestInfo.getUid(), e.toString());
        } finally {
            expectMap.clear();
            ratioMap.clear();
            numMap.clear();
            cotagInnerRatioMap.clear();
            cotagInnerNumMap.clear();
        }

        //如果召回不足，用cotag进行补充
        needNum = needNum - results.size();
        needNum = 0;
        if (needNum > 0) {
            try {
                List<RecallResult> cotagSupply = cotagDoc.subList(Math.min(IncreaseRecallNum.COTAG_DOC, cotagDoc.size()), cotagDoc.size());
                if (cotagSupply.size() == 0) {
                    cotagSupply = new ArrayList<RecallResult>();
                } else {
                    for (RecallResult recallResult : cotagSupply) {
                        if (recallResult.getWhyReason().getValue().equals(WhyReason.COTAG_D_LONG)) {
                            recallResult.setWhyReason(WhyReason.COTAG_SUPPLY_LONG);
                        } else if (recallResult.getWhyReason().getValue().equals(WhyReason.COTAG_D_RECENT.getValue())) {
                            recallResult.setWhyReason(WhyReason.COTAG_SUPPLY_RECENT);
                        } else if (recallResult.getWhyReason().getValue().equals(WhyReason.COTAG_D_LAST.getValue())) {
                            recallResult.setWhyReason(WhyReason.COTAG_SUPPLY_LAST);
                        }
                    }
                }
                Collections.shuffle(cotagSupply);
                results.addAll(cotagSupply.subList(0, Math.min(needNum, cotagSupply.size())));
            } catch (Exception e) {
                logger.error("{} increase control:{}", mixRequestInfo.getUid(), e);
            }
        }


//        去掉补足逻辑 by yx 20190926
//        若还召回不足，并且相差较大，则用explore补充
//        try {
//            List<RecallResult> exp_d_supply = expDoc.subList(Math.min(expDocSize, expDoc.size()), Math.min(IncreaseRecallNum.EXPLORE_Supply, expDoc.size()));
//            List<RecallResult> exp_v_supply = expVideo.subList(Math.min(expVideoSize, expVideo.size()), Math.min(IncreaseRecallNum.EXPLORE_Supply, expVideo.size()));
//            if (exp_d_supply.size() > 0) {
//                for (RecallResult recallResult : exp_d_supply) {
//                    recallResult.setWhyReason(WhyReason.EXP_D_SUPPLY);
//                }
//            }
//            if (exp_v_supply.size() > 0) {
//                for (RecallResult recallResult : exp_v_supply) {
//                    recallResult.setWhyReason(WhyReason.EXP_V_SUPPLY);
//                }
//            }
//
//            if (results.size() < 200 && mixRequestInfo.getRecomChannel() == "videoapp") {
//                results.addAll(exp_d_supply);
//                results.addAll(exp_v_supply);
//                expSupplyNum = exp_d_supply.size() + exp_v_supply.size();
//            } else if (results.size() < 100 && mixRequestInfo.getRecomChannel() != "videoapp") {
//                results.addAll(exp_d_supply);
//                results.addAll(exp_v_supply);
//                expSupplyNum = exp_d_supply.size() + exp_v_supply.size();
//
//            }
//
//
//        } catch (Exception e) {
//            logger.error("{} increase control:{}", mixRequestInfo.getUid(), e);
//        }


        /**
         * 增加expect Number 和实际recall Number对比 便于统计
         */

//
        channelRatio.put("CotagSize", (double) (Math.min(cotagVideoNSize, cotagVideoN.size()) + Math.min(cotagDoc.size(), cotagDocSize) + Math.min(cotagNewDocSize, cotagNewDoc.size()) + Math.min(cotagVideoNewSize, cotagVideoNew.size())));
        channelRatio.put("CotagSimSize", (double) (Math.min(cotagDSimSize, cotagDSim.size()) + Math.min(cotagVSimSize, cotagVSim.size())));
        channelRatio.put("ExcellentSize", (double) (Math.min(excellentVideoSize, excellentVideo.size()) + Math.min(excellentDocpicSize, excellentDocpic.size())));
        channelRatio.put("LdaSize", (double) (Math.min(ldaTopicSize, ldaTopic.size())));
        channelRatio.put("MediaSize", (double) (Math.min(docMediaSize, docMedia.size()) + Math.min(videoMediaSize, videoMedia.size())));
        channelRatio.put("SubSize", (double) (Math.min(subSize, sub.size())));
        channelRatio.put("SearchSize", (double) (Math.min(searchSize, search.size())));
        channelRatio.put("UcfSize", (double) (Math.min(userCfAlsSize, userCfAls.size()) + Math.min(userCfDssmSize, userCfDssm.size())));
        channelRatio.put("TotalSize", channelRatio.get("CotagSize") + channelRatio.get("CotagSimSize") + channelRatio.get("ExcellentSize") + channelRatio.get("LdaSize")
                + channelRatio.get("MediaSize") + channelRatio.get("SubSize") + channelRatio.get("UcfSize") + channelRatio.get("SearchSize"));

        channelRatio.put("CotagRatio", channelRatio.get("CotagSize") / channelRatio.get("TotalSize"));
        channelRatio.put("CotagSimRatio", channelRatio.get("CotagSimSize") / channelRatio.get("TotalSize"));
        channelRatio.put("ExcellentRatio", channelRatio.get("ExcellentSize") / channelRatio.get("TotalSize"));
        channelRatio.put("LdaRatio", channelRatio.get("LdaSize") / channelRatio.get("TotalSize"));
        channelRatio.put("MediaRatio", channelRatio.get("MediaSize") / channelRatio.get("TotalSize"));
        channelRatio.put("SubRatio", channelRatio.get("SubSize") / channelRatio.get("TotalSize"));
        channelRatio.put("SearchRatio", channelRatio.get("SearchSize") / channelRatio.get("TotalSize"));
        channelRatio.put("UcfRatio", channelRatio.get("UcfSize") / channelRatio.get("TotalSize"));

        if (mixRequestInfo.getDevMap().getOrDefault("FinalNegative", "base").equals("true")) {
            results = negativeResultFilter(mixRequestInfo, results);
        }

        if (mixRequestInfo.getDevMap().getOrDefault("GraphCotag", "base").equals("true")) {
            logger.info("check cotagGraph Number uid:{} Dnum:{} Vnum:{}", mixRequestInfo.getUid(), cotagDGraph.size(), cotagVGraph.size());
        }

        return results;
    }

    /**
     * 此处对最终结果进行过滤
     *
     * @param result
     * @return
     */
    private static List<RecallResult> negativeResultFilter(MixRequestInfo mixRequestInfo, List<RecallResult> result) {
        Map<String, List<String>> filterMap = new HashMap<>();
        Set<String> negativeFeatureSet = mixRequestInfo.getNegativeMap().getOrDefault("featureWord", new HashMap<>()).keySet();
        Map<String, Double> negativeCateMap = mixRequestInfo.getNegativeMap().getOrDefault("cotag", new HashMap<>());
        List<String> need2Filter = new ArrayList<>();

        continueOut:
        for (RecallResult item : result) {
            try {
//                String c = item.getDocument().getC();
                String sc = item.getDocument().getSubcate();
                String features = item.getDocument().getCotag();
                String docid = item.getDocument().getDocId();
                if (sc == null || features == null || features.length() == 0 || sc.length() == 0) {
                    continue;
                }

//                c = c.split("\\^")[0];
                sc = sc.split("\\^")[0];
                for (String feature : features.split(" ")) {
                    if (!feature.contains("-")) {
                        continue;
                    }
                    if (negativeFeatureSet.contains(feature.split("-")[1])) {
                        continue continueOut;
                    }
                }

//                if(!filterMap.keySet().contains(c)){
//                    List<String> tmpList = new ArrayList<>();
//                    tmpList.add(docid);
//                    filterMap.put(c,tmpList);
//                }else{
//                    filterMap.get(c).add(docid);
//                }
                if (!filterMap.keySet().contains(sc)) {
                    List<String> tmpList = new ArrayList<>();
                    tmpList.add(docid);
                    filterMap.put(sc, tmpList);
                } else {
                    filterMap.get(sc).add(docid);
                }
            } catch (Exception e) {
                logger.error("get FilteredResult step1 error uid:{} error:{}", mixRequestInfo.getUid(), e);
            }

        }

        try {
            for (Map.Entry<String, List<String>> entry : filterMap.entrySet()) {
                if (negativeCateMap.containsKey(entry.getKey())) {
                    List<String> resultList = filterMap.get(entry.getKey());
                    need2Filter.addAll(resultList.subList((int) (negativeCateMap.get(entry.getKey()) * resultList.size()), resultList.size()));
                }
            }
        } catch (Exception e) {
            logger.error("get FilteredResult step2 error uid:{} error:{}", mixRequestInfo.getUid(), e);

        }

        List<RecallResult> finalResult = result.stream().filter(item -> !need2Filter.contains(item.getDocument().getDocId())).collect(Collectors.toList());


        logger.info("filter Size uid:{} resultSize:{} finalResultSize:{}", mixRequestInfo.getUid(), result.size(), finalResult.size());


        return finalResult;

    }

    /**
     * 动态调节参数 获取各个通道的召回比例 ADD BY ZGX YX 20190528
     */
    private static Map<String, Double> getNumMap(MixRequestInfo mixRequestInfo, int cotagNum, int cotagSimNum, int exploreNum, int excellentNum, int ldaNum
            , int mediaNum, int subNum, int ucfNum, int totalNum, Map<String, Map<String, String>> RatioMap) {

        Map<String, Double> numRationConfigMap = new HashMap<>();

        int pullCount = mixRequestInfo.getPullCount();

        if (pullCount == 0) {
            pullCount = 1;
        }

        double daily_pullNum = 0.0;
        try {
            daily_pullNum = Double.valueOf(mixRequestInfo.getUserModel().getDaily_pullNum());
        } catch (Exception e) {
            daily_pullNum = 1.0;
        }


        Map<String, String> recallNumMap = getRatioNumMap(mixRequestInfo, RatioMap, pullCount, daily_pullNum);

        Double cotagRatio = (totalNum * Double.valueOf(recallNumMap.get("cotag"))) / cotagNum;
        Double cotagSimRatio = (totalNum * Double.valueOf(recallNumMap.get("cotagSim"))) / cotagSimNum;
        Double exploreRatio = (totalNum * Double.valueOf(recallNumMap.get("ex"))) / exploreNum;
        Double excellentRatio = (totalNum * Double.valueOf(recallNumMap.get("excellent"))) / excellentNum;
        Double ldaRatio = (totalNum * Double.valueOf(recallNumMap.get("lda"))) / ldaNum;
        Double mediaRatio = (totalNum * Double.valueOf(recallNumMap.get("media"))) / mediaNum;
        Double subRatio = (totalNum * Double.valueOf(recallNumMap.get("sub"))) / subNum;
        Double ucfRatio = (totalNum * Double.valueOf(recallNumMap.get("usercf"))) / ucfNum;

        numRationConfigMap.put("cotagRatio", cotagRatio);
        numRationConfigMap.put("cotagSimRatio", cotagSimRatio);
        numRationConfigMap.put("exploreRatio", exploreRatio);
        numRationConfigMap.put("excellentRatio", excellentRatio);
        numRationConfigMap.put("ldaRatio", ldaRatio);
        numRationConfigMap.put("mediaRatio", mediaRatio);
        numRationConfigMap.put("subRatio", subRatio);
        numRationConfigMap.put("ucfRatio", ucfRatio);
        numRationConfigMap.put("daily_pullNum", daily_pullNum);
        numRationConfigMap.put("pullCount", Double.valueOf(pullCount));
        numRationConfigMap.put("totalNum", Double.valueOf(totalNum));

        return numRationConfigMap;
    }

    /**
     * 新方法 用于设置动态的召回参数 By ZGX YX  20190526
     */
    private static Map<String, String> getRatioNumMap(MixRequestInfo mixRequestInfo, Map<String, Map<String, String>> ratioMap, int pullCount, double daily_pullNum) {
        Map<String, String> numRatioMap = new HashMap<String, String>();
        try {
            if (daily_pullNum <= 7) { //userType 1
                numRatioMap = ratioMap.get("userType1-pullGroup" + String.valueOf(getPullGroup(pullCount)));
//            logicParamsNew.setRatio(numRatioMap);
            } else if (daily_pullNum >= 8 && daily_pullNum <= 15) { //userType 2
                numRatioMap = ratioMap.get("userType2-pullGroup" + String.valueOf(getPullGroup(pullCount)));
            } else if (daily_pullNum >= 16 && daily_pullNum <= 30) { //userType 3
                numRatioMap = ratioMap.get("userType3-pullGroup" + String.valueOf(getPullGroup(pullCount)));
            } else if (daily_pullNum >= 31 && daily_pullNum <= 200) {  //userType 4
                numRatioMap = ratioMap.get("userType4-pullGroup" + String.valueOf(getPullGroup(pullCount)));
            } else {
                numRatioMap = ratioMap.get("userType1-pullGroup" + String.valueOf(getPullGroup(pullCount)));

            }
        } catch (Exception e) {
            logger.error("{} ratioMap get ERROR:{}", mixRequestInfo.getUid(), e);
            numRatioMap = ratioMap.get("userType1-pullGroup" + String.valueOf(getPullGroup(pullCount)));
        }

        return numRatioMap;

    }

    /**
     * 新方法 用于设置动态的召回参数 By ZGX YX  20190526
     */
    private static Integer getPullGroup(int pullCount) {
        int groupNum = 0;
        if (pullCount == 1) {
            groupNum = 1;
        } else if (pullCount == 2) {
            groupNum = 2;
        } else if (pullCount == 3 || pullCount == 4) {
            groupNum = 3;
        } else if (pullCount == 5 || pullCount == 6) {
            groupNum = 5;
        } else if (pullCount == 7 || pullCount == 8) {
            groupNum = 7;
        } else if (pullCount == 9 || pullCount == 10) {
            groupNum = 9;
        } else if (pullCount >= 11 && pullCount <= 15) {
            groupNum = 11;
        } else if (pullCount >= 16 && pullCount <= 20) {
            groupNum = 16;
        } else if (pullCount >= 21 && pullCount <= 25) {
            groupNum = 21;
        } else if (pullCount >= 26 && pullCount <= 30) {
            groupNum = 26;
        } else if (pullCount >= 31 && pullCount <= 35) {
            groupNum = 31;
        } else if (pullCount >= 36 && pullCount <= 40) {
            groupNum = 36;
        } else if (pullCount >= 41 && pullCount <= 45) {
            groupNum = 41;
        } else if (pullCount >= 46 && pullCount <= 50) {
            groupNum = 46;
        } else if (pullCount >= 51 && pullCount <= 60) {
            groupNum = 51;
        } else if (pullCount >= 61 && pullCount <= 70) {
            groupNum = 61;
        } else if (pullCount >= 71 && pullCount <= 80) {
            groupNum = 71;
        } else if (pullCount >= 81 && pullCount <= 90) {
            groupNum = 81;
        } else if (pullCount >= 91 && pullCount <= 100) {
            groupNum = 91;
        } else {
            groupNum = 101;
        }
        return groupNum;
    }


    /**
     * 动态调节参数 获取cotag各个通道的召回比例
     */
    private static Map<String, Double> getCotagInnerNumMap(MixRequestInfo mixRequestInfo, int cotagDocSize, int cotagNewDocSize,
                                                           int cotagVideoNewSize, int cotagVideoNSize, Map<String, Map<String, String>> RatioMap) {

//        logger.info("step 1 CotagInner uid:{} being in getCotagInnerNumMap....", mixRequestInfo.getUid());
        Map<String, Double> numRationConfigMap = new HashMap<>();

        int pullCount = mixRequestInfo.getPullCount();

        if (pullCount == 0) {
            pullCount = 1;
        }
//        String daily_pullNum = mixRequestInfo.getUserModel().getDaily_pullNum();
//        if(daily_pullNum.equals("") || daily_pullNum == null || daily_pullNum == "null"){
//
//        }
        double daily_pullNum = 0.0;
        try {
            daily_pullNum = Double.valueOf(mixRequestInfo.getUserModel().getDaily_pullNum());
        } catch (Exception e) {
            daily_pullNum = 1.0;
//            logger.error("{} daily_pullNum get ERROR:{}", mixRequestInfo.getUid(), e);
        }


        Map<String, String> recallNumMap = getRatioNumMap(mixRequestInfo, RatioMap, pullCount, daily_pullNum);
//        logger.info("step 2 CotagInner uid:{} recallNumMap:{}", mixRequestInfo.getUid(), recallNumMap);

        int totalNum = cotagDocSize + cotagNewDocSize + cotagVideoNewSize + cotagVideoNSize;


        Double co_d_r_Ratio = Double.valueOf(recallNumMap.getOrDefault("co_d_r", "0"));
        Double co_d_l_base_Ratio = Double.valueOf(recallNumMap.getOrDefault("co_d_l_base", "0"));
        Double co_d_r_n_Ratio = Double.valueOf(recallNumMap.getOrDefault("co_d_r_n", "0"));
        Double co_d_l_n_Ratio = Double.valueOf(recallNumMap.getOrDefault("co_d_l_n", "0"));
        Double co_v_r_Ratio = Double.valueOf(recallNumMap.getOrDefault("co_v_r", "0"));
        Double co_v_l_Ratio = Double.valueOf(recallNumMap.getOrDefault("co_v_l", "0"));
        Double co_v_r_n_Ratio = Double.valueOf(recallNumMap.getOrDefault("co_v_r_n", "0"));
        Double co_v_l_n_base_Ratio = Double.valueOf(recallNumMap.getOrDefault("co_v_l_n_base", "0"));

        numRationConfigMap.put("co_d_r_Ratio", co_d_r_Ratio);
        numRationConfigMap.put("co_d_l_base_Ratio", co_d_l_base_Ratio);
        numRationConfigMap.put("co_d_r_n_Ratio", co_d_r_n_Ratio);
        numRationConfigMap.put("co_d_l_n_Ratio", co_d_l_n_Ratio);
        numRationConfigMap.put("co_v_r_Ratio", co_v_r_Ratio);
        numRationConfigMap.put("co_v_l_Ratio", co_v_l_Ratio);
        numRationConfigMap.put("co_v_r_n_Ratio", co_v_r_n_Ratio);
        numRationConfigMap.put("co_v_l_n_base_Ratio", co_v_l_n_base_Ratio);

        numRationConfigMap.put("daily_pullNum", daily_pullNum);
        numRationConfigMap.put("pullCount", Double.valueOf(pullCount));
        numRationConfigMap.put("totalNum", Double.valueOf(totalNum));

        return numRationConfigMap;
    }

}