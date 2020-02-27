package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.common.util.UserProfileUtils;
import com.ifeng.recom.mixrecall.core.channel.excutor.LdaDocpicExecutor;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool.getCotagExecutorResult;

/**
 * Created by liligeng on 2018/8/21.
 */
@Service
public class LdaDocpicChannelImpl {
    private static final Logger logger = LoggerFactory.getLogger(LdaDocpicChannelImpl.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    private static final int TIMEOUT = 300;

    private RecallResult.WeightAndPreloadPositionComparator weightAndPreloadPositionComparator = new RecallResult.WeightAndPreloadPositionComparator();

    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo) {
//        logger.info("check step1 uid:{}",mixRequestInfo.getUid());
        long startTime = System.currentTimeMillis();

        String uid = mixRequestInfo.getUid();
        UserModel userModel = mixRequestInfo.getUserModel();
        LogicParams logicParams = mixRequestInfo.getLogicParams();

        List<RecallResult> result = ldaDocpicTopic(mixRequestInfo, logicParams, userModel);
        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("ldaDocPic {} cost:{}", uid, cost);
        }

        return result;
    }

    private List<RecallResult> ldaDocpicTopic(MixRequestInfo mixRequestInfo, LogicParams logicParams, UserModel userModel) {
        Map<UserProfileEnum.TagPeriod, Future<List<RecallResult>>> threadResultMap = new HashMap<>();

        Future<List<RecallResult>> ldaLongResult = ExecutorThreadPool.submitExecutorTask(new LdaDocpicExecutor(mixRequestInfo, UserProfileUtils.profileTagWeightFilter(userModel.getDocpicLdaTopic(),RecallConstant.PROFILE_CUT_OFF_WEIGHT), UserProfileEnum.TagPeriod.LONG, logicParams.getLdaTopicLongNum()));
        Future<List<RecallResult>> ldaRecentResult = ExecutorThreadPool.submitExecutorTask(new LdaDocpicExecutor(mixRequestInfo, UserProfileUtils.profileTagWeightFilter(userModel.getRecentDocpicLdaTopic(),RecallConstant.PROFILE_CUT_OFF_WEIGHT), UserProfileEnum.TagPeriod.RECENT, logicParams.getLdaTopicRecentNum()));

        threadResultMap.put(UserProfileEnum.TagPeriod.LONG, ldaLongResult);
        threadResultMap.put(UserProfileEnum.TagPeriod.RECENT, ldaRecentResult);

        Map<UserProfileEnum.TagPeriod, List<RecallResult>> result = getCotagExecutorResult(threadResultMap,"ldaDocpic", TIMEOUT);
        List<RecallResult> recallResults = result.values().stream().collect(ArrayList::new, List::addAll, List::addAll);

        recallResults.sort(weightAndPreloadPositionComparator);

        return recallResults;
    }
}
