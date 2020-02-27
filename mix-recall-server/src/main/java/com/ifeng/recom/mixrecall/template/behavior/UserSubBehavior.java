package com.ifeng.recom.mixrecall.template.behavior;

import static com.ifeng.recom.mixrecall.threadpool.ThreadUtils.getAsyncExecutorResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.common.collect.Lists;
import com.ifeng.recom.mixrecall.common.constant.RecallChannelBeanName;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.item.MixResult;
import com.ifeng.recom.mixrecall.common.service.handler.remove.IItemRemoveHandler;
import com.ifeng.recom.mixrecall.common.service.handler.remove.RemoverHandlerService;
import com.ifeng.recom.mixrecall.model.RecallChannelResult;
import com.ifeng.recom.mixrecall.model.RecallConfig;
import com.ifeng.recom.mixrecall.threadpool.RecallExecutor;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecallThreadResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.template.BaseTemplate;

import javax.annotation.Resource;

/**
 * 召回的增量和全量公用一个逻辑，只是调用量的size不同
 * 处理用户订阅频道数据
 * Created by jibin on 2018/1/19.
 */
@Service
public class UserSubBehavior extends BaseTemplate<MixResult> {
    private static final Logger logger = LoggerFactory.getLogger(UserSubBehavior.class);


    @Autowired
    private RemoverHandlerService removerHandlerService;

    @Autowired
    private RecallExecutor recallThreadPool;

    @Resource(name = "recallUserSubThreadPool")
    private ThreadPoolExecutor poolExecutor;

    @Override
    public MixResult doRecom(MixRequestInfo mixRequestInfo) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        String uid = mixRequestInfo.getUid();
        List<RecallConfig> recallConfigs = Lists.newArrayList();
        List<IItemRemoveHandler<Document>> removeHandlers = removerHandlerService.buildCommonHandlers(mixRequestInfo);
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_DOC).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.USER_SUB).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.PositiveFeedDocpic).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.PositiveFeedVideo).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.USER_CF_ALS).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_V_N).setRemoverList(removeHandlers));
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_V).setRemoverList(removeHandlers));
        timer.addStartTime("getAsyncExecutorResult");
        List<RecallChannelResult> channelResults = recallThreadPool.recall(mixRequestInfo, recallConfigs, 500, poolExecutor);
        logger.info("recall channel statistics result:{}, uid:{}", recallThreadPool.storeLoggerInfo(channelResults), uid);
        Map<RecallConstant.CHANNEL, List<RecallResult>> channelDocsMap = recallThreadPool.change(channelResults);
        timer.addEndTime("getAsyncExecutorResult");

        List<RecallResult> userCFList = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.USER_CF_ALS, Collections.emptyList());
        List<RecallResult> cotagList = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.COTAG_DOC, Collections.emptyList());
        List<RecallResult> posVideos = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.PositiveFeedVideo, Collections.emptyList());
        List<RecallResult> posDocpics = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.PositiveFeedDocpic, Collections.emptyList());
        List<RecallResult> cotagVideoN = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.COTAG_V_N, new ArrayList<>());
        List<RecallResult> cotagVideoNew = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.COTAG_V, new ArrayList<>());
        List<RecallResult> userSub = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.USER_SUB, Collections.emptyList());

        List<RecallResult> resultDocs = outputChannelNumControl(mixRequestInfo,  cotagList, userCFList, posDocpics, posVideos, userSub,cotagVideoNew,cotagVideoN);

        //debug 日志
        if (mixRequestInfo.isDebugUser()) {
            for (RecallResult recallResult : resultDocs) {
                logger.info("uid:{} docId:{} title:{} channel:{} debug:{}", uid, recallResult.getDocument().getDocId(), recallResult.getDocument().getTitle(), recallResult.getWhyReason().getValue(), recallResult.getRecallTag());
            }
        }

        return buildMixResult(mixRequestInfo, resultDocs, GyConstant.isOldResult);
    }


    private static List<RecallResult> outputChannelNumControl(MixRequestInfo mixRequestInfo, List<RecallResult> cotag, List<RecallResult> userCF, List<RecallResult> posVideo, List<RecallResult> posDocpic, List<RecallResult> userSub
            , List<RecallResult> cotagVideoNew, List<RecallResult> cotagVideoN) {

        LogicParams logicParams = mixRequestInfo.getLogicParams();
        int needNum = logicParams.getResult_size();

        List<RecallResult> results = new ArrayList<>();

        //修改为比例控制
        int posDocpicNum = (int) (0.1 * needNum);
        int posVideoNum = (int) (0.1 * needNum);

        results.addAll(userSub);
        results.addAll(userCF.subList(0, Math.min(100, userCF.size())));
        results.addAll(posDocpic.subList(0, Math.min(posDocpicNum, posDocpic.size())));
        results.addAll(posVideo.subList(0, Math.min(posVideoNum, posVideo.size())));
        results.addAll(cotag.subList(0, Math.min(needNum, cotag.size())));
        results.addAll(cotagVideoNew.subList(0, Math.min(needNum, cotagVideoNew.size())));
        results.addAll(cotagVideoN.subList(0, Math.min(needNum, cotagVideoN.size())));
        return results;
    }
}