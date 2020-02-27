package com.ifeng.recom.mixrecall.prerank.tools;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhaohh @ 2017-09-30 17:16
 **/
public class MediaEvalLevelCache implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(MediaEvalLevelCache.class);
	private static final long serialVersionUID = 4013090960066310931L;

	private Map<String, String> videoSourceEvalLevelMap = new HashMap<>();
	private Map<String, String> docSourceEvalLevelMap = new HashMap<>();

	/**
	 * 读取数据初始化
	 */
	public void init() {
		try {
			this.videoSourceEvalLevelMap = loadVideoSourceMap();
			this.docSourceEvalLevelMap = loadDocSourceMap();
			logger.info("media eval level init success, video size: {}, doc size: {}", videoSourceEvalLevelMap.size(), docSourceEvalLevelMap.size());
			System.out.println(String.format("media eval level init success, video size: %s, doc size: %s", videoSourceEvalLevelMap.size(), docSourceEvalLevelMap.size()));
		} catch (Exception e) {
			logger.error("!!! media eval level init failed: {}", e);
			e.printStackTrace();
		}
	}

	/**
	 * 更新数据
	 */
	public void update() {
		try {
			Map<String, String> videoMap = loadVideoSourceMap();
			Map<String, String> docMap = loadDocSourceMap();
			if (videoMap.size() > 0) {
				this.videoSourceEvalLevelMap = videoMap;
			}
			if (docMap.size() > 0) {
				this.docSourceEvalLevelMap = docMap;
			}
			logger.info("media eval level update success, video size: {}, doc size: {}", videoSourceEvalLevelMap.size(), docSourceEvalLevelMap.size());
		} catch (Exception e) {
			logger.error("!!! media eval level update failed: {}", e);
			e.printStackTrace();
		}
	}

	private Map<String, String> loadVideoSourceMap() {
		List<SourceInfoItem> videoSourceInfoItems = SourceInfoJdocUtil.getVideoSourceInfo();
		return convertToMap(videoSourceInfoItems);
	}
	private Map<String, String> loadDocSourceMap() {
		List<SourceInfoItem> docSourceInfoItems = SourceInfoJdocUtil.getDocSourceInfo();
		return convertToMap(docSourceInfoItems);
	}

	private Map<String, String> convertToMap(List<SourceInfoItem> sourceInfoItems) {
		Map<String, String> sourceEvalLevelMap = new HashMap<>();
		for (SourceInfoItem item : sourceInfoItems) {
			sourceEvalLevelMap.put(item.getManuscriptName(), item.getEvalLevel());
		}
		return sourceEvalLevelMap;
	}

	private Map<String, String> loadDataFromRedis() {
		Jedis jedis = getJedis();
		Set<String> allKeys = jedis.keys("*");
		System.out.println("all media size: " + allKeys.size());

		// pipeline to get response
		Map<String, Response<String>> responseMap = new HashMap<>();
		Pipeline pipeline = jedis.pipelined();
		for (String key : allKeys) {
			Response<String> response = pipeline.hget(key, "evalLevel");
			responseMap.put(key, response);
		}
		pipeline.sync();

		// get eval value
		Map<String, String> evalLevelMap = new HashMap<>();
		for (String key : responseMap.keySet()) {
			evalLevelMap.put(key, responseMap.get(key).get());
		}
		jedis.close();

		return evalLevelMap;
	}

	private Jedis getJedis() {
		Jedis jedis = new Jedis("10.90.3.55", 6379);
		jedis.select(14);
		return jedis;
	}

	/**
	 * 获取文章类型对应的媒体评级
	 * @param doctype
	 * @param media
	 * @return
	 */
	public String getMediaEvalLevel(String doctype, String media) {
		String defaultValue = "-";
		if (media == null) {
			return defaultValue;
		}

		// 获取对应的媒体评级，优先取对应文章类型的评级
		String evalLevel;
		if (doctype != null && doctype.equals("video")) {
			evalLevel = getPreferedValue(videoSourceEvalLevelMap, docSourceEvalLevelMap, media);
		} else {
			evalLevel = getPreferedValue(docSourceEvalLevelMap, videoSourceEvalLevelMap, media);
		}

		if (evalLevel == null) {
			return defaultValue;
		} else {
			return evalLevel;
		}
	}

	private String getPreferedValue(Map<String, String> firstMap, Map<String, String> secondMap, String key) {
		String firstValue = firstMap.get(key);
		if (firstValue != null) {
			return firstValue;
		}
		return secondMap.get(key);
	}

	public static void main(String[] args) throws IOException {
		MediaEvalLevelCache cache = new MediaEvalLevelCache();
		cache.init();
		System.out.println(cache.getMediaEvalLevel("doc", "快手达人"));
		System.out.println(cache.getMediaEvalLevel("doc", "明星大剧抢先看"));
		System.out.println(cache.getMediaEvalLevel("doc", null));
	}

}