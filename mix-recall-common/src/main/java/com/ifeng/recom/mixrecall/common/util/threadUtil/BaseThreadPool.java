package com.ifeng.recom.mixrecall.common.util.threadUtil;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jibin on 2017/5/23.
 */
public class BaseThreadPool {
    private static final Logger logger = LoggerFactory.getLogger(BaseThreadPool.class);

    /**
     * 用户个性化推荐的hbase的查询线程池
     */
    public static ExecutorService THREAD_POOL_UpdateIndex = null;


    static {
        THREAD_POOL_UpdateIndex = Executors.newFixedThreadPool(50, new ThreadFactoryBuilder().setNameFormat("updateIndex-%d").build());
        logger.info("updateIndex pool init succeed! thread nums: " + 10);


    }

}
