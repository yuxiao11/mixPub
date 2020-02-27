package com.ifeng.recom.mixrecall.core.cache.preload;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.core.util.PreloadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by liligeng on 2018/8/22.
 * lda topic缓存
 */
public class LdaTopicCache {

    private static Logger logger = LoggerFactory.getLogger(LdaTopicCache.class);

    public static LoadingCache<String, List<String>> cache;

    private static int LDA_REDIS_DB = 4;

    static {
        initCache();
    }

    public static void initCache() {
        cache = CacheBuilder
                .newBuilder()
                .concurrencyLevel(5)
                .expireAfterWrite(3, TimeUnit.HOURS)
                .initialCapacity(5000)
                .maximumSize(10000)
                .build(new CacheLoader<String, List<String>>() {
                           @Override
                           public List<String> load(String topic) throws Exception {
                               long start = System.currentTimeMillis();
                               List<String> idList = PreloadUtils.getDocumentId(topic, LDA_REDIS_DB);
                               long cost = System.currentTimeMillis() - start;
                               if (cost > 50) {
                                   ServiceLogUtil.debug("lda-topic {} cost:{}", topic, cost);
                               }
                               if (idList == null) {
                                   return Collections.emptyList();
                               } else {
                                   return idList;
                               }
                           }
                       }
                );
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
        return null;
    }
}
