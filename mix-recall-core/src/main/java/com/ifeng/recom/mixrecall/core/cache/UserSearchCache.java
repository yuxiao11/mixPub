package com.ifeng.recom.mixrecall.core.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.core.channel.excutor.UserSearchQuery;
import com.ifeng.recom.mixrecall.common.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by lilg1 on 2018/1/24.
 */
public class UserSearchCache {
    private static final Logger logger = LoggerFactory.getLogger(UserSearchCache.class);
    private static LoadingCache<String, List<Document>> cache;

    static {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(3, TimeUnit.HOURS)
                .recordStats()
                .initialCapacity(1000)
                .maximumSize(5000)
                .build(new CacheLoader<String, List<Document>>() {
                    @Override
                    public List<Document> load(String searchContent) throws Exception {
                        long start = System.currentTimeMillis();
                        List<Document> result = UserSearchQuery.searchQueryEs(searchContent);
                        long cost = System.currentTimeMillis() - start;
                        if (cost > 50) {
                            ServiceLogUtil.debug("UserSearch {} cost:{}", searchContent, cost);
                        }
                        return result;
                    }
                });
    }

    public static Map<String, List<Document>> getDocuments(List<String> searchQuery) {
        try {
            return cache.getAll(searchQuery);
        } catch (ExecutionException e) {
            logger.error("UserSearchCache getAll error", e);
        }
        return Collections.emptyMap();
    }

    public static void checkStatus() {
        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
    }

}
