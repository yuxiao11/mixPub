package com.ifeng.recom.mixrecall.core.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.dao.elastic.EsClientFactory;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by liligeng on 2019/5/22.
 */
public class DocidGuidCache {

    private static final Logger logger = LoggerFactory.getLogger(DocidGuidCache.class);

    private static LoadingCache<String, String> docidGuidCache;

    private static TransportClient client;

    private static final String INDEX = "preload-news";

    private static final String TYPE = "_doc";

    static {
        initCache();
        client = EsClientFactory.getClient();
    }

    static void initCache() {
        docidGuidCache = CacheBuilder
                .newBuilder()
                .recordStats()
                .concurrencyLevel(15)
                .expireAfterWrite(180, TimeUnit.MINUTES)
                .initialCapacity(100000)
                .maximumSize(150000)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String docId) throws Exception {
                        String guid = queryGuidByDocid(docId);
                        if (guid != null) {
                            return guid;
                        } else {
                            return "";
                        }
                    }
                });
    }

    public static String getGuidFromCache(String docId) {
        try {
            String guid = docidGuidCache.get(docId);
            return guid;
        } catch (ExecutionException e) {
            logger.error("query err:", e);
        }
        return null;
    }


    private static String queryGuidByDocid(String docId) {
        if (StringUtils.isBlank(docId)) {
            return null;
        }

        QueryBuilder qb = boolQuery().must(termQuery("docType", "video"))
                .must(termQuery("docId", docId));

        SearchResponse response = client.prepareSearch(INDEX)
                .setFetchSource(new String[]{"url"}, new String[]{})
                .setQuery(qb)
                .setTimeout(new TimeValue(500))
                .setSize(1).get();

        SearchHits searchHits = response.getHits();

        String guid = null;
        for (SearchHit searchHit : searchHits.getHits()) {
            Object object = searchHit.getSourceAsMap().get("url");
            if(object==null){
                return null;
            }
            guid = (String)object;
            break;
        }

        return guid;
    }

}
