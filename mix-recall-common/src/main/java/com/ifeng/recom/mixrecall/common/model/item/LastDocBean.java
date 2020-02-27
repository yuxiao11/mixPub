package com.ifeng.recom.mixrecall.common.model.item;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户最近点击的docid
 * Created by jibin on 2017/10/23.
 */
@Getter
@Setter
public class LastDocBean {
    private String docId;
    private String simId;
    private String source;
    /**
     * 观看时长
     */
    private String time;


    public LastDocBean() {
    }

    public LastDocBean(String docId, String simId, String source) {
        this.docId = docId;
        this.simId = simId;
        this.source = source;
    }

    public LastDocBean(String docId, String simId) {
        this.docId = docId;
        this.simId = simId;
    }

    @Override
    public String toString() {
        return "LastDocBean{" +
                "docId='" + docId + '\'' +
                ", simId='" + simId + '\'' +
                ", source='" + source + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
