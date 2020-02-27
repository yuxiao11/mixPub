package com.ifeng.recom.mixrecall.prerank.featureV1030;


import com.ifeng.recom.mixrecall.prerank.FeatureItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaohh @ 2018-11-16 10:59
 **/
public class FeatureExtractUtils {

	/**
	 * 获取值为空的特征
	 * @param featureName
	 * @return
	 */
	public static FeatureItem getNoneFeature(String featureName, int featureType) {
		FeatureItem featureItem = new FeatureItem();
		featureItem.setFeatureName(featureName);
		featureItem.setFeatureType(featureType);
		featureItem.setValueStr("none");
		return featureItem;
	}


	/**
	 * 获取值为空的特征列表
	 * @param featureName
	 * @param featureType
	 * @return
	 */
	public static List<FeatureItem> getNoneFeatures(String featureName, int featureType) {
		List<FeatureItem> features = new ArrayList<>();
		FeatureItem featureItem = new FeatureItem();
		featureItem.setFeatureName(featureName);
		featureItem.setFeatureType(featureType);
		featureItem.setValueStr("none");
		features.add(featureItem);
		return features;
	}


	/**
	 * 添加多个特征
	 * @param featureItems
	 * @param subList
	 */
	public static void addFeatureItems(List<FeatureItem> featureItems, List<FeatureItem> subList) {
		if (subList != null && subList.size() > 0){
			if(FmConstant.FEATURE_LIST_THRESHOLD > 0){
				if (subList.size() >= FmConstant.FEATURE_LIST_THRESHOLD){
					featureItems.addAll(subList.subList(0, FmConstant.FEATURE_LIST_THRESHOLD));
				}else{
					featureItems.addAll(subList);
				}
			}else{
				featureItems.addAll(subList);
			}
		}
	}

	/**
	 * 添加单个特征
	 * @param featureItems
	 * @param featureItem
	 */
	public static void addSingleFeatureItem(List<FeatureItem> featureItems, FeatureItem featureItem) {
		if (featureItem != null) {
			featureItems.add(featureItem);
		}
	}



}
