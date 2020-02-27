package com.ifeng.recom.mixrecall.common.util.flowType;

import com.ifeng.recom.mixrecall.common.constant.RecomChannelEnum;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;

/**
 * Created by jibin on 2018/2/6.
 */
public class FlowTypeUtils {

    /**
     * 判断是头条频道, 快头条流量要单独处理
     *
     * @param mixRequestInfo
     * @return
     */
    public static boolean isHeadline(MixRequestInfo mixRequestInfo) {
        if (RecomChannelEnum.headline.getValue().equals(mixRequestInfo.getFlowType())) {
            return true;
        }
        return false;
    }


    /**
     * 判断是关注频道 ，这部分流量要单独处理
     *
     * @param mixRequestInfo
     * @return
     */
    public static boolean isMomentsnew(MixRequestInfo mixRequestInfo) {
        if (RecomChannelEnum.momentsnew.getValue().equals(mixRequestInfo.getRecomChannel())) {
            return true;
        }
        return false;
    }

}
