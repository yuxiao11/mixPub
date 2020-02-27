package com.ifeng.recom.mixrecall.prerank.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.Properties;

/**
 * 读取spring中配置文件
 * spring配置文件最终会有{@link org.springframework.core.io.support.PropertiesLoaderSupport#mergeProperties()}
 * 方法加载配置文件，重写此方法，并在xml中配置加载配置文件的类为此类即可获取配置
 */
public class BaseConfig extends PropertyPlaceholderConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(PropertyPlaceholderConfigurer.class);

    private static Properties props;

    @Override
    protected Properties mergeProperties() throws IOException {
        props = super.mergeProperties();
        return props;
    }

    public static String getProperty(String key) {
        if(props == null) {
            return null;
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key,String defaultValue) {
        if(props == null) {
            return null;
        }
        String value = props.getProperty(key);
        return value == null ? defaultValue : value;
    }

    public static String setProperty(String key,String value){
        if(props==null){
            return null;
        }
        props.setProperty(key,value);
        return props.getProperty(key);
    }

    public static Properties getAllProperty() {
        return props;
    }
}