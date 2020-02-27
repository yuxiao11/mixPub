package com.ifeng.recom.mixrecall.prerank.modelconfig;

/**
 * Created by zhaohh @ 2018-03-06 17:21
 **/
public class ModelConfig {
	private boolean considerDurationWeight;
	private String modelType;

	public String getModelType() {
		return modelType;
	}
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	public boolean isConsiderDurationWeight() {
		return considerDurationWeight;
	}
	public void setConsiderDurationWeight(boolean considerDurationWeight) {
		this.considerDurationWeight = considerDurationWeight;
	}
}
