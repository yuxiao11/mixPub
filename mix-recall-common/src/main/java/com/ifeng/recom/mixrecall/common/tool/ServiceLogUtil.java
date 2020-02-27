package com.ifeng.recom.mixrecall.common.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ifeng.recom.mixrecall.common.constant.GyConstant.linuxLocalIp;

/**
 * 调用其他服务的耗时日志
 * Created by jibin on 2018/5/23.
 */
public class ServiceLogUtil {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogUtil.class);

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

}
