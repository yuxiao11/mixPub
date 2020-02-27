package com.ifeng.recom.mixrecall.core.cache.result;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ifeng.recom.mixrecall.common.service.BloomFilter.bloomFilterForRecallResult;

/**
 * 缓存User CF 召回结果
 **/

public class UserCFCache {
    private static final Logger logger = LoggerFactory.getLogger(UserCFCache.class);
    private static Cache<String, List<RecallResult>> cache;

    static {
        initCache();
    }

    private static void initCache() {
        cache = CacheBuilder
                .newBuilder()
                .recordStats()
                .concurrencyLevel(10)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .initialCapacity(20000)
                .maximumSize(30000)
                .build();
    }

    public static void putDocCache(String uid, List<RecallResult> results) {
        cache.put(uid, results);
    }

    public static List<RecallResult> getDocs(String uid) {
        List<RecallResult> recallResults = cache.getIfPresent(uid);

        if (recallResults != null) {
            recallResults = bloomFilterForRecallResult(uid, recallResults);

            if (recallResults.size() < 300) {
                cache.invalidate(uid);
            } else {
                cache.put(uid, recallResults);
            }
        }

        return recallResults;
    }

    public static void checkStatus() {
        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
    }
}
