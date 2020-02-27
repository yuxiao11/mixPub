package com.ifeng.recom.mixrecall.common.cache;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 静态map, 采用rcu进行更新
 *
 * @param <V>
 */
public abstract class AbstractMapCache<V> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMapCache.class);

    private volatile Map<String, V> cache = new HashMap<>();

    /**
     * 定期加载
     */
    public void load() {
        Map<String, V> v = new HashMap<>(cache.size());
        long start = System.currentTimeMillis();
        try {
            loadAll(v);
        } catch (Exception e) {
            logger.error("load cache error, {}, {}", getClass().getSimpleName(), e);
            return;
        }
        logger.info("load static cache, old:{}, new:{}, cost:{}, {}", cache.size(), v.size(), System.currentTimeMillis() - start, getClass().getSimpleName());
        if (v != null && !v.isEmpty()) {
            cache = v;
        }
    }

    /**
     * 全量数据加载
     *
     * @param cache
     */
    public abstract void loadAll(Map<String, V> cache);

    public V get(String sourceName) {
        return cache.get(sourceName);
    }

    public Map<String, V> getAll(Collection<String> ids) {
        Map<String, V> c = cache;
        Map<String, V> r = new HashMap<>(ids.size());
        for (String i : ids) {
            if (c.containsKey(i)) {
                r.put(i, c.get(i));
            }
        }
        return r;
    }

    public Set<String> containsKeys(Collection<String> keys) {
        return containsKeys(keys, Function.identity());
    }

    public <T> Set<String> containsKeys(Collection<T> keys, Function<T, String> mapper) {
        if (keys == null && keys.isEmpty()) {
            return Sets.newHashSet();
        }
        final Map<String, V> c = cache;
        return keys.stream().
                filter(Objects::nonNull).
                map(mapper).
                filter(k -> c.containsKey(k)).
                collect(Collectors.toSet());
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    public void checkStatus() {
        logger.debug("cache_size:{}", cache.size());
    }
}
