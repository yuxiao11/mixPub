package com.ifeng.recom.mixrecall.prerank.featureV1030;



import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.negative.StringUtils;
import com.ifeng.recom.mixrecall.prerank.FeatureItem;
import com.ifeng.recom.mixrecall.prerank.entity.BasicContext;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import com.ifeng.recom.mixrecall.prerank.entity.UIPortraitMatchResult;
import com.ifeng.recom.mixrecall.prerank.executor.Operator;
import com.ifeng.recom.mixrecall.prerank.tools.DateUtils;

import java.util.*;


public class FMPortraitMatchOperator extends Operator {

    private static final long serialVersionUID = 1909651064988691320L;

    private static final int MAX_DISLIKE_TOPIC_FEATURES = 5;

    public static void main(String[] args) {
        FMPortraitMatchOperator operator = new FMPortraitMatchOperator();
        UserModel userProfile = new UserModel();
        userProfile.setUser_cluster("[{\"bad\":[\"tp96\",\"tp80\",\"tp40\",\"tp37\",\"tp75\",\"tp71\",\"tp76\",\"tp35\",\"tp55\",\"tp31\",\"tp88\",\"tp52\",\"tp87\",\"tp53\",\"tp92\",\"tp77\",\"tp72\",\"tp95\",\"tp98\",\"tp32\",\"tp78\",\"tp91\",\"tp57\",\"tp33\",\"tp93\",\"tp73\",\"tp97\"],\"cate\":\"社会\",\"good\":[\"tp100\"],\"isdeep\":\"1\",\"sens\":\"1\"},{\"bad\":[\"tp97\",\"tp100\",\"tp96\",\"tp77\",\"tp80\",\"tp91\",\"tp31\",\"tp40\",\"tp71\",\"tp78\",\"tp72\",\"tp99\",\"tp36\",\"tp38\",\"tp76\",\"tp95\",\"tp35\",\"tp92\",\"tp55\",\"tp52\",\"tp73\"],\"cate\":\"娱乐\",\"good\":[\"tp98\"],\"sens\":\"1\"},{\"bad\":[\"tp72\",\"tp78\",\"tp93\"],\"cate\":\"萌宠萌娃\",\"good\":[\"tp73\"],\"sens\":\"1\"},{\"bad\":[\"tp93\",\"tp33\"],\"cate\":\"军情\"},{\"bad\":[\"tp32\"],\"cate\":\"教育\"},{\"bad\":[\"tp77\",\"tp72\"],\"cate\":\"国际\"},{\"bad\":[\"tp96\",\"tp76\",\"tp97\",\"tp37\",\"tp92\",\"tp40\",\"tp86\",\"tp51\",\"tp32\",\"tp26\",\"tp60\",\"tp55\",\"tp77\",\"tp52\",\"tp36\",\"tp87\",\"tp90\",\"tp98\",\"tp75\",\"tp27\",\"tp99\",\"tp93\",\"tp91\",\"tp71\",\"tp66\",\"tp80\",\"tp100\",\"tp35\"],\"cate\":\"财经\",\"good\":[\"tp31\"],\"isdeep\":\"1\",\"sens\":\"1\"},{\"bad\":[\"tp76\",\"tp91\",\"tp92\",\"tp80\",\"tp73\",\"tp95\",\"tp75\",\"tp35\",\"tp98\",\"tp78\",\"tp40\",\"tp100\",\"tp97\",\"tp72\",\"tp77\",\"tp32\"],\"cate\":\"体育\",\"good\":[\"tp96\"],\"isdeep\":\"1\",\"sens\":\"1\"},{\"bad\":[\"tp100\"],\"cate\":\"时尚\"},{\"bad\":[\"tp97\",\"tp92\",\"tp93\"],\"cate\":\"科学探索\"},{\"bad\":[\"tp92\",\"tp97\"],\"cate\":\"电影\"},{\"bad\":[\"tp37\",\"tp76\",\"tp40\",\"tp96\",\"tp91\",\"tp80\",\"tp71\",\"tp95\",\"tp35\",\"tp33\",\"tp98\",\"tp31\",\"tp36\",\"tp60\",\"tp97\",\"tp32\",\"tp92\",\"tp77\"],\"cate\":\"时政\"},{\"bad\":[\"tp37\",\"tp97\",\"tp40\",\"tp98\",\"tp96\",\"tp92\",\"tp91\",\"tp36\",\"tp60\",\"tp38\",\"tp93\",\"tp100\",\"tp31\"],\"cate\":\"历史\"},{\"bad\":[\"tp93\"],\"cate\":\"综艺\"},{\"bad\":[\"tp97\",\"tp33\",\"tp53\",\"tp78\",\"tp93\",\"tp73\",\"tp48\",\"tp52\",\"tp32\"],\"cate\":\"搞笑\",\"sens\":\"1\"},{\"bad\":[\"tp92\",\"tp91\",\"tp73\",\"tp98\",\"tp71\",\"tp95\",\"tp93\",\"tp96\",\"tp77\",\"tp97\"],\"cate\":\"旅游\",\"sens\":\"1\"},{\"bad\":[\"tp97\"],\"cate\":\"其他\"},{\"bad\":[\"tp97\",\"tp95\",\"tp35\",\"tp40\",\"tp80\",\"tp76\",\"tp91\",\"tp96\"],\"cate\":\"房产\",\"good\":[\"tp100\",\"tp31\"],\"isdeep\":\"1\"},{\"bad\":[\"tp100\"],\"cate\":\"宗教\"},{\"bad\":[\"tp72\"],\"cate\":\"育儿\"},{\"bad\":[\"tp92\",\"tp72\",\"tp98\",\"tp77\",\"tp36\",\"tp55\",\"tp51\",\"tp35\",\"tp37\",\"tp90\",\"tp73\",\"tp30\",\"tp97\",\"tp31\",\"tp32\",\"tp66\",\"tp86\",\"tp95\",\"tp93\",\"tp75\",\"tp40\",\"tp76\"],\"cate\":\"科技\",\"good\":[\"tp91\",\"tp96\",\"tp100\"],\"isdeep\":\"1\",\"sens\":\"1\"},{\"bad\":[\"tp1\",\"tp5\",\"tp6\",\"tp61\"],\"cate\":\"通用\",\"good\":[\"tp33\",\"tp38\",\"tp39\",\"tp34\"]}]");
        Document itemProfile = new Document();
        itemProfile.setPerformance("{\"rank\":\"tp96\",\"id\":\"cmpp_43050614\"} ");
        List<String> Cate = new ArrayList<>();
        Cate.add("社会");
        itemProfile.setCategory(Cate);
        List<FeatureItem> featureItems = new ArrayList<>();
        long featureId = 1;


    }

