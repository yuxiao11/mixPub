package com.ifeng.recom.mixrecall.common.config;

import com.ctrip.framework.apollo.Apollo;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ifeng.recom.mixrecall.common.config.constant.ApolloConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lilg1 on 2017/11/2.
 * 从配置中心获取debug用户配置
 */
@Service
public class BossUserConfig {

    private static Map<String,Set<String>> bossUserMap;

    private static Logger logger = LoggerFactory.getLogger(BossUserConfig.class);

    static {
        bossUserMap = Maps.newHashMap();
    }

    public void init(Config bossUserConfig){
        String yml = bossUserConfig.getProperty(ApolloConstant.boss_users_key, "");
        loadBossUserConfig(yml);
    }

    public void onChangeJob(ConfigChangeEvent configChangeEvent){
        //property未更新，不执行修改
        if(!configChangeEvent.changedKeys().contains(ApolloConstant.boss_users_key)){
            return;
        }
        ConfigChange configChange = configChangeEvent.getChange(ApolloConstant.boss_users_key);
        String yml = configChange.getNewValue();
        logger.info("update debug user configuration:  {}", yml);
        loadBossUserConfig(yml);
    }

    /**
     *
     * @param updateBossUserMap
     */
    public void updateBossUsers(Map<String,Set<String>> updateBossUserMap){
        if(updateBossUserMap !=null){
            bossUserMap.putAll(updateBossUserMap);
        }
    }

    /**
     *
     * @param key
     * @param userSet
     */
    public void putUserSet(String key,Set<String> userSet){
        if(bossUserMap !=null ){
            bossUserMap.put(key,userSet);
        }
    }

    /**
     *
     * @param bossUserSetKey
     * @return
     */
    public static Set<String> getBossUser(String bossUserSetKey){
        Set<String> bossUsers = bossUserMap.get(bossUserSetKey);
        if(bossUsers != null){
            return bossUsers;
        }
        return Sets.newHashSet();
    }

    /**
     * 解析yml配置到debug用户组
     */
    public void loadBossUserConfig(String yml) {
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
                this.putUserSet(key, userSet);
            }
        } catch (Exception e) {
            logger.error("parse debug user configuration error: {}", e);
        }
    }
}
