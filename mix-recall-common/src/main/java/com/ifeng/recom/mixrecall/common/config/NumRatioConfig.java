package com.ifeng.recom.mixrecall.common.config;

import com.ctrip.framework.apollo.Apollo;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ifeng.recom.mixrecall.common.config.constant.ApolloConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class NumRatioConfig {
    private static Map<String,Map<String, Map<String,String>>> NumRationConfigMap;

    private static Logger logger = LoggerFactory.getLogger(Apollo.class);

    static {
        NumRationConfigMap = Maps.newHashMap();
    }

    public  void init(Config debugConfig) {
        String yml = debugConfig.getProperty(ApolloConstant.Pull_Num_Ratio, "");
        loadNumRatioConfig(yml);
    }
    public  void onChangeJob(ConfigChangeEvent configChangeEvent) {
        //property未更新，不执行修改
        if (!configChangeEvent.changedKeys().contains(ApolloConstant.Test_Users)) {
            return;
        }
        ConfigChange configChange = configChangeEvent.getChange(ApolloConstant.Test_Users);
        String yml = configChange.getNewValue();
        logger.info("update debug user configuration:  {}", yml);
        loadNumRatioConfig(yml);
    }

    /**
     * @param updateTestUserMap
     */
//    public  void updateTestUsers(Map<String, Set<String>> updateTestUserMap) {
//        if (updateTestUserMap != null) {
//            ColdStartPolicyMap.putAll(updateTestUserMap);
//        }
//    }

    /**
     * @param key
     * @param userSet
     */
    public  void putUserRatioMap(String key, Map<String,Map<String,String>> userSet) {
        if (NumRationConfigMap != null) {
            NumRationConfigMap.put(key, userSet);
        }
    }

    /**
     * @param debugerSetKey
     * @return
     */
    public static Map<String,Map<String,String>> getRatioNum(String userkey) {
        Map<String,Map<String,String>> ratioMap = NumRationConfigMap.get(userkey);
        if (ratioMap != null) {
            return ratioMap;
        }
        return Maps.newHashMap();
    }

    /**
     * 解析yml配置到动态分配召回数量
     */
    public  void loadNumRatioConfig(String yml) {
        Yaml yaml = new Yaml();
        try {
            if (StringUtils.isBlank(yml)) {
                logger.warn("parse debug user configuration empty! ");
            }
            Object obj = yaml.load(yml);
            Map<String, Object> userMap = (Map) obj;
            for (String key : userMap.keySet()) {
                Map<String,Map<String,String>> maps = (Map<String,Map<String,String>>) userMap.get(key);
//                Set<String> userSet = Sets.newHashSet();

                putUserRatioMap(key, maps);
            }
        } catch (Exception e) {
            logger.error("parse debug user configuration error: {}", e);
        }

    }
    public static void main(String[] args){
//        NumRatioConfig.init()
        Map<String,Map<String,String>> s = NumRatioConfig.getRatioNum(ApolloConstant.Pull_Num_Ratio);
        int ss = 5;

    }

}
