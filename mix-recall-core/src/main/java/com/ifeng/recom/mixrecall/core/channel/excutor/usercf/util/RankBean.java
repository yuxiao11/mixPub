package com.ifeng.recom.mixrecall.core.channel.excutor.usercf.util;

import java.util.Comparator;
import java.util.Set;

/**
 * Created by geyl on 2018/1/8.
 */
public class RankBean {
    private String docId;
    private double score;
    private Set<Integer> posL;

    public RankBean(String docId, double score, Set<Integer> posL) {
        this.docId = docId;
        this.score = score;
        this.posL = posL;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Set<Integer> getPosL() {
        return posL;
    }

    public void setPosL(Set<Integer> posL) {
        this.posL = posL;
    }

    @Override
    public String toString() {
        return "RankBean{" +
                "docId='" + docId + '\'' +
                ", score=" + score +
                ", posL=" + posL +
                '}';
    }

    public static class scoreComparator implements Comparator<RankBean> {
        @Override
        public int compare(RankBean o1, RankBean o2) {
            return Double.compare(o2.getScore(), o1.getScore());
        }
    }
}

