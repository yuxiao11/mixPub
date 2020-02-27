package com.ifeng.recom.mixrecall.prerank.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaohh @ 2017-11-23 14:19
 * 保存用户画像特征词和文章画像特征词匹配的结果
 * 包括匹配上的权重和位置
 **/
public class UIPortraitMatchResult implements Serializable {

	private static final long serialVersionUID = -6848055196094904260L;
	// 画像类型
	private boolean isNewPortrait;
	// 画像大小
	private int userPortraitSize;
	// 匹配上权重列表
	private List<Double> matchWeightList;
	// 匹配上的位置列表
	private List<Integer> matchPosList;		// 位置的取值范围是0到userPortraitSize-1

	// 匹配上权重之和（区分新旧画像减去0.5）
	private double sumWeight;
	public double getSumWeight() {
		return sumWeight;
	}
	public void setSumWeight(double sumWeight) {
		this.sumWeight = sumWeight;
	}

	// 初始化
	public UIPortraitMatchResult(boolean isNewPortrait, int userPortraitSize) {
		this.isNewPortrait = isNewPortrait;
		this.userPortraitSize = userPortraitSize;
		this.matchWeightList = new ArrayList<>();
		this.matchPosList = new ArrayList<>();
		this.sumWeight = 0.0;
	}

	// 添加match上的权重和位置
	public void addMatch(double matchWeight, int matchPos) {
		this.matchWeightList.add(matchWeight);
		this.matchPosList.add(matchPos);
	}

	// 抽取第几个match上的特征词的信息，k从1开始
	public double getKthMatchWeight(int k) {
		if (k > matchWeightList.size()) {
			return -1.0;
		}
		return matchWeightList.get(k-1);
	}
	public double getKthMatchRelativePos(int k) {
		if (k > matchPosList.size() || userPortraitSize == 0) {
			return -1.0;
		}

		int matchPos = matchPosList.get(k-1);
		return ((double)(matchPos + 1)) / userPortraitSize;
	}



}
