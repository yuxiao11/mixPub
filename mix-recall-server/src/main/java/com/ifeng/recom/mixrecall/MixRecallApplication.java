package com.ifeng.recom.mixrecall;

import com.ifeng.recom.mixrecall.common.MixCommonConfig;
import com.ifeng.recom.mixrecall.core.MixCoreConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by lilg1 on 2017/11/17.
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {MixRecallApplication.class, MixCoreConfig.class, MixCommonConfig.class})
@EnableScheduling
@EnableConfigurationProperties
@SpringBootApplication
@EnableAsync
@EnableCaching
public class MixRecallApplication {
    private static final Logger logger = LoggerFactory.getLogger(MixRecallApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MixRecallApplication.class, args);
        logger.info("start spring boot");
    }
}