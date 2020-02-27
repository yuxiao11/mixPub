package com.ifeng.recom.mixrecall.prerank.constant;

import com.ifeng.recom.mixrecall.prerank.PathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jibin on 2017/5/5.
 */
public class CTRConstant {


    /**
     * 当前工程jar包路径
     */
    public static final String BATH_PATH = PathUtils.getCurrentPath(CTRConstant.class);

    /**
     * 实验框架的配置路径
     */
    public static final String EXPFW_PATH = BATH_PATH + "/file/expfw/";
    /**
     * 模型配置路径
     */
    public static final String MODEL_PATH = BATH_PATH + "/file/model";

    /**
     * segment配置文件路径
     */
    public static final String SEGMENT_PATH = BATH_PATH + "/file/segment";
    /**
     * 特征配置文件路径
     */
    public static final String FEATURE_PATH = BATH_PATH + "/file/feature";
    /**
     * 模型配置文件路径
     */
    public static final String MODEL_CONFIG_PATH = BATH_PATH + "file/model_config/model_config.json";

    /**
     * 内容画像本地缓存路径
     */
    public static final String ITEM_PROFILE_LOCAL_CACHE = BaseConfig.getProperty("itemProfileCacheDir");



    /**
     * ctr模块的trigger名
     */
    public static final String TRIGGER_CTR_ALL = "TRIGGER_CTR_ALL";

    /**
     * ctr模块的模型名
     */
    public static final String FLAG_CTR_MODEL = "FLAG_CTR_MODEL";

    /**
     * ctr模块的特征名
     */
    public static final String FLAG_CTR_FEATURE = "FLAG_CTR_FEATURE";


    /**
     * ctr服务的系统错误返回码
     */
    public static final int SYSTEM_ERROR_EXIT_STATUS = -1;

    /**
     * 添加特征版本名 by yx 20191228
     */
    public static final String Feature_Version_Name = "headline _1030";


//    /**
//     * 多线程调用ItemProfile的超时时间 50毫秒
//     */
//    public static final int MaxTimeOut_ItemProfileFuture =  Integer.valueOf(BaseConfig.getProperty("timeout_common"));
//
//    /**
//     * 头条item根据cmppid转换为staticsticid的查询超时时间 50毫秒，有缓存，缓存基本不变,这个接口性能一般
//     */
//    public static final int MaxTimeOut_CmppId2Staticid =  Integer.valueOf(BaseConfig.getProperty("timeout_common"));
//
//    /**
//     * 调用Item的实时统计数据超时时间 40毫秒，有缓存，
//     */
//    public static final int MaxTimeOut_ItemStatisticsInfo =  Integer.valueOf(BaseConfig.getProperty("timeout_common"));

    /**查询用户画像中的短期兴趣 last
     * userProfile的超时时间 50毫秒，有缓存，
     */
    public static final int MaxTimeOut_UserProfileFuture = Integer.valueOf(BaseConfig.getProperty("timeout_userprofile_future"));

    /**
     * 多线程计算ctr的超时时间  总耗时
     */
    public static final int MaxTimeOut_CalcCtr =  Integer.valueOf(BaseConfig.getProperty("timeout_calc_ctr"));

    /**多线程从ikv查询内容画像  总耗时
     *
     */
    public static final int MaxTimeOut_ikvItemProfile = Integer.valueOf(BaseConfig.getProperty("timeout_ikv_itemprofile"));


    /**多线程从hbase查询用户像  总耗时
     *
     */
    public static final int MaxTimeOut_hbaseUserProfile = Integer.valueOf(BaseConfig.getProperty("timeout_hbase_userprofile"));

    /**
     * 多线程从redis查询内容画像统计信息
     */
    public static final int MaxTimeOut_RedisItemStatistic = Integer.valueOf(BaseConfig.getProperty("timeout_item_statistic"));

    /**
     * 多线程从hbase查询内容画像统计信息
     */

    public static final int MaxTimeOut_HbaseItemStatistic = Integer.valueOf(BaseConfig.getProperty("timeout_item_statistic"));

//    /**
//     * 该工程用到的模型
//     */
//    public static final List<String> Models = ParseStringArray(BaseConfig.getProperty("models").split(";"));

    /**
     * 默认ctr值 0.01
     */
    public static final String DEFAULT_CTR = "0.01";


