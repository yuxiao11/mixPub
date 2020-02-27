package com.ifeng.recom.mixrecall.core.config;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lilg1 on 2018/4/12.
 */
@EnableApolloConfig({"application","DebugUsers"})
@Configuration
public class AppConfig {


    @Bean
    public ApolloConfiguration getApolloConf() {
        return new ApolloConfiguration();
    }
}
