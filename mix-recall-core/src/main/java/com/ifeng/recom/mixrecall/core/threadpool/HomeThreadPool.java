package com.ifeng.recom.mixrecall.core.threadpool;

import com.ifeng.recom.mixrecall.common.constant.LogFileName;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.tool.LoggerUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by liligeng on 2019/8/14.
 */
public class HomeThreadPool {

    private static final Logger logger = LoggerFactory.getLogger(HomeThreadPool.class);
    private static final Logger timeoutLogger = LoggerUtils.Logger(LogFileName.TIMEOUT);

    public static ExecutorService threadPool;

    static{
        threadPool = new ThreadPoolExecutor(20,20,1000l, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(200), new ThreadPoolExecutor.DiscardOldestPolicy());
    }


    public static Future<List<RecallResult>> submitExecutorTask(Callable<List<RecallResult>> task) {
        return threadPool.submit(task);
    }

    public static Map<String, List<RecallResult>> getExecutorResult(Map<String, Future<List<RecallResult>>> futureMap, int timeout){
        Map<String, List<RecallResult>> resultMap = new HashMap<>();
        for(Map.Entry<String, Future<List<RecallResult>>> entry : futureMap.entrySet()){
            String key = entry.getKey();
            Future<List<RecallResult>> future = entry.getValue();
            try{
                List<RecallResult> result = future.get(timeout, TimeUnit.MILLISECONDS);
                if(CollectionUtils.isNotEmpty(result)){
                    resultMap.put(key, result);
                }
            }catch (Exception e){
                future.cancel(true);
                timeoutLogger.info("Get Home executor result timeout:{}", e.toString());
            }
        }
        return resultMap;
    }

}
