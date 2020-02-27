package com.ifeng.recom.mixrecall.common.constant;

/**
 * 文章内容类型的特殊标记，用来做过滤使用
 */
public enum PartCategoryEnum {
    miniVideo("miniVideo"), //小视频

    ppLive("ppLive"),  //泡泡直播

    ;


    private final String value;

    PartCategoryEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
