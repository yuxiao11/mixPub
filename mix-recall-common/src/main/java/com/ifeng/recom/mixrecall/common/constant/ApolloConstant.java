package com.ifeng.recom.mixrecall.common.constant;

/**
 * Created by jibin on 2018/5/18.
 */
public class ApolloConstant {

    /**
     * ip分流进行abtest
     */
    public static final String ip_start_abtest="ip_start_abtest";

    /**
     * 调远程布隆开关打开标识
     */
    public static final String Switch_on ="on";

    /**
     * 针对wxb的内容安全过滤控制开关，只有必要时候打开，进行严格过滤
     */
    public static final String WxbContentSecuritySwitch = "WxbContentSecuritySwitch";

    /**
     * 头条首屏、增量全量只出安全内容开关
     */
    public static final String SafeContentSwitch = "SafeContentSwitch";


    /**
     * 冷启动测试数据 图文
     */
    public static final String ColdStartPolicyForDoc = "ColdStartPolicyForDoc";


    /**
     * 冷启动测试数据 视频
     */
    public static final String ColdStartPolicyForVideo = "ColdStartPolicyForVideo";



    public static final String EXP_UserTest_Key = "EXP_UserTest_Key";

    public static final String SOURCE_N = "SOURCE_N";

    public static final String pullnumRatio = "pullnumRatio";

    public static final String MixRecall_DebugUsers = "MixDebugUsers";

    /**
     * cotag内部通道动态调整占比
     */
    public static final String cotagInnerRatio = "cotag_inner_rate";

}
