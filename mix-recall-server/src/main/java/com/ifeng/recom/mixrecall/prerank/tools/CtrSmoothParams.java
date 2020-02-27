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
public class CtrSmoothParams implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(CtrSmoothParams.class);
	private static final long serialVersionUID = 1821888063275980888L;

	private Map<String, Map<String, double[]>> smoothParams;

	private final double LAST3H_DEFAULT_ALPHA = 1.7950168848378338;
	private final double LAST3H_DEFAULT_BETA = 16.306917373983477;

	private final double LAST1D_DEFAULT_ALPHA = 1.4436285434140308;
	private final double LAST1D_DEFAULT_BETA = 15.280826298382292;

	private final double TODAY_DEFAULT_ALPHA = 1.8081524936494586;
	private final double TODAY_DEFAULT_BETA = 17.186748453769436;

	private final String REDIS_KEY = "CTR_SMOOTH_PARAMS";

	public CtrSmoothParams() {
		init();
	}

	public double[] getLast3hAlphaAndBeta(String topCategory) {
		return getAlphaAndBeta("last3h", topCategory, LAST3H_DEFAULT_ALPHA, LAST3H_DEFAULT_BETA);
	}
	public double[] getLast1dAlphaAndBeta(String topCategory) {
		return getAlphaAndBeta("last1d", topCategory, LAST1D_DEFAULT_ALPHA, LAST1D_DEFAULT_BETA);
	}
	public double[] getTodayAlphaAndBeta(String topCategory) {
		return getAlphaAndBeta("today", topCategory, TODAY_DEFAULT_ALPHA, TODAY_DEFAULT_BETA);
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
			System.out.println("init ctr smooth parameters success, size: " + this.smoothParams.size());
			logger.info("init ctr smooth parameters success, size: " + this.smoothParams.size());
		} catch (Exception e) {
			logger.error("!!! ctr smooth parameters init failed: {}", e);
			e.printStackTrace();
		}
	}

	/**
	 * 更新数据，重新load redis数据
	 */
	public void update() {
		try {
			this.smoothParams = loadDataFromRedis();
			logger.info("ctr smooth params update success, size: " + smoothParams.size());
		} catch (Exception e) {
			logger.error("!!! ctr smooth params update failed: {}", e);
			e.printStackTrace();
		}
	}

	private Map<String, Map<String, double[]>> loadDataFromRedis() {
		JedisCluster client = RedisUtil.getStatJedisCluster();
		String paramStr = client.get(REDIS_KEY);
		JSONObject jsonObject = JSONObject.parseObject(paramStr);
		Map<String, Map<String, double[]>> result = new HashMap<>();
		result.put("last3h", parseParamsMap(jsonObject.getJSONObject("last3h")));
		result.put("last1d", parseParamsMap(jsonObject.getJSONObject("last1d")));
		result.put("today", parseParamsMap(jsonObject.getJSONObject("today")));

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
		CtrSmoothParams params = new CtrSmoothParams();

		System.out.println(params.getLast1dAlphaAndBeta("hello")[0]);
		System.out.println(params.getLast1dAlphaAndBeta("OverAll")[0]);

		params.test();

		System.out.println("done");
	}

}
