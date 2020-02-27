package com.ifeng.recom.mixrecall.common.tool;

/**
 * Created by geyl on 2018/4/19.
 */

import com.ifeng.recom.mixrecall.common.constant.LogFileName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {
    public static <T> Logger Logger(Class<T> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 打印到指定的文件下
     *
     * @param desc 日志文件名称
     * @return
     */
    public static Logger Logger(LogFileName desc) {
        return LoggerFactory.getLogger(desc.getLogFileName());
    }
}
