package com.ifeng.recom.mixrecall.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ifeng.recom.mixrecall.common.dao.redis.RedisUtils.getPreloadJedisClient;

/**
 * 缓存打分过滤词，读取redis中的关键词，写入缓存
 */
public class ScoreKeywordCache {
    private static final Logger logger = LoggerFactory.getLogger(ScoreKeywordCache.class);
    private static final int REDIS_DB_FILTER_WORD = 14;
    public static Cache<String, Integer> cache;

    static {
        initCacheIds();
        putScoreKeywordToCache();
    }

    private static void initCacheIds() {
        cache = CacheBuilder
                .newBuilder()
                .concurrencyLevel(10)
                .expireAfterWrite(12, TimeUnit.HOURS)
                .initialCapacity(30000)
                .maximumSize(100000)
                .build();
    }

    public static void putScoreKeywordToCache() {
        Jedis jedis = getPreloadJedisClient();
        if (jedis != null) {
            jedis.select(REDIS_DB_FILTER_WORD);
        } else {
            logger.error("get score keyword error");
            return;
        }

        List<String> keys = new ArrayList<>();

        ScanParams scanParameters = new ScanParams();
        scanParameters.match("keyword*");
        scanParameters.count(500);

        try {
            ScanResult<String> scanResult = jedis.scan(0, scanParameters);
            String nextCursor = scanResult.getStringCursor();
            List<String> scanResults = scanResult.getResult();

            while (true) {
                keys.addAll(scanResults);

                if (nextCursor.equals("0")) {
                    break;
                }

                scanResult = jedis.scan(nextCursor, scanParameters);
                nextCursor = scanResult.getStringCursor();
                scanResults = scanResult.getResult();
            }

            for (String key : keys) {
                if (StringUtils.isNotBlank(key)) {
                    int size = key.length() - 8;
                    if (size <= 1 || size > 6) {
                        continue;
                    }

                    try {
                        String score = jedis.hget(key, "sensetiveLevel");
                        cache.put(key.replace("keyword:", "").toLowerCase(), Integer.valueOf(score));
                    } catch (Exception e) {
                        logger.error("cache redis keyword", e);
                    }
                }
            }

            logger.info("put filter score keyword to cache, size:" + keys.size());

        } catch (Exception e) {
            logger.error("put filter score keyword to cache error", e);
        } finally {
            jedis.close();
        }
    }

    public static Integer getScore(String keyword) {
        return cache.getIfPresent(keyword);
    }

    public static void main(String[] args) {
        putScoreKeywordToCache();
        System.out.println(cache.size());
        System.out.println(cache.asMap());
    }
}
