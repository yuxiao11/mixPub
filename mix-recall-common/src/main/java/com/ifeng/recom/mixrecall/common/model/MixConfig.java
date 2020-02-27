package com.ifeng.recom.mixrecall.common.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by geyl on 2017/11/27.
 */
@ConfigurationProperties(prefix = "cotag")
@PropertySource(value = {"classpath:application.properties"},encoding="utf-8")
@Component
public class MixConfig {

    private String longNum;
    private String recentNum;
    private String lastNum;


    public String getLongNum() {
        return longNum;
    }

    public void setLongNum(String longNum) {
        this.longNum = longNum;
    }

    public String getRecentNum() {
        return recentNum;
    }

    public void setRecentNum(String recentNum) {
        this.recentNum = recentNum;
    }

    public String getLastNum() {
        return lastNum;
    }

    public void setLastNum(String lastNum) {
        this.lastNum = lastNum;
    }
}
