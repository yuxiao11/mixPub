package com.ifeng.recom.mixrecall.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by geyl on 2017/11/16.
 */
public class CotagSims {
    @SerializedName("b")
    private Source originalCotag;

    @SerializedName("ex")
    private List<Source> extendCotagList;

    public CotagSims() {
    }

    public CotagSims(Source originalCotag, List<Source> extendCotagList) {
        this.originalCotag = originalCotag;
        this.extendCotagList = extendCotagList;
    }

    public Source getOriginalCotag() {
        return originalCotag;
    }

    public void setOriginalCotag(Source originalCotag) {
        this.originalCotag = originalCotag;
    }

    public List<Source> getExtendCotagList() {
        return extendCotagList;
    }

    public void setExtendCotagList(List<Source> extendCotagList) {
        this.extendCotagList = extendCotagList;
    }

    @Override
    public String toString() {
        return "CotagSims{" +
                "originalCotag=" + originalCotag +
                ", extendCotagList=" + extendCotagList +
                '}';
    }
}
