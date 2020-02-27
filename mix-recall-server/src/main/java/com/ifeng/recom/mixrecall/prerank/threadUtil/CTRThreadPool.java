package com.ifeng.recom.mixrecall.prerank.threadUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jibin on 2017/5/23.
 */
public class CTRThreadPool {
    private static final Logger logger = LoggerFactory.getLogger(CTRThreadPool.class);

    /**
     * cpu计算时候的系统运行时的核数
     */
    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();


    public static ExecutorService THREAD_POOL_Calc = null;

    public static ExecutorService THREAD_POOL_UserProfile = null;

    public static ExecutorService THREAD_POOL_Item = null;

    public static ExecutorService THREAD_POOL_ItemStatisticsInfo = null;

    public static ExecutorService THREAD_POOL_OnlineRerank = null;

    public static CompletionService CompletionService_ThreadPool = null;
    public static int CompletionService_ThreadPool_Num = 30;


    static {
        THREAD_POOL_Calc = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new CTRServiceThreadFactory("Calc-threadUtil-"));
        logger.info("Calc  pool init succeed! thread nums: " + Runtime.getRuntime().availableProcessors());


        THREAD_POOL_Item = Executors.newFixedThreadPool(20, new CTRServiceThreadFactory("Item-threadUtil-"));
        logger.info("Item thread pool init succeed! thread nums: " + 20);

        THREAD_POOL_UserProfile = Executors.newFixedThreadPool(20, new CTRServiceThreadFactory("user-profile-from-HBase-"));
        logger.info("Item thread pool init succeed! thread nums: " + 20);

        THREAD_POOL_ItemStatisticsInfo = Executors.newFixedThreadPool(20, new CTRServiceThreadFactory("Item-statistics-info-"));
        logger.info("ItemStatisticsInfo thread pool init succeed! thread nums: " + 20);

        THREAD_POOL_OnlineRerank = Executors.newFixedThreadPool(CompletionService_ThreadPool_Num,new CTRServiceThreadFactory("Online-rerank-"));
        logger.info("OnlineRerank thread pool init succeed! thread nums: " + CompletionService_ThreadPool_Num);

        CompletionService_ThreadPool = new ExecutorCompletionService(Executors.newFixedThreadPool(CompletionService_ThreadPool_Num,new CTRServiceThreadFactory("Online-rerank-")));
        logger.info("CompletionService_ThreadPool init succeed! thread nums: " + CompletionService_ThreadPool_Num);

    }

}
