package com.ifeng.recom.mixrecall.common.model.item;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * Created by yeben on 2018/3/14.
 */
public class ExposeInfo implements Serializable {


    private static final long serialVersionUID = 1L;

    /** 文章docid(imcp_id或url)  */
    private String i;

    /** 文章是否点击 */
    private boolean cl;

    /** 数据为json格式的个性化召回原因，如果没有则不传 */
    private String y;

    /** ReadableFeature，文章ReadableFeature字段 */
    private String f;

    /** 文章曝光时间 */
    private long t;

    /** cotagSet */
    private Set<String> cotags;

    public List<String> getLdatopics() {
        return ldatopics;
    }

    public void setLdatopics(List<String> ldatopics) {
        this.ldatopics = ldatopics;
    }

    /** lda_topic*/
    private List<String> ldatopics;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getSubcates() {
        return subcates;
    }

    public void setSubcates(List<String> subcates) {
        this.subcates = subcates;
    }

    /** categories */
    private List<String> categories;

    /** subcates */
    private List<String> subcates;

    public Set<String> getCotags() {
        return cotags;
    }

    public void setCotags(Set<String> cotags) {
        this.cotags = cotags;
    }

    public ExposeInfo() {
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public boolean isCl() {
        return cl;
    }

    public void setCl(boolean cl) {
        this.cl = cl;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }
}
