package com.ifeng.recom.mixrecall.common.service;

import java.util.List;

public class BloomResult {
    private String status;
    private List<String> simids;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getSimids() {
        return simids;
    }

    public void setSimids(List<String> simids) {
        this.simids = simids;
    }

    @Override
    public String toString() {
        return "BloomResult{" +
                "status='" + status + '\'' +
                ", simids=" + simids +
                '}';
    }
}
