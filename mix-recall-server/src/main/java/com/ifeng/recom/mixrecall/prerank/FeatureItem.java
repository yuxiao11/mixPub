package com.ifeng.recom.mixrecall.prerank;

import com.ifeng.recom.mixrecall.prerank.constant.Constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 特征返回值：根据不同操作类型封装特征抽取后的数据
 * Created by zhaozp on 2017/5/11.
 */
public class FeatureItem implements Serializable {
	
	/**
	 * 特征ID
	 */
	private Long featureId;
	
	/**
	 * 特征名称
	 */
	private String featureName;
	
	/**
	 * 特征类型
	 */
	private int featureType;
	
	/**
	 * 特征数值id，转化后的值
	 */
	private Long valueId;
	
	/**
	 * 特征转化后的值
	 */
	private String valueStr;

	public String getOriginStr() {
		return originStr;
	}

	public void setOriginStr(String originStr) {
		this.originStr = originStr;
	}

	/**
	 * 特征原始值
	 */
	private String originStr = "-";
	
	/**
	 * 特征取值，统一double显示
	 */
	private double value;

	// constructor with another feature item
	public FeatureItem(FeatureItem otherItem) {
		super();
		this.featureId = otherItem.getFeatureId();
		this.featureName = otherItem.getFeatureName();
		this.featureType = otherItem.getFeatureType();
		this.valueId = otherItem.getValueId();
		this.valueStr = otherItem.getValueStr();
		this.value = otherItem.getValue();
	}

	public FeatureItem(Long featureId, String featureName, int featureType, Long valueId,
                       String valueStr, double value) {
		super();
		this.featureId = featureId;
		this.featureName = featureName;
		this.featureType=featureType;
		this.valueId = valueId;
		this.valueStr = valueStr;
		this.value = value;
	}
	
	public FeatureItem(Long featureId, int featureType, Long valueId){
		this(featureId,null,featureType,valueId,null,Double.NaN);
	}
	public FeatureItem(Long featureId, String featureName, int featureType, Long valueId){
		this(featureId,featureName,featureType,valueId,null,Double.NaN);
	}
	
	public FeatureItem(Long featureId, int featureType, String valueStr){
		this(featureId,null,featureType,0l,valueStr,Double.NaN);
	}
	public FeatureItem(Long featureId, String featureName, int featureType, String valueStr){
		this(featureId,featureName,featureType,0l,valueStr,Double.NaN);
	}
	
	public FeatureItem(Long featureId, int featureType, double value){
		this(featureId,null,featureType,0l,null,value);
	}


	// 默认取指标类型
	public FeatureItem(Long featureId, Long valueId){
		this(featureId,0,valueId);
	}
	
	public FeatureItem(Long featureId, String valueStr){
		this(featureId,0,valueStr);
	}
	
	public FeatureItem(Long featureId, double value){
		this(featureId,0,value);
	}
	
	public FeatureItem(){
		
	}


	public Long getFeatureId() {
		return featureId;
	}


	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}


	public String getFeatureName() {
		return featureName;
	}


	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}


	public Long getValueId() {
		return valueId;
	}


	public void setValueId(Long valueId) {
		this.valueId = valueId;
	}


	public String getValueStr() {
		return valueStr;
	}


	public void setValueStr(String valueStr) {
		this.valueStr = valueStr;
	}


	public double getValue() {
		return value;
	}


	public void setValue(double value) {
		this.value = value;
	}
	
	public int getFeatureType() {
		return featureType;
	}

	public void setFeatureType(int featureType) {
		this.featureType = featureType;
	}
	

	private String featureString = null;
	public String getFeatureString() {
		if (this.featureString == null) {
			this.featureString = toString();
		}
		return this.featureString;
	}


	@Override
	public String toString() {
		StringBuilder sb =new StringBuilder();
		sb.append(this.featureName).append(Constant.FEATURE_SPLIT_TAG).append(this.valueStr).append(Constant.FEATURE_SPLIT_TAG).append(this.featureType);
		return sb.toString();
	}

	public String toStringWithOriginal(){
		StringBuilder sb =new StringBuilder();
		sb.append(this.featureName).append(Constant.FEATURE_SPLIT_TAG).append(this.valueStr).append(Constant.FEATURE_SPLIT_TAG).
				append(this.originStr).append(Constant.FEATURE_SPLIT_TAG).append(this.featureType);
		return sb.toString();
	}
	

	
	/**
	 * 解析稀疏型表示的特征样本
	 * @param lines
	 * @return
	 */
	public static List<FeatureItem> parse(String[] lines){
		if( null == lines || lines.length == 0){
			return null;
		}
		List<FeatureItem> list = new ArrayList<FeatureItem>();
		String[] splits = null;
		FeatureItem featureItem = null;
		for(String line:lines){
			splits = line.split(":");
			if( splits.length < 2){
				continue;
			}else{
				featureItem = new FeatureItem(Long.parseLong(splits[0]),0,splits[1]);
				list.add(featureItem);
			}
		}
		return list;
	}
	


}
