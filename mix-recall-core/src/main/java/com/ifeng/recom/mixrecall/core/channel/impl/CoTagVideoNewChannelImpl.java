package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.common.util.UserUtils;
import com.ifeng.recom.mixrecall.core.channel.excutor.cotag.CoTagVideoRecallNew;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.CollectionUtils;
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
 * Created by geyl on 2017/10/30.
 * 根据画像中的Combine Tag 字段进行召回
 */
@Service
public class CoTagVideoNewChannelImpl {
    private static final Logger logger = LoggerFactory.getLogger(CoTagVideoNewChannelImpl.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);


    private static final int TIMEOUT = 300;
    private RecallResult.WeightAndPreloadPositionComparator weightAndPreloadPositionComparator = new RecallResult.WeightAndPreloadPositionComparator();

    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo) {
        long startTime = System.currentTimeMillis();

        String uid = mixRequestInfo.getUid();
        UserModel userModel = mixRequestInfo.getUserModel();
        LogicParams logicParams = mixRequestInfo.getLogicParams();


        List<RecallResult> result = cotagVideo(mixRequestInfo, logicParams, userModel);

        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("CoTagVideoNew {} cost:{}", uid, cost);
        }

        return result;
    }

    private List<RecallResult> cotagVideo(MixRequestInfo mixRequestInfo, LogicParams logicParams, UserModel userModel) {
        Map<TagPeriod, Future<List<RecallResult>>> threadResultMap = new HashMap<>();

        //debug用户多召回一些
        if (mixRequestInfo.isDebugUser()) {
            logicParams.setCotagVideoLongNum(450);
        }


        Future<List<RecallResult>> cotagLong = ExecutorThreadPool.submitExecutorTask(new CoTagVideoRecallNew(mixRequestInfo, userModel.getCombineTagList(), TagPeriod.LONG, logicParams.getCotagVideoLongNum()));

        List<RecordInfo> cotagLongList = userModel.getCombineTagList();
        if (CollectionUtils.isEmpty(cotagLongList) || cotagLongList.size() < GyConstant.cotagLongNotEnough) {
            Future<List<RecallResult>> cotagRecent = ExecutorThreadPool.submitExecutorTask(new CoTagVideoRecallNew(mixRequestInfo, userModel.getRecentCombineTagList(), TagPeriod.RECENT, logicParams.getCotagVideoRecentNum()));
            threadResultMap.put(TagPeriod.RECENT, cotagRecent);
        }

        //如是lastSim0测试组 则不用lastcotag召回
        if(!UserUtils.isLastSim0Model(mixRequestInfo)&&!UserUtils.isLastSimVideoModel(mixRequestInfo)) {
            Future<List<RecallResult>> cotagLast = ExecutorThreadPool.submitExecutorTask(new CoTagVideoRecallNew(mixRequestInfo, userModel.getLastCombineTagList(), TagPeriod.LAST, logicParams.getCotagVideoLastNum()));
            threadResultMap.put(TagPeriod.LAST, cotagLast);
        }
        threadResultMap.put(TagPeriod.LONG, cotagLong);


        Map<TagPeriod, List<RecallResult>> result = getCotagExecutorResult(threadResultMap, "cotagVideoNew", TIMEOUT);

        List<RecallResult> recallResults = result.values().stream().collect(ArrayList::new, List::addAll, List::addAll);

        recallResults.sort(weightAndPreloadPositionComparator);

        return recallResults;
    }


}