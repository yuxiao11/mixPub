package com.ifeng.recom.mixrecall.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.CacheStats;
//import com.google.common.cache.LoadingCache;

public abstract class AbstractAsyncCache<V> implements ICache<V> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAsyncCache.class);
    private final static Gson gson = new Gson();
    protected final static TypeToken<KV<List<String>>> gsonListToken = new TypeToken<KV<List<String>>>() {
    };


    public static class AsyncConfig {
        /**
         * 队列长度
         */
        private int queueLength;
        /**
         * 异步线程数量
         */
        private int threadNum;
        /**
         * 最大批量查询数量
         */
        private int maxBatchNum;
        /**
         * 数量不足最长等待间隔
         */
        private int maxWaitMilliseconds;

        private String dumpPath;

        private boolean dumpFlag;

        private AsyncConfig(AsyncConfigBuilder builder) {
            queueLength = builder.queueLength;
            threadNum = builder.threadNum;
            maxBatchNum = builder.maxBatchNum;
            maxWaitMilliseconds = builder.maxWaitMilliseconds;
            dumpFlag = builder.dumpFlag;
            dumpPath = builder.dumpPath;
        }


        public int getQueueLength() {
            return queueLength;
        }

        public int getThreadNum() {
            return threadNum;
        }

        public int getMaxBatchNum() {
            return maxBatchNum;
        }

        public int getMaxWaitMilliseconds() {
            return maxWaitMilliseconds;
        }

        public String getDumpPath() {
            return dumpPath;
        }

        public boolean isDumpFlag() {
            return dumpFlag;
        }
    }

    public static AbstractAsyncCache.AsyncConfigBuilder newConfigBuilder() {
        return new AbstractAsyncCache.AsyncConfigBuilder();
    }

    public static final class AsyncConfigBuilder {
        private int queueLength;
        private int threadNum;
        private int maxBatchNum;
        private int maxWaitMilliseconds;
        private String dumpPath;
        private boolean dumpFlag;

        private AsyncConfigBuilder() {
        }

        public AsyncConfigBuilder setQueueLength(int val) {
            queueLength = val;
            return this;
        }

        public AsyncConfigBuilder setThreadNum(int val) {
            threadNum = val;
            return this;
        }

        public AsyncConfigBuilder setMaxBatchNum(int val) {
            maxBatchNum = val;
            return this;
        }

        public AsyncConfigBuilder setMaxWaitMilliseconds(int val) {
            maxWaitMilliseconds = val;
            return this;
        }

        public AsyncConfigBuilder setDumpPath(String dumpPath) {
            this.dumpPath = dumpPath;
            return this;
        }

        public AsyncConfigBuilder setDumpFlag(boolean dumpFlag) {
            this.dumpFlag = dumpFlag;
            return this;
        }

        public AsyncConfig build() {
            return new AsyncConfig(this);
        }
    }


    /**
     * cache 构建起
     *
     * @param
     * @return
     */
    public CacheBuilder getCacheBuilder() {
        return null;
    }

    public Caffeine getCaffeineBuilder() {
        return null;
    }

    /**
     * 获得哑对象. 对象尽量是静待数据
     *
     * @param <T>
     * @return
     */
    protected abstract <T extends V> T getDummyVariablesObject();

    /**
     * 加载gson的泛解析typetoken
     *
     * @return
     */
    protected abstract TypeToken<KV<V>> loadGsonFromToken();

    //    private LoadingCache<String, V> cache;
