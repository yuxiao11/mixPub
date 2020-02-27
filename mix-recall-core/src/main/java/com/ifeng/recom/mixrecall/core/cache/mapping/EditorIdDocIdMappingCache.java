package com.ifeng.recom.mixrecall.core.cache.mapping;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.ifeng.recom.mixrecall.common.util.DocUtils.getItemIndex;

/**
 * 缓存sub之类的带有各种前缀的id->推荐id
 **/

public class EditorIdDocIdMappingCache {
    private static final Logger logger = LoggerFactory.getLogger(EditorIdDocIdMappingCache.class);
    private static LoadingCache<String, String> cache;

    static {
        initCache();
    }

    public static void initCache() {
        cache = CacheBuilder
                .newBuilder()
                .recordStats()
                .concurrencyLevel(10)
                .expireAfterWrite(10, TimeUnit.HOURS)
                .initialCapacity(100000)
                .maximumSize(1000000)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String id) throws Exception {
                        long start = System.currentTimeMillis();
                        String docId = getItemIndex(id).replace("cmpp_", "");
                        long cost = System.currentTimeMillis() - start;
                        if (cost > 50) {
                            ServiceLogUtil.debug("EditorIdDocIdMapping {} cost:{}", id, cost);
                        }
                        return docId;
                    }
                });
    }


    public static Map<String, String> getBatchDocIds(Set<String> ids) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("getBatchDocIds");
        Map<String, String> map = new HashMap<>();
        try {
            map = cache.getAll(ids);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        timer.addEndTime("getBatchDocIds");
        return map;
    }

    private static String getDocId(String id) {
        try {
            return cache.get(id);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static void checkStatus() {
        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
    }


    public static void main(String[] args) {
        String document = getDocId("sub_38201254");
        System.out.println(document);
    }

}
