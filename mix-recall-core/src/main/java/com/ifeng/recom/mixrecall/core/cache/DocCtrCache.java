package com.ifeng.recom.mixrecall.core.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ifeng.recom.mixrecall.common.dao.redis.NewStatisticsRedis;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 缓存的为特定天数内最大的pv ev ctr
 */
public class DocCtrCache {
    private static final Logger logger = LoggerFactory.getLogger(DocCtrCache.class);
    public static LoadingCache<String, Map<String, Double>> cache;

    static {
        initCache();
    }

    public static void initCache() {
        cache = CacheBuilder
                .newBuilder()
                .recordStats()
                .concurrencyLevel(10)
                .expireAfterWrite(3, TimeUnit.HOURS)
                .initialCapacity(600000)
                .maximumSize(600000)
                .build(new CacheLoader<String, Map<String, Double>>() {
                    @Override
                    public Map<String, Double> load(String simId) throws Exception {
                        long start = System.currentTimeMillis();
                        Map<String, Double> ctr = getNewDocStr(simId);
                        long cost = System.currentTimeMillis() - start;
                        if (cost > 50) {
                            ServiceLogUtil.debug("DocCtr {} cost:{}", simId, cost);
                        }
                        return ctr;
                    }
                });
    }


    private static Map<String, Double> getNewDocStr(String id) {
        Map<String,Double> evPvMap = NewStatisticsRedis.getCtrEvPv(id);
        return evPvMap;
    }


    public static Map<String, Map<String, Double>> getBatchDocCtrFromCache(Set<String> ids) {
        try {
            Map<String, Map<String, Double>> map = cache.getAll(ids);
            return map;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public static void checkStatus() {
        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
    }
}