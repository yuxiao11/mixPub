package com.ifeng.recom.mixrecall.common.model.cache;

import com.ifeng.recom.mixrecall.common.model.Document;

public class LoseDocument extends Document {
    public LoseDocument() {
    }

    public LoseDocument(String docId) {
        super();
        setDocId(docId);
    }


    public static final LoseDocument lose_document = new LoseDocument("-1");

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof LoseDocument;
    }
}
