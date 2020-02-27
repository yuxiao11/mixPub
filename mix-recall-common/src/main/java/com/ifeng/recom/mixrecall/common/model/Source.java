package com.ifeng.recom.mixrecall.common.model;

import java.util.Comparator;

/**
 * Created by geyl on 2017/10/23.
 * 用户画像中的媒体
 */
public class Source {
    private String name;
    private Double score;

    public Source() {
    }

    public Source(String name, Double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }


    public static class ScoreComparator implements Comparator<Source> {
        @Override
        public int compare(Source o1, Source o2) {
            return o2.getScore().compareTo(o1.getScore());
        }
    }


    @Override
    public String toString() {
        return "Source{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
