package com.ifeng.recom.mixrecall.common.model.item;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * 召回最终的返回结果
 */
@Getter
@Setter
public class Index4User implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(Index4User.class);


    //------------------公共字段-----------------------------------------
    /**
     * docid
     */
    private String i;
    /**
     * ctr score
     */
    private String c;

    /**
     * protobuf的压缩信息 (召回透传详细信息字段)
     */
    private String p;


    //------------------以下是mix召回使用的字段，涉及面较多-----------------------------------------
    /**
     * strategy推荐策略，分流标记使用  (召回透传详细信息字段)
     */
    private String s;
    /**
     * reason 召回通道  (召回透传详细信息字段)
     */
    private String r;

    /**
     * 召回通道列表  （多通道召回时会有）
     */
    private List<String> ch;

    /**
     * recallTag多值召回标签  (召回透传详细信息字段)
     */
    private String rT;

    /**
     * debug信息  只有debug用户添加(召回透传详细信息字段)
     */
    private String d;

    /**
     * hotBoost 热度值(召回透传详细信息字段)
     */
    private Double h;

    /**
     * 添加多通道的时候的所有的tags
     */
    private List<String> tags;


    //------------------以下是头条引擎自己使用的字段-----------------------------------------
    /**
     * q值 video排序使用(头条引擎自己使用的字段)
     */
    @Deprecated
    private String q;

    /**
     * ctr old 原有的CTR值(头条引擎自己使用的字段)
     */
    @Deprecated
    private String co;

    /**
     * 目前为冷启动试探ucb 专用 透传给头条自己使用
     */

    private Double u;

    public Index4User() {

    }

    public Index4User(String i) {
        this.i = i;
    }


    @Override
    public Index4User clone() {
        Index4User index4User = null;
        try {
            index4User = (Index4User) super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("CloneNotSupportedException", e);
        }
        return index4User;
    }
}
