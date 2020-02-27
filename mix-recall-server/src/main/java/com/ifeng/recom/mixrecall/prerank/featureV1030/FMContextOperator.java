package com.ifeng.recom.mixrecall.prerank.featureV1030;


import com.ifeng.recom.mixrecall.negative.StringUtils;
import com.ifeng.recom.mixrecall.prerank.FeatureItem;
import com.ifeng.recom.mixrecall.prerank.entity.BasicContext;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import com.ifeng.recom.mixrecall.prerank.executor.Operator;
import com.ifeng.recom.mixrecall.prerank.tools.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class FMContextOperator extends Operator {
	private static final long serialVersionUID = 5793391748460484896L;

	@Override
	public List<FeatureItem> compute(FeatureContext entity, Long featureId, String featureName, int type, String attrDimension) throws Exception {
		List<FeatureItem> featureItems = new ArrayList<FeatureItem>();
		if (entity == null || entity.getBasicContext() == null ) {
			featureItems.add(FeatureExtractUtils.getNoneFeature("uid", 1));
			featureItems.add(FeatureExtractUtils.getNoneFeature("time", 1));
			featureItems.add(FeatureExtractUtils.getNoneFeature("week", 1));
			featureItems.add(FeatureExtractUtils.getNoneFeature("net", 1));
			featureItems.add(FeatureExtractUtils.getNoneFeature("recallType", 1));
			return featureItems;
		}
		BasicContext basicContext = entity.getBasicContext();


		// 用户uid
		addSingleFeatureItem(featureItems, getUidFeature("uid", basicContext));

		Date time = getTime(basicContext);
		// 加载时间: 0-23
		addSingleFeatureItem(featureItems, getHourFeature("time", time));
		// 日期特征：星期一到星期六
		addSingleFeatureItem(featureItems, getWeekFeature("week", time));
		// 网络状态特征
		addSingleFeatureItem(featureItems, getNetFeature("net", basicContext));
		// 文章召回通道
		addSingleFeatureItem(featureItems, getReaSonFeature("recallType", basicContext));

		return featureItems;
	}

	private void addSingleFeatureItem(List<FeatureItem> featureItems, FeatureItem featureItem) {
		if (featureItem != null) {
			featureItems.add(featureItem);
		}
	}


	/**
	 * 获取用户id特征
	 * @param featureName
	 * @param basicContext
	 * @return
	 */
	private FeatureItem getUidFeature(String featureName, BasicContext basicContext) {
		if (StringUtils.isNullString(basicContext.getUid())) {
			return FeatureExtractUtils.getNoneFeature(featureName, 1);
		}
		FeatureItem featureItem = new FeatureItem();
		featureItem.setFeatureName(featureName);
		featureItem.setFeatureType(1);
		featureItem.setValueStr(basicContext.getUid());
		return featureItem;
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

	/**
	 * 获取曝光小时特征 0-23
	 * @param featureName
	 * @param time
	 * @return
	 */
	private FeatureItem getHourFeature(String featureName, Date time) {
		if (time == null) {
			return FeatureExtractUtils.getNoneFeature(featureName, 1);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

		FeatureItem featureItem = new FeatureItem();
		featureItem.setFeatureName(featureName);
		featureItem.setFeatureType(1);
		featureItem.setValueStr(String.valueOf(hourOfDay));
		return featureItem;
	}

	/**
	 * 获取星期特征 1-7
	 * @param featureName
	 * @param time
	 * @return
	 */
	private FeatureItem getWeekFeature(String featureName, Date time) {
		if (time == null) {
			return FeatureExtractUtils.getNoneFeature(featureName, 1);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		FeatureItem featureItem = new FeatureItem();
		featureItem.setFeatureName(featureName);
		featureItem.setFeatureType(1);
		featureItem.setValueStr(String.valueOf(dayOfWeek));
		return featureItem;
	}

	/**
	 * 获取网络状态特征
	 * @param basicContext
	 * @param featureName
	 * @return
	 */
	private FeatureItem getNetFeature(String featureName, BasicContext basicContext) {
		String net = basicContext.getNet();
		if (StringUtils.isNullString(net)) {
			return FeatureExtractUtils.getNoneFeature(featureName, 1);
		}
		int netCode = -1;
		if (net.equals("2g")) {
			netCode = 0;
		} else if (net.equals("3g")) {
			netCode = 1;
		} else if (net.equals("4g")) {
			netCode = 2;
		} else if (net.startsWith("wifi")) {
			netCode = 3;
		} else if (net.equals("offline")) {
			netCode = 4;
		} else if (net.equals("disconnected")) {
			netCode = 5;
		} else if (net.startsWith("unknow")) {
			netCode = 6;
		}
		if (netCode >= 0) {
			FeatureItem featureItem = new FeatureItem();
			featureItem.setFeatureName(featureName);
			featureItem.setFeatureType(1);
			featureItem.setValueStr(String.valueOf(netCode));
			return featureItem;
		}
		return FeatureExtractUtils.getNoneFeature(featureName, 1);
	}

	private FeatureItem getReaSonFeature(String featureName, BasicContext basicContext) {
		String reason = basicContext.getReason();
		if (StringUtils.isNullString(reason)) {
			return FeatureExtractUtils.getNoneFeature(featureName, 1);
		}
		FeatureItem featureItem = new FeatureItem();
		featureItem.setFeatureName(featureName);
		featureItem.setFeatureType(1);
		featureItem.setValueStr(reason);
		return featureItem;
	}


}
