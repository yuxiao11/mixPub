package com.ifeng.recom.mixrecall.common.model;

import com.ifeng.recom.mixrecall.common.constant.RecallConstant;

import java.util.List;


public class RecallThreadResult {
    //通道名称
    private RecallConstant.CHANNEL name;

    private List<RecallResult> recallResultNew;

    public RecallThreadResult(RecallConstant.CHANNEL name, List<RecallResult> recallResultNew) {
        this.name = name;
        this.recallResultNew = recallResultNew;
    }

    public List<RecallResult> getRecallResultNew() {
        return recallResultNew;
    }

    public void setRecallResultNew(List<RecallResult> recallResultNew) {
        this.recallResultNew = recallResultNew;
    }

    public RecallConstant.CHANNEL getName() {
        return name;
    }

    public void setName(RecallConstant.CHANNEL name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RecallThreadResult{" +
                "name=" + name +
                '}';
    }
}
