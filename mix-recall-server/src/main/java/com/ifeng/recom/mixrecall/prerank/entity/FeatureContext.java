package com.ifeng.recom.mixrecall.prerank.entity;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;

import com.ifeng.recom.mixrecall.prerank.tools.CtrSmoothParams;
import com.ifeng.recom.mixrecall.prerank.tools.CtrSmoothParamsNew ;


import java.io.Serializable;
import java.util.Map;
import java.util.Set;


/**
 * Created by zhaozp on 2017/5/11.
 */
public class FeatureContext implements Serializable {

    private static final long serialVersionUID = -4763622455213386182L;
    //基础上下文
    private BasicContext basicContext;
    //    //用户画像
    private UserModel userProfile;
    //    //内容画像
    private Document document;
    //统计数据
    private Map<String,Map<String,Double>> StatsMap;
    // 文章simId对应的历史CTR以及细分历史CTR
    private double[] historyCtrArray;

    private Map<String,String> paramMap;

    private RecallResult recallResult;

    // 文章媒体评级
    private String itemMediaEvalLevel;

    // 文章simid所对应的平均阅读时间（中位数）
    private double itemMedianDuration;

    // 文章历史CTR的平滑参数
    private CtrSmoothParams ctrSmoothParams;

    // 新的文章历史CTR平滑参数
    private CtrSmoothParamsNew ctrSmoothParamsNew;


    /**
     * 返回阅读时长权重
     * @param considerDurationWeight
     * @return
     */
    public double getDurationWeight(boolean considerDurationWeight) {
        if (considerDurationWeight) {
            double durationInMinutes  = 1.0;
            if (itemMedianDuration < 0 || itemMedianDuration > 10000) {
                durationInMinutes = 0.75;                           // duration中位数为 45s
            } else if (itemMedianDuration >= 60*20.0) {  // 大于20 min的点击均视为20 min
                durationInMinutes = 20.0;
            } else {
                durationInMinutes = (itemMedianDuration + 1) / 60.0;
            }
//            double weight = Math.cbrt(durationInMinutes);     // 开立方
            // 分段计算时长权重
            double weight = 1.0;
            double const1 = Math.sqrt(1.0/6);
            double const2 = Math.pow(1.0/6, 0.05);
            if (durationInMinutes >= 0 && durationInMinutes <= 1.0/6) {             // 10秒钟以内，为低质量点击，开20次方，排序主要由CTR决定
                weight = Math.pow(durationInMinutes, 0.05) - 0.5;
            } else if (durationInMinutes > 1.0/6 && durationInMinutes <= 5.0) {     // 10秒到5分钟之间，权重为时长开平方
                weight = Math.sqrt(durationInMinutes) - const1 + const2 - 0.5;
            } else {
                weight = Math.cbrt(durationInMinutes) - Math.cbrt(5.0) + Math.sqrt(5.0) - const1 + const2 - 0.5;    // 5分钟以上，权重为时长开立方
            }
            return weight;
        } else {
            return 1.0;
        }
    }


    /***
     * 用户和新闻的画像Match结果，只计算一次保存起来，防止重复计算
      */
    // 长期特征匹配
    private UIPortraitMatchResult userT1MatchResult = null;
    private UIPortraitMatchResult userT2MatchResult = null;
    private UIPortraitMatchResult userT3MatchResult = null;
    // 短期特征匹配
    private UIPortraitMatchResult userRecentT1MatchResult = null;
    private UIPortraitMatchResult userRecentT2MatchResult = null;
    private UIPortraitMatchResult userRecentT3MatchResult = null;
    // 实时特征匹配
    private UIPortraitMatchResult userLastT1MatchResult = null;
    private UIPortraitMatchResult userLastT2MatchResult = null;
    private UIPortraitMatchResult userLastT3MatchResult = null;
    // Slide特征匹配
    private UIPortraitMatchResult userSlideT1MatchResult = null;
    private UIPortraitMatchResult userSlideT2MatchResult = null;
    private UIPortraitMatchResult userSlideT3MatchResult = null;
    // Video特征匹配
    private UIPortraitMatchResult userVideoT1MatchResult = null;
    private UIPortraitMatchResult userVideoT2MatchResult = null;
    private UIPortraitMatchResult userVideoT3MatchResult = null;
    // 实时dis特征匹配
    private UIPortraitMatchResult userLastDisT1MatchResult = null;
    private UIPortraitMatchResult userLastDisT2MatchResult = null;
    private UIPortraitMatchResult userLastDisT3MatchResult = null;

