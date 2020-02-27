package com.ifeng.recom.mixrecall.prerank.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaohh @ 2018-01-25 15:03
 **/
public class CtrSmoothParamsManager {
	private final static Logger logger = LoggerFactory.getLogger(CtrSmoothParamsManager.class);

	private static CtrSmoothParams ctrSmoothParams = null;
	private static CtrSmoothParamsNew ctrSmoothParamsNew = null;

	public static void init() {
		ctrSmoothParams = new CtrSmoothParams();
		ctrSmoothParamsNew = new CtrSmoothParamsNew();
	}

	public static CtrSmoothParams getInstance() {
		if (ctrSmoothParams == null) {
			ctrSmoothParams = new CtrSmoothParams();
		}
		return ctrSmoothParams;
	}

	public static CtrSmoothParamsNew getNewInstance() {
		if (ctrSmoothParamsNew == null) {
			ctrSmoothParamsNew = new CtrSmoothParamsNew();
		}
		return ctrSmoothParamsNew;
	}

}