    @Override
    public List<FeatureItem> compute(FeatureContext entity, Long featureId, String featureName, int type, String attrDimension) throws Exception {

        List<FeatureItem> featureItems = new ArrayList<FeatureItem>();
        // featureId = 301~306 - 文章cotag与用户长期cotag匹配上的权重与位置
        if (entity == null || entity.getItemDocument() == null  || entity.getUserProfile() == null ) {
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("cotagMatchW1", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("cotagMatchW2", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("cotagMatchW3", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("cotagMatchP1", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("cotagMatchP2", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("cotagMatchP3", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentCotagMatchW1", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentCotagMatchW2", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentCotagMatchW3", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentCotagMatchP1", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentCotagMatchP2", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentCotagMatchP3", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastCotagMatchW1", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastCotagMatchW2", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastCotagMatchP1", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastCotagMatchP2", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastDislikeCotagW1", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastDislikeCotagW2", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastDislikeCotagP1", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastDislikeCotagP2", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("docMediaMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentDocMediaMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("videoMediaMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentVideoMediaMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("scMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentScMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentDislikeScMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastLDAMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastDocScMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastDislikeDocScMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastVideoScMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastDislikeVideoScMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("LDAMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("dislikeLDAMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastCMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("lastDislikeCMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentLDAMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("recentDislikeLDAMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("userClusterDeep", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("userClusterGood", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("userClusterBad", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("userCurDocScMatch", 4));
            addSingleFeatureItem(featureItems, FeatureExtractUtils.getNoneFeature("userCurVidScMatch", 4));
            return featureItems;
        }
        Document headLineItemProfile = entity.getItemDocument();
        UserModel headLineUserProfile = entity.getUserProfile();
        // 文章cotag与用户长期cotag匹配上的权重与位置
        addSingleFeatureItem(featureItems, getMatchWeightFeature("cotagMatchW1", entity.getUserCotagMatchResult(), 1)); //TODO 此处只利用前几个特征进行匹配
        addSingleFeatureItem(featureItems, getMatchWeightFeature("cotagMatchW2", entity.getUserCotagMatchResult(), 2));
        addSingleFeatureItem(featureItems, getMatchWeightFeature("cotagMatchW3", entity.getUserCotagMatchResult(), 3));
        addSingleFeatureItem(featureItems, getMatchPosFeature("cotagMatchP1", entity.getUserCotagMatchResult(), 1));
        addSingleFeatureItem(featureItems, getMatchPosFeature("cotagMatchP2", entity.getUserCotagMatchResult(), 2));
        addSingleFeatureItem(featureItems, getMatchPosFeature("cotagMatchP3", entity.getUserCotagMatchResult(), 3));

        // 文章cotag与用户近期cotag匹配上的权重与位置
        addSingleFeatureItem(featureItems, getMatchWeightFeature("recentCotagMatchW1", entity.getUserRecentCotagMatchResult(), 1));
        addSingleFeatureItem(featureItems, getMatchWeightFeature("recentCotagMatchW2", entity.getUserRecentCotagMatchResult(), 2));
        addSingleFeatureItem(featureItems, getMatchWeightFeature("recentCotagMatchW3", entity.getUserRecentCotagMatchResult(), 3));
        addSingleFeatureItem(featureItems, getMatchPosFeature("recentCotagMatchP1", entity.getUserRecentCotagMatchResult(), 1));
        addSingleFeatureItem(featureItems, getMatchPosFeature("recentCotagMatchP2", entity.getUserRecentCotagMatchResult(), 2));
        addSingleFeatureItem(featureItems, getMatchPosFeature("recentCotagMatchP3", entity.getUserRecentCotagMatchResult(), 3));

        // 文章cotag与用户last cotag匹配上的权重与位置
        addSingleFeatureItem(featureItems, getMatchWeightFeature("lastCotagMatchW1", entity.getUserLastCotagMatchResult(), 1));
        addSingleFeatureItem(featureItems, getMatchWeightFeature("lastCotagMatchW2", entity.getUserLastCotagMatchResult(), 2));
        addSingleFeatureItem(featureItems, getMatchPosFeature("lastCotagMatchP1", entity.getUserLastCotagMatchResult(), 1));
        addSingleFeatureItem(featureItems, getMatchPosFeature("lastCotagMatchP2", entity.getUserLastCotagMatchResult(), 2));

        // 文章cotag与用户last dis cotag匹配上的权重与位置
        addSingleFeatureItem(featureItems, getMatchWeightFeature("lastDislikeCotagW1", entity.getUserLastDisCotagMatchResult(), 1));
        addSingleFeatureItem(featureItems, getMatchWeightFeature("lastDislikeCotagW1", entity.getUserLastDisCotagMatchResult(), 2));
        addSingleFeatureItem(featureItems, getMatchPosFeature("lastDislikeCotagP1", entity.getUserLastDisCotagMatchResult(), 1));
        addSingleFeatureItem(featureItems, getMatchPosFeature("lastDislikeCotagP2", entity.getUserLastDisCotagMatchResult(), 2));

        // 用户分群与文章performance匹配特征
        addSingleFeatureItem(featureItems, getUserClusterMatchDeep("userClusterDeep", headLineItemProfile, headLineUserProfile));
        addSingleFeatureItem(featureItems, getUserClusterMatchGood("userClusterGood", headLineItemProfile, headLineUserProfile));
        addSingleFeatureItem(featureItems, getUserClusterMatchBad("userClusterBad", headLineItemProfile, headLineUserProfile));
        // 用户观看媒体与文章媒体匹配特征
        addSingleFeatureItem(featureItems, getMatchWeightFeature("docMediaMatch", entity.getDopicMediaMatchResult(), 1));
        addSingleFeatureItem(featureItems, getMatchWeightFeature("recentDocMediaMatch", entity.getRecentDocpicMediaMatchResult(), 1));
        addSingleFeatureItem(featureItems, getMatchWeightFeature("videoMediaMatch", entity.getVideoMediaMatchResult(), 1));
        addSingleFeatureItem(featureItems, getMatchWeightFeature("recentVideoMediaMatch", entity.getRecentVideoMediaMatchResult(), 1));
        // 用户sc与文章sc匹配
        // 用户近期sc与文章sc匹配
        // 用户近期dislike sc与文章sc匹配
        if (!("video").equals(headLineItemProfile.getDocType())) {
            addSingleFeatureItem(featureItems, getUIMatchResult("scMatch", headLineUserProfile.getDocpicSubcate(), headLineItemProfile.getScList()));
            addSingleFeatureItem(featureItems, getUIMatchResult("recentScMatch", headLineUserProfile.getRecentDocpicSubcate(), headLineItemProfile.getScList()));
            addSingleFeatureItem(featureItems, getUIDislikeMatchResult("recentDislikeScMatch", headLineUserProfile.getRecentDocpicSubcate(), headLineItemProfile.getScList()));
        } else {
            addSingleFeatureItem(featureItems, getUIMatchResult("scMatch", headLineUserProfile.getVideoSubcate(), headLineItemProfile.getScList()));
            addSingleFeatureItem(featureItems, getUIMatchResult("recentScMatch", headLineUserProfile.getRecentVideoSubcate(), headLineItemProfile.getScList()));
            addSingleFeatureItem(featureItems, getUIDislikeMatchResult("recentDislikeScMatch", headLineUserProfile.getRecentVideoSubcate(), headLineItemProfile.getScList()));
        }
        // 用户last_lda_topic与文章lda_topic匹配
        addSingleFeatureItem(featureItems, getUIMatchResult("lastLDAMatch", headLineUserProfile.getLastLDATopicFeature(), headLineItemProfile.getLdaTopicList()));
        // 用户喜爱的实时docpic_sc与文章sc匹配个数
        // 用户喜爱的实时docpic_sc与文章sc匹配个数
        if (!"video".equals(headLineItemProfile.getDocType())) {
            addSingleFeatureItem(featureItems, getUIMatchResult("lastDocScMatch", headLineUserProfile.getLastDocpicSubcate(), headLineItemProfile.getScList()));
            addSingleFeatureItem(featureItems, getUIDislikeMatchResult("lastDislikeDocScMatch", headLineUserProfile.getLastDocpicSubcate(), headLineItemProfile.getScList()));
        } else {
            addSingleFeatureItem(featureItems, getUIMatchResult("lastVideoScMatch", headLineUserProfile.getLastVideoSubcateTN(), headLineItemProfile.getScList()));
            addSingleFeatureItem(featureItems, getUIDislikeMatchResult("lastDislikeVideoScMatch", headLineUserProfile.getLastVideoSubcateTN(), headLineItemProfile.getScList()));
        }
        // 用户喜爱的长期lda与文章lda匹配个数
        addSingleFeatureItem(featureItems, getUIMatchResult("LDAMatch", headLineUserProfile.getDocpicLDATopicFeature(), headLineItemProfile.getLdaTopicList()));
        // 用户不喜爱的长期lda与文章lda匹配个数
        addSingleFeatureItem(featureItems, getUIDislikeMatchResult("dislikeLDAMatch", headLineUserProfile.getDocpicLDATopicFeature(), headLineItemProfile.getLdaTopicList()));


        // TODO ____________________________________________________________________________________已完成_________________________________________________________________________________________________
        // 用户喜爱的实时cate与文章cate匹配个数
        // 用户不喜爱的实时cate与文章cate比配个数
        if (!"video".equals(headLineItemProfile.getDocType())) {
            addSingleFeatureItem(featureItems, getUIMatchResult("lastCMatch", headLineUserProfile.getLastDocpicCate(), headLineItemProfile.getCateList()));
            addSingleFeatureItem(featureItems, getUIDislikeMatchResult("lastDislikeCMatch", headLineUserProfile.getLastDocpicCate(), headLineItemProfile.getCateList()));
        } else {
            addSingleFeatureItem(featureItems, getUIMatchResult("lastCMatch", headLineUserProfile.getLastVideoCateTN(), headLineItemProfile.getCateList()));
            addSingleFeatureItem(featureItems, getUIDislikeMatchResult("lastDislikeCMatch", headLineUserProfile.getLastVideoCateTN(), headLineItemProfile.getCateList()));
        }
        // 用户喜爱的近期lda与文章lda匹配个数
        addSingleFeatureItem(featureItems, getUIMatchResult("recentLDAMatch", headLineUserProfile.getRecentDocpicLDATopicFeature(), headLineItemProfile.getLdaTopicList()));
        // 用户不喜爱的近期lda与文章lda匹配个数
        addSingleFeatureItem(featureItems, getUIDislikeMatchResult("recentDislikeLDAMatch", headLineUserProfile.getRecentDocpicLDATopicFeature(), headLineItemProfile.getLdaTopicList()));


        //featureId = 334 - 用户当前时刻喜爱的sc与文章sc匹配
        if(!"video".equals(headLineItemProfile.getDocType())) {
            addSingleFeatureItem(featureItems, getCurrentDocSubcate("userCurDocScMatch", entity.getBasicContext(), headLineUserProfile, headLineItemProfile));
        }else {
            addSingleFeatureItem(featureItems, getCurrentVideoSubcate("userCurVidScMatch", entity.getBasicContext(), headLineUserProfile, headLineItemProfile));
        }

        return featureItems;
    }

    private void addFeatureItems(List<FeatureItem> featureItems, List<FeatureItem> subList) {
        if (subList != null && subList.size() > 0) {
            featureItems.addAll(subList);
        }
    }

    private void addSingleFeatureItem(List<FeatureItem> featureItems, FeatureItem featureItem) {
        if (featureItem != null) {
            featureItems.add(featureItem);
        }
    }


    /**
     * 第K个匹配上的特征权重
     *
     * @param featureName
     * @param matchResult
     * @param Kth
     * @return
     */
    private FeatureItem getMatchWeightFeature(String featureName, UIPortraitMatchResult matchResult, int Kth) {
        if (matchResult == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        double matchWeight = getMatchWeightValue(matchResult, Kth);
        if (matchWeight < 0 || matchWeight > 1.0) {
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        int weightLevel = getWeightLevel(matchWeight); //将特征进行分级

        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(4);
        featureItem.setValueStr(String.valueOf(weightLevel));
        return featureItem;
    }

    /**
     * 第K个匹配上的特征位置
     *
     * @param featureName
     * @param matchResult
     * @param Kth
     * @return
     */
    private FeatureItem getMatchPosFeature(String featureName, UIPortraitMatchResult matchResult, int Kth) {
        if (matchResult == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        double matchPos = getMatchPosValue(matchResult, Kth);
        if (matchPos < 0 || matchPos > 1.0) {
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        int posLevel = getWeightLevel(matchPos);

        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(4);
        featureItem.setValueStr(String.valueOf(posLevel));
        return featureItem;
    }

    private double getMatchWeightValue(UIPortraitMatchResult matchResult, int Kth) {
        double weight = -1.0;
        if (matchResult != null) {
            weight = matchResult.getKthMatchWeight(Kth);
        }
        return weight;
    }

    private double getMatchPosValue(UIPortraitMatchResult matchResult, int Kth) {
        double relativePos = -1.0;
        if (matchResult != null) {
            relativePos = matchResult.getKthMatchRelativePos(Kth);
        }
        return relativePos;
    }

    /**
     * 将0~1的权重分为50份
     *
     * @param weight
     * @return
     */
    private int getWeightLevel(double weight) {
        double step = 0.02;
        return (int) (weight / step);
    }

    private UserModel.UserClusterElement userClusterElement = null;

    private UserModel.UserClusterElement getUserClusterElement(Document itemProfile, UserModel userProfile) {
        if (userClusterElement != null) {
            return userClusterElement;
        }
        List<UserModel.UserClusterElement> userClusterFeature = userProfile.getUserClusterFeature();
        if (userClusterFeature == null) {
            return null;
        }
        Map<String, UserModel.UserClusterElement> resultMap = new HashMap<>();
        for (UserModel.UserClusterElement element : userClusterFeature) {
            if (element.cate != null) {
                resultMap.put(element.cate, element);
            }
        }
        String firstCatetory = "通用";
        if (itemProfile.getCategory() != null && itemProfile.getCategory().size() > 0) {
            firstCatetory = itemProfile.getCategory().get(0);
        }
        UserModel.UserClusterElement userClusterElement = resultMap.get(firstCatetory);
        return userClusterElement;

    }

    private FeatureItem getUserClusterMatchDeep(String featureName, Document itemProfile, UserModel userProfile) {
        UserModel.UserClusterElement userClusterElement = getUserClusterElement(itemProfile, userProfile);
        if (userClusterElement == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(4);
        if (userClusterElement.isdeep != null) {
            featureItem.setValueStr(userClusterElement.isdeep);
        } else {
            featureItem.setValueStr("-1");
        }
        return featureItem;
    }

    private FeatureItem getUserClusterMatchGood(String featureName, Document itemProfile, UserModel userProfile) {
        UserModel.UserClusterElement userClusterElement = getUserClusterElement(itemProfile, userProfile);
        if (userClusterElement == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        String performance = itemProfile.getPerformancevalue();
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(4);
        if (userClusterElement.good != null && userClusterElement.good.indexOf(performance) > -1) {
            featureItem.setValueStr("1");
        } else {
            featureItem.setValueStr("-1");
        }
        return featureItem;
    }

    private FeatureItem getUserClusterMatchBad(String featureName, Document itemProfile, UserModel userProfile) {
        UserModel.UserClusterElement userClusterElement = getUserClusterElement(itemProfile, userProfile);
        if (userClusterElement == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        String performance = itemProfile.getPerformancevalue();
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(4);
        if (userClusterElement.bad != null && userClusterElement.bad.indexOf(performance) > -1) {
            featureItem.setValueStr("1");
        } else {
            featureItem.setValueStr("-1");
        }
        return featureItem;
    }


    public FeatureItem getUIMatchResult(String featureName, UserModel.UserFeatureTn userFeatureTn, List<String> itemFeatures) {
        if (userFeatureTn == null || itemFeatures == null || userFeatureTn.getFeatureWordList() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        int matchCount = 0;
        for (UserModel.UserFeatureWord featureWord : userFeatureTn.getFeatureWordList()) {
            if (itemFeatures.indexOf(featureWord.getWord()) > -1 && featureWord.getWeight() > 0.5) {
                matchCount += 1;
            }
        }
        FeatureItem featureItem = new FeatureItem();
        featureItem.setValueStr(String.valueOf(matchCount));
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(4);
        return featureItem;

    }

    public FeatureItem getUIDislikeMatchResult(String featureName, UserModel.UserFeatureTn userFeatureTn, List<String> itemFeatures) {
        if (userFeatureTn == null || itemFeatures == null || userFeatureTn.getTailWordList() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        int matchCount = 0;
        for (UserModel.UserFeatureWord featureWord : userFeatureTn.getTailWordList()) {
            if (itemFeatures.indexOf(featureWord.getWord()) > -1) {
                matchCount += 1;
            }
        }
        FeatureItem featureItem = new FeatureItem();
        featureItem.setValueStr(String.valueOf(matchCount));
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(4);
        return featureItem;

    }

    public FeatureItem getCurrentDocSubcate(String featureName, BasicContext basicContext, UserModel headLineUserProfile, Document headLineItemProfile){
        if(basicContext ==null){
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        Date time = getTime(basicContext);
        if(time != null && headLineUserProfile.getDocpicScPeriod() != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            List<String> scList = headLineItemProfile.getScList();
            if(scList == null || scList.size() == 0) {
                return FeatureExtractUtils.getNoneFeature(featureName, 4);
            }
            int matchCount = 0;
            if(weekDay >0 && weekDay < 7) {
                if (hourOfDay >= 4 && hourOfDay <= 10)
                {
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getDocpicScPeriod().weekday_morning;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }
                }
                if (hourOfDay >=11 && hourOfDay <= 14){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getDocpicScPeriod().weekday_noon;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if (hourOfDay >=15 && hourOfDay <= 19){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getDocpicScPeriod().weekday_afternoon;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if ((hourOfDay >=20 && hourOfDay <= 23) || (hourOfDay >=0 && hourOfDay <= 03)){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getDocpicScPeriod().weekend_night;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
            }else{
                if (hourOfDay >= 4 && hourOfDay <= 10)
                {
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getDocpicScPeriod().weekend_morning;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if (hourOfDay >=11 && hourOfDay <= 14){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getDocpicScPeriod().weekend_noon;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if (hourOfDay >=15 && hourOfDay <= 19){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getDocpicScPeriod().weekend_afternoon;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if ((hourOfDay >=20 && hourOfDay <= 23) || (hourOfDay >=0 && hourOfDay <= 03)){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getDocpicScPeriod().weekend_night;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }
                }
            }
            if(matchCount > 0){
                FeatureItem featureItem = new FeatureItem();
                featureItem.setFeatureName(featureName);
                featureItem.setFeatureType(4);
                featureItem.setValueStr(String.valueOf(matchCount));
                return featureItem;
            }
        }
        return FeatureExtractUtils.getNoneFeature(featureName, 4);

    }

    public FeatureItem getCurrentVideoSubcate(String featureName, BasicContext basicContext, UserModel headLineUserProfile, Document headLineItemProfile){
        if(basicContext ==null){
            return FeatureExtractUtils.getNoneFeature(featureName, 4);
        }
        Date time = getTime(basicContext);
        if(time != null && headLineUserProfile.getVideoScPeriod() != null){
            List<FeatureItem> subList = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            List<String> scList = headLineItemProfile.getScList();
            if(scList == null || scList.size() == 0){
                return FeatureExtractUtils.getNoneFeature(featureName, 4);
            }
            int matchCount = 0;
            if(weekDay >0 && weekDay < 7) {
                if (hourOfDay >= 4 && hourOfDay <= 10)
                {
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getVideoScPeriod().weekday_morning;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if (hourOfDay >=11 && hourOfDay <= 14){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getVideoScPeriod().weekday_noon;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if (hourOfDay >=15 && hourOfDay <= 19){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getVideoScPeriod().weekday_afternoon;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if ((hourOfDay >=20 && hourOfDay <= 23) || (hourOfDay >=0 && hourOfDay <= 03)){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getVideoScPeriod().weekend_night;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }
                }
            }else{
                if (hourOfDay >= 4 && hourOfDay <= 10)
                {
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getVideoScPeriod().weekend_morning;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if (hourOfDay >=11 && hourOfDay <= 14){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getVideoScPeriod().weekend_noon;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if (hourOfDay >=15 && hourOfDay <= 19){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getVideoScPeriod().weekend_afternoon;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
                if ((hourOfDay >=20 && hourOfDay <= 23) || (hourOfDay >=0 && hourOfDay <= 03)){
                    List<UserModel.UProfileElement> elements = headLineUserProfile.getVideoScPeriod().weekend_night;
                    if(elements != null) {
                        for (UserModel.UProfileElement element : elements) {
                            if (scList.indexOf(element.n) > -1) {
                                matchCount += 1;
                            }
                        }
                    }

                }
            }
            if(matchCount > 0){
                FeatureItem featureItem = new FeatureItem();
                featureItem.setFeatureName(featureName);
                featureItem.setFeatureType(4);
                featureItem.setValueStr(String.valueOf(matchCount));
                return featureItem;
            }
        }
        return FeatureExtractUtils.getNoneFeature(featureName, 4);

    }

    private Date getTime(BasicContext basicContext) {
        String loadtimeStr = basicContext.getTs();
        if (StringUtils.isNullString(loadtimeStr)) {
            return new Date();
        }
        if (loadtimeStr.length() >= 19) {
            return  DateUtils.strToDate(loadtimeStr.substring(0, 19));
        } else {
            return null;
        }
    }

}
