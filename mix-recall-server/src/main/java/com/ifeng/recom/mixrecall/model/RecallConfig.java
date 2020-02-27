package com.ifeng.recom.mixrecall.model;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.handler.remove.IItemRemoveHandler;

import java.util.List;
import java.util.function.Function;

public class RecallConfig {
    private String beanName;
    private MixRequestInfo info;
    private List<IItemRemoveHandler<Document>> removerList;

    private RecallConfig() {
    }

    public static RecallConfig build() {
        return new RecallConfig();
    }

    public MixRequestInfo getInfo() {
        return info;
    }

    public RecallConfig setInfo(MixRequestInfo info) {
        this.info = info;
        return this;
    }

    public List<IItemRemoveHandler<Document>> getRemoverList() {
        return removerList;
    }

    public RecallConfig setRemoverList(List<IItemRemoveHandler<Document>> removerList) {
        this.removerList = removerList;
        return this;
    }

    public String getBeanName() {
        return beanName;
    }

    public RecallConfig setBeanName(String beanName) {
        this.beanName = beanName;
        return this;
    }
}
