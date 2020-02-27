package com.ifeng.recom.mixrecall.prerank;


import com.ifeng.recom.mixrecall.prerank.constant.CTRConstant;
import com.ifeng.recom.mixrecall.prerank.ctrmodel.CtrModelRedisClusterUtil;
import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 模型管理工具类
 * Created by jibin on 2017/5/5.
 */
@Service
public class FMModelUtil {

    private final static Logger logger = LoggerFactory.getLogger(FMModelUtil.class);


    @Autowired
    CtrModelRedisClusterUtil ctrModelRedisClusterUtil;

    /**
     * 模型map，按名字存储多模型
     */
    private static HashMap<String, FMModelEntity> MODEL_MAP = new HashMap<String, FMModelEntity>();

    /**
     * 模型文件名后缀
     */
    private static final String MODEL_NAME_NAME = ".model";
    private static final String FM_DELTA_MODEL_PREFIX = "delta-";


    /**
     * 初始化模型
     *
     * @throws Exception
     */
    public void init(String modelPath) throws Exception {
        MODEL_MAP = readModelsByRedis();
    }


    public HashMap<String, FMModelEntity> readModelsByRedis() throws Exception {
        HashMap<String, FMModelEntity> modelMap = new HashMap<>();
        Set<String> keys_all = ctrModelRedisClusterUtil.smembers(CTRConstant.KEY_CTR_FMMODEL_TO_ALL);
        if (keys_all != null && keys_all.size() > 0) {
            for (String key : keys_all) {
                if (key.endsWith(MODEL_NAME_NAME)) {
                    String flowtag = key.substring(0, key.length()-MODEL_NAME_NAME.length());
                    FMModelEntity model = readModelByRedisKey(key);
                    if (model != null) {
                        modelMap.put(flowtag, model);
                    }
                } else {
                    logger.error("FM模型redis数据加载失败，文件名不合法  name:{}", key);
                }
            }
            return modelMap;
        } else {
            throw new Exception("FM模型 加载失败，keys_all is wrong!\tkeys_all: " + keys_all.toString());
        }
    }


    /**
     * load FM模型数据进内存
     */
    private FMModelEntity readModelByRedisKey(String key) throws NumberFormatException, IOException {
        List<String> strList = ctrModelRedisClusterUtil.scanModelList(key);
        if (strList == null || strList.size() == 0) {
            return null;
        }
        double intercept = 0.0d;
        THashMap<String, double[]> weightsMap = new THashMap<>();
        int invalid = 0;
        for (String line : strList) {
            String[] array = line.split("\t");
            if (array.length < 2) {
                invalid += 1;
                continue;
            }
            if (array[0].equals("0")) {
                intercept = Double.parseDouble(array[1]);
            } else {
                String feature = array[0];
                String[] vecStr = array[1].split(",");
                if (vecStr.length != CTRConstant.FM_K + 1) {
                    throw new IllegalArgumentException("wrong length vector: " + array[1]);
                }
                double [] vector = new double[vecStr.length];
                for (int i = 0; i < vecStr.length; i++) {
                    vector[i] = Double.parseDouble(vecStr[i]);
                }
                weightsMap.put(feature, vector);
            }
        }
        logger.info("初始化成功, 模型key: {}, weights size: {}, intercept: {}, invalid: {}", key, weightsMap.size(), intercept, invalid);
        return new FMModelEntity(intercept, weightsMap);
    }


    /**
     * 从redis中增量更新指定的模型
     * @param key
     * @throws Exception
     */
    public void updateModelByName(String key) throws Exception {
        if (key.endsWith(MODEL_NAME_NAME)) {                // key例如：headline_0828.model
            String deltaKey = FM_DELTA_MODEL_PREFIX + key;  // 模型增量key例如：delta-headline_0828.model
            String flowTag = key.substring(0, key.length()-MODEL_NAME_NAME.length());
            FMModelEntity deltaModel = readModelByRedisKey(deltaKey);
            if (deltaModel == null) {
                logger.info("delta model is null");
            } else {
                logger.info("delta model key: {}, size: {}", deltaKey, deltaModel.size());
            }

            if (deltaModel != null) {
                FMModelEntity currentModel = MODEL_MAP.get(flowTag);
                FMModelEntity newModel = mergeModelEntity(currentModel, deltaModel);
                logger.info("new model size: {}", newModel.size());
                MODEL_MAP.put(flowTag, newModel);
            }
        }
    }

    private FMModelEntity mergeModelEntity(FMModelEntity model, FMModelEntity deltaModel) {
        double intercept = model.getIntercept();
        if (deltaModel.getIntercept() != 0.0d) {
            intercept = deltaModel.getIntercept();
        }
        THashMap<String, double[]> weights = new THashMap<>();
        weights.putAll(model.getWeights());
        weights.putAll(deltaModel.getWeights());
        return new FMModelEntity(intercept, weights);
    }


    /**
     * 根据模型名字获取对应模型
     *
     * @param modelName 模型名字
     * @return 返回对应模型，如不存在该名字则返回null
     */
    public static FMModelEntity getModelByName(String modelName) {
        return MODEL_MAP.get(modelName);
    }

}

