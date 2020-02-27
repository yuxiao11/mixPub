package com.ifeng.recom.mixrecall.common.service.handler.remove;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * 相同内容doc过滤
 */
public class DupRemoveHandler implements IItemRemoveHandler<Document> {
    private Set<String> set = new HashSet<>();

    @Override
    public boolean remove(MixRequestInfo info, Document item) {
        return !set.add(item.getDocId());
    }

    @Override
    public String handlerName() {
        return "dup";
    }

}
