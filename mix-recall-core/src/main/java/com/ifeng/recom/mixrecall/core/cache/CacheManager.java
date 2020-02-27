package com.ifeng.recom.mixrecall.core.cache;


import com.ifeng.recom.mixrecall.core.cache.preload.FalseDocsCache;
import com.ifeng.recom.mixrecall.core.cache.preload.TrueDocsCache;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 管理所有cache
 **/
public class CacheManager {

    public enum PreloadDocType {DOC, SLIDE, VIDEO}

    public static Map<String, List<String>> getPreloadDocId(Set<String> tags, PreloadDocType docType) {
        if (docType.equals(PreloadDocType.DOC)) {
            return TrueDocsCache.getFromCache(tags);
        }

        return Collections.emptyMap();
    }

    public static void init() {
    }
}
