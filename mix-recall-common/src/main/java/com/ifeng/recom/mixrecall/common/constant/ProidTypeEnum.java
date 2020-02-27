package com.ifeng.recom.mixrecall.common.constant;

/**
 */
public enum ProidTypeEnum {
    ifengnewslite("ifengnewslite"),

    ifengnewsdiscovery("ifengnewsdiscovery"),  //探索版

    ifengnewsgold("ifengnewsgold"),  //金头条

    ifengnewssdk("ifengnewssdk"),

    ifengnews("ifengnews");

    private String value;

    ProidTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
