package com.ifeng.recom.mixrecall.common.model;

import java.io.Serializable;

/**
 * Created by zhaohh on 2017/7/26.
 */
public class FeatureWord implements Serializable {
    private static final long serialVersionUID = -733098528422392124L;
    //feature中词元素
    public String word;
    public String type;
    public double weight;
    //combinetag中词元素
    public String level1;
    public String type1;
    public String level2;
    public String type2;
    public double cotagweight;
    public String tagStr;
    //构造feature实体
    public FeatureWord(String word, String type, double weight) {
        this.word = word;
        this.type = type;
        this.weight = weight;
    }
    //构造cotag实体
    public FeatureWord(String level1, String type1, String level2, String type2, double cotagweight , String tagStr) {
        this.level1 = level1;
        this.type1 = type1;
        this.level2 = level2;
        this.type2 = type2;
        this.cotagweight = cotagweight;
        this.tagStr = tagStr;
    }
    public String toString() {
        return "(" + word + "," + type + "," + weight + ")";
    }
    public String cotagtoString() {
        return "(" + level1 + "," + type1 + ","  + level2 + "," + type2 + ","+ cotagweight + tagStr+ ")";
    }
}
