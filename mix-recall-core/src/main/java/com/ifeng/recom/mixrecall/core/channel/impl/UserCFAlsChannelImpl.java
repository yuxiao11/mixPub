package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.dao.hbase.UserCFClick;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserCF;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.core.cache.result.UserCFCache;
import com.ifeng.recom.mixrecall.core.channel.excutor.usercf.UserCFRecallExecutorALS;
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
 * User CFAls 如果补足在用 usercf——e  补足
 */
@Service
public class UserCFAlsChannelImpl {
    private static final Logger logger = LoggerFactory.getLogger(UserCFAlsChannelImpl.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);


    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo) {
        long startTime = System.currentTimeMillis();

        String uid = mixRequestInfo.getUid();

        String executor;
        List<RecallResult> results;

        // 从缓存中获取结果
        List<RecallResult> cacheResult = UserCFCache.getDocs(uid + "als");

        long step1= System.currentTimeMillis();
        int flag = 0;

        if (cacheResult != null && !cacheResult.isEmpty()) {
            results = cacheResult;
            executor = "cache";
        } else {

            try {

                results = recallUserCfAlsCache(uid, mixRequestInfo);
                if(mixRequestInfo.isDebugUser()){
                    logger.info("{} usercf redis debug test: {}", uid, results);
                }
            } catch (Exception e) {
                flag = 1;
                logger.error("{} recall usercf als from redis err:{}", uid, e);
                results = recallUserCfE(uid, mixRequestInfo);
            }
//            if (results == null || results.isEmpty()) {
//                flag = 2;
//                results = recallUserCfAls(uid, mixRequestInfo);
//            }
            executor = "als";

//            logger.info("{} als is :{}", uid, results.isEmpty());
//            if (results.isEmpty()) {
//                flag = 2;
//                executor = "e1";
//                results = recallUserCfE(uid, mixRequestInfo);
//            }
        }

        long step2= System.currentTimeMillis();


        if (results.size() > 0) {
            UserCFCache.putDocCache(uid + "als", results);
        }

        logger.info("recall info, uid:{} executor:{} rt_size:{} flag: {} step1Cost:{} step2Cost:{}", uid, executor, results.size(),flag,step1-startTime,step2-step1);
        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("UserCF {} cost:{}", uid, cost);
        }

        return results;
    }

    private List<RecallResult> recallUserCfAls(String uid, MixRequestInfo mixRequestInfo) {
        List<RecallResult> results = new ArrayList<>();

        UserCF userCF = UserCFClick.getNeighborClick_als(uid);

        if (userCF != null && userCF.getNeighborClick() != null && !userCF.getNeighborClick().isEmpty()) {

            results = executorAls(mixRequestInfo, userCF);
            setWhyForRecallResult(WhyReason.USER_CF_ALS, results);
        }

        return results;
    }

    private List<RecallResult> recallUserCfAlsCache(String uid, MixRequestInfo mixRequestInfo) {
        List<RecallResult> results = new ArrayList<>();
        UserCF userCF = UserCFClick.getNeighborClickAlsFromRedis(uid);
        if (userCF != null && userCF.getNeighborClick() != null && !userCF.getNeighborClick().isEmpty()) {
            if(mixRequestInfo.isDebugUser()) {
                logger.info("{} usercf redis debug test: {}", uid, userCF);
            }
            results = executorAls(mixRequestInfo, userCF);
            setWhyForRecallResult(WhyReason.USER_CF_ALS_CACHE, results);
        }
        return results;
    }


    private List<RecallResult> executorAls(MixRequestInfo mixRequestInfo, UserCF userCF) {
        List<Future<List<RecallResult>>> threadList = new ArrayList<>();
        Future<List<RecallResult>> result = ExecutorThreadPool.submitExecutorTask(new UserCFRecallExecutorALS(mixRequestInfo, userCF));
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