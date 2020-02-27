package com.ifeng.recom.mixrecall.common.model;

import com.ifeng.recom.mixrecall.common.constant.WhyReason;

import java.io.Serializable;

public class Why implements Serializable, Cloneable {
    //示例
    private String strategy; //推荐策略                                                                                                            暂无
    private WhyReason reason;   //推荐原因                                   mix             corec (正反馈)       local(本地)      SupplyHot
    private String source;   //推荐通道                                 preload          preload              preload
    private String sensitive;  //时效或长效文章                       limitTime(longTime)  longTime相当于 PerfectNew  limitTime相当于preload
    private String tag;     //文章中在通道中被具体tag召回的标签名称           科技                                   CF,DeepChel等                   北京市
    private Integer tagRank;  //这个tag召回的这篇文章的排名顺序             10               5                     6
    private Integer tagnum;   //这个tag召回的文章总数量                    100              100                  100
    private Double tagsimScore; //这篇文章和这个tag的相关度                0.6              cf若没有则为null        0.8
    private Double hotBoost;//热度值                                      0.3
    private String piplineReason;


    public Object clone(){
        Why w=null;
        try{
            w=(Why)super.clone();
        }catch(Exception e){

        }
        return w;
    }


    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public WhyReason getReason() {
        return reason;
    }

    public void setReason(WhyReason reason) {
        this.reason = reason;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSensitive() {
        return sensitive;
    }

    public void setSensitive(String sensitive) {
        this.sensitive = sensitive;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getTagRank() {
        return tagRank;
    }

    public void setTagRank(Integer tagRank) {
        this.tagRank = tagRank;
    }

    public Integer getTagnum() {
        return tagnum;
    }

    public void setTagnum(Integer tagnum) {
        this.tagnum = tagnum;
    }

    public Double getTagsimScore() {
        return tagsimScore;
    }

    public void setTagsimScore(Double tagsimScore) {
        this.tagsimScore = tagsimScore;
    }

    public Double getHotBoost() {
        return hotBoost;
    }

    public void setHotBoost(Double hotBoost) {
        this.hotBoost = hotBoost;
    }

    public String getPiplineReason() {
        return piplineReason;
    }

    public void setPiplineReason(String piplineReason) {
        this.piplineReason = piplineReason;
    }

    @Override
    public String toString() {
        return "Why{" +
                "strategy='" + strategy + '\'' +
                ", reason='" + reason + '\'' +
                ", source='" + source + '\'' +
                ", sensitive='" + sensitive + '\'' +
                ", tag='" + tag + '\'' +
                ", tagRank=" + tagRank +
                ", tagnum=" + tagnum +
                ", tagsimScore=" + tagsimScore +
                ", hotBoost=" + hotBoost +
                ", piplineReason='" + piplineReason + '\'' +
                '}';
    }
}
