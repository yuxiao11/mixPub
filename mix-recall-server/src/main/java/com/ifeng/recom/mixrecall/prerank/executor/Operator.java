package com.ifeng.recom.mixrecall.prerank.executor;

import com.ifeng.recom.mixrecall.prerank.FeatureItem;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.List;


/**
 * 特征操作算子定义
 * @author zhaozp
 *
 */
public abstract class Operator implements Serializable {
	
	protected Log mLOG = LogFactory.getLog(Operator.class);

	protected String mFEATURE_NAME;

	public Operator() {
		this.mFEATURE_NAME = getClass().getSimpleName();
	}

	public String getFeatureName() {
		return mFEATURE_NAME;
	}
	
	/**
	 * 特征操作算子计算函数
	 */
	public abstract List<FeatureItem> compute(FeatureContext entity, Long featureId, String featureName, int type, String attrDimension)  throws Exception;


}
