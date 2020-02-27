package com.ifeng.recom.mixrecall.common.config.constant;

/**
 * Created by lilg1 on 2018/4/12.
 * apollo配置中心常量
 */
public class ApolloConstant {

    //debug 用户配置namespace
    public static final String Debug_User_Key = "DebugUsers";

    //debug 用户测试分组用户key名
    public static final String Test_Users = "TestUsers";

    //增量混入精品池用户
    public static final String Test_IncreaseJp_Group = "increase_jp_users";

    //增量混入精品池用户
    public static final String DSSM_TEST_USERS = "dssm_Test_users";
    /**
     * 对特殊用户的特殊通道 namespace
     */
    public static final String BOSS_USERS = "BossUsers";
    /**
     * 对特殊用户的特殊通道
     */
    public static final String boss_users_key = "boss_users";

    /**
     * 新增测试数据 后期改为动态配置召回数量
     */
    public static final String Cold_Start_Policy = "ColdStartPolicy";

    /**
     * 新增按比例动态分配召回数量
     */
    public static final String Pull_Num_Ratio = "pullnumRatio";
}
