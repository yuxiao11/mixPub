package com.ifeng.recom.mixrecall.prerank.modelconfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaohh @ 2018-03-06 17:22
 **/
public class ModelConfigParser {
	private static final Log logger = LogFactory.getLog(ModelConfigParser.class);
	private static Map<String, ModelConfig> modelConfigMap = new HashMap<>();

	public static boolean loadModelConfigs(String configPath) throws IOException {
		File configFile = new File(configPath);
		if (!configFile.exists()) {
			logger.error("模型配置文件不存在: " + configPath);
			return false;
		}

		String jsonStr = FileUtils.readFileToString(configFile);
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		for (String key : jsonObject.keySet()) {
			JSONObject modelObject = jsonObject.getJSONObject(key);
			ModelConfig modelConfig = new ModelConfig();
			modelConfig.setConsiderDurationWeight(modelObject.getBoolean("consider_duration_weight"));
			modelConfig.setModelType(modelObject.getString("model_type"));
			modelConfigMap.put(key, modelConfig);
		}
		logger.info("成功读取模型配置: " + modelConfigMap.size());
		return true;
	}

	public static ModelConfig getModelConfig(String modelName) {
		if (modelConfigMap.containsKey(modelName)) {
			return modelConfigMap.get(modelName);
		}
		return modelConfigMap.get("default");
	}

	public static void main(String[] args) throws IOException {
		ModelConfigParser.loadModelConfigs("E:\\gitlab\\ifeng-recom-rank\\recom-rank-service\\recom-ctr-service\\src\\main\\resources\\file\\model_config\\model_config.json");
		System.out.println(ModelConfigParser.getModelConfig("headline_0109").isConsiderDurationWeight());
	}

}
