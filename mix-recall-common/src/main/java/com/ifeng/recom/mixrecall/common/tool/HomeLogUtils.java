package com.ifeng.recom.mixrecall.common.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liligeng on 2018/11/22.
 * 输出mix首屏日志，争取把首屏耗时调整到50ms以下
 *
 */
public class HomeLogUtils {

    private static final Logger logger= LoggerFactory.getLogger(HomeLogUtils.class);

    public static void info(String format, Object... argArray){
        logger.info(format, argArray);
    }
}
