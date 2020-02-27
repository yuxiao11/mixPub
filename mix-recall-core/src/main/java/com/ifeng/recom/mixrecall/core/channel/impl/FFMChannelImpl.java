package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.core.channel.excutor.FFMRecallExecutor;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

import static com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool.getFutureResult;

/**
 * Created by liligeng on 2019/3/15.
 */
@Service
public class FFMChannelImpl {

    private final static Logger logger = LoggerFactory.getLogger(FFMChannelImpl.class);

    private final static int timeout = 250;



    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo,boolean isNeedVideo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        long startTime = System.currentTimeMillis();

        String uid = mixRequestInfo.getUid();

        //判断是否是近7天内有访问的用户
        UserModel userModel = mixRequestInfo.getUserModel();
        String lastIn = userModel.getLastIn();
        if (StringUtils.isBlank(lastIn)) {
            return new ArrayList<>();
        }

        try {
            Date date = sdf.parse(lastIn);
            Calendar now = Calendar.getInstance();
            Calendar lastInDate = Calendar.getInstance();
            lastInDate.setTime(date);
            now.add(Calendar.DATE, -15);
            if (lastInDate.before(now)) {
                logger.info("uid:{} ffm lastIn:{} too long", uid, lastIn);
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("uid:{} parse date err:{}", uid, e);
        }


        int number = mixRequestInfo.getLogicParams().getFfmNum();
        if(mixRequestInfo.getDevMap().getOrDefault("ffmNumTest","base").equals("true")){
            number = 300;
        }

        List<RecallResult> recallResults = executorFFM(mixRequestInfo, number,isNeedVideo);
        long cost = System.currentTimeMillis() - startTime;
        logger.info("recall info, uid:{} ffm size:{} cost:{}", uid, recallResults.size(), cost);

        if (cost > 50) {
            ServiceLogUtil.debug("FFM {} cost:{}", uid, cost);
        }

        return recallResults;
    }


    /**
     * 召回出ffm文章
     *
     * @param mixRequestInfo
     * @param number
     * @return
     */
    private List<RecallResult> executorFFM(MixRequestInfo mixRequestInfo, int number,boolean isNeedVideo) {
        Future<List<RecallResult>> future = ExecutorThreadPool.submitExecutorTask(new FFMRecallExecutor(mixRequestInfo, number,GyConstant.FFM_URL));
        List<RecallResult> resultVideo=null;
        if(isNeedVideo){
            Future<List<RecallResult>> futureVideo = ExecutorThreadPool.submitExecutorTask(new FFMRecallExecutor(mixRequestInfo, number,GyConstant.FFM_URL_V));
            resultVideo = getFutureResult(WhyReason.FFM.getValue(), futureVideo, timeout);
        }
        List<RecallResult> result = getFutureResult(WhyReason.FFM.getValue(), future, timeout);
        if(CollectionUtils.isNotEmpty(resultVideo)){
            result.addAll(resultVideo);
        }
        return result;
    }
}
