package com.ifeng.recom.mixrecall.core.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.LogFileName;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum;
import com.ifeng.recom.mixrecall.common.dao.hbase.UserCFClick;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserCF;
import com.ifeng.recom.mixrecall.common.tool.LoggerUtils;
import com.ifeng.recom.mixrecall.common.util.MonitorTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 执行executor的线程池
 * Created by geyl on 2017/11/2.
 */
public class ExecutorThreadPool {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorThreadPool.class);
    private static final Logger timeoutLogger = LoggerUtils.Logger(LogFileName.TIMEOUT);

    public static ExecutorService threadPool = new ThreadPoolExecutor(60, 60,
            60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200),
            new ThreadFactoryBuilder().setNameFormat("mix-inner-timer-%d").build());



    public static Future<List<Document>> submitTaskForRecallDocument(Callable<List<Document>> task) {
        return threadPool.submit(task);
    }

    public static Future<List<RecallResult>> submitExecutorTask(Callable<List<RecallResult>> task) {
        return threadPool.submit(task);
    }


    public static List<RecallResult> getThreadRecallResultWithoutCancel(List<Future<List<RecallResult>>> threadList, int timeout) {
        List<RecallResult> combineDocList = new ArrayList<>();

        for (Future<List<RecallResult>> future : threadList) {
            try {
                combineDocList.addAll(future.get(timeout, TimeUnit.MILLISECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.error("get executor result from thread pool," + e.toString());
                future.cancel(true);
            }
        }

        return combineDocList;
    }

    public static List<RecallResult> getFutureResult(String channel, Future<List<RecallResult>> future, int timeout) {
        List<RecallResult> results = null;
        try {
            results = future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error(channel, e);
            future.cancel(true);
        }

        if (results != null && !results.isEmpty()) {
            return results;
        }

        return new ArrayList<>();
    }

    public static void checkPoolStatus() {
        logger.debug("executor pool status:" + threadPool.toString());
    }

    public static void close() {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取cotag executor 计算结果
     *
     * @param threadMap
     * @param timeout
     * @return
     */
    public static Map<UserProfileEnum.TagPeriod, List<RecallResult>> getCotagExecutorResult(Map<UserProfileEnum.TagPeriod, Future<List<RecallResult>>> threadMap, String channel, int timeout) {
        Map<UserProfileEnum.TagPeriod, List<RecallResult>> threadResult = new HashMap<>();

        for (Map.Entry<UserProfileEnum.TagPeriod, Future<List<RecallResult>>> entry : threadMap.entrySet()) {
            Future<List<RecallResult>> future = entry.getValue();
            try {
                List<RecallResult> results = future.get(timeout, TimeUnit.MILLISECONDS);
                if (results != null && !results.isEmpty()) {
                    threadResult.put(entry.getKey(), results);
                }
            } catch (Exception e) {
                future.cancel(true);
                timeoutLogger.info("Get cotag executor result timeout:{}, channel:{}, err:{}", timeout, channel, e.toString());
            }
        }
        return threadResult;
    }

    /**
     * 获取cotag executor 计算结果
     *
     * @param threadMap
     * @param timeout
     * @return
     */
    public static Map<String, List<RecallResult>> getExecutorResult(Map<String, Future<List<RecallResult>>> threadMap, int timeout) {
        Map<String, List<RecallResult>> threadResult = new HashMap<>();

        for (Map.Entry<String, Future<List<RecallResult>>> entry : threadMap.entrySet()) {
            Future<List<RecallResult>> future = entry.getValue();
            try {
                List<RecallResult> results = future.get(timeout, TimeUnit.MILLISECONDS);
                if (results != null && !results.isEmpty()) {
                    threadResult.put(entry.getKey(), results);
                }
            } catch (Exception e) {
                future.cancel(true);
                timeoutLogger.info("Get executor result timeout:{}", e.toString());
            }
        }
        return threadResult;
    }

}
