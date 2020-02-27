package com.ifeng.recom.mixrecall.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ifeng.recom.mixrecall.common.dao.mysql.dao.SourceInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.ifeng.recom.mixrecall.common.dao.redis.RedisUtils.getPreloadJedisClient;

/**
 * 根据redis中准备好的simId进行过滤,数据由张阳写入
 */
@Service
public class FilterDocsCache extends AbstractMapCache<String>{
    private static final Logger logger = LoggerFactory.getLogger(FilterDocsCache.class);
    //    private static Cache<String, String> cacheIds;
////    private static final int REDIS_DB = 14;
    @Autowired
    private SourceInfoDao sourceInfoDao;
//    static {
//        initCacheIds();
//    }

//    private  static void initCacheIds() {
//        cacheIds = CacheBuilder
//                .newBuilder()
//                .recordStats()
//                .concurrencyLevel(10)
//                .expireAfterWrite(7, TimeUnit.DAYS)
//                .initialCapacity(1000000)
//                .maximumSize(1000000)
//                .build();
//    }


    @PostConstruct
    public void init() {
        load();


    }

    public Map<String, String> checkFilteredId(Set<String> ids) {
//        return cacheIds.getAllPresent(ids);
        return super.getAll(ids);
    }

    @Override
    public void loadAll(Map<String, String> cache) {
        List<String> sansuSimIds = sourceInfoDao.getSansuSimIds();
        for (String id : sansuSimIds) {
            cache.put(id,"");
        }
        logger.info("update filter simId from zhangyang, size:{}, cached size:{}", sansuSimIds.size(), cache.size());
    }


//    public static void checkStatus() {
//        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cacheIds.stats().hitCount(), cacheIds.stats().hitRate(), cacheIds.stats().loadCount(), cacheIds.size());
//    }
}