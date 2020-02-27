package com.ifeng.recom.mixrecall.threadpool;

import com.ifeng.recom.mixrecall.common.constant.LogFileName;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecallThreadResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by geyl on 2018/4/19.
 */
public class ThreadUtils {
    private static final Logger logger = LoggerFactory.getLogger(ThreadUtils.class);
    private static final Logger timeoutLogger = LoggerUtils.Logger(LogFileName.TIMEOUT);
//    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    /**
     * 从future 线程中获取召回结果:RecallResult
     *
     * @param mixRequestInfo
     * @param futureResultList
     * @param timeout
     * @return
     */
    public static Map<RecallConstant.CHANNEL, List<RecallResult>> getAsyncExecutorResult(MixRequestInfo mixRequestInfo, List<Future<RecallThreadResult>> futureResultList, int timeout) {
        Map<RecallConstant.CHANNEL, List<RecallResult>> channelDocsMap = new HashMap<>();
        final long start = System.nanoTime();
        final long total = TimeUnit.MILLISECONDS.toNanos(timeout);
//        TimerEntity entity = new TimerEntity();
//        entity.addStartTime("total");
        for (Future<RecallThreadResult> resultFuture : futureResultList) {
            if (resultFuture == null) {
                continue;
            }
            try {
                final long remainderTimeout = total - (System.nanoTime() - start);
                RecallThreadResult result = resultFuture.get(remainderTimeout <= 0 ? 0 : remainderTimeout, TimeUnit.NANOSECONDS);
                if (result != null && result.getRecallResultNew() != null) {
                    channelDocsMap.put(result.getName(), result.getRecallResultNew());
//                    entity.addTime(result.getName().toString(), result.getStartTime(), result.getEndTime());
                }
            } catch (TimeoutException e) {
                resultFuture.cancel(true);
                timeoutLogger.info("Get async executor result timeout:" + e.toString());
            } catch (Exception e) {
                resultFuture.cancel(true);
                logger.error("Get async executor result error:" + e.toString(), e);
            }
        }
//        entity.addEndTime("total");
//        timeLogger.info("channelSingleAll {} uid:{},flowType:channelSingleAll", entity.getStaticsInfo(), mixRequestInfo.getUid());

        return channelDocsMap;
    }
}
