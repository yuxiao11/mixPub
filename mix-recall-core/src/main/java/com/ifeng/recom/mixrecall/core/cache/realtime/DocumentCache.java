package com.ifeng.recom.mixrecall.core.cache.realtime;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.dao.elastic.Query;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.cache.LoseDocument;
import com.ifeng.recom.mixrecall.common.util.StringUtil;
import com.ifeng.recom.mixrecall.core.cache.AbstractAsyncCache;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * {@link DocPreloadCache}
 */
public class DocumentCache extends AbstractAsyncCache<Document> {
    private static final Logger logger = LoggerFactory.getLogger(DocumentCache.class);

    private GuidCache guidCache;

    public DocumentCache(AbstractAsyncCache.AsyncConfig asyncConfig, GuidCache guidCache) {
        super(asyncConfig);
        this.guidCache = guidCache;
    }

//    public CacheBuilder getCacheBuilder() {
//        return CacheBuilder
//                .newBuilder()
//                .concurrencyLevel(15)
//                .refreshAfterWrite(180, TimeUnit.MINUTES)
//                .initialCapacity(1000000)
//                .maximumSize(2000000);
//    }

    @Override
    public Caffeine getCaffeineBuilder() {
        return Caffeine.newBuilder()
//                .refreshAfterWrite(180, TimeUnit.MINUTES)
                .expireAfterWrite(180, TimeUnit.MINUTES)
                .initialCapacity(2000000)
                .maximumSize(2000000)
                ;

    }

    @Override
    public Document getDummyVariablesObject() {
        return LoseDocument.lose_document;
    }

    @Override
    public Map<String, Document> batchQuery(Set<String> keys) {
        return innerBatchQuery(keys);
    }

    @Override
    public Document query(String key) {
        return Query.queryDocument(key);
    }


    private static final TypeToken<KV<Document>> gsonType = new TypeToken<KV<Document>>(){};
    @Override
    public TypeToken<KV<Document>> loadGsonFromToken() {
        return gsonType;
    }

    @Override
    public Map<String, Document> batchByCache(Collection<String> keys) {
        // 进行字段翻译
        Map<String, String> docId2oldId = new HashMap<>(keys.size());
        for (String k : keys) {
            String nk = translateKey(k);
            if (StringUtils.isBlank(nk)) {
                continue;
            }
            docId2oldId.put(nk, k);
        }
        Map<String, Document> documentMap = super.batchByCache(docId2oldId.keySet());
        return documentMap.entrySet()
                .stream()
                .collect(Collectors.toMap(s -> docId2oldId.get(s.getKey()), s -> s.getValue()));
    }

    @Override
    public Document getByCache(String key) {
        // 进行id翻译
        String newKey = translateKey(key);

        return super.getByCache(newKey);
    }

    /**
     * 进行key的翻译
     * 1. guid兼容
     * 2. docid不是数字开头的需要修改
     *
     * @param key
     * @return
     */
    private String translateKey(String key) {
        if (key.length() >= GyConstant.GUID_Length) {
            String k = guidCache.getByCache(key);
            if (StringUtils.isBlank(k)) {
                return "";
            }
            return StringUtil.startWithNum(k) ? k :
                    k.substring(k.indexOf(GyConstant.Symb_Underline) + 1);
        }
//        兼容字符串开头的其他id，
//        if (!StringUtil.startWithNum(docId)) {
//            docId = docId.substring(docId.indexOf(GyConstant.Symb_Underline) + 1);
//        }
        return StringUtil.startWithNum(key) ? key :
                key.substring(key.indexOf(GyConstant.Symb_Underline) + 1);
    }

    private static Map<String, Document> innerBatchQuery(Set<String> docIds) {
        try {
            Map<String, Document> values = Query.batchQueryByDocId(docIds);
            return values;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Maps.newHashMap();
    }
}