    /**
     * 符号 逗号
     */
    public static final String Symb_Comma = ",";
    /**
     * 符号 下划线
     */
    public static final String Symb_Underline = "_";

    /**
     * 符号 制表符
     */
    public static final String Symb_Tab = "\t";
    /**
     * 符号 冒号
     */
    public static final String Symb_Colon =  ":";
    /**
     * 符号 分隔符\001
     */
    public static final String Symb_Split1 =  "\001";

    /**
     * 符号 分隔符\002
     */
    public static final String Symb_Split2 =  "\002";

    /**
     * 符号  占位符 -
     */
    public static final String Symb_PLACEHOLDER = "-";

    /**
     * 符号 空格
     */
    public static final String Symb_blank =  " ";
    /**
     * 符号 井号
     */
    public static final String Symb_PoundSign =  "#";




    /**
     * 因为调用方id不统一，对于数字开头的itemid需要拼接上 sub_ 的前缀
     */
    public static final String Sub_Prefix="sub_";

    /**
     * ctr测试使用，不走ctr模型
     */
    public static final String FlagCtr_NoModel="NoModel";

    /**
     * 待更新的模型文件名的key
     */
    public static final String KEY_CTRMOEL_TO_UPDATE = "KEY_CTRMOEL_TO_UPDATE";


    /**
     * 所有的模型文件名的key
     */
    public static final String KEY_CTRMOEL_TO_ALL = "KEY_CTRMOEL_TO_ALL";


    /**
     * 所有的模型更新的时间戳
     */
    public static final String KEY_CTRMOEL_UPDATETIME = "KEY_CTRMOEL_UPDATETIME";


    // ================================= FM Model更新相关的key ======================================
    public static final int FM_K = 12;
    public static final long FM_USERID_FEATURE_ID = 201;
    public static final double FM_SUM_MAX = 50.0d;
    public static final double FM_SUM_MIN = -50.0d;

    public static final String KEY_CTR_FMMODEL_TO_ALL = "KEY_CTR_FMMODEL_TO_ALL";
    public static final String KEY_CTR_FMMODEL_TO_UPDATE = "KEY_CTR_FMMODEL_TO_UPDATE";
    public static final String KEY_CTR_FMMODEL_UPDATETIME = "KEY_CTR_FMMODEL_UPDATETIME";
    public static final String KEY_CTR_FMMODEL_UPDATETIME_MAP = "KEY_CTR_FMMODEL_UPDATETIME_MAP";

    // ================================= FFM Model更新相关的key ======================================
    public static final int FFM_K = 10;
    public static final int FFM_F = 5;
    public static final long FFM_USERID_FEATURE_ID = 201;
    public static final long FFM_SIMID_FEATURE_ID = 113;
    public static final double FFM_SUM_MAX = 50.0d;
    public static final double FFM_SUM_MIN = -50.0d;

    public static final String KEY_CTR_FFMMODEL_TO_ALL = "KEY_CTR_FFMMODEL_TO_ALL";
    public static final String KEY_CTR_FFMMODEL_TO_UPDATE = "KEY_CTR_FFMMODEL_TO_UPDATE";
    public static final String KEY_CTR_FFMMODEL_UPDATETIME = "KEY_CTR_FFMMODEL_UPDATETIME";
    public static final String KEY_CTR_FFMMODEL_UPDATETIME_MAP = "KEY_CTR_FFMMODEL_UPDATETIME_MAP";



    // ================================== Segment自动更新相关key =====================================
    /**
     * 所有的segment更新的时间戳
     */
    public static final String KEY_CTRSegment_UPDATETIME = "KEY_CTRSegment_UPDATETIME";
    /**
     * segment文件名前缀
     */
    public static final String Segment_NAME = ".ls";

    /**
     * 新segment文件名后缀
     */
    public static final String SEGMENT_NAME_X = ".lsx";

    /**
     * 待更新的segment文件名的key
     */
    public static final String KEY_CTRSEGMENT_TO_UPDATE = "KEY_CTRSegment_TO_UPDATE";

    /**
     * 所有的segment文件名的key
     */
    public static final String KEY_CTRSEGMENT_TO_ALL = "KEY_CTRSegment_TO_ALL";


    // ================================ 历史CTR自动更新相关 ==================================
    /**
     * 历史CTR数据是否需要更新
     */
    public static final String KEY_HISTORY_CTR_NEEDS_UPDATE = "KEY_HISTORY_CTR_NEEDS_UPDATE";

