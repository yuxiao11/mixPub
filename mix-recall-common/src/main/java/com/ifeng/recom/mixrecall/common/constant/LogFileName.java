package com.ifeng.recom.mixrecall.common.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by geyl on 2018/4/19.
 */
public enum LogFileName {
    ACCESS("userAccess"),
    TIMEOUT("timeout"),
    LOGSTASH("logstash");

    private String logFileName;

    LogFileName(String fileName) {
        this.logFileName = fileName;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public static LogFileName getAwardTypeEnum(String value) {
        LogFileName[] arr = values();
        for (LogFileName item : arr) {
            if (null != item && StringUtils.isNotBlank(item.logFileName)) {
                return item;
            }
        }
        return null;
    }

}
