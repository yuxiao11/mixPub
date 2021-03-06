package com.ifeng.recom.mixrecall.common.service.filter;

import com.ifeng.recom.mixrecall.common.cache.AbstractMapCache;
import com.ifeng.recom.mixrecall.common.model.Document;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO 修改为cache 2020-02-01
 * Created by liligeng on 2019/2/14.
 */
@Service
public class MediaFilter extends AbstractMapCache<Integer> {

    private static Logger logger = LoggerFactory.getLogger(MediaFilter.class);

//    private static Cache<String, Integer> banMediaCache = CacheBuilder
//            .newBuilder()
//            .recordStats()
//            .concurrencyLevel(15)
//            .expireAfterWrite(5, TimeUnit.HOURS)
//            .initialCapacity(10000)
//            .maximumSize(10000)
//            .build();


    private static JedisPool jedisPool;

    static {
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(10);
            config.setMaxIdle(10);
            config.setMaxWaitMillis(10000);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            config.setBlockWhenExhausted(true);
            jedisPool = new JedisPool(config, "10.90.11.60", 6380, 10000);
        }
    }

    private static int dbNum = 14;

    private static String mediaScanPattern = "media:*";
    private static String mediaFilterPrefix = "media:";

    private static String scoreFilterWordField_level = "sensetiveLevel";
    private static String scoreFilterWordField_expireTs = "expireTs";

    @PostConstruct
    public void loadCache() {
        load();
//        long start = System.currentTimeMillis();
//        try {
//            Map<String, Integer> banMediaMap = loadMediaFromRedis();
//            banMediaCache.putAll(banMediaMap);
//        } catch (Exception e) {
//            logger.error("init ban media error: {}", e);
//        }
//        logger.info("init ban media size:{} cost:{}", banMediaCache.size(), System.currentTimeMillis() - start);
    }

    public static Jedis getJedisClient(int dbNum) {
        Jedis jedis = jedisPool.getResource();
        if (jedis != null) {
            jedis.select(dbNum);
            return jedis;
        } else {
            return null;
        }
    }

    private Map<String, Integer> loadMediaFromRedis() {
        Map<String, Integer> banMediaMap = new HashMap<>();

        ScanParams scanParams = new ScanParams();
        scanParams.match(mediaScanPattern);
        scanParams.count(1000);

        Jedis jedis = getJedisClient(dbNum);
        try {
            ScanResult<String> scanResult = jedis.scan(ScanParams.SCAN_POINTER_START, scanParams);

            String nextCursor = scanResult.getStringCursor();
            List<String> scanResultMap = scanResult.getResult();
            long now = System.currentTimeMillis();

            while (true) {
                for (String key : scanResultMap) {
                    Map<String, String> mapTemp = jedis.hgetAll(key);
                    if (CollectionUtils.isEmpty(mapTemp)) {
                        continue;
                    }
                    long expireTs = NumberUtils.toLong(mapTemp.get(scoreFilterWordField_expireTs), 0l);
                    if (now <= expireTs) {
                        banMediaMap.put(StringUtils.replaceOnce(key, mediaFilterPrefix, ""),
                                NumberUtils.toInt(mapTemp.get(scoreFilterWordField_level), 1));
                    }
                }
                if (nextCursor == null || ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
                    break;
                }
                scanResult = jedis.scan(nextCursor, scanParams);
                nextCursor = scanResult.getStringCursor();
                scanResultMap = scanResult.getResult();
            }
        } catch (Exception e) {
            logger.error("ScoreFilterCache loadError:{}", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return banMediaMap;
    }

    @Override
    public void loadAll(Map<String, Integer> cache) {
        cache.putAll(loadMediaFromRedis());
    }
}
