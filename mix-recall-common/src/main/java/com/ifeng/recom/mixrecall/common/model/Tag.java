package com.ifeng.recom.mixrecall.common.model;

import java.io.Serializable;

/**
 * Created by geyl on 2017/8/16.
 */
public class Tag implements Serializable, Cloneable, Comparable<Tag> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private String name;

    //用户画像中的得分
    private double uscore;

    //用户画像中的字段
    private String utype;
    //文章画像中的类型
    private String dtype;
    //文章画像的得分
    private double dscore;

    // 调整权重 归一化以后的 权重
    private double weight;


    public Tag() {
    }

/*    public Tag(String name,double dscore, String dtype) {
        this.name = name;
        this.dtype=dtype;
        this.dscore=dscore;
    }

    public Tag(String name, String utype, double uscore) {
        this.name = name;
        this.utype=utype;
        this.uscore = uscore;
    }*/


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUscore() {
        return uscore;
    }

    public void setUscore(double uscore) {
        this.uscore = uscore;
    }

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public double getDscore() {
        return dscore;
    }

    public void setDscore(double dscore) {
        this.dscore = dscore;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public Tag clone() throws CloneNotSupportedException {

        Tag clone = null;
        clone = (Tag) super.clone();
        return clone;
    }

    @Override
    public int compareTo(Tag o) {
        double score1 = this.uscore;
        double score2 = o.uscore;
        if (score1 > score2) {
            return -1;
        } else if (score1 == score2) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(obj instanceof Tag){
            Tag a=(Tag)obj;
            if(this.name.equals(a.name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", uscore=" + uscore +
                ", utype='" + utype + '\'' +
                ", weight=" + weight +
                '}';
    }
}
