package com.ifeng.recom.mixrecall.core.cache;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 基于异步, 在查询不到的情况下同步查询. 丢失key已经进入异步队列, 会产生多次查询覆盖的问题. 数据保持相同
 *
 * @param <V>
 */
public abstract class AbstractSyncCache<V> implements ICache<V> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSyncCache.class);

    protected AbstractAsyncCache<V> asyncCache;

    public AbstractSyncCache(AbstractAsyncCache<V> asyncCache) {
        this.asyncCache = asyncCache;
    }

    @Override
    public V getByCache(String key) {
        V v = asyncCache.getByCache(key);
        if (v == null) {
            v = query(key);
            asyncCache.writeCache(v, key);

        }
        return v;
    }

    @Override
    public Map<String, V> batchByCache(Collection<String> key) {
        Map<String, V> r = asyncCache.batchByCache(key);
        if (r.size() < key.size()) {
            Set<String> loseKey = Sets.newHashSet();
            for (String k : key) {
                if (!r.containsKey(k)) {
                    loseKey.add(k);
                }
            }
            if (!loseKey.isEmpty()) {
                try {
                    Map<String, V> r2 = batchQuery(loseKey);
                    asyncCache.writeCache(r2, loseKey);
                    r.putAll(r2);
                } catch (Exception e) {
                    logger.error("batch Query error,{}", loseKey, e);
                }
            }
        }
        return r;
    }

    @Override
    public V query(String key) {
        return asyncCache.query(key);
    }

    @Override
    public Map<String, V> batchQuery(Set<String> keys) {
        return asyncCache.batchQuery(keys);
    }

    @Override
    public String status() {
        return asyncCache.status();
    }
}
