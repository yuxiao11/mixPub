package com.ifeng.recom.mixrecall.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by geyl on 2017/10/23.
 * 用户画像中的SourceSims
 */
public class SourceSims {
    @SerializedName("b")
    Source originalSource;

    @SerializedName("ex")
    List<Source> extendSourceList;



    public SourceSims() {
    }

    public SourceSims(Source originalSource, List<Source> extendSourceList) {
        this.originalSource = originalSource;
        this.extendSourceList = extendSourceList;
    }

    public Source getOriginalSource() {
        return originalSource;
    }

    public void setOriginalSource(Source originalSource) {
        this.originalSource = originalSource;
    }

    public List<Source> getExtendSourceList() {
        return extendSourceList;
    }

    public void setExtendSourceList(List<Source> extendSourceList) {
        this.extendSourceList = extendSourceList;
    }

    @Override
    public String toString() {
        return "SourceSims{" +
                "originalSource=" + originalSource +
                ", extendSourceList=" + extendSourceList +
                '}';
    }
}
