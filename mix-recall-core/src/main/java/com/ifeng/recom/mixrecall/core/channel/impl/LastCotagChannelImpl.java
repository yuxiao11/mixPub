package com.ifeng.recom.mixrecall.core.channel.impl;

import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.core.channel.excutor.cotag.CoTagDRecall;
import com.ifeng.recom.mixrecall.core.channel.excutor.cotag.LastCoTagExecutor;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory.ListString;

/**
 * Created by liligeng on 2019/6/14.
 */
@Service
public class LastCotagChannelImpl {

    private final static Logger logger = LoggerFactory.getLogger(LastCotagChannelImpl.class);

    private static final int TIMEOUT = 100;
    private RecallResult.WeightAndPreloadPositionComparator weightAndPreloadPositionComparator = new RecallResult.WeightAndPreloadPositionComparator();

    private static Gson gson = new Gson();

    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo) {
        long startTime = System.currentTimeMillis();

        //获取用户uid, 用户画像userModel
        int size = mixRequestInfo.getSize();
        String uid = mixRequestInfo.getUid();
        UserModel userModel = mixRequestInfo.getUserModel();

        String cotagJson = mixRequestInfo.getLastCotag();
        List<String> recallTagList = GsonUtil.json2Object(cotagJson, ListString);


        Map<String, Future<List<RecallResult>>> threadResultMap = new HashMap<>();
        Future<List<RecallResult>> lastCotagVideo = ExecutorThreadPool.submitExecutorTask(new LastCoTagExecutor(mixRequestInfo, recallTagList, DocType.VIDEO, userModel, size));
        Future<List<RecallResult>> lastCotagDocpic = ExecutorThreadPool.submitExecutorTask(new LastCoTagExecutor(mixRequestInfo, recallTagList, DocType.DOCPIC, userModel, size));
        threadResultMap.put(DocType.VIDEO.getValue(), lastCotagVideo);
        threadResultMap.put(DocType.DOCPIC.getValue(), lastCotagDocpic);

        Map<String, List<RecallResult>> resultMap = ExecutorThreadPool.getExecutorResult(threadResultMap, TIMEOUT);

        logger.info("uid:{} lastCoTag Recall size:{}", mixRequestInfo.getUid(), threadResultMap.size());
        List<RecallResult> recallResults = resultMap.values().stream().collect(ArrayList::new, List::addAll, List::addAll);
        recallResults.sort(weightAndPreloadPositionComparator);

        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("cotagdocpic {} cost:{}", uid, cost);
        }
        return recallResults;
    }
}
