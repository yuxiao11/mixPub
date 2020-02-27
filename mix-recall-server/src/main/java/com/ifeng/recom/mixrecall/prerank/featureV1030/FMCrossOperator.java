package com.ifeng.recom.mixrecall.prerank.featureV1030;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.prerank.FeatureItem;
import com.ifeng.recom.mixrecall.prerank.entity.BasicContext;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import com.ifeng.recom.mixrecall.prerank.executor.Operator;
import com.ifeng.recom.mixrecall.prerank.tools.DateUtils;
import com.ifeng.recom.mixrecall.prerank.tools.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


public class FMCrossOperator extends Operator {
    private static final long serialVersionUID = -6359305110074284082L;
    private static final Log logger = LogFactory.getLog(FMCrossOperator.class);

    @Override
    public List<FeatureItem> compute(FeatureContext entity, Long featureId, String featureName, int type, String attrDimension) throws Exception {

        List<FeatureItem> featureItems = new ArrayList<FeatureItem>();
        // 时间段和lda-topic交叉特征：<工作日/周末> x <小时> x <Topic>
        addFeatureItems(featureItems, getTimeTopicCrossFeatures("time-ldaTopic", entity));
        // doctype和lda-topic交叉特征
        addFeatureItems(featureItems, getDoctypeTopicCrossFeatures("docType-ldaTopic", entity));
        // 是否是用户订阅稿源
        addSingleFeatureItem(featureItems, isUserSubFeature("isSubscribed", entity));
        // 文章发布时间到曝光时间的间隔
        addSingleFeatureItem(featureItems, getPublishedIntervalFeature("publishToExpose", entity));
        // 时间段和用户对视频喜爱程度交叉特征
        addSingleFeatureItem(featureItems, getUserVideoLikeFeature("isUserlikeVideoCurrent", entity));
        // 召回通道与下拉次数交叉
        addSingleFeatureItem(featureItems, getRecallPullNumCrossFeature("recallType-pullnum", entity));
        // 时间段和sc交叉
        addFeatureItems(featureItems, getWeekScCrossFeature("time-sc", entity));
        // 文章长度和sc交叉
        addFeatureItems(featureItems, getLenScCrossFeature("length-sc", entity));
        // 文章长度和lda-topic交叉
        addFeatureItems(featureItems, getLenLdaTopicCrossFeature("length-ldaTopic", entity));
        // 时效性评级和发布时间交叉
        addSingleFeatureItem(featureItems, getTimeCrossFeature("timeSensitive-pubilish", entity));
        // 时效性评级与用户时效性敏感度交叉
        addSingleFeatureItem(featureItems, getSensitiveLevelUserCross("timeSensitive-user", entity));
        // sc与发布时间交叉
        addFeatureItems(featureItems, getTimeDeltaScCrossFeature("sc-publishTime", entity));
        // lda与发布时间交叉
        addFeatureItems(featureItems, getTimeLdaCrossFeature("lda-publishTime", entity));
        //generalLoc与user_group中城市級別的交叉
        addSingleFeatureItem(featureItems, getLocAndUserGroupLoc("loc-userGroupLoc", entity));

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
     * 文章发布时间到曝光时间的间隔
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getPublishedIntervalFeature(String featureName, FeatureContext featureContext) {
        if (featureContext == null || featureContext.getItemDocument() == null || featureContext.getBasicContext() == null ) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        BasicContext basicContext = featureContext.getBasicContext();
        int publishIntervalCode = getTimeDeltaCode(headLineItemProfile, basicContext);
        if (publishIntervalCode >= 0) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(String.valueOf(publishIntervalCode));
            return featureItem;
        }
        return null;
    }

    /**
     * 是否是用户订阅稿源
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem isUserSubFeature(String featureName, FeatureContext featureContext) {
        if (featureContext.getUserProfile() == null ) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        UserModel headLineUserProfile = featureContext.getUserProfile();
        Document headLineItemProfile = featureContext.getItemDocument();
        String source = headLineItemProfile.getSource();
        Set<String> userSubs = headLineUserProfile.getGroupUbSet();
        if (source == null || userSubs == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        } else {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(String.valueOf(userSubs.contains(source)));
            return featureItem;
        }
    }


    /**
     * 获取时间段和lda-topic的交叉特征，形式为：<是否周末>:<小时>:<Topic>
     *
     * @param featureName
     * @param entity
     * @return
     */
    private List<FeatureItem> getTimeTopicCrossFeatures(String featureName, FeatureContext entity) {
        if (entity == null || entity.getItemDocument() == null || entity.getBasicContext() == null) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        Document headLineItemProfile = entity.getItemDocument();
        BasicContext basicContext = entity.getBasicContext();
        List<String> ldaTopicList = headLineItemProfile.getLdaTopicList();
        if (ldaTopicList == null || ldaTopicList.size() == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        Date time = getTime(basicContext);
        if (time == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int isWeekend = 0;
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
            isWeekend = 1;
        }
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        List<FeatureItem> subList = new ArrayList<>();
        for (String ldaTopic : ldaTopicList) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(isWeekend + ":" + hourOfDay + ":" + ldaTopic);
            subList.add(featureItem);
        }
        return subList;
    }

