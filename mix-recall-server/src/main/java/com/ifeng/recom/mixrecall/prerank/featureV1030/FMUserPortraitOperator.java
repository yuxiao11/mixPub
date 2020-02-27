package com.ifeng.recom.mixrecall.prerank.featureV1030;



import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.prerank.FeatureItem;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import com.ifeng.recom.mixrecall.prerank.executor.Operator;
import com.ifeng.recom.mixrecall.prerank.tools.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class FMUserPortraitOperator extends Operator {
	private static final long serialVersionUID = 5793391748460484896L;

	private static final int MAX_LDA_TOPIC_FEATURES = 32;
	private static final int FEATURE_GROUP_SIZE = 8;

	@Override
	public List<FeatureItem> compute(FeatureContext entity, Long featureId, String featureName, int type, String attrDimension) throws Exception {
		List<FeatureItem> featureItems = new ArrayList<FeatureItem>();
		// TODO: 确定特征数之后，不上相应数量的空特征
		if (entity == null || entity.getUserProfile() == null) {
			// 为每个field补上空特征值
			featureItems.add(FeatureExtractUtils.getNoneFeature("userCity", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("isVidUser", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userDevice", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userSource", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userLikeVidWorkDay", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userLikeVidWorkNight", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userLikeVidWeekends", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userActivity", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userVidTimeSensitive", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userDocTimeSensitive", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userFullness", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userDailyPullnum", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userDocSCWeekdayMorning", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userDocSCWeekdayNoon", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userDocSCWeekdayAfternoon", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userDocSCWeekendMorning", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userDocSCWeekendNoon", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userDocSCWeekendAfternoon", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userDocSCWeekendNight", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userVidSCWeekdayMorning", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userVidWeekdayNoon", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userVidWeekdayAfternoon", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userVidWeekdayNight", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userVidWeekendMorning", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userVidWeekendNoon", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userVidWeekendAfternoon", 5));
			featureItems.add(FeatureExtractUtils.getNoneFeature("userVidWeekendNight", 5));
			return featureItems;
		}

		UserModel headLineUserProfile = entity.getUserProfile();
		// 地域（城市）
		addSingleFeatureItem(featureItems, getCityFeature("userCity", headLineUserProfile));

		// 是否视频用户
		addSingleFeatureItem(featureItems, getLikeVideoFeature("isVidUser", headLineUserProfile));

		// 设备类型：iphone，ipad，android
		addSingleFeatureItem(featureItems, getUmosFeature("userDevice", headLineUserProfile));

		// 用户订阅稿源
		addFeatureItems(featureItems, getUserUbFeatures("userSource", headLineUserProfile));
		// 用户工作日白天视频喜爱度
		addSingleFeatureItem(featureItems, getLikeVideoCode("userLikeVidWorkDay", headLineUserProfile.getGeneral_likeVidR_dayInWork()));
		// 用户工作日夜晚视频喜爱度
		addSingleFeatureItem(featureItems, getLikeVideoCode("userLikeVidWorkNight", headLineUserProfile.getGeneral_likeVidR_nightInWork()));
		// 用户周末视频喜爱度
		addSingleFeatureItem(featureItems, getLikeVideoCode("userLikeVidWeekends", headLineUserProfile.getGeneral_likeVidR_weekends()));
		// 用户活跃度
		addSingleFeatureItem(featureItems, getUserActivity("userActivity", headLineUserProfile));
		// 用户对视频的实效敏感度
		addSingleFeatureItem(featureItems, getUserVideoTimeSensitiveFeature("userVidTimeSensitive", headLineUserProfile));
		// 用户对文章的实效敏感度
		addSingleFeatureItem(featureItems, getUserDocTimeSensitiveFeature("userDocTimeSensitive", headLineUserProfile));

		// 用户画像丰满度
		addSingleFeatureItem(featureItems, getUserFullness("userFullness", headLineUserProfile));
		// 用户日均下拉次数
		addSingleFeatureItem(featureItems, getUserDailyPullnum("userDailyPullnum", headLineUserProfile));


		// 用户工作日早晨sc
		addFeatureItems(featureItems, getDocPeriodFeatures("userDocSCWeekdayMorning", headLineUserProfile));
		// 用户工作日中午sc
		addFeatureItems(featureItems, getDocPeriodFeatures("userDocSCWeekdayNoon", headLineUserProfile));
		// 用户工作日下午sc
		addFeatureItems(featureItems, getDocPeriodFeatures("userDocSCWeekdayAfternoon", headLineUserProfile));
		// 用户工昨日晚上sc
		addFeatureItems(featureItems, getDocPeriodFeatures("userDocSCWeekdayNight", headLineUserProfile));
		// 用户周末早晨sc
		addFeatureItems(featureItems, getDocPeriodFeatures("userDocSCWeekendMorning", headLineUserProfile));
		// 用户周末中午sc
		addFeatureItems(featureItems, getDocPeriodFeatures("userDocSCWeekeendNoon", headLineUserProfile));
		// 用户周末下午sc
		addFeatureItems(featureItems, getDocPeriodFeatures("userDocSCWeekendAfternoon", headLineUserProfile));
		// 用户周末晚上sc
		addFeatureItems(featureItems, getDocPeriodFeatures("userDocSCWeekendNight", headLineUserProfile));
		// 用户工作日早晨sc
		addFeatureItems(featureItems, getVideoPeriodFeatures("userVidSCWeekdayMorning", headLineUserProfile));
		// 用户工作日中午sc
		addFeatureItems(featureItems, getVideoPeriodFeatures("userVidSCWeekdayNoon", headLineUserProfile));
		// 用户工作日下午sc
		addFeatureItems(featureItems, getVideoPeriodFeatures("userVidSCWeekdayAfternoon", headLineUserProfile));
		// 用户工昨日晚上sc
		addFeatureItems(featureItems, getVideoPeriodFeatures("userVidSCWeekdayNight", headLineUserProfile));
		// 用户周末早晨sc
		addFeatureItems(featureItems, getVideoPeriodFeatures("userVidSCWeekendMorning", headLineUserProfile));
		// 用户周末中午sc
		addFeatureItems(featureItems, getVideoPeriodFeatures("userVidSCWeekendNoon", headLineUserProfile));
		// 用户周末下午sc
		addFeatureItems(featureItems, getVideoPeriodFeatures("userVidSCWeekendAfternoon", headLineUserProfile));
		// 用户周末晚上sc
		addFeatureItems(featureItems, getVideoPeriodFeatures("userVidSCWeekendNight", headLineUserProfile));
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

	private List<FeatureItem> parseLastFeaturePositive(String featureName, UserModel.UserFeatureTn lastFeature, int startIndex, int length,
													   int clickThresh, int impressionThresh) {
		List<FeatureItem> resultList = new ArrayList<>();
		if (lastFeature == null) {
			return FeatureExtractUtils.getNoneFeatures(featureName, 5);
		}
		List<UserModel.UserFeatureWord> positiveSorted = lastFeature.getPositiveSorted();
		for (int i = startIndex; i < startIndex+length; i++) {
			if (i < positiveSorted.size()) {
				UserModel.UserFeatureWord featureWord = positiveSorted.get(i);
				FeatureItem featureItem = new FeatureItem();
				featureItem.setFeatureName(featureName);
				featureItem.setFeatureType(5);
				String featureValueStr = calcLastPrefercence(featureWord.getWord(), featureWord.getClick(), featureWord.getImpression(), clickThresh, impressionThresh);
				featureItem.setValueStr(featureValueStr);
				resultList.add(featureItem);
			}
		}

		if (resultList.size() == 0) {
			return FeatureExtractUtils.getNoneFeatures(featureName, 5);
		}

		return resultList;
	}

	private List<FeatureItem> parseLastFeatureNegative(String featureName, UserModel.UserFeatureTn lastFeature, int startIndex, int length,
													   int clickThresh, int impressionThresh) {
		List<FeatureItem> resultList = new ArrayList<>();
		if (lastFeature == null) {
			return FeatureExtractUtils.getNoneFeatures(featureName, 5);
		}
		List<UserModel.UserFeatureWord> negativeSorted = lastFeature.getNegativeSorted();
		for (int i = startIndex; i < startIndex+length; i++) {
			if (i < negativeSorted.size()) {
				UserModel.UserFeatureWord featureWord = negativeSorted.get(i);
				FeatureItem featureItem = new FeatureItem();
				featureItem.setFeatureName(featureName);
				featureItem.setFeatureType(5);
				String featureValueStr = calcLastPrefercence(featureWord.getWord(), featureWord.getClick(), featureWord.getImpression(), clickThresh, impressionThresh);
				featureItem.setValueStr(featureValueStr);
				resultList.add(featureItem);
			}
		}
		if (resultList.size() == 0) {
			FeatureExtractUtils.getNoneFeatures(featureName, 5);
		}

		return resultList;
	}

	/**
	 * 计算实时特征偏好
	 * @param featureWord 特征词
	 * @param clicks 点击
	 * @param impressions 曝光
	 * @param clickThresh 点击阈值
	 * @param impressionThresh 曝光阈值
	 * @return
	 */
	private String calcLastPrefercence(String featureWord, Integer clicks, Integer impressions, Integer clickThresh, Integer impressionThresh) {
		if (clicks == 0) {
			if (impressions <= impressionThresh) {
				return featureWord + ":" + impressions + ":" + clicks;
			} else {
				return featureWord + ":" + impressionThresh + ":" + clicks;
			}
		} else {
			if (clicks <= clickThresh) {
				if (impressions <= impressionThresh) {
					return featureWord + ":" + impressions + ":" + clicks;
				} else {
					int normClicks = Math.round(clicks.floatValue() * impressionThresh.floatValue() / impressions.floatValue());
					return featureWord + ":" + impressionThresh + ":" + normClicks;
				}
			} else {
				if (impressions <= impressionThresh) {
					return featureWord + ":" + impressions + ":" + clickThresh;
				} else {
					float normClicks = clicks.floatValue() * impressionThresh.floatValue() / impressions.floatValue();
					if (normClicks <= clickThresh) {
						return featureWord + ":" + impressionThresh + ":" + Math.round(normClicks);
					} else {
						int normImpressions = Math.round(impressions.floatValue() * clickThresh.floatValue() / clicks.floatValue());
						return featureWord + ":" + normImpressions + ":" + clickThresh;
					}
				}
			}
		}
	}

	// 解析用户正向喜好
	private List<FeatureItem> parseUserFeaturePostive(String featureName, UserModel.UserFeatureTn userFeature, int stepSize, int maxLength) {
		List<FeatureItem> resultList = new ArrayList<>();
		if (userFeature == null) {
			return FeatureExtractUtils.getNoneFeatures(featureName, 5);
		}
		// 顺次取n个feature word
		List<UserModel.UserFeatureWord> featureWordList = userFeature.getFeatureWordList();
		for(int i = 0; i < featureWordList.size(); i++){
			if(i >= maxLength){
				break;
			}
			if (featureWordList.get(i).getClick() > 1) {
				UserModel.UserFeatureWord featureWord = featureWordList.get(i);
				FeatureItem featureItem = new FeatureItem();
				featureItem.setFeatureName(featureName);
				featureItem.setFeatureType(5);
				int group = i / stepSize;
//				featureItem.setValueStr(featureWord.getWord() + ":" + impressionLevel);
				featureItem.setValueStr(group + ":" + featureWord.getWord());
				resultList.add(featureItem);
			}
		}

		if (resultList.size() == 0) {
			resultList.add(FeatureExtractUtils.getNoneFeature(featureName, 5));
		}
		return resultList;
	}

	/**
	 * 解析用户负向喜好
	 * 这里的reverseStartIndex是从最后一个特征词开始数的，最后一个特征词为0，倒数第二个为1，以此类推
 	 */
	private List<FeatureItem> parseUserFeatureNegative(String featureName, UserModel.UserFeatureTn userFeature, int stepSize, int maxLength) {
		List<FeatureItem> resultList = new ArrayList<>();
		if (userFeature == null) {
			return FeatureExtractUtils.getNoneFeatures(featureName, 5);
		}
		// 顺次取n个feature word
		List<UserModel.UserFeatureWord> featureWordList = userFeature.getFeatureWordList();
		int totalCount = 0;
		for(int i = featureWordList.size() - 1; i > -1; i--){
			if(totalCount > maxLength) {
				break;
			}
			if(featureWordList.get(i).getClick() <= 1){
				UserModel.UserFeatureWord featureWord = featureWordList.get(i);
				FeatureItem featureItem = new FeatureItem();
				featureItem.setFeatureName(featureName);
				featureItem.setFeatureType(5);
				int group = totalCount / stepSize;
				featureItem.setValueStr(group + ":" + featureWord.getWord());
				resultList.add(featureItem);
			}
			totalCount += 1;
		}
		if (resultList.size() == 0) {
			return FeatureExtractUtils.getNoneFeatures(featureName, 5);
		}
		return resultList;
	}


	/**
	 * 获取用用户地域特征词：取200个主要城市
	 * @param headLineUserProfile
	 * @param featureName
	 * @return
	 */
	private FeatureItem getCityFeature(String featureName, UserModel headLineUserProfile) {
		String generalLoc = headLineUserProfile.getGeneralLoc();
		if (StringUtils.isNullString(generalLoc)) {
			return FeatureExtractUtils.getNoneFeature(featureName, 5);
		}
		String city = "other";
		String [] arr = generalLoc.split("_");
		if (arr.length >= 3) {
			city = arr[2];
		}
		if (!FmConstant.MAIN_CITY.contains(city)) {
			city = "other";
		}

		FeatureItem featureItem = new FeatureItem();
		featureItem.setFeatureName(featureName);
		featureItem.setFeatureType(5);
		featureItem.setValueStr(city);

		return featureItem;
	}


	/**
	 * 获取是否视频用户特征
	 * @param headLineUserProfile
	 * @param featureName
	 * @return
	 */
	private FeatureItem getLikeVideoFeature(String featureName, UserModel headLineUserProfile) {
		Boolean likeVideo = headLineUserProfile.getLikevideo();
		if (likeVideo != null) {
			return FeatureExtractUtils.getNoneFeature(featureName, 5);
		}
		int likeVideoCode = -1;
		if (likeVideo) {
			likeVideoCode = 0;
		} else if (likeVideo) {
			likeVideoCode = 1;
		}
		if (likeVideoCode >= 0) {
			FeatureItem featureItem = new FeatureItem();
			featureItem.setFeatureName(featureName);
			featureItem.setFeatureType(5);
			featureItem.setValueStr(String.valueOf(likeVideoCode));
			return featureItem;
		}
		return FeatureExtractUtils.getNoneFeature(featureName, 5);
	}

	/**
	 * 获取设备类型特征
	 * @param headLineUserProfile
	 * @param featureName
	 * @return
	 */
	private FeatureItem getUmosFeature(String featureName, UserModel headLineUserProfile) {
		String umos = headLineUserProfile.getUmos();
		if (StringUtils.isNullString(umos)) {
			return FeatureExtractUtils.getNoneFeature(featureName, 5);
		}
		String umosValue;
		if (umos.startsWith("iphone")) {
			umosValue = "iph";
		} else if (umos.startsWith("ipad")) {
			umosValue = "ipd";
		} else if (umos.startsWith("android")) {
			umosValue = "adr";
		} else {
			umosValue = "otr";
		}

		FeatureItem featureItem = new FeatureItem();
		featureItem.setFeatureName(featureName);
		featureItem.setFeatureType(5);
		featureItem.setValueStr(String.valueOf(umosValue));
		return featureItem;
	}

	private List<FeatureItem> getUserUbFeatures(String featureName, UserModel headLineUserProfile){
		List<FeatureItem> result = new ArrayList<>();
		if(headLineUserProfile.getUbSet() == null || headLineUserProfile.getUbSet().size() == 0) {
			return FeatureExtractUtils.getNoneFeatures(featureName, 5);
		}
		for(String ub: headLineUserProfile.getUbSet()) {
			FeatureItem featureItem = new FeatureItem();
			featureItem.setFeatureName(featureName);
			featureItem.setFeatureType(5);
			featureItem.setValueStr(ub);
			result.add(featureItem);
		}
		return result;
	}

	private FeatureItem getLikeVideoCode(String featureName, String likeVideoRate) {
		if(likeVideoRate == null) {
			return FeatureExtractUtils.getNoneFeature(featureName, 5);
		}
		try {
			FeatureItem featureItem = new FeatureItem();
			featureItem.setFeatureName(featureName);
			featureItem.setFeatureType(5);
			featureItem.setValueStr(String.valueOf(getFeatureCode(0.05, Double.parseDouble(likeVideoRate))));
			return featureItem;
		}catch (Exception e){
			return FeatureExtractUtils.getNoneFeature(featureName, 5);
		}
	}

	private FeatureItem getUserActivity(String featureName, UserModel headLineUserProfile) {
		if(headLineUserProfile.getUa_v() == 1) {
			return FeatureExtractUtils.getNoneFeature(featureName, 5);
		}
		FeatureItem featureItem = new FeatureItem();
		featureItem.setFeatureName(featureName);
		featureItem.setFeatureType(5);
		featureItem.setValueStr(String.valueOf(headLineUserProfile.getUa_v()));
		return featureItem;
	}

	private FeatureItem getUserDocTimeSensitiveFeature(String featureName, UserModel headLineUserProfile) {
		if(headLineUserProfile.getGeneral_doc_timeSensitive() == null || headLineUserProfile.getGeneral_doc_timeSensitive().length() == 0) {
			return FeatureExtractUtils.getNoneFeature(featureName, 5);
		}
		try {
			FeatureItem featureItem = new FeatureItem();
			featureItem.setFeatureName(featureName);
			featureItem.setFeatureType(5);
			featureItem.setValueStr(String.valueOf(getFeatureCode(0.05, Double.parseDouble(headLineUserProfile.getGeneral_doc_timeSensitive()))));
			return featureItem;
		}catch (Exception e){
			return FeatureExtractUtils.getNoneFeature(featureName, 5);
		}
	}

	private FeatureItem getUserVideoTimeSensitiveFeature(String featureName, UserModel headLineUserProfile){
		if(headLineUserProfile.getGeneral_vid_timeSensitive() == null || headLineUserProfile.getGeneral_vid_timeSensitive().length() == 0) {
			return FeatureExtractUtils.getNoneFeature(featureName, 5);
		}
		try {
			FeatureItem featureItem = new FeatureItem();
			featureItem.setFeatureName(featureName);
			featureItem.setFeatureType(5);
			featureItem.setValueStr(String.valueOf(getFeatureCode(0.05, Double.parseDouble(headLineUserProfile.getGeneral_vid_timeSensitive()))));
			return featureItem;
		}catch (Exception e){
			return FeatureExtractUtils.getNoneFeature(featureName, 5);
		}
	}



	private FeatureItem getUserFullness(String featureName, UserModel headLineUserProfile){
		String fullness = headLineUserProfile.getFullness();
		if(StringUtils.isDouble(fullness)){
			FeatureItem featureItem = new FeatureItem();
			featureItem.setFeatureName(featureName);
			featureItem.setFeatureType(5);
			featureItem.setValueStr(String.valueOf((int)(Double.parseDouble(fullness)/ 0.1)));
			return featureItem;
		}
		return FeatureExtractUtils.getNoneFeature(featureName, 5);
	}

	private FeatureItem getUserDailyPullnum(String featureName, UserModel headLineUserProfile){
		String daily_pullnum = headLineUserProfile.getDaily_pullNum();
		if(StringUtils.isDouble(daily_pullnum)){
			FeatureItem featureItem = new FeatureItem();
			double value = Double.parseDouble(daily_pullnum);
			String featureValue = (int)value >= 50? "50": String.valueOf((int) value);
			featureItem.setFeatureName(featureName);
			featureItem.setFeatureType(5);
			featureItem.setValueStr(featureValue);
			return featureItem;
		}
		return FeatureExtractUtils.getNoneFeature(featureName, 5);
	}

	private List<FeatureItem> getDocPeriodFeatures(String featureName, UserModel headLineUserProfile){
		UserModel.UserScPeriod userScPeriod = headLineUserProfile.getDocpicScPeriod();
		List<FeatureItem> subList = new ArrayList<>();
		if(userScPeriod == null) {
			return FeatureExtractUtils.getNoneFeatures(featureName, 5);
		}
		List<UserModel.UProfileElement> elements = null;
		if("userDocSCWeekdayMorning".equals(featureName)){
			elements = userScPeriod.weekend_morning;
		}
		if("userDocSCWeekdayNoon".equals(featureName)){
			elements = userScPeriod.weekday_noon;
		}
		if("userDocSCWeekdayAfternoon".equals(featureName)){
			elements = userScPeriod.weekday_afternoon;
		}
		if("userDocSCWeekdayNight".equals(featureName)){
			elements = userScPeriod.weekday_night;
		}
		if("userDocSCWeekendMorning".equals(featureName)){
			elements = userScPeriod.weekend_morning;
		}
		if("userDocSCWeekendNoon".equals(featureName)){
			elements = userScPeriod.weekday_noon;
		}
		if("userDocSCWeekendAfternoon".equals(featureName)){
			elements = userScPeriod.weekday_afternoon;
		}
		if("userDocSCWeekendNight".equals(featureName)){
			elements = userScPeriod.weekend_night;
		}
		if(elements == null){
		    return FeatureExtractUtils.getNoneFeatures(featureName, 5);
        }else {
            for (UserModel.UProfileElement element : elements) {
                String value = element.n;
                FeatureItem featureItem = new FeatureItem();
                featureItem.setFeatureName(featureName);
                featureItem.setFeatureType(5);
                featureItem.setValueStr(value);
                subList.add(featureItem);
            }
            return subList;
        }
	}

	private List<FeatureItem> getVideoPeriodFeatures(String featureName, UserModel headLineUserProfile){
		UserModel.UserScPeriod userScPeriod = headLineUserProfile.getVideoScPeriod();
		List<FeatureItem> subList = new ArrayList<>();
		if(userScPeriod == null) {
			return FeatureExtractUtils.getNoneFeatures(featureName, 5);
		}
		List<UserModel.UProfileElement> elements = null;
		if("userVidSCWeekdayMorning".equals(featureName)){
			elements = userScPeriod.weekend_morning;
		}
		if("userVidSCWeekdayNoon".equals(featureName)){
			elements = userScPeriod.weekday_noon;
		}
		if("userVidSCWeekdayAfternoon".equals(featureName)){
			elements = userScPeriod.weekday_afternoon;
		}
		if("userVidSCWeekdayNight".equals(featureName)){
			elements = userScPeriod.weekday_night;
		}
		if("userVidSCWeekendMorning".equals(featureName)){
			elements = userScPeriod.weekend_morning;
		}
		if("userVidSCWeekendNoon".equals(featureName)){
			elements = userScPeriod.weekday_noon;
		}
		if("userVidSCWeekendAfternoon".equals(featureName)){
			elements = userScPeriod.weekday_afternoon;
		}
		if("userVidSCWeekendNight".equals(featureName)){
			elements = userScPeriod.weekend_night;
		}
        if(elements == null){
            return FeatureExtractUtils.getNoneFeatures(featureName, 5);
        }else {
            for (UserModel.UProfileElement element : elements) {
                String value = element.n;
                FeatureItem featureItem = new FeatureItem();
                featureItem.setFeatureName(featureName);
                featureItem.setFeatureType(5);
                featureItem.setValueStr(value);
                subList.add(featureItem);
            }
            return subList;
        }
	}

	private int getFeatureCode(double step, double original) {
		return (int)(original/ step) + 1;
	}

	public static void main(String[] args) {
		FMUserPortraitOperator operator = new FMUserPortraitOperator();
		System.out.println(operator.calcLastPrefercence("feature", 2, 28, 10, 20));
	}

}
