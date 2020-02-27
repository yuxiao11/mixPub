package com.ifeng.recom.mixrecall.core;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lilg1 on 2017/11/20.
 *
 */
@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = MixCoreConfig.class)
public class MixCoreConfig {

}
