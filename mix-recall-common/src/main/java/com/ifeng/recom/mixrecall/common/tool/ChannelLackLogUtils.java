package com.ifeng.recom.mixrecall.common.tool;

import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by liligeng on 2019/4/19.
 */

public class ChannelLackLogUtils {

    private final static Logger logger = LoggerFactory.getLogger(ChannelLackLogUtils.class);

    private final static String recordUserCfIP = "10.90.86.137";
    private final static String recordUserCfIP2 = "10.90.88.141";

    public static void recordChannelLack(String channel, List<RecallResult> recallResultList) {
        try {
            if (!(recordUserCfIP2.equals(GyConstant.linuxLocalIp)
                    || recordUserCfIP.equals(GyConstant.linuxLocalIp))) {

                return;
            }
            logger.info("channel:{}, lack:{}", channel, CollectionUtils.isEmpty(recallResultList));
        } catch (Exception e) {
            logger.error("Channel Lack err:{}", e);
        }
    }

}
