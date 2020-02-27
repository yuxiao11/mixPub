package com.ifeng.recom.mixrecall.core.cache.mapping;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.RedisConstant;
import com.ifeng.recom.mixrecall.common.dao.redis.jedisPool.SimIdMappingJedisUtil;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.ifeng.recom.mixrecall.common.dao.elastic.Query.queryDocId;

/**
 * 缓存 simId,docId
 **/

public class SimIdDocIdMappingCache {
    private static final Logger logger = LoggerFactory.getLogger(SimIdDocIdMappingCache.class);
    public static LoadingCache<String, String> cache;

    static {
        initCache();
    }

    public static void initCache() {
        cache = CacheBuilder
                .newBuilder()
                .recordStats()
                .concurrencyLevel(10)
                .expireAfterWrite(10, TimeUnit.HOURS)
                .initialCapacity(2000000)
                .maximumSize(3000000)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String simId) throws Exception {

                        long start = System.currentTimeMillis();
                        String docId = SimIdMappingJedisUtil.get(RedisConstant.SimIdMapping_redis_db, simId);

                        long cost = System.currentTimeMillis() - start;
                        if (cost > 50) {
                            ServiceLogUtil.debug("SimIdMapping-R {} cost:{}", simId, cost);
                        }
                        if (StringUtils.isBlank(docId)) {
                            docId = queryDocId(simId);
                            long time1 = System.currentTimeMillis();
                            cost = time1 - start;
                            if (cost > 50) {
                                ServiceLogUtil.debug("SimIdMapping-E {} cost:{}", simId, cost);
                            }
                            if (StringUtils.isNoneBlank(docId)) {
                                SimIdMappingJedisUtil.setex(RedisConstant.SimIdMapping_redis_db, simId, GyConstant.ttl_SimIdMapping, docId);
                                cost = System.currentTimeMillis() - time1;
                                if (cost > 50) {
                                    ServiceLogUtil.debug("SimIdMapping-set {} cost:{}", simId, cost);
                                }
                            }
                        }
                        return docId;
                    }
                });
    }

    public static Map<String, String> getBatchDocIds(Set<String> ids) {
        Map<String, String> map = new HashMap<>();
        try {
            map = cache.getAll(ids);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static String getDocId(String id) {
        try {
            return cache.get(id);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void putSimIdDocId(String simId, String docId) {
        cache.put(simId, docId);
    }

    public static void checkStatus() {
        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
    }

    public static void main(String[] args) {
        CacheManager.init();
        String document = getDocId("clusterId_23524797");
        System.out.println(document);
    }

}