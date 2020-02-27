package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.core.channel.excutor.cotag.CoTagSimExecutor;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool.getCotagExecutorResult;


@Service
public class CoTagDocSimlImpl {
    private static final Logger logger = LoggerFactory.getLogger(CoTagDocSimlImpl.class);

    private static final int TIMEOUT = 300;
    private RecallResult.WeightAndPreloadPositionComparator weightAndPreloadPositionComparator = new RecallResult.WeightAndPreloadPositionComparator();

    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo) {
        long startTime = System.currentTimeMillis();

        String uid = mixRequestInfo.getUid();
        UserModel userModel = mixRequestInfo.getUserModel();
        LogicParams logicParams = mixRequestInfo.getLogicParams();

        List<RecallResult> result = cotagDocSim(mixRequestInfo, logicParams, userModel);

        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("CoTagDocSimlImpl {} cost:{}", uid, cost);
        }

        return result;
    }

    private List<RecallResult> cotagDocSim(MixRequestInfo mixRequestInfo, LogicParams logicParams, UserModel userModel) {
        Map<TagPeriod, Future<List<RecallResult>>> threadResultMap = new HashMap<>();

        Future<List<RecallResult>> cotagLast = ExecutorThreadPool.submitExecutorTask(new CoTagSimExecutor(mixRequestInfo, userModel.getLastCotagSim(), DocType.DOCPIC,TagPeriod.LAST, logicParams.getCotagDocpicSimLastNum()));
        Future<List<RecallResult>> cotagRecent = ExecutorThreadPool.submitExecutorTask(new CoTagSimExecutor(mixRequestInfo, userModel.getRecentCotagDSim(),DocType.DOCPIC, TagPeriod.RECENT, logicParams.getCotagDocpicSimRecentNum()));

        threadResultMap.put(TagPeriod.LAST, cotagLast);
        threadResultMap.put(TagPeriod.RECENT, cotagRecent);

        Map<TagPeriod, List<RecallResult>> result = getCotagExecutorResult(threadResultMap,"cotagDocpicSim", TIMEOUT);

        List<RecallResult> recallResults = result.values().stream().collect(ArrayList::new, List::addAll, List::addAll);

        recallResults.sort(weightAndPreloadPositionComparator);

        return recallResults;
    }


}