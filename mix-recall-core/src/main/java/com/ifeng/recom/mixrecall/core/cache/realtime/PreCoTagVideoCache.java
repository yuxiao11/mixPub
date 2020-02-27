package com.ifeng.recom.mixrecall.core.cache.realtime;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.ifeng.recom.mixrecall.common.model.cache.LoseList;
import com.ifeng.recom.mixrecall.core.cache.AbstractAsyncCache;
import com.ifeng.recom.mixrecall.core.cache.preload.CotagVideoNewCache;
import com.ifeng.recom.mixrecall.core.util.PreloadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * {@link CotagVideoNewCache}
 */
public class PreCoTagVideoCache extends AbstractAsyncCache<List<String>> {
    private static final Logger logger = LoggerFactory.getLogger(PreCoTagVideoCache.class);
    public static LoadingCache<String, List<String>> cache;

    //待修改
    private static int REDIS_DB_DOC = 2;

    public PreCoTagVideoCache(AbstractAsyncCache.AsyncConfig asyncConfig) {
        super(asyncConfig);
    }

    public Map<String, List<String>> getFromCacheFirstHome(Set<String> tags) {
        try {
            return batchQuery(tags);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    @Override
    public Caffeine getCaffeineBuilder() {
        return Caffeine.newBuilder()
//                .refreshAfterWrite(1, TimeUnit.HOURS)
                .expireAfterWrite(60,TimeUnit.MINUTES)
                .initialCapacity(1500000)
                .maximumSize(1500000);
    }

    @Override
    public List<String> getDummyVariablesObject() {
        return LoseList.loseList;
    }

    @Override
    public Map<String, List<String>> batchQuery(Set<String> keys) {
        Map<String, List<String>> rs = Maps.newHashMap();
        for (String k : keys) {
            try {
                List<String> l = query(k);
                rs.put(k, l);
            } catch (Exception e) {
            }
        }
        return rs;
    }

    @Override
    public List<String> query(String key) {
        List<String> idList = PreloadUtils.getDocIdNew(key, REDIS_DB_DOC);
        return idList;
    }

    @Override
    public TypeToken<KV<List<String>>> loadGsonFromToken() {
        return gsonListToken;
    }
}