    /**
     * 历史CTR的两部分数据在redis中的key
     */
    public static final String KEY_HISTORY_CTR_DICT_KEY = "KEY_HISTORY_CTR_DICT_KEY";
    public static final String KEY_HISTORY_CTR_DATA_KEY = "KEY_HISTORY_CTR_DATA_KEY";




    /**
     * ctr接口返回结果中的排序好的新闻list的key
     */
    public static final String RESULT_KEY_ITEMS="items";


    /**
     * 实验框架的abtest标记
     */
    public static final String RESULT_KEY_ABTEST = "abtest";


    /**
     * 头条新闻的编辑新闻，（置顶）
     */
    public static final String HEADLINE_EDITOR = "editor";
    /**
     * 头条新闻的bidding新闻，固定排在第四位,只有一个
     */
    public static final String HEADLINE_BIDDING = "bidding";
    /**
     * 头条新闻的大图新闻，大图不在首位不在末位，且大图之间不相邻
     */
    public static final String HEADLINE_SpecialView = "specialView";

    /**
     * ikv查询的前缀，如果是数字开头，则拼接 cmpp_的前缀
     */
    public static final String IKV_Prefix="cmpp_";


    /**
     * 服务调用的最大尝试次数
     */
    public static final int Num_MaxTry = 3;


    /**
     * ctr格式，保留四位小数
     */
    public static final String Ctr_Format="%.4f";


    /**
     * guid的长度 36位 md5值
     */
    public static final int GUID_Length = 36;

    /**
     * debuguid 标记
     */
    public static final String debugUidFlag = "debug";



    /**
     * 符号 ikv的ReadableFeatures 的c 分类
     */
    public static final String Symb_C_ReadableFeatures = "c";
    /**
     * hbase中 itemf里的 wemediaLevel
     */
    public static final String Symb_wemediaLevel_ItemOther = "wemediaLevel";


    public static final String uid_Jibin = "867305035296545";




    /**
     * 业务线流量标记
     * 获取behavior使用
     */
    public static enum PtypeName {

        /**
         * 头条客户端的流量
         */
        HeadLine("headline"),
        /**
         * 视频头条的流量标记
         */
        Video("video"),
        /**
         * 业务线参数错误，走默认逻辑
         */
        DefaultBehavior("defaultBehavior");

        private String value;

        PtypeName(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    /**
     * 内容画像的标识
     */
    public static enum ItemProfileName {

        /**
         * 新闻头条的IKV的内容画像
         */
        HeadLine_ITEM_PROFILE("headline");

        private String value;

        ItemProfileName(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }


    /**
     * 用户画像的标识
     */
    public static enum UserProfileName {

        /**
         * 新闻头条的用户画像
         */
        HeadLine_User_PROFILE("headline");

        private String value;

        UserProfileName(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }


    public static enum CacheName {

        /**
         * item内容画像 ehcache key
          */
        ITEM_PROFILE_ENTITY_CACHEKEY("itemProfileEntity"),

        /**
         * 内容画像performance字段的ehcache key，过期时间与画像不一样
         */
        ITEM_PROFILE_FEATURES_CACHEKEY("itemProfileFeatures"),

        /**
         * item内容画像统计信息 ehcache key
         */
        ITEM_PROFILE_STATISTICS_CACHEKEY("itemProfileStatistics"),

        /**
         * 用户画像  缓存时长10min
         */
        USER_PROFILE_CACHE("userProfile"),

        /**
         * debug用户uid and docid 结果缓存
         */
        UIDANDDOCID_RESULT_CACHE("uidAndDocIdResultForDebug")

        ;

        private String value;

        CacheName(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**请求ctr服务的不同业务场景来源
     *
     */
    public enum requestSource{

        /** 头条引擎*/
        HEADLINE("headline"),

        /** 头条引擎增量部分*/
        STORM_MERGE("stormMerge"),

        /** 离线任务*/
        OFFLINE_JOB("offlineJob"),

        /** 机器push*/
        MACHINE_PUSH("machinepush")


        ;

        private String value;

        requestSource(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    public static List<String> ParseStringArray(String[] array) {
        List<String> rs = new ArrayList<>();
        for(String item: array) {
            rs.add(item);
        }
        return rs;
    }

}
