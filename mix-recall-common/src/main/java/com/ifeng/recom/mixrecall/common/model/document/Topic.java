package com.ifeng.recom.mixrecall.common.model.document;

import java.io.Serializable;

/**
 * Created by geyl on 2018/1/25.
 */
public class Topic implements Serializable {
    private String name;
    private Float score;

    public Topic() {
    }

    public Topic(String name, Float score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
