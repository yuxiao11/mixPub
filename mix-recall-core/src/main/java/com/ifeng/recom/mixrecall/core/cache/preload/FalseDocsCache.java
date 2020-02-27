package com.ifeng.recom.mixrecall.core.cache.preload;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.ifeng.recom.mixrecall.core.util.PreloadUtils.getDocumentId;

/**
 * 缓存 isAvailable=false 的tag 和 id 对应关系
 * 读取预加载 redis db6
 */

public class FalseDocsCache {
    private static final Logger logger = LoggerFactory.getLogger(FalseDocsCache.class);
    private static LoadingCache<String, List<String>> cache;
    private static int REDIS_PRELOAD_FALSE_DB = 6;
    static {
        initCache();
    }
    public static void initCache() {
        cache = CacheBuilder
                .newBuilder()
                .concurrencyLevel(10)
                .expireAfterWrite(3, TimeUnit.HOURS)
                .initialCapacity(100000)
                .maximumSize(1000000)
                .build(new CacheLoader<String, List<String>>() {
                    @Override
                    public List<String> load(String tag) throws Exception {
                        long start = System.currentTimeMillis();
                        List<String> idList = getDocumentId(tag, REDIS_PRELOAD_FALSE_DB);
                        long cost = System.currentTimeMillis() - start;
                        if (cost > 50) {
                            ServiceLogUtil.debug("FalseTag {} cost:{}", tag, cost);
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
        return null;
    }

    private static List<String> getFromCache(String docId) {
        try {
            return cache.get(docId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        CacheManager.init();

//        System.out.println(getDocumentId("乒乓球-会员", REDIS_PRELOAD_FALSE_DB));

        List<String> documentList = getFromCache("IT-AS");
        System.out.println(documentList);
    }

}
