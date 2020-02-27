package com.ifeng.recom.mixrecall.prerank;

import com.alibaba.fastjson.JSONObject;
import com.ifeng.recom.mixrecall.prerank.constant.CTRConstant;

import java.util.ArrayList;
import java.util.List;
import static com.ifeng.recom.mixrecall.common.model.JsonUtils.writeToJSON;


public class AdNatureEntity {
	//内部类用于存储新闻、视频item的特征和权重
	public class AdFeatureAndValue{
		//特征id1
		long setId;
		//特征id2
		long valueId;
		//特征对应值
		double value;
		
		public long getSetId() {
			return setId;
		}

		public void setSetId(long setId) {
			this.setId = setId;
		}

		public long getValueId() {
			return valueId;
		}

		public void setValueId(long valueId) {
			this.valueId = valueId;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public AdFeatureAndValue(long setId, long valueId, double value) {
			this.setId = setId;
			this.valueId = valueId;
			this.value = value;
		}
	}
	
	List<AdFeatureAndValue> AdFeatureAndValues = null;
	private String sId;
	private String expid;
	private String itemId;
	private String trackId;

	
	public AdNatureEntity(String sId, String expid, String itemId, String trackId) {
		this.sId = sId;
		this.expid = expid;
		this.itemId = itemId;
		this.trackId=trackId;
		AdFeatureAndValues = new ArrayList<AdFeatureAndValue>();
	}
	
	public void addAd(long setId, long valueId, double value) {
		AdFeatureAndValues.add(new AdFeatureAndValue(setId, valueId, value));
	}
	
	private String listToString() {
		StringBuilder sb = new StringBuilder();
		for(AdFeatureAndValue adFeatureAndValue : AdFeatureAndValues) {
			if (sb.length() > 0) {
				sb.append(CTRConstant.Symb_Comma);
			}
			sb.append(adFeatureAndValue.getSetId()).append(CTRConstant.Symb_Colon)
			.append(adFeatureAndValue.getValueId()).append(CTRConstant.Symb_Colon)
			.append(adFeatureAndValue.getValue());
		}
		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(sId).append(CTRConstant.Symb_Tab)
		.append(itemId).append(CTRConstant.Symb_Tab)
		.append(writeToJSON(AdFeatureAndValues)).append(CTRConstant.Symb_Tab)
		.append(expid).append(CTRConstant.Symb_Tab)
		.append(trackId);
		return sb.toString();
	}

	public JSONObject toJsonObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sid", sId);
		jsonObject.put("itemid", itemId);
		jsonObject.put("userid", trackId);
		jsonObject.put("expid", expid);
		jsonObject.put("data", AdFeatureAndValues);
		return jsonObject;
	}
	
//	public void pushToQueue() {
//		if (AdFeatureAndValues.size() > 0) {
//			Manage.addAdNatureQueue(this);
//		}
//	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public List<AdFeatureAndValue> getAdFeatureAndValues() {
		return AdFeatureAndValues;
	}

	public void setAdFeatureAndValues(List<AdFeatureAndValue> adFeatureAndValues) {
		AdFeatureAndValues = adFeatureAndValues;
	}

	public String getsId() {
		return sId;
	}

	public void setsId(String sId) {
		this.sId = sId;
	}

	public String getExpid() {
		return expid;
	}

	public void setExpid(String expid) {
		this.expid = expid;
	}

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	public static void main(String[] args) {

	}

}
