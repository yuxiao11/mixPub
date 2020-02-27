package com.ifeng.recom.mixrecall.common.model;

import java.util.List;
import java.util.Map;

/**
 * Created by geyl on 2018/1/5.
 */
public class UserCF {
    private String uid;
    private Map<Integer, List<String>> neighborClick;

    public UserCF(String uid, Map<Integer, List<String>> neighborClick) {
        this.uid = uid;
        this.neighborClick = neighborClick;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<Integer, List<String>> getNeighborClick() {
        return neighborClick;
    }

    public void setNeighborClick(Map<Integer, List<String>> neighborClick) {
        this.neighborClick = neighborClick;
    }

    @Override
    public String toString() {
        return "UserCF{" +
                "uid='" + uid + '\'' +
                ", neighborClick=" + neighborClick +
                '}';
    }
}
