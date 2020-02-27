package com.ifeng.recom.mixrecall.prerank.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaohh @ 2017-12-13 11:16
 **/
public class MediaEvalLevelCacheManager {
	private final static Logger logger = LoggerFactory.getLogger(MediaEvalLevelCacheManager.class);

	private static MediaEvalLevelCache cache = null;

	public static void init() {
		cache = new MediaEvalLevelCache();
		cache.init();
	}

	public static MediaEvalLevelCache getInstance() {
		if (cache == null) {
			cache = new MediaEvalLevelCache();
			cache.init();
		}
		return cache;
	}

}