    /**
     * 生成当前时刻用户对视频的喜爱程度特征
     *
     * @param featureName
     * @param entity
     * @return
     */
    private FeatureItem getUserVideoLikeFeature(String featureName, FeatureContext entity) {
        if (entity == null || entity.getUserProfile() == null || entity.getBasicContext() == null) {
            FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        UserModel headLineUserProfile = entity.getUserProfile();
        if (headLineUserProfile == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        BasicContext basicContext = entity.getBasicContext();
        Date time = getTime(basicContext);
        if (time == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
            String generalLikeVideo = headLineUserProfile.getGeneral_likeVidR_weekends();
            if (!StringUtils.isNullString(generalLikeVideo)) {
                FeatureItem featureItem = new FeatureItem();
                featureItem.setFeatureName(featureName);
                featureItem.setFeatureType(2);
                featureItem.setValueStr(String.valueOf((int) (Double.parseDouble(generalLikeVideo) / 0.05) + 1));
                return featureItem;
            } else {
                return FeatureExtractUtils.getNoneFeature(featureName, 2);
            }
        } else {
            if (hourOfDay >= 8 && hourOfDay <= 7) {
                String generalLikeVideo = headLineUserProfile.getGeneral_likeVidR_dayInWork();
                if (!StringUtils.isNullString(generalLikeVideo)) {
                    FeatureItem featureItem = new FeatureItem();
                    featureItem.setFeatureName(featureName);
                    featureItem.setFeatureType(2);
                    featureItem.setValueStr(String.valueOf((int) (Double.parseDouble(generalLikeVideo) / 0.05) + 1));
                    return featureItem;
                } else {
                    return FeatureExtractUtils.getNoneFeature(featureName, 2);
                }
            } else {
                String generalLikeVideo = headLineUserProfile.getGeneral_likeVidR_nightInWork();
                if (!StringUtils.isNullString(generalLikeVideo)) {
                    FeatureItem featureItem = new FeatureItem();
                    featureItem.setFeatureName(featureName);
                    featureItem.setFeatureType(2);
                    featureItem.setValueStr(String.valueOf((int) (Double.parseDouble(generalLikeVideo) / 0.05) + 1));
                    return featureItem;
                } else {
                    return FeatureExtractUtils.getNoneFeature(featureName, 2);
                }
            }
        }


    }

    /**
     * 获取文章doctype和lda-topic的交叉特征，反映用户对不同类型文章的偏好
     *
     * @param featureName
     * @param entity
     * @return
     */
    private List<FeatureItem> getDoctypeTopicCrossFeatures(String featureName, FeatureContext entity) {
        if (entity == null || entity.getItemDocument() == null || entity.getBasicContext() == null) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        Document headLineItemProfile = entity.getItemDocument();
        List<String> ldaTopicList = headLineItemProfile.getLdaTopicList();
        if (ldaTopicList == null || ldaTopicList.size() == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        String docType = headLineItemProfile.getDocType();
        if (docType == null) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        List<FeatureItem> subList = new ArrayList<>();
        for (String ldaTopic : ldaTopicList) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(docType + ":" + ldaTopic);
            subList.add(featureItem);
        }
        return subList;
    }

    /**
     * 召回通道与用户下拉次数交叉特征
     *
     * @param featureName
     * @param entity
     * @return
     */
    private FeatureItem getRecallPullNumCrossFeature(String featureName, FeatureContext entity) {
        if (entity == null || entity.getBasicContext() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        BasicContext basicContext = entity.getBasicContext();
        if (basicContext.getReason() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        String valueStr = basicContext.getReason() + ":" + basicContext.getPullNum();
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(2);
        featureItem.setValueStr(valueStr);
        return featureItem;
    }

    /**
     * 星期x与sc的交叉
     *
     * @param featureName
     * @param entity
     * @return
     */
    private List<FeatureItem> getWeekScCrossFeature(String featureName, FeatureContext entity) {
        if (entity == null || entity.getItemDocument() == null || entity.getBasicContext() == null ) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        Document headLineItemProfile = entity.getItemDocument();
        if (headLineItemProfile.getScList() == null) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        List<String> scList = headLineItemProfile.getScList();
        if (scList == null || scList.size() == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        BasicContext basicContext = entity.getBasicContext();
        Date time = getTime(basicContext);
        if (time == null) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int isWeekend = 0;
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
            isWeekend = 1;
        }
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        List<FeatureItem> subList = new ArrayList<>();
        for (String sc : scList) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(isWeekend + ":" + hourOfDay + ":" + sc);
            subList.add(featureItem);
        }
        return subList;
    }

    /**
     * 文章长度与sc交叉
     *
     * @param featureName
     * @param entity
     * @return
     */
    private List<FeatureItem> getLenScCrossFeature(String featureName, FeatureContext entity) {
        if (entity == null || entity.getItemDocument() == null || entity.getBasicContext() == null) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        Document headLineItemProfile = entity.getItemDocument();
        List<FeatureItem> subList = new ArrayList<>();
        if (headLineItemProfile.getScList() == null || headLineItemProfile.getNewslenlevel() == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        int newsLenLevel = headLineItemProfile.getNewslenlevel();
        List<String> scList = headLineItemProfile.getScList();
        for (String sc : scList) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(sc + ":" + newsLenLevel);
            subList.add(featureItem);
        }
        return subList;
    }

    /**
     * 文章长度与ldatopic交叉
     *
     * @param featureName
     * @param entity
     * @return
     */
    private List<FeatureItem> getLenLdaTopicCrossFeature(String featureName, FeatureContext entity) {
        if (entity == null || entity.getItemDocument() == null || entity.getBasicContext() == null) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        List<FeatureItem> subList = new ArrayList<>();
        Document headLineItemProfile = entity.getItemDocument();
        if (headLineItemProfile.getNewslenlevel() == 0 || headLineItemProfile.getLdaTopicList() == null || headLineItemProfile.getLdaTopicList().size() == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        List<String> ldaTopicList = headLineItemProfile.getLdaTopicList();
        if (ldaTopicList == null || ldaTopicList.size() == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        for (String ldaTopic : ldaTopicList) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(ldaTopic + ":" + headLineItemProfile.getNewslenlevel());
            subList.add(featureItem);
        }
        return subList;
    }


    /**
     * 时效性评级与发布时间交叉
     *
     * @param featureName
     * @param entity
     * @return
     */
    private FeatureItem getTimeCrossFeature(String featureName, FeatureContext entity) {
        if (entity == null || entity.getItemDocument() == null || entity.getBasicContext() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        Document headLineItemProfile = entity.getItemDocument();
        if (headLineItemProfile.getTimeSensitiveLevel() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        BasicContext basicContext = entity.getBasicContext();
        int publishIntervalCode = getTimeDeltaCode(headLineItemProfile, basicContext);
        if (publishIntervalCode < 0) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(2);
        featureItem.setValueStr(headLineItemProfile.getTimeSensitiveLevel() + ":" + publishIntervalCode);
        return featureItem;
    }

    /**
     * 文章发布时间间隔与sc交叉
     *
     * @param featureName
     * @param entity
     * @return
     */
    private List<FeatureItem> getTimeDeltaScCrossFeature(String featureName, FeatureContext entity) {
        if (entity == null || entity.getItemDocument() == null || entity.getBasicContext() == null) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        List<FeatureItem> res = new ArrayList<>();
        Document headLineItemProfile = entity.getItemDocument();
        BasicContext basicContext = entity.getBasicContext();
        List<String> scList = headLineItemProfile.getScList();
        int publishIntervalCode = getTimeDeltaCode(headLineItemProfile, basicContext);
        if (publishIntervalCode < 0 || scList == null || scList.size() == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        for (String sc : scList) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(sc + ":" + publishIntervalCode);
            res.add(featureItem);
        }
        return res;
    }

    /**
     * 发布时间间隔与lda交叉
     *
     * @param featureName
     * @param entity
     * @return
     */
    private List<FeatureItem> getTimeLdaCrossFeature(String featureName, FeatureContext entity) {
        List<FeatureItem> res = new ArrayList<>();
        if (entity == null || entity.getItemDocument() == null || entity.getBasicContext() == null) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        Document headLineItemProfile = entity.getItemDocument();
        BasicContext basicContext = entity.getBasicContext();
        List<String> ldaList = headLineItemProfile.getLdaTopicList();
        int publishIntervalCode = getTimeDeltaCode(headLineItemProfile, basicContext);
        if (publishIntervalCode < 0 || ldaList == null || ldaList.size() == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 2);
        }
        for (String lda : ldaList) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(lda + ":" + publishIntervalCode);
            res.add(featureItem);
        }
        return res;
    }


    public int getTimeDeltaCode(Document headLineItemProfile, BasicContext basicContext) {
        Date exposeTime = getTime(basicContext);
        Date publishTime = null;
        try {
            publishTime = DateUtils.strToDate(headLineItemProfile.getPublishedTime());
        } catch (Exception e) {
            if (headLineItemProfile.getPublishedTime() != null) {
                logger.error("Parse publishTime error: " + headLineItemProfile.getPublishedTime());
            }
        }
        if (exposeTime == null || publishTime == null) {
            return -1;
        }
        int publishIntervalCode = -1;
        long deltaSeconds = (exposeTime.getTime() - publishTime.getTime()) / 1000;
        if (deltaSeconds > 0 && deltaSeconds < 3 * 60 * 60) {    // 3小时以内
            publishIntervalCode = 0;
        } else if (deltaSeconds >= 3 * 3600 && deltaSeconds < 12 * 3600) {     // 3-12小时
            publishIntervalCode = 1;
        } else if (deltaSeconds >= 12 * 3600 && deltaSeconds < 24 * 3600) { // 12-24小时
            publishIntervalCode = 2;
        } else if (deltaSeconds >= 24 * 3600 && deltaSeconds < 48 * 3600) {  // 1-2天
            publishIntervalCode = 3;
        } else if (deltaSeconds >= 48 * 3600 && deltaSeconds < 72 * 3600) {     // 2-3天
            publishIntervalCode = 4;
        } else if (deltaSeconds >= 72 * 3600 && deltaSeconds < 96 * 3600) {  // 3-4天
            publishIntervalCode = 5;
        } else if (deltaSeconds >= 96 * 3600 && deltaSeconds < 120 * 3600) { // 4-5天
            publishIntervalCode = 6;
        } else if (deltaSeconds >= 120 * 3600 && deltaSeconds < 144 * 3600) {// 5-6天
            publishIntervalCode = 7;
        } else if (deltaSeconds >= 144 * 3600 && deltaSeconds < 168 * 3600) {// 6-7天
            publishIntervalCode = 8;
        } else if (deltaSeconds >= 168 * 3600) {    // 7天及以上
            publishIntervalCode = 9;
        }
        return publishIntervalCode;
    }

    private FeatureItem getSensitiveLevelUserCross(String featureName, FeatureContext entity) {
        if (entity == null || entity.getItemDocument() == null || entity.getBasicContext() == null
                || entity.getUserProfile() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        Document headLineItemProfile = entity.getItemDocument();
        if (headLineItemProfile.getTimeSensitiveLevel() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        String docType = headLineItemProfile.getDocType();
        UserModel headLineUserProfile = entity.getUserProfile();
        String userTimeSensitive = docType.equals("video") ? headLineUserProfile.getGeneral_vid_timeSensitive() : headLineUserProfile.getGeneral_doc_timeSensitive();
        if (userTimeSensitive == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        try {
            int userTimeSensitiveCode = (int) (Double.parseDouble(userTimeSensitive) / 0.05);
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(headLineItemProfile.getTimeSensitiveLevel() + ":" + userTimeSensitiveCode);
            return featureItem;
        } catch (Exception e) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
    }

    private Date getTime(BasicContext basicContext) {
        String loadtimeStr = basicContext.getTs();
        if (StringUtils.isNullString(loadtimeStr)) {
            return new Date();
        }
        if (loadtimeStr.length() >= 19) {
            return DateUtils.strToDate(loadtimeStr.substring(0, 19));
        } else {
            return null;
        }
    }

    private FeatureItem getLocAndUserGroupLoc(String featureName, FeatureContext entity) {
        if (entity == null || entity.getUserProfile() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        UserModel headLineUserProfile = entity.getUserProfile();
        String generalLoc = headLineUserProfile.getGeneralLoc();
        String userGroup = headLineUserProfile.getUserGroup();
        String city = "other";
        String grade = "";
        if (generalLoc == null || userGroup ==null){
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }
        try{
            String [] arr = generalLoc.split("_");
            if (arr.length >= 3) {
                city = arr[2];
            }
            if (!FmConstant.MAIN_CITY.contains(city)) {
                city = "other";
            }
            if(userGroup.contains("一线城市")){grade = "1";}
            else if(userGroup.contains("二线城市")){grade = "2";}
            else if(userGroup.contains("三线城市")){grade = "3";}
            else if(userGroup.contains("四线城市")){grade = "4";}
            else if(userGroup.contains("五线城市")){grade = "5";}
            else {grade = "0";}
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(2);
            featureItem.setValueStr(city + ":" + grade);
            return featureItem;
        } catch (Exception e){
            return FeatureExtractUtils.getNoneFeature(featureName, 2);
        }


    }

}