    /**
     * 判断用户和新闻画像是否有一个为null
     * @param userProfile
     * @param itemProfile
     * @return
     */
    private boolean eitherNull(UserModel userProfile, Document itemProfile) {
        if (userProfile == null ) {
            return true;
        }
        if (itemProfile == null) {
            return true;
        }
        return false;
    }



    /***
     * 用户和新闻画像的cotag匹配结果，只计算一次保存起来，防止重复计算
     */
    private UIPortraitMatchResult cotagMatchResult = null;
    private UIPortraitMatchResult recentCotagMatchResult = null;
    private UIPortraitMatchResult lastCotagMatchResult = null;
    private UIPortraitMatchResult lastDisCotagMatchResult = null;

    public UIPortraitMatchResult getUserCotagMatchResult() {
        if (cotagMatchResult != null) {
            return cotagMatchResult;
        }
        if (eitherNull(userProfile, document)) {
            return null;
        }

        this.cotagMatchResult = getUserItemCotagMatch(userProfile.getUcombineTagTn(), document.getcoTagSet());
        return cotagMatchResult;
    }
    public UIPortraitMatchResult getUserRecentCotagMatchResult() {
        if (recentCotagMatchResult != null) {
            return recentCotagMatchResult;
        }
        if (eitherNull(userProfile, document)) {
            return null;
        }

        this.recentCotagMatchResult = getUserItemCotagMatch(userProfile.getRecent_ucombineTagTn(), document.getcoTagSet());
        return recentCotagMatchResult;
    }
    public UIPortraitMatchResult getUserLastCotagMatchResult() {
        if (lastCotagMatchResult != null) {
            return lastCotagMatchResult;
        }
        if (eitherNull(userProfile, document)) {
            return null;
        }
        this.lastCotagMatchResult = getUserItemCotagMatch(userProfile.getLast_ucombineTagTn(), document.getcoTagSet());
        return lastCotagMatchResult;
    }
    public UIPortraitMatchResult getUserLastDisCotagMatchResult() {
        if (lastDisCotagMatchResult != null) {
            return lastDisCotagMatchResult;
        }
        if (eitherNull(userProfile, document)) {
            return null;
        }
        this.lastDisCotagMatchResult = getUserItemCotagMatch(userProfile.getLast_dis_ucombineTagTn(), document.getcoTagSet());
        return lastDisCotagMatchResult;
    }

    /**
     * 获取用户画像和新闻画像的cotag匹配详细结果
     * @param userFeatureTn
     * @param cotagSet
     * @return
     */
    private UIPortraitMatchResult getUserItemCotagMatch(UserModel.UserFeatureTn userFeatureTn, Set<String> cotagSet) {
        // 任意画像字段为null
        if (userFeatureTn == null || cotagSet == null || userFeatureTn.getFeatureWordList().size() == 0 || cotagSet.size() == 0) {
            return null;
        }

        // 作匹配
        int userFeatureSize = userFeatureTn.getFeatureWordList().size();
        UIPortraitMatchResult matchResult = new UIPortraitMatchResult(userFeatureTn.isNewPortrait(), userFeatureSize);
        String userWord;
        UserModel.UserFeatureWord userFeatureWord;
        for (int index = 0; index < userFeatureSize; index ++) {
            userFeatureWord = userFeatureTn.getFeatureWordList().get(index);
            userWord = userFeatureWord.getWord();
            if (userWord != null && cotagSet.contains(userWord)) {
                double userWeight = userFeatureWord.getWeight();
                int matchPos = index;
                matchResult.addMatch(userWeight, matchPos);
            }
        }
        return matchResult;
    }

    private UIPortraitMatchResult dopicMediaMatchResult = null;
    private UIPortraitMatchResult recentDocpicMediaMatchResult = null;
    private UIPortraitMatchResult videoMediaMatchResult = null;
    private UIPortraitMatchResult recentVideoMediaMatchResult = null;

    public UIPortraitMatchResult getDopicMediaMatchResult() {
        if (dopicMediaMatchResult != null) {
            return dopicMediaMatchResult;
        }
        if (eitherNull(userProfile, document)) {
            return null;
        }

        this.dopicMediaMatchResult = getUserItemSourceMatch(userProfile.getDocpicMediaFeature(), document.getSource());
        return lastDisCotagMatchResult;
    }

