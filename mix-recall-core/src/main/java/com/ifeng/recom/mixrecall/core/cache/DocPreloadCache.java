package com.ifeng.recom.mixrecall.core.cache;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.common.util.cache.CachePersist;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.ifeng.recom.mixrecall.common.dao.elastic.Query.queryDocument;

/**
 * 缓存Document对象 <docId,Document>
 **/

public class DocPreloadCache {
    private static final Logger logger = LoggerFactory.getLogger(DocPreloadCache.class);
    public static LoadingCache<String, Document> cache;

    private static final String CACHE_DUMP_PATH = "/data/prod/service/mix-recall/cache_dump/DocPreloadCache";

    static {
        initCache();
    }

    static void initCache() {
        cache = CacheBuilder
                .newBuilder()
                .recordStats()
                .concurrencyLevel(15)
                .expireAfterWrite(180, TimeUnit.MINUTES)
                .initialCapacity(1000000)
                .maximumSize(2000000)
                .build(new CacheLoader<String, Document>() {
                    @Override
                    public Document load(String docId) throws Exception {
                        long start = System.currentTimeMillis();
                        Document document = queryDocument(docId);
                        long cost = System.currentTimeMillis() - start;
                        if (cost > 50) {
                            ServiceLogUtil.debug("DocPre {} cost:{}", docId, cost);
                        }

                        if (document != null) {
                            return document;
                        } else {
                            return new Document();
                        }
                    }
                });
    }

    public static void putDocCache(String docId, Document document) {
        cache.put(docId, document);
    }


    public static Map<String, Document> getBatchDocsNoClone(Set<String> docIds) {
        try {
            return cache.getAll(docIds);
        } catch (ExecutionException e) {
            logger.error("getDoc", e);
        }

        return Collections.emptyMap();
    }

    public static Map<String, Document> getBatchDocsFirstHome(Set<String> docIds) {
        try {
            return cache.getAllPresent(docIds);
        } catch (Exception e) {
            logger.error("getBatchDocsFirstHome", e);
        }

        return Collections.emptyMap();
    }

    public static Map<String, Document> getBatchDocsWithQuery(Set<String> docIds) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("getBatchDocsWithQuery");
        Map<String, Document> clonedMap = new HashMap<>();
        Map<String, Document> map;
        try {
            map = cache.getAll(docIds);

            for (Map.Entry<String, Document> entry : map.entrySet()) {
                Document d = entry.getValue();
                try {
                    clonedMap.put(entry.getKey(), d.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        timer.addEndTime("getBatchDocsWithQuery");
        return clonedMap;
    }

    public static Map<String, Document> getBatchDocsWithQueryNoClone(Set<String> docIds) {
        if (docIds.isEmpty()) {
            return Collections.emptyMap();
        }

        docIds.removeIf(String::isEmpty);

        try {
            Map<String, Document> map = cache.getAll(docIds);
            return map;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    public static void checkStatus() {
        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
    }


    public static void main(String[] args) {


        Set<String> docIds=new HashSet<>();
        docIds.add("127674682");
        System.out.println(System.currentTimeMillis());
//        ImmutableMap<String,Document> s= cache.getAll(docIds);

        try{
            System.out.println("1:"+cache.getAllPresent(docIds));
            System.out.println("2:"+cache.getAll(docIds));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}