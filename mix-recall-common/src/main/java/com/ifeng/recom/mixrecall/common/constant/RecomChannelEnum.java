package com.ifeng.recom.mixrecall.common.constant;


/**
 * 头条频道和推荐频道的流量区分标记
 * recomChannel=recom
 * recomChannel=headline
 */
public enum RecomChannelEnum {


    /**
     * 视频app
     */
    videoapp("videoapp"),

    /**
     *视频频道
     */
    videochannel("videochannel"),

    /**
     * 推荐频道标记
     */
    recom("recom"),

    /**
     * 头条频道标记
     */
    headline("headline"),

    /**
     * 机器push  葛亚鲁
     */
    machinepush("machinepush"),


    /**
     * 关注频道
     */
    momentsnew("momentsnew"),


    ;

    private String value;

    private RecomChannelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
