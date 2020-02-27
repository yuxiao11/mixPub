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

/**
 * Created by lilg1 on 2018/4/12.
 */
@Service
public class TestUserConfig {

    private static Map<String, Set<String>> testUserMap;

    private static Logger logger = LoggerFactory.getLogger(Apollo.class);

    static {
        testUserMap = Maps.newHashMap();
    }

    public void init(Config debugConfig) {
        String yml = debugConfig.getProperty(ApolloConstant.Test_Users, "");
        loadTestUserConfig(yml);
    }

    public  void onChangeJob(ConfigChangeEvent configChangeEvent) {
        //property未更新，不执行修改
        if (!configChangeEvent.changedKeys().contains(ApolloConstant.Test_Users)) {
            return;
        }
        ConfigChange configChange = configChangeEvent.getChange(ApolloConstant.Test_Users);
        String yml = configChange.getNewValue();
        logger.info("update debug user configuration:  {}", yml);
        loadTestUserConfig(yml);
    }

    /**
     * @param updateTestUserMap
     */
    public  void updateTestUsers(Map<String, Set<String>> updateTestUserMap) {
        if (updateTestUserMap != null) {
            testUserMap.putAll(updateTestUserMap);
        }
    }

    /**
     * @param key
     * @param userSet
     */
    public  void putUserSet(String key, Set<String> userSet) {
        if (testUserMap != null) {
            testUserMap.put(key, userSet);
        }
    }

    /**
     * @param debugerSetKey
     * @return
     */
    public static Set<String> getTestUser(String debugerSetKey) {
        Set<String> debugUsers = testUserMap.get(debugerSetKey);
        if (debugUsers != null) {
            return debugUsers;
        }
        return Sets.newHashSet();
    }

    /**
     * 解析yml配置到test用户组
     */
    public  void loadTestUserConfig(String yml) {
        Yaml yaml = new Yaml();
        try {
            if (StringUtils.isBlank(yml)) {
                logger.warn("parse debug user configuration empty! ");
            }
            Object obj = yaml.load(yml);
            Map<String, Object> userMap = (Map) obj;
            for (String key : userMap.keySet()) {
                List<Object> list = (List<Object>) userMap.get(key);
                Set<String> userSet = Sets.newHashSet();
                list.forEach(x -> userSet.add(String.valueOf(x)));
                logger.info("init debug:{} , users:{}", key, userSet);
                putUserSet(key, userSet);
            }

        } catch (Exception e) {
            logger.error("parse debug user configuration error: {}", e);
        }

    }
}
