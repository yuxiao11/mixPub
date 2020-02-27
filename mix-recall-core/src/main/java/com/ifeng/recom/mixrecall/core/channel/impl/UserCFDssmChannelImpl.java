package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.dao.hbase.UserCFClick;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserCF;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.core.cache.result.UserCFCache;
import com.ifeng.recom.mixrecall.core.channel.excutor.usercf.UserCFRecallExecutorDSSM;
import com.ifeng.recom.mixrecall.core.channel.excutor.usercf.UserCFRecallExecutorE;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.ifeng.recom.mixrecall.common.dao.hbase.UserCFClick.getNeighborClick;
import static com.ifeng.recom.mixrecall.common.util.WhyFiledUtils.setWhyForRecallResult;
import static com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool.getThreadRecallResultWithoutCancel;

/**
 * Created by geyl on 2017/10/30.
 * User CFDssm 如果不足在用 usercf——e  补足
 */
@Service
public class UserCFDssmChannelImpl {
    private static final Logger logger = LoggerFactory.getLogger(UserCFDssmChannelImpl.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);


    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo) {
        long startTime = System.currentTimeMillis();

        String uid = mixRequestInfo.getUid();
        Map<String, Boolean> userType = mixRequestInfo.getUserTypeMap();

        String executor;
        List<RecallResult> results;

        // 从缓存中获取结果
        List<RecallResult> cacheResult = UserCFCache.getDocs(uid + "dssm");
        if (cacheResult != null && !cacheResult.isEmpty()) {
            results = cacheResult;
            executor = "cache";
        } else {
            results = recallUserCfDssm(uid, mixRequestInfo);
            executor = "dssm";

            logger.info("{} dssm is :{}", uid, results.isEmpty());

        }

        if (results.size() > 0) {
            UserCFCache.putDocCache(uid + "dssm", results);
        }

        logger.info("recall info, uid:{} executor:{} rt_size:{}", uid, executor, results.size());
        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("UserCF {} cost:{}", uid, cost);
        }

        return results;
    }

    private List<RecallResult> recallUserCfDssm(String uid, MixRequestInfo mixRequestInfo) {
        List<RecallResult> results = new ArrayList<>();

        UserCF userCF = UserCFClick.getNeighborClick_dssm(uid);

        if (userCF != null && userCF.getNeighborClick() != null && !userCF.getNeighborClick().isEmpty()) {
            results = executorDssm(mixRequestInfo, userCF);
            setWhyForRecallResult(WhyReason.USER_CF_DSSM, results);
        }

        return results;
    }


    private List<RecallResult> executorDssm(MixRequestInfo mixRequestInfo, UserCF userCF) {
        List<Future<List<RecallResult>>> threadList = new ArrayList<>();
        Future<List<RecallResult>> result = ExecutorThreadPool.submitExecutorTask(new UserCFRecallExecutorDSSM(mixRequestInfo, userCF));
        threadList.add(result);

        return getThreadRecallResultWithoutCancel(threadList, 500);
    }



    private List<RecallResult> executorE(MixRequestInfo mixRequestInfo, UserCF userCF) {
        List<Future<List<RecallResult>>> threadList = new ArrayList<>();
        Future<List<RecallResult>> result = ExecutorThreadPool.submitExecutorTask(new UserCFRecallExecutorE(mixRequestInfo, userCF));
        threadList.add(result);

        return getThreadRecallResultWithoutCancel(threadList, 500);
    }


    /**
     * User CF E
     *
     * @param uid
     * @param mixRequestInfo
     * @return
     */
    public List<RecallResult> recallUserCfE(String uid, MixRequestInfo mixRequestInfo) {
        UserCF userCF = getNeighborClick(uid);

        List<RecallResult> results = new ArrayList<>();

        if (userCF != null && userCF.getNeighborClick() != null && !userCF.getNeighborClick().isEmpty()) {
            results = executorE(mixRequestInfo, userCF);
            setWhyForRecallResult(WhyReason.USER_CF_E1, results);
        }

        return results;
    }


}