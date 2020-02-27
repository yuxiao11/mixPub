package com.ifeng.recom.mixrecall.negative;

import java.io.Serializable;

/**
 * Created by jibin on 2017/5/8.
 */
public class Item implements Serializable, Cloneable {

    private static final long serialVersionUID = 4734303559628442938L;

    /**
     * 内容类型（新闻、视频、广告等）
     */
    private String itemtype;
    /**
     * 内容id
     */
    private String id;

    /**
     * 原始id（调用方传过来的id可能和内容画像中的id不一致，需要转换，保存原始id使用）
     */
    private String originId;
    /**
     * simid
     */
    private String simid;
    /**
     * 内容来源（归属频道等）
     */
    private String itemsource;
    /**
     * 召回策略（算法通道）
     */
    private String rectype;
    /**
     * 策略权重
     */
    private String weight;
    /**
     * 本次召回排序位置
     */
    private String pos;
    /**
     * 新闻入库时归属频道
     */
    private String ch;
    /**
     * 推荐引擎
     */
    private String engine;
    /**
     * 检索请求来源频道
     */
    private String sch;
    /**
     * 本次曝光时刻在所有item相对位置
     */
    private String wholepos;
    /**
     * ctr计算结果的质量分
     */
    private String q;




    public String getItemtype() {
        return itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSimid() {
        return simid;
    }

    public void setSimid(String simid) {
        this.simid = simid;
    }

    public String getItemsource() {
        return itemsource;
    }

    public void setItemsource(String itemsource) {
        this.itemsource = itemsource;
    }

    public String getRectype() {
        return rectype;
    }

    public void setRectype(String rectype) {
        this.rectype = rectype;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getSch() {
        return sch;
    }

    public void setSch(String sch) {
        this.sch = sch;
    }

    public String getWholepos() {
        return wholepos;
    }

    public void setWholepos(String wholepos) {
        this.wholepos = wholepos;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

}
