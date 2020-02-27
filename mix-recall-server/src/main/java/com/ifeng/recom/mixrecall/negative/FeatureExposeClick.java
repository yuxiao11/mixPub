package com.ifeng.recom.mixrecall.negative;

/**
 * Created by zhaohh @ 2018-03-15 11:09
 * 记录一个特征词的曝光和点击，据此对包含此feature的文章CTR作调整
 **/
public class FeatureExposeClick {
	// 特征词类别
	private String type; //C sc Topic等
	// 特征词
	private String featureWord;//特征词 比如 体育
	// 曝光数
	private double expose; //曝光数 此处乘以权重进行降权
	// 点击数
	private double click;//点击 此处乘以权重进行降权

	public FeatureExposeClick(String type, String featureWord) {
		this(type, featureWord, 0, 0);
	}

	public FeatureExposeClick(String type, String featureWord, int expose, int click) {
		this.type = type;
		this.featureWord = featureWord;
		this.expose = expose;
		this.click = click;
	}

	// 增加曝光和点击数
	public void addExpose(double n) {
		this.expose += n;
	}
	public void addClick(double n) {
		this.click += n;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFeatureWord() {
		return featureWord;
	}

	public void setFeatureWord(String featureWord) {
		this.featureWord = featureWord;
	}

	public double getExpose() {
		return expose;
	}

	public void setExpose(double expose) {
		this.expose = expose;
	}

	public double getClick() {
		return click;
	}

	public void setClick(double click) {
		this.click = click;
	}
}
