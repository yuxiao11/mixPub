package com.ifeng.recom.mixrecall.prerank.tools;

import com.ifeng.recom.mixrecall.prerank.constant.CTRConstant;
import com.ifeng.recom.mixrecall.prerank.executor.FeatureVectorExtractor;
import com.ifeng.recom.mixrecall.prerank.modelconfig.ModelConfigParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class initCTRUtil {
    private static final Logger logger = LoggerFactory.getLogger(initCTRUtil.class);


    /**
     * 此处添加媒体评级缓存 计算CTR by yx 20191227
     */
    @PostConstruct
    public void initCTRPart(){

        try{
            // 初始化媒体评级
            MediaEvalLevelCacheManager.init();
            // 初始化历史CTR平滑参数信息
            CtrSmoothParamsManager.init();

            //初始化模型配置
            ModelConfigParser.loadModelConfigs(CTRConstant.MODEL_CONFIG_PATH);
//            initABTestParam(mixRequestInfo);

            //初始化特征
            FeatureVectorExtractor.loadFeatureConfigs(CTRConstant.FEATURE_PATH);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("CTR Part init error:{}",e);
        }


    }
}
