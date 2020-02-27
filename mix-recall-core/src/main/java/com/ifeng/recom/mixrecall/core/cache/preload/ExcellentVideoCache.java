package com.ifeng.recom.mixrecall.core.cache.preload;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import com.ifeng.recom.mixrecall.core.util.PreloadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class ExcellentVideoCache {
    private static final Logger logger = LoggerFactory.getLogger(ExcellentVideoCache.class);
    public static LoadingCache<String, List<String>> cache;


    private static int REDIS_DB =13;

    static {
        initCache();
    }

    public static void initCache() {


        cache = CacheBuilder
                .newBuilder()
                .concurrencyLevel(10)
                .expireAfterWrite(50, TimeUnit.MINUTES)
                .initialCapacity(1000000)
                .maximumSize(1000000)
                .build(new CacheLoader<String, List<String>>() {
                    @Override
                    public List<String> load(String tag) throws Exception {
                        long start = System.currentTimeMillis();
                        List<String> idList = PreloadUtils.getDocIdNew(tag, REDIS_DB);
                        long cost = System.currentTimeMillis() - start;
                        if (cost > 50) {
                            ServiceLogUtil.debug("ExcellentVideoCache {} cost:{}", tag, cost);
                        }
                        if (idList == null) {
                            return Collections.emptyList();
                        } else {
                            return idList;
                        }
                    }
                });
    }

    public static Map<String, List<String>> getFromCache(Set<String> tags) { //TODO video
        try {
            return cache.getAll(tags);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    private static List<String> getFromCache(String docId) {
        try {
            return cache.get(docId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void checkStatus() {
        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
    }
}
