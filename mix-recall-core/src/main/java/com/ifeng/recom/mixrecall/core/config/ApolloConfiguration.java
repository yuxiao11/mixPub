package com.ifeng.recom.mixrecall.core.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ifeng.recom.mixrecall.common.config.ApplicationConfig;
import com.ifeng.recom.mixrecall.common.config.BossUserConfig;
import com.ifeng.recom.mixrecall.common.config.NumRatioConfig;
import com.ifeng.recom.mixrecall.common.config.TestUserConfig;
import com.ifeng.recom.mixrecall.common.config.constant.ApolloConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


/**
 * Created by lilg1 on 2018/4/12.
 */
public class ApolloConfiguration {

    private static Logger logger = LoggerFactory.getLogger(ApolloConfiguration.class);

    @ApolloConfig
    private Config config;


    @ApolloConfig(ApolloConstant.Debug_User_Key)
    private Config debugConfig;

    @ApolloConfig(ApolloConstant.Debug_User_Key)
    private Config bossConfig;

    @ApolloConfig(ApolloConstant.Pull_Num_Ratio)
    private Config numRatioConfig;


    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    TestUserConfig testUserConfig;

    @Autowired
    BossUserConfig bossUserConfig;

    @Autowired
    NumRatioConfig numRatioMapConfig;





    @PostConstruct
    public void init(){
        //初始化全局配置
        applicationConfig.init(config);
        //初始化debug用户配置
        testUserConfig.init(debugConfig);
        //初始化boss用户配置
        bossUserConfig.init(bossConfig);
        //初始化动态分配策略
        numRatioMapConfig.init(numRatioConfig);

    }


    @ApolloConfigChangeListener("application")
    public void onChange(ConfigChangeEvent changeEvent) {
        applicationConfig.onChangeJob(changeEvent);

    }

    @ApolloConfigChangeListener(ApolloConstant.Debug_User_Key)
    public void onDebugUserChange(ConfigChangeEvent changeEvent) {
        testUserConfig.onChangeJob(changeEvent);
    }

    @ApolloConfigChangeListener(ApolloConstant.Debug_User_Key)
    public void onBossUserChange(ConfigChangeEvent changeEvent) {
        bossUserConfig.onChangeJob(changeEvent);
    }

    @ApolloConfigChangeListener(ApolloConstant.Debug_User_Key)
    public void onNumRatioMapChange(ConfigChangeEvent changeEvent) {
        numRatioMapConfig.onChangeJob(changeEvent);
    }

    public static void main(String[] args){
        ApolloConfiguration s = new ApolloConfiguration();
        s.init();
    }
}
