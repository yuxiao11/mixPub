package com.ifeng.recom.mixrecall.core.channel.impl;

import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.common.util.UserUtils;
import com.ifeng.recom.mixrecall.core.channel.excutor.cotag.CoTagDRecall;
import com.ifeng.recom.mixrecall.core.channel.excutor.cotag.CoTagLastRecall;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import com.ifeng.recom.mixrecall.core.util.MathUtil;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CoTagDocpicChannelImpl {
    private static final Logger logger = LoggerFactory.getLogger(CoTagDocpicChannelImpl.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    private static final int TIMEOUT = 200;
    private RecallResult.WeightAndPreloadPositionComparator weightAndPreloadPositionComparator = new RecallResult.WeightAndPreloadPositionComparator();

    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo) {
        long startTime = System.currentTimeMillis();

        String uid = mixRequestInfo.getUid();
        UserModel userModel = mixRequestInfo.getUserModel();
        LogicParams logicParams = mixRequestInfo.getLogicParams();

        List<RecallResult> result;

        //wxb用户多召回
        Map<String, Boolean> userTypeMap = mixRequestInfo.getUserTypeMap();
        if (userTypeMap.getOrDefault("isWxb", false)) {
            logicParams.setCotagLongNum(800);
            logicParams.setCotagRecentNum(500);
            logicParams.setCotagLastNum(400);
        }
        result = cotagDocpic(mixRequestInfo, logicParams, userModel);
        logger.info("uid:{} cotag recall size:{}", uid, result.size());

        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("cotagdocpic {} cost:{}", uid, cost);
        }
        return result;
    }


    private List<RecallResult> cotagDocpic(MixRequestInfo mixRequestInfo, LogicParams logicParams, UserModel userModel) {
        Map<TagPeriod, Future<List<RecallResult>>> threadResultMap = new HashMap<>();


        Future<List<RecallResult>> cotagLong  = ExecutorThreadPool.submitExecutorTask(new CoTagDRecall(mixRequestInfo, userModel.getCombineTagList(), TagPeriod.LONG, logicParams.getCotagLongNum()));
        Future<List<RecallResult>> cotagRecent = ExecutorThreadPool.submitExecutorTask(new CoTagDRecall(mixRequestInfo, userModel.getRecentCombineTagList(), TagPeriod.RECENT, logicParams.getCotagRecentNum()));

        //如是lastSim0测试组 则不用lastcotag召回
        if(!UserUtils.isLastSim0Model(mixRequestInfo)){
            Future<List<RecallResult>> cotagLast = ExecutorThreadPool.submitExecutorTask(new CoTagDRecall(mixRequestInfo, userModel.getLastCombineTagList(), TagPeriod.LAST, logicParams.getCotagLastNum()));
            threadResultMap.put(TagPeriod.LAST, cotagLast);
        }
        threadResultMap.put(TagPeriod.LONG, cotagLong);
        threadResultMap.put(TagPeriod.RECENT, cotagRecent);

        Map<TagPeriod, List<RecallResult>> result = getCotagExecutorResult(threadResultMap, "cotagDocpic",TIMEOUT);
        List<RecallResult> recallResults = result.values().stream().collect(ArrayList::new, List::addAll, List::addAll);
        recallResults.sort(weightAndPreloadPositionComparator);

        return recallResults;
    }

    public List<RecallResult> doRecallLast(MixRequestInfo mixRequestInfo) {
        long startTime = System.currentTimeMillis();

        String uid = mixRequestInfo.getUid();
        UserModel userModel = mixRequestInfo.getUserModel();
        LogicParams logicParams = mixRequestInfo.getLogicParams();

        List<RecallResult> result;
        result = cotagLast(mixRequestInfo, logicParams, userModel);
        logger.info("uid:{} lastTopic recall size:{}", uid, result.size());

        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("cotagLast {} cost:{}", uid, cost);
        }
        return result;
    }

    private List<RecallResult> cotagLast(MixRequestInfo mixRequestInfo, LogicParams logicParams, UserModel userModel) {
        Map<TagPeriod, Future<List<RecallResult>>> threadResultMap = new HashMap<>();
        List<RecallResult> recallResults=new ArrayList<>();
        try{
            List<RecordInfo> lastCombineListOld=dealLastSim(userModel,userModel.getLastLdaTopicList());

            Future<List<RecallResult>> topicLast = ExecutorThreadPool.submitExecutorTask(new CoTagLastRecall(mixRequestInfo, lastCombineListOld, DocType.DOCPIC,TagPeriod.LAST, 200));
            threadResultMap.put(TagPeriod.LAST, topicLast);

            Map<TagPeriod, List<RecallResult>> result = getCotagExecutorResult(threadResultMap, "cotagLast", TIMEOUT);
            recallResults = result.values().stream().collect(ArrayList::new, List::addAll, List::addAll);
            recallResults.sort(weightAndPreloadPositionComparator);

            if(mixRequestInfo.isDebugUser()){
                logger.info("{} lastCombineListOld:{} recallResults:{}",mixRequestInfo.getUid(),new Gson().toJson(lastCombineListOld),recallResults.size());
            }
        }catch (Exception e){
            logger.error("{} CoTagDocpicChannelImpl lastTopic error:{}",mixRequestInfo.getUid(),e);
        }

        return recallResults;
    }

    private List<RecordInfo> dealLastSim(UserModel userModel, List<RecordInfo> recordInfoList){
        List<RecordInfo> recordInfos=new ArrayList<>();

        if(userModel==null||CollectionUtils.isEmpty(recordInfoList)){
            return recordInfos;
        }
        for(RecordInfo recordInfo:recordInfoList){
            try{
                if(recordInfo.getWeight()<0.5){
                    continue;
                }
                double sim=recordInfo.getSim();
                int expo=recordInfo.getExpose();
                int click=recordInfo.getReadFrequency();
                if(sim<=0.4){
                    if(expo>=4&&click/expo<=0.25){
                        continue;
                    }
                    recordInfos.add(recordInfo);
                }
            }catch (Exception e){
                logger.error("{},{} dealLastSim error:{}",userModel.getUserId(),recordInfo.getRecordName(),e);
            }
        }
        return recordInfos;
    }
}