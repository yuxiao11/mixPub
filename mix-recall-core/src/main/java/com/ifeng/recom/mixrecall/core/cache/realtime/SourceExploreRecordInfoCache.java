package com.ifeng.recom.mixrecall.core.cache.realtime;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.ifeng.recom.mixrecall.common.dao.redis.RedisUtils;
import com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.cache.LoseList;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.core.cache.AbstractAsyncCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SourceExploreRecordInfoCache extends AbstractAsyncCache<List<RecordInfo>> {

    private static final Logger logger = LoggerFactory.getLogger(SourceExploreRecordInfoCache.class);


    public SourceExploreRecordInfoCache(AbstractAsyncCache.AsyncConfig asyncConfig) {
        super(asyncConfig);
    }

    @Override
    public Caffeine getCaffeineBuilder() {
        return Caffeine.newBuilder()
//                .refreshAfterWrite(3, TimeUnit.HOURS)
                .expireAfterWrite(3, TimeUnit.HOURS)
                .initialCapacity(10000)
                .maximumSize(80000)
                ;

    }

    private static int REDIS_DB_DOC = 12;

    @Override
    public List<RecordInfo> getDummyVariablesObject() {
        return LoseList.loseList;
    }

    /**
     * redis 查询, 单条数据可能过大, 改为一条条查询.
     *
     * @param keys
     * @return
     */
    @Override
    public Map<String, List<RecordInfo>> batchQuery(Set<String> keys) {
        Map<String, List<RecordInfo>> result = Maps.newHashMap();
        for (String uid : keys) {
            try {
                List<RecordInfo> l = query(uid);
                result.put(uid, l);
            } catch (Exception e) {
                logger.error("load source recordInfo error, uid:{}, {}", uid, e);
            }
        }
        return result;
    }

    @Override
    public List<RecordInfo> query(String uid) {
        Jedis jedis = RedisUtils.getJedisPoolSourceRedis();
        try {
            if (jedis == null) {
                return getDummyVariablesObject();
            }
            jedis.select(REDIS_DB_DOC);
            String rt = jedis.get(uid);
            if (rt == null) {
                return getDummyVariablesObject();
            }
            List<RecordInfo> sourceForExplores = GsonUtil.json2ObjectWithoutExpose(rt, JsonTypeFactory.ListRecordInfo);
            return sourceForExplores;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public TypeToken<KV<List<RecordInfo>>> loadGsonFromToken() {
        return new TypeToken<KV<List<RecordInfo>>>(){};
    }

}