    public UIPortraitMatchResult getRecentVideoMediaMatchResult() {
        if (recentDocpicMediaMatchResult != null) {
            return recentDocpicMediaMatchResult;
        }
        if (eitherNull(userProfile, document)) {
            return null;
        }
        
        this.recentDocpicMediaMatchResult = getUserItemSourceMatch(userProfile.getRecentDocpicMediaFeature(), document.getSource());
        return recentDocpicMediaMatchResult;
    }

    public UIPortraitMatchResult getVideoMediaMatchResult() {
        if (videoMediaMatchResult != null) {
            return videoMediaMatchResult;
        }
        if (eitherNull(userProfile, document)) {
            return null;
        }
        this.videoMediaMatchResult = getUserItemSourceMatch(userProfile.getVideoMediaFeature(), document.getSource());
        return videoMediaMatchResult;
    }

    public UIPortraitMatchResult getRecentDocpicMediaMatchResult() {
        if (recentVideoMediaMatchResult != null) {
            return recentVideoMediaMatchResult;
        }
        if (eitherNull(userProfile, document)) {
            return null;
        }

        this.recentVideoMediaMatchResult = getUserItemSourceMatch(userProfile.getRecentVideoMediaFeature(), document.getSource());
        return recentVideoMediaMatchResult;
    }


    private UIPortraitMatchResult getUserItemSourceMatch(UserModel.UserFeatureTn userFeatureTn, String source) {
        if(userFeatureTn == null || source == null) {
            return null;
        }
        // 作匹配
        int userFeatureSize = userFeatureTn.getFeatureWordList().size();
        UIPortraitMatchResult matchResult = new UIPortraitMatchResult(userFeatureTn.isNewPortrait(), userFeatureSize);
        String userWord;
        UserModel.UserFeatureWord userFeatureWord;
        for (int index = 0; index < userFeatureSize; index ++) {
            userFeatureWord = userFeatureTn.getFeatureWordList().get(index);
            userWord = userFeatureWord.getWord();
            if (userWord != null && source.equals(userWord)) {
                double userWeight = userFeatureWord.getWeight();
                int matchPos = index;
                matchResult.addMatch(userWeight, matchPos);
            }
        }
        return matchResult;
    }

    public FeatureContext(){
        super();
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public RecallResult getRecallResult() {
        return recallResult;
    }

    public void setRecallResult(RecallResult recallResult) {
        this.recallResult = recallResult;
    }

    public BasicContext getBasicContext() {
        return basicContext;
    }

    public void setBasicContext(BasicContext basicContext) {
        this.basicContext = basicContext;
    }

    public UserModel getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserModel userProfile) {
        this.userProfile = userProfile;
    }

    public Document getItemDocument() {
        return this.document;
    }

    public void setItemDocument(Document document) {
        this.document = document;
    }

    public double[] getHistoryCtrArray() {
        return historyCtrArray;
    }

    public void setHistoryCtrArray(double[] historyCtrArray) {
        this.historyCtrArray = historyCtrArray;
    }

    public Map<String, Map<String, Double>> getStatsMap() {
        return StatsMap;
    }

    public void setStatsMap(Map<String, Map<String, Double>> statsMap) {
        StatsMap = statsMap;
    }

    public String getItemMediaEvalLevel() {
        return itemMediaEvalLevel;
    }
    public void setItemMediaEvalLevel(String itemMediaEvalLevel) {
        this.itemMediaEvalLevel = itemMediaEvalLevel;
    }

    public double getItemMedianDuration() {
        return itemMedianDuration;
    }
    public void setItemMedianDuration(double itemMedianDuration) {
        this.itemMedianDuration = itemMedianDuration;
    }

    public CtrSmoothParams getCtrSmoothParams() {
        return ctrSmoothParams;
    }
    public void setCtrSmoothParams(CtrSmoothParams ctrSmoothParams) {
        this.ctrSmoothParams = ctrSmoothParams;
    }

    public CtrSmoothParamsNew getCtrSmoothParamsNew() {
        return ctrSmoothParamsNew;
    }
    public void setCtrSmoothParamsNew(CtrSmoothParamsNew ctrSmoothParamsNew) {
        this.ctrSmoothParamsNew = ctrSmoothParamsNew;
    }
}
