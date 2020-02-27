package com.ifeng.recom.mixrecall.core.cache.preload;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ifeng.recom.mixrecall.common.dao.redis.RedisUtils;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory.ListRecordInfo;


public class SourceExploreCache {
    private static final Logger logger = LoggerFactory.getLogger(SourceExploreCache.class);
    public static Cache<String, List<RecordInfo>> cache;

    private static int REDIS_DB_DOC = 12;

    static {
        initCache();
    }

    public static void initCache() {
        cache = CacheBuilder
                .newBuilder()
                .concurrencyLevel(10)
                .expireAfterWrite(10, TimeUnit.HOURS)
                .initialCapacity(1000000)
                .maximumSize(1000000)
                .build();
    }


    public static List<RecordInfo> getFromCache(String uid) {
        List<RecordInfo> sourceForExplores=new ArrayList<>();
        sourceForExplores=cache.getIfPresent(uid);
        if(sourceForExplores==null||sourceForExplores.size()==0) {
            Jedis jedis = RedisUtils.getJedisPoolSourceRedis();
            try {
                if (jedis != null) {
                    jedis.select(REDIS_DB_DOC);
                } else {
                    return Collections.emptyList();
                }
                String rt = jedis.get(uid);
                if (rt == null) {
                    return sourceForExplores;
                }
                sourceForExplores = GsonUtil.json2ObjectWithoutExpose(rt, ListRecordInfo);
                putCache(uid,sourceForExplores);
            }catch (Exception e){
                logger.error("uid:{},sourceExploreCache error:{}",uid, e);
            }finally {
                jedis.close();
            }
        }
        return sourceForExplores;
    }

    public static void putCache(String uid, List<RecordInfo> results) {
        cache.put(uid, results);
    }

    public static void main(String[] args) {
        List<RecordInfo> results=getFromCache("865969031431182");
        System.out.println(results);
    }
}
