package com.ifeng.recom.mixrecall.common.constant;

import com.ifeng.recom.mixrecall.common.util.IPUtil;

/**
 * Created by jibin on 2017/12/26.
 */
public class GyConstant {


    /**
     * 本机ip，用作系统缓存分片使用
     */
    public final static String linuxLocalIp = IPUtil.getLinuxLocalIp();

    /**
     * 头条实时调用的流量，设置超时时间为300ms
     */
    public static final int timeout_Toutiao_Online = 150;


    /**
     * 头条实时调用的流量，设置超时时间为150ms
     */
    public static final int timeout_Toutiao_First = 150;


    /**
     * 画像超时时间，设置超时时间为1200ms
     */
    public static final int timeout_UserModel_long = 1200;


    /**
     * 获取usercf的超时控制
     */
    public static final int timeout_UserCF = 500;

    /**
     * 获取实时画像信息的超时控制
     */
    public static final int timeout_UserModel_last = 800;

    public static final int timeout_Toutiao_Online_cotag = 200;

    /**
     * 兴趣试探超时时间，设置超时时间为100ms
     */
    public static final int timeout_explore_long = 100;

    /**
     * 正反馈调用超时
     */
    public static final int timeout_positiveFeed = 500;

    /**
     * 符号 空格
     */
    public static final String Symb_blank = " ";

    /**
     * 符号 es使用的分隔符
     */
    public static final String Symb_es_split = "\\^";


    public static final String Symb_brackets_left = "(";
    public static final String Symb_brackets_right = ")";

    /**
     * 符号 冒号
     */
    public static final String Symb_Colon = ":";

    /**
     * 符号 小于号
     */
    public static final String Symb_Less = "<";

    /**
     * 符号 大于号
     */
    public static final String Symb_Greater = ">";

    /**
     * 符号 逗号
     */
    public static final String Symb_Comma = ",";
    /**
     * 符号 分号
     */
    public static final String Symb_Semicolon = ";";

    /**
     * 符号  井号
     */
    public static final String Symb_Pound = "#";
    /**
     * 符号 等号
     */
    public static final String Symb_equal = "=";

    /**
     * 符号 下划线
     */
    public static final String Symb_Underline = "_";


    /**
     * 左 花括号
     */
    public static final String Symb_openBrace = "{";
    /**
     * 右 花括号
     */
    public static final String Symb_closeBrace = "}";


    /**
     * 正反馈强插的上限设置 20条
     */
    public static final int itemcf_limit = 20;

    /**
     * guid的长度 32位 md5值
     */
    public static final int GUID_Length = 32;

    /**
     * 解析topic用来查询，设置对应的限制上限，避免超时
     */
    public static final int topicQueryLimit = 5;

    /**
     * solr查询时效过滤
     */
    public static final String quer_solr_available = " AND (available:true)";
    public static final String quer_solr_or = " OR ";
    public static final String quer_solr_key_topic1 = "topic1:";
    public static final String quer_solr_key_topic3 = "topic3:";
    public static final String quer_solr_key_date = "date";
    public static final String quer_solr_key_doctype = "doctype:";
    public static final String quer_solr_key_itemid="itemid";
    public static final String quer_solr_key_simID="simID";
    public static final String quer_solr_and = " AND ";
    public static final int quer_solr_limit = 5;


    /**
     * 增量结果条数
     */
    public static final int ResultSize_IncreasedateMerge = 700;


    /**
     * 头条实时调用的流量，设置超时时间为300ms
     */
    public static final int num_Sub = 70;

    /**
     * 订阅频道出150条 订阅
     */
    public static final int num_Sub_Momentsnew = 300;

    /**
     * 每个帖子的召回量
     */
    public static final int eacheNum_Sub_Momentsnew = 15;

    /**
     * 其他频道cotags,S级媒体默认出50条
     */
    public static final int cotags_Total_Default = 50;

