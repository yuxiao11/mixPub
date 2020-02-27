package com.ifeng.recom.mixrecall.template.behavior;

import com.google.common.collect.Lists;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.RecallChannelBeanName;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecallThreadResult;
import com.ifeng.recom.mixrecall.common.model.item.MixResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.handler.remove.IItemRemoveHandler;
import com.ifeng.recom.mixrecall.common.service.handler.remove.RemoverHandlerService;
import com.ifeng.recom.mixrecall.model.RecallChannelResult;
import com.ifeng.recom.mixrecall.model.RecallConfig;
import com.ifeng.recom.mixrecall.template.BaseTemplate;
import com.ifeng.recom.mixrecall.threadpool.RecallExecutor;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static com.ifeng.recom.mixrecall.threadpool.ThreadUtils.getAsyncExecutorResult;

/**
 * 处理用户lastTopic信息
 */
@Service
public class UserLastBehavior extends BaseTemplate<MixResult> {
    private static final Logger logger = LoggerFactory.getLogger(UserLastBehavior.class);

    @Autowired
    private RemoverHandlerService removerHandlerService;

    @Autowired
    private RecallExecutor recallThreadPool;

    @Resource(name = "recallThreadPool")
    private ThreadPoolExecutor poolExecutor;

    @Override
    public MixResult doRecom(MixRequestInfo mixRequestInfo) {
        if (mixRequestInfo.isDebugUser()) {
            logger.info("uid:{} debugTest increaseBehavior start");
        }
        TimerEntity timer = TimerEntityUtil.getInstance();
        String uid = mixRequestInfo.getUid();

        List<RecallConfig> recallConfigs = Lists.newArrayList();
        List<IItemRemoveHandler<Document>> removeHandlers = Lists.newArrayList();
        recallConfigs.add(RecallConfig.build().setBeanName(RecallChannelBeanName.COTAG_L_SIM).setRemoverList(removeHandlers));
        List<RecallResult> lastResultList =new ArrayList<>();

        try {
            timer.addStartTime("getAsyncExecutorResult");
            List<RecallChannelResult> channelResults = recallThreadPool.recall(mixRequestInfo, recallConfigs, 200, poolExecutor);
            logger.info("recall channel statistics result:{}, uid:{}", recallThreadPool.storeLoggerInfo(channelResults), uid);
            Map<RecallConstant.CHANNEL, List<RecallResult>> channelDocsMap = recallThreadPool.change(channelResults);
            timer.addEndTime("getAsyncExecutorResult");

            lastResultList = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.COTAG_L_SIM, new ArrayList<>());

            if (mixRequestInfo.isDebugUser()) {
                for (RecallResult recallResult : lastResultList) {
                    logger.info("lastResultList uid:{} docId:{} simId:{} docType:{} available:{} disType:{} times:{} hotboost:{} title:{} channel:{} debug:{}", uid, recallResult.getDocument().getDocId(), recallResult.getDocument().getSimId(), recallResult.getDocument().getDocType(), recallResult.getDocument().isAvailable(), recallResult.getDocument().getDistype(), recallResult.getDocument().getTimeSensitive().replace("\r\n", ""), recallResult.getDocument().getHotBoost(), recallResult.getDocument().getTitle(), recallResult.getWhyReason().getValue(), recallResult.getRecallTag());
                }
            }
        } catch (Exception e) {
            logger.error("{} UserLastBehavior ERROR:{}", mixRequestInfo.getUid(), e);
        }
        return buildMixResultForLast(mixRequestInfo, lastResultList, GyConstant.isNewResult);

    }


}
