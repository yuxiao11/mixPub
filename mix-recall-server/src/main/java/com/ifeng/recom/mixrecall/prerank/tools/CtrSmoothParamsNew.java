package com.ifeng.recom.mixrecall.prerank.tools;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;
import java.util.*;

/**
 * Created by zhaohh @ 2018-01-24 20:02
 **/
public class CtrSmoothParamsNew implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(CtrSmoothParamsNew.class);
	private static final long serialVersionUID = -120420984962441518L;

	private Map<String, Map<String, double[]>> smoothParams;

	private final double LAST3H_DEFAULT_ALPHA = 2.0781619423708295;
	private final double LAST3H_DEFAULT_BETA = 23.314816857700695;

	private final double LAST1D_DEFAULT_ALPHA = 1.9408225304235116;
	private final double LAST1D_DEFAULT_BETA = 22.592027505990885;

	private final double LAST3D_DEFAULT_ALPHA = 1.8919059368497635;
	private final double LAST3D_DEFAULT_BETA = 21.575208607592042;

	private final double TOTAL_DEFAULT_ALPHA = 1.935656308838918;
	private final double TOTAL_DEFAULT_BETA = 20.078706379653088;

	private final String REDIS_KEY = "CTR_SMOOTH_PARAMS_NEW";

	public CtrSmoothParamsNew() {
		init();
	}

	public double[] getLast3hAlphaAndBeta(String topCategory) {
		return getAlphaAndBeta("last3h", topCategory, LAST3H_DEFAULT_ALPHA, LAST3H_DEFAULT_BETA);
	}
	public double[] getLast1dAlphaAndBeta(String topCategory) {
		return getAlphaAndBeta("last1d", topCategory, LAST1D_DEFAULT_ALPHA, LAST1D_DEFAULT_BETA);
	}
	public double[] getLast3dAlphaAndBeta(String topCategory) {
		return getAlphaAndBeta("last3d", topCategory, LAST3D_DEFAULT_ALPHA, LAST3D_DEFAULT_BETA);
	}
	public double[] getTotalAlphaAndBeta(String topCategory) {
		return getAlphaAndBeta("total", topCategory, TOTAL_DEFAULT_ALPHA, TOTAL_DEFAULT_BETA);
	}

	/**
	 * 获取数据
	 * @param type
	 * @param topCategory
	 * @param defaultAlpha
	 * @param defaultBeta
	 * @return
	 */
	private double[] getAlphaAndBeta(String type, String topCategory, double defaultAlpha, double defaultBeta) {
		if (!smoothParams.containsKey(type)) {
			return new double [] {LAST1D_DEFAULT_ALPHA, LAST1D_DEFAULT_BETA};
		}
		Map<String, double[]> paramsMap = smoothParams.get(type);
		if (topCategory == null || !paramsMap.containsKey(topCategory)) {
			if (paramsMap.containsKey("OverAll")) {
				return paramsMap.get("OverAll");
			} else {
				return new double [] {defaultAlpha, defaultBeta};
			}
		}
		return paramsMap.get(topCategory);
	}

	/**
	 * 初始化数据
	 */
	public void init() {
		try {
			this.smoothParams = loadDataFromRedis();
			logger.info("init new ctr smooth parameters success, size: " + this.smoothParams.size());
		} catch (Exception e) {
			logger.error("!!! new ctr smooth parameters init failed: {}", e);
		}
	}

	/**
	 * 更新数据，重新load redis数据
	 */
	public void update() {
		try {
			this.smoothParams = loadDataFromRedis();
			logger.info("new ctr smooth params update success, size: " + smoothParams.size());
		} catch (Exception e) {
			logger.error("!!! new ctr smooth params update failed: {}", e);
		}
	}

	private Map<String, Map<String, double[]>> loadDataFromRedis() {
		JedisCluster client = RedisUtil.getStatJedisCluster();
		String paramStr = client.get(REDIS_KEY);
		JSONObject jsonObject = JSONObject.parseObject(paramStr);
		Map<String, Map<String, double[]>> result = new HashMap<>();
		result.put("last3h", parseParamsMap(jsonObject.getJSONObject("last3h")));
		result.put("last1d", parseParamsMap(jsonObject.getJSONObject("last1d")));
		result.put("last3d", parseParamsMap(jsonObject.getJSONObject("last3d")));
		result.put("total", parseParamsMap(jsonObject.getJSONObject("total")));

		return result;
	}

	private Map<String, double[]> parseParamsMap(JSONObject jsonObject) {
		Map<String, double[]> paramsMap = new HashMap<>();
		for (String topCategory : jsonObject.keySet()) {
			String alphaAndBeta = jsonObject.getString(topCategory);
			String [] arr = alphaAndBeta.split(":");
			double alpha = Double.parseDouble(arr[0]);
			double beta = Double.parseDouble(arr[1]);
			double [] params = new double[] {alpha, beta};
			paramsMap.put(topCategory, params);
		}
		return paramsMap;
	}

	public void test() {
		for (String type : smoothParams.keySet()) {
			System.out.println("=========== " + type + " ============");
			Map<String, double[]> paramsMap = smoothParams.get(type);
			final Map<String, Double> ctrMap = new HashMap<>();
			for (String topCategory : paramsMap.keySet()) {
				double [] params = paramsMap.get(topCategory);
				double alpha = params[0];
				double beta = params[1];
				double ctr = alpha / (alpha + beta);
				ctrMap.put(topCategory, ctr);
			}
			List<String> topCategories = new ArrayList<>(ctrMap.keySet());
			Collections.sort(topCategories, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return ctrMap.get(o1).compareTo(ctrMap.get(o2));
				}
			});
			for (String topCategory : topCategories) {
				System.out.println(topCategory + "\t" + ctrMap.get(topCategory) + "\t" + paramsMap.get(topCategory)[0] + "\t" + paramsMap.get(topCategory)[1]);
			}
 		}
	}

	public static void main(String[] args) {
		CtrSmoothParamsNew params = new CtrSmoothParamsNew();
		params.test();
	}

}
