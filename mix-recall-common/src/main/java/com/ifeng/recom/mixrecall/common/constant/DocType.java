package com.ifeng.recom.mixrecall.common.constant;

/**
 * Created by geyl on 2017/11/07.
 */
public enum DocType {

    DOC("doc"),
    DOCPIC("docpic"),
    SLIDE("slide"),
    VIDEO("video"),

    //为ucb试探用
    VIDEO_SC("video_sc"),
    DOC_SC("doc_sc"),
    SVIDEO("svideo"),;

    private final String value;

    DocType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
