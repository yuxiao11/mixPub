package com.ifeng.recom.mixrecall.common.model;

import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by geyl on 2018/1/19.
 */
@Getter
@Setter
public class RecallResult {

    /**
     * 结果doc
     */
    public Document document;
    /**
     * recallTag
     */
    private String recallTag;

    /**
     * 通道组合策略
     */
    private String strategy;

    /**
     * 单个通道的召回原因
     */
    private WhyReason whyReason;

    /**
     * 召回通道原因列表，所有的召回通道
     */
    private List<WhyReason> channels;

    /**
     * debug用户的debug信息
     */
    private Map<String, String> debugInfo;

    /**
     * ctr值
     */
    private Double ctr;


    /**
     * user cf score
     */
    private Double userCFScore;


    private Double positionWeight;


    private int PreloadPosition;

    private int position;//每篇文章在预加载中真实的绝对位置，PreloadPosition是经过个性化过滤后的相对位置

    private Double ucb;

    /**
     * 添加CTR 分值
     */
    private Double ctrScore;

    /**
     * 添加多通道的时候的所有的tags
     */
    private Set<String> tags;

    public RecallResult() {
    }

    public RecallResult(Document document, String recallTag, WhyReason whyReason) {
        this.document = document;
        this.recallTag = recallTag;
        this.whyReason = whyReason;
    }

    /**
     * ctr score comparator
     */
    public static class CtrScoreComparator implements Comparator<RecallResult> {
        @Override
        public int compare(RecallResult o1, RecallResult o2) {
            if (o1.getCtr() == null && o2.getCtr() == null) {
                return 0;
            }
            if (o1.getCtr() == null) {
                return 1;
            }
            if (o2.getCtr() == null) {
                return -1;
            }

            return Double.compare(o2.getCtr(), o1.getCtr());
        }
    }

    /**
     * ctr score comparator
     */
    public static class UserCfScoreComparator implements Comparator<RecallResult> {
        @Override
        public int compare(RecallResult o1, RecallResult o2) {
            if (o1.getUserCFScore() == null && o2.getUserCFScore() == null) {
                return 0;
            }
            if (o1.getUserCFScore() == null) {
                return 1;
            }
            if (o2.getUserCFScore() == null) {
                return -1;
            }

            return Double.compare(o2.getUserCFScore(), o1.getUserCFScore());
        }
    }


    public static class WeightAndPreloadPositionComparator implements Comparator<RecallResult> {
        @Override
        public int compare(RecallResult o1, RecallResult o2) {
            int rt;
            if (o1.getPositionWeight() == null || o2.getPositionWeight() == null) {
                rt = 0;
            } else {
                rt = Double.compare(o1.getPositionWeight(), o2.getPositionWeight());
            }

            if (rt == 0) {
                return Integer.compare(o1.getPreloadPosition(), o2.getPreloadPosition());
            } else {
                return rt;
            }
        }
    }

    @Override
    public String toString() {
        return "RecallResult{" +
                "document=" + document +
                ", recallTag='" + recallTag + '\'' +
                ", whyReason=" + whyReason +
                ", ctr=" + ctr +
                '}';
    }
}
