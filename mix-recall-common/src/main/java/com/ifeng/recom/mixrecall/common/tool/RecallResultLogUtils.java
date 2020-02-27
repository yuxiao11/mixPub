package com.ifeng.recom.mixrecall.common.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liligeng on 2018/11/22.
 * 输出mix首屏日志，争取把首屏耗时调整到50ms以下
 *
 */
public class RecallResultLogUtils {

    private static final Logger logger= LoggerFactory.getLogger(RecallResultLogUtils.class);

    /**
     * 记录用户数据更新的详细日志
     * @param format
     * @param argArray
     */
    public static void debug(String format, Object... argArray) {

        logger.info(format, argArray);
    }

    public static void main(String[] args) {
        ServiceLogUtil.debug("UserProfile {} {} cost:{}", "123", "long", 123);
    }

//    public static void info(String format, Object... argArray){
//        logger.info(format, argArray);
//    }
}
