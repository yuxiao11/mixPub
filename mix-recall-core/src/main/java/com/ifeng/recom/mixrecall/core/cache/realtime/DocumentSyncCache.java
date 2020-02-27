package com.ifeng.recom.mixrecall.core.cache.realtime;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.core.cache.AbstractAsyncCache;
import com.ifeng.recom.mixrecall.core.cache.AbstractSyncCache;

public class DocumentSyncCache extends AbstractSyncCache<Document> {

    public DocumentSyncCache(AbstractAsyncCache<Document> asyncConfig) {
        super(asyncConfig);
    }
}
