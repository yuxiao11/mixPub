package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.common.util.UserProfileUtils;
import com.ifeng.recom.mixrecall.core.channel.excutor.csc.DocpicCRecall;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class DocpicCChannelImpl {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DocpicCChannelImpl.class);
    private static final int TIMEOUT = 300;
    private RecallResult.WeightAndPreloadPositionComparator weightAndPreloadPositionComparator = new RecallResult.WeightAndPreloadPositionComparator();

    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo) {
        long startTime = System.currentTimeMillis();
        String uid = mixRequestInfo.getUid();

        UserModel userModel = mixRequestInfo.getUserModel();
        LogicParams logicParams = mixRequestInfo.getLogicParams();
        List<RecallResult> result = callDocpicC(mixRequestInfo, logicParams, userModel);

        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("DocpicCScChannelImpl {} cost:{}", uid, cost);
        }
        return result;
    }

    private List<RecallResult> callDocpicC(MixRequestInfo mixRequestInfo, LogicParams logicParams, UserModel userModel) {
        List<RecallResult> recallResults = new ArrayList<>();
        Map<String, Future<List<RecallResult>>> threadResultMap = new HashMap<>();

        try {
            Future<List<RecallResult>> docpicCResult = ExecutorThreadPool.submitExecutorTask(new DocpicCRecall(mixRequestInfo, UserProfileUtils.profileTagWeightFilter(userModel.getDocpic_cate(),RecallConstant.PROFILE_CUT_OFF_WEIGHT), TagPeriod.LONG, logicParams.docpicCNum));
            if (docpicCResult != null) {
                threadResultMap.put(TagPeriod.LONG.toString(), docpicCResult);
            }
            Map<String, List<RecallResult>> result = ExecutorThreadPool.getExecutorResult(threadResultMap, TIMEOUT);
            recallResults = result.values().stream().collect(ArrayList::new, List::addAll, List::addAll);
            recallResults.sort(weightAndPreloadPositionComparator);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{} callDocpicCSc ERROR:{}", mixRequestInfo.getUid(), e.toString());
        }

        return recallResults;
    }
}
