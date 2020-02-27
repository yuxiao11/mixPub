package com.ifeng.recom.mixrecall.core.cache.realtime;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.ifeng.recom.mixrecall.common.dao.elastic.Query;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.core.cache.AbstractAsyncCache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * {@link Query}
 */
public class GuidCache extends AbstractAsyncCache<String> {
    public GuidCache(AbstractAsyncCache.AsyncConfig asyncConfig) {
        super(asyncConfig);
    }

    private static final String DUMMY_VARIABLES = "null";

    @Override
    public Caffeine getCaffeineBuilder() {
        return Caffeine.newBuilder()
//                .refreshAfterWrite(10, TimeUnit.HOURS)
                .expireAfterWrite(10, TimeUnit.HOURS)
                .initialCapacity(1000)
                .maximumSize(50000)
                ;

    }

    @Override
    public String getDummyVariablesObject() {
        return DUMMY_VARIABLES;
    }

    @Override
    public Map<String, String> batchQuery(Set<String> keys) {
        Map<String, String> rs = Maps.newHashMap();
        for (String k : keys) {
            try {
                String l = query(k);
                rs.put(k, l);
            } catch (Exception e) {
            }
        }
        return rs;
    }

    @Override
    public String query(String key) {
        return Query.queryDocIdByGuid(key);
    }

    private static final TypeToken<KV<String>> gsonType = new TypeToken<KV<String>>(){};

    @Override
    protected TypeToken<KV<String>> loadGsonFromToken() {
        return gsonType;
    }

}