    /**
     * 头条频道S级媒体默认出500条
     */
    public static final int cotags_Total_Headline = 500;


    /**
     * 正反馈强插的标记，透传给头条标记,使用简写
     */
    public final static String strategyPositiveFeed = "posi";     //正反馈强插的前缀source

    /**
     * 处理ctr默认值，使用hotBoost*阈值进行补全
     */
    public static final double CTRQ_hotBoost_threshold = 0.01;
    /**
     * HotBoostCtr格式，保留5位小数
     */
    public static final String HotBoostCtr_Format="%.5f";

    /**
     * 处理类型转换失败的情况，设置默认值-1
     */
    public static final long timeSensitive_null = -1;

    /**
     * 长效标记
     */
    public static final String timeSensitive_nt = "nt";
    public static final String dateTimeSensitive_nt = "2300-1-1 23:59:00";


    public static final boolean isNewResult = true;
    public static final boolean isOldResult = false;


    public static final String uid_Jibin = "867305035296545";
    public static final String uid_pd = "865969031431182";
    public static final String uid_madi = "867463030265112";

    public static final String OK = "OK";

    /**
     * 设置simIdMapping 过期时间，一天
     */
    public static final int ttl_SimIdMapping = 3600 * 24;



    /**
     * 趣头条抓取过滤规则
     */
    public static final String needQttFilter = "needQttFilter";

    /**
     * 小视频过滤规则
     */
    public static final String needMinVideoFilter = "needMinVideoFilter";

    public static final String isBeiJingUserNotWxb = "isBeiJingUserNotWxb";

    public static final String isColdNotBj = "isColdNotBj";


    /**
     * 小视频的标记
     */
    public static final String miniVideo = "miniVideo";

    /**
     * 世界杯标记
     */
    public static final String worldCup = "worldCup";

    /**
     * 趣头条抓取 标记
     */
    public static final String qutt = "qutt";


    /**
     * 负反馈使用的 来源
     */
    public static final String UserNegs_Src = "src";

    /**
     * 最多渠道标签数
     */
    public static final int sourceTagNum = 15;


    /**
     * debugType： userModel
     */
    public static final String debugType_userModel = "userModel";

    public static final String bossUsers= "65357bb574334ad683d459ee20eb119e|829584937ae04994b028008a3bdbab75|27d7344be6c44fc880db8ec6de6fc7a5|5e192a33f6804f5592c98f5d6f2809de|debug237|debugmadi3|869456039950054";


    /**
     * debugType： userCF
     */
    public static final String debugType_userCf = "userCF";

    /**
     * debugType： userCFDetail
     */
    public static final String debugType_userCf_detail = "userCFDetail";

    /**
     * doc持久化的最大文件数
     */
    public static final int doc_txt_Num = 5;


    /**
     * 本机缓存的目录
     */
    public static final String localCacheDir = "/data/prod/service/mix-recall/cache_dump/";

    public static final String personalcacheOfpath = localCacheDir + "DocPreloadCache.txt";


    /**
     * cotag小于50条则认为cotag不足
     */
    public static final int cotagLongNotEnough = 50;
    /**
     * docpic prefix
     */
    public static final String docpic_prefix = "d-";
    /**
     * video prefix
     */
    public static final String video_prefix = "v-";

    /**
     * video jx_suffix
     */
    public static final String jx_suffix = "_jx";

    /**
     * cate prefix
     */
    public static final String cate_prefix = "c-";
    /**
     * subcate prefix
     */
    public static final String subcate_prefix = "sc-";

    /**
     * docpic 后缀
     */
    public static final String d_suffix = "_d";

    /**
     * video 后缀
     */
    public static final String v_suffix = "_v";

    public static final String key_Source = "source=";

    //ffm调用url
    public static final String FFM_URL = "http://local.recom.ifeng.com/recall/ffm/user";
    public static final String FFM_URL_V = "http://local.recom.ifeng.com/recall/ffmv/user";

}
