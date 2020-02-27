package com.ifeng.recom.mixrecall.core.cache.preload;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.ifeng.recom.mixrecall.common.dao.redis.RedisUtils.getPreloadJedisClient;
import static com.ifeng.recom.mixrecall.core.util.PreloadUtils.getDocumentId;

public class TrueDocsCache {
    private static final Logger logger = LoggerFactory.getLogger(TrueDocsCache.class);
    public  static LoadingCache<String, List<String>> cache;
    private static int REDIS_DB_DOC = 5;

    static {
        initCache();
    }

    public static void initCache() {
        cache = CacheBuilder
                .newBuilder()
                .recordStats()
                .concurrencyLevel(15)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .initialCapacity(200000)
                .maximumSize(200000)
                .build(new CacheLoader<String, List<String>>() {
                    @Override
                    public List<String> load(String tag) throws Exception {
                        long start = System.currentTimeMillis();
                        List<String> idList = getDocumentId(tag, REDIS_DB_DOC);
                        long cost = System.currentTimeMillis() - start;
                        if (cost > 50) {
                            ServiceLogUtil.debug("TrueTag {} cost:{}", tag, cost);
                        }
                        if (idList == null) {
                            return Collections.emptyList();
                        } else {
                            return idList;
                        }
                    }
                });
    }

    public static Map<String, List<String>> getFromCache(Set<String> tags) {
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
        return Collections.emptyList();
    }

    public static void loadCache() {
        Jedis jedis = getPreloadJedisClient();
        if (jedis != null) {
            jedis.select(REDIS_DB_DOC);
        } else {
            return;
        }

        List<String> keys = new ArrayList<>();

        ScanParams scanParameters = new ScanParams();
        scanParameters.match("*-*");
        scanParameters.count(1000);

        try {
            ScanResult<String> scanResult = jedis.scan(0, scanParameters);
            String nextCursor = scanResult.getStringCursor();
            List<String> scanResults = scanResult.getResult();

            while (true) {
                keys.addAll(scanResults);

                Set<String> tags = new HashSet<>(scanResults);
                getFromCache(tags);
                logger.info("init document cache: size:{}", cache.size());

                if (keys.size() > 200000) {
                    break;
                }

                if (nextCursor.equals("0")) {
                    break;
                }
                scanResult = jedis.scan(nextCursor, scanParameters);
                nextCursor = scanResult.getStringCursor();
                scanResults = scanResult.getResult();
            }

            logger.info("load cotag to cache, size:" + keys.size());

        } catch (Exception e) {
            logger.error("load cotag to cache error", e);
        } finally {
            jedis.close();
        }
    }


    public static void checkStatus() {
        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
    }


    public static void main(String[] args) {
        loadCache();
//        List<String> documentList = getFromCache("国际-c-习近平-et");
//        System.out.println(documentList);
    }

}