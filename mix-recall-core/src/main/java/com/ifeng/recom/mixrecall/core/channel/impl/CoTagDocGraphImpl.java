package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.core.channel.excutor.cotag.CoTagGraphExecutor;
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
public class CoTagDocGraphImpl {
    private static final Logger logger = LoggerFactory.getLogger(CoTagDocGraphImpl.class);

    private static final int TIMEOUT = 300;
    private RecallResult.WeightAndPreloadPositionComparator weightAndPreloadPositionComparator = new RecallResult.WeightAndPreloadPositionComparator();

    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo) {
        long startTime = System.currentTimeMillis();

        String uid = mixRequestInfo.getUid();
        UserModel userModel = mixRequestInfo.getUserModel();
        LogicParams logicParams = mixRequestInfo.getLogicParams();

        List<RecallResult> result = cotagDocGraph(mixRequestInfo, logicParams, userModel);

        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("CoTagDocGraphImpl {} cost:{}", uid, cost);
        }

        return result;
    }

    private List<RecallResult> cotagDocGraph(MixRequestInfo mixRequestInfo, LogicParams logicParams, UserModel userModel) {
        Map<TagPeriod, Future<List<RecallResult>>> threadResultMap = new HashMap<>();

        //此处由于画像还没添加短期图谱数据 所以注释
//        Future<List<RecallResult>> cotagLast = ExecutorThreadPool.submitExecutorTask(new CoTagSimExecutor(mixRequestInfo, userModel.getLastCotagSim(), DocType.DOCPIC,TagPeriod.LAST, logicParams.getCotagDocpicSimLastNum()));
        Future<List<RecallResult>> cotagRecent = ExecutorThreadPool.submitExecutorTask(new CoTagGraphExecutor(mixRequestInfo, userModel.getRecentCotagDGraph(),DocType.DOCPIC, TagPeriod.RECENT, logicParams.getCotagDocpicGraphRecentNum()));
        Future<List<RecallResult>> cotagLong = ExecutorThreadPool.submitExecutorTask(new CoTagGraphExecutor(mixRequestInfo, userModel.getLongCotagDGraph(),DocType.DOCPIC, TagPeriod.LONG, logicParams.getCotagDocpicGraphLongNum()));

        threadResultMap.put(TagPeriod.RECENT, cotagRecent);
        threadResultMap.put(TagPeriod.LONG, cotagLong);

        Map<TagPeriod, List<RecallResult>> result = getCotagExecutorResult(threadResultMap,"cotagDocpicGraph", TIMEOUT);

        List<RecallResult> recallResults = result.values().stream().collect(ArrayList::new, List::addAll, List::addAll);

        recallResults.sort(weightAndPreloadPositionComparator);

        return recallResults;
    }


}