//    private LoadingCache<String, V> cache;
    private Cache<String, V> cache;
    private AsyncConfig asyncConfig;
    private BlockingQueue<String> queue;
    private List<Thread> threads = new ArrayList<>();
    private String className = getClass().getSimpleName();

    public AbstractAsyncCache(AsyncConfig asyncConfig) {
        init(asyncConfig);
        for (Thread t : threads) {
            t.start();
        }
    }

    private void init(AsyncConfig asyncConfig) {
        if (asyncConfig.queueLength <= 0 || asyncConfig.queueLength >= 50000) {
            throw new IllegalArgumentException("0 < queueLength < 50000");
        }
        if (asyncConfig.threadNum <= 0 || asyncConfig.threadNum >= 10) {
            throw new IllegalArgumentException("0 < threadNum < 10");
        }
        if (asyncConfig.maxBatchNum <= 0 || asyncConfig.maxBatchNum >= 500) {
            throw new IllegalArgumentException("0 < maxBatchNum < 500");
        }
        if (asyncConfig.maxWaitMilliseconds <= 0 || asyncConfig.maxWaitMilliseconds >= 1000) {
            throw new IllegalArgumentException("0 < maxWaitMilliseconds < 1000");
        }
        queue = new ArrayBlockingQueue<>(asyncConfig.queueLength);

        cache = getCaffeineBuilder().
                recordStats().
                build();
//                build(new CacheLoader<String, V>() {
//                    @Override
//                    public V load(String s) throws Exception {
//                        if (StringUtils.isBlank(s)) {
//                            return getDummyVariablesObject();
//                        }
//                        if (queue.offer(s)) {
//                            return getDummyVariablesObject();
//                        } else {
//                            return query(s);
//                        }
//                    }
//                });

        this.asyncConfig = asyncConfig;

        for (int i = 0; i < asyncConfig.threadNum; i++) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Set<String> keys = Sets.newHashSet();
                        try {
                            fillQueryKeys(keys, asyncConfig.maxBatchNum);
                            if (keys.size() < asyncConfig.maxBatchNum) {
                                String s = queue.poll(asyncConfig.maxWaitMilliseconds, TimeUnit.MILLISECONDS);
                                if (StringUtils.isNoneBlank(s)) {
                                    keys.add(s);
                                }
                            }
                            fillQueryKeys(keys, asyncConfig.maxBatchNum);
                            if (keys.isEmpty()) {
                                continue;
                            }
                            long start = System.currentTimeMillis();
                            logger.info("load key, {}, keySize:{}", className, keys.size());
                            Map<String, V> values = null;
                            try {
                                values = batchQuery(keys);
                            } catch (Exception e) {
                                logger.error("batchQuery error,{} , keys:{}, {}", className, keys, e);
                                continue;
                            }
                            writeCache(values, keys);
                            logger.info("load key, {}, loadSize:{}, cost:{}", className, values.size(), System.currentTimeMillis() - start);
                        } catch (Exception e) {
                            logger.error("asnyc load cache, {}, error, keys:{}, {}", className, keys, e);
                        }
                    }
                }
            }, className + "-cache-loader"));
        }
    }

    /**
     * @param maps    查询到的key
     * @param allKeys 全量key, 存在可以移除的key
     */
    protected void writeCache(Map<String, V> maps, Set<String> allKeys) {
        for (String key : allKeys) {
            V v = maps.get(key);
            if (v != null) {
                cache.put(key, v);
            } else {
//                cache.invalidate(key);
            }
        }
    }

    protected void writeCache(V v, String key) {
        if (v != null) {
            cache.put(key, v);
        }
    }

    private void fillQueryKeys(Set<String> keys, int max) {
        int size = keys.size();
        while (size < max) {
            String s = queue.poll();
            if (StringUtils.isBlank(s)) {
                return;
            }
            size++;
            keys.add(s);
        }
    }

    @Override
    public V getByCache(String key) {
        try {
            V v = cache.getIfPresent(key);
            if (v == null) {
                queue.offer(key);
            }
//            if (getDummyVariablesObject().equals(v)) {
//                return null;
//            }
            return v;
        } catch (Exception e) {
            cache.invalidate(key);
            logger.error("error,{}, {}, {}", className, key, e);
        }
        return null;
    }

    @Override
    public Map<String, V> batchByCache(Collection<String> key) {
        try {
//            Map<String, V> vs = cache.getAll(key);
//            V dv = getDummyVariablesObject();
//            Map<String, V> r = key.stream()
//                    .filter(v -> !dv.equals(vs.get(v)))
//                    .collect(Collectors.toMap(v -> v, v -> vs.get(v)));
            // return r;
            Map<String, V> vs = new HashMap<>(key.size());
            for (String k : key) {
                V v = getByCache(k);
                if (v != null) {
                    vs.put(k, v);
                }
            }
            logger.info("batch Query,{}, querySize:{}, resultSize:{}", className, key.size(), vs.size());
            return vs;
        } catch (Exception e) {
            logger.error("batch error,{}, {}, {}", className, key, e);
        }
        return Maps.newHashMap();
    }

    protected static class KV<V> {
        String key;
        V value;

        public String getKey() {
            return key;
        }

        public KV<V> setKey(String key) {
            this.key = key;
            return this;
        }

        public V getValue() {
            return value;
        }

        public KV<V> setValue(V value) {
            this.value = value;
            return this;
        }
    }

    public String status() {
        return innerStatusStr(cache.stats(), cache.estimatedSize());
    }

    public static String innerStatusStr(CacheStats stats, long size) {
        return String.format("hit_count:%d, hit_rate:%5.2f, load_count:%d, " +
                        "loadSuccessCount:%d, loadExceptionCount:%d, loadExceptionRate:%5.2f, " +
                        "missCount:%d, missRate:%5.2f, cache_size:%d",
                stats.hitCount(), stats.hitRate(), stats.loadCount(),
                stats.loadSuccessCount(), stats.loadFailureCount(), stats.loadFailureRate(),
                stats.missCount(), stats.missRate(), size);
    }

    public void dump() throws IOException {
        if (asyncConfig.dumpFlag && StringUtils.isNoneBlank(asyncConfig.dumpPath)) {
            Path path = Paths.get(asyncConfig.dumpPath + ".temp");
            if (Files.exists(path)) {
                Files.delete(path);
            }
            path = Paths.get(asyncConfig.dumpPath + ".temp");
            innerDump(path);
            Files.move(path, Paths.get(asyncConfig.dumpPath), StandardCopyOption.REPLACE_EXISTING);
        }

    }

    /**
     * 滤掉没有意义的呀对象
     * TODO 可能需要删除无效对象. 例如空的list, map, 超时的文档, 减少load和dump压力.
     *
     * @param write
     * @throws IOException
     */
    private void innerDump(Path write) throws IOException {
        Map<String, V> dump = cache.asMap();
        final V dummyVariables = getDummyVariablesObject();

        Iterator<String> outIterator = dump.entrySet().stream()
                .map(kv -> {
                    if (StringUtils.isNoneBlank(kv.getKey()) &&
                            kv.getValue() != null && !dummyVariables.equals(kv.getValue())) {
                        try {
                            KV o = new KV();
                            o.setKey(kv.getKey());
                            o.setValue(kv.getValue());
                            return gson.toJson(o);
                        } catch (Exception e) {
                        }
                    }
                    return "";
                })
                .filter(s -> StringUtils.isNoneBlank(s))
                .iterator();

        Files.write(write, new Iterable<String>() {
                    @Override
                    public Iterator<String> iterator() {
                        return outIterator;
                    }
                }, Charset.forName("utf-8"),
                StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    public void load() throws IOException {
        if (asyncConfig.dumpFlag && StringUtils.isNoneBlank(asyncConfig.dumpPath)) {
            Path path = Paths.get(asyncConfig.dumpPath);
            if (Files.exists(path) && Files.isReadable(path)) {
                innerLoad(path);
            }
        }
    }

    /**
     * 假设没有哑对象, 所以不保存哑对象逻辑.
     *
     * @param read
     * @throws IOException
     */
    private void innerLoad(Path read) throws IOException {
        Stream<String> stream = Files.lines(read, Charset.forName("utf-8"));
        Type jsonType = loadGsonFromToken().getType();
        stream.forEach(
                s -> {
                    try {
                        KV<V> kv = gson.fromJson(s, jsonType);
                        if (StringUtils.isNoneBlank(kv.getKey()) && kv.getValue() != null) {
                            cache.put(kv.getKey(), kv.getValue());
                        }
                    } catch (Exception e) {
                    }
                }
        );
    }

    public String getClassName() {
        return className;
    }

    public void addKeys(Collection<String> key) {
        for (String k : key) {
            if (StringUtils.isNoneBlank(k)) {
                queue.offer(k);
            }
        }
    }
}
