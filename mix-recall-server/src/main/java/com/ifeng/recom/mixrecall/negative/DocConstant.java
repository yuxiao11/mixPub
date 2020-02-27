package com.ifeng.recom.mixrecall.negative;

import java.util.regex.Pattern;

/**
 * Created by lilg1 on 2018/3/8.
 * 文章的字段名
 */
public class DocConstant {

    /**
     * HBase 文章column名称
     */
    public final static String Doc_Title= "title";
    public final static String Doc_Type = "docType";
    public final static String Doc_SimId = "simId";
    public final static String Doc_Source = "source";
    public final static String Doc_Features = "features";
    public final static String Doc_PublishedTime = "publishedTime";
    public final static String Doc_QualityLevel = "qualityLevel";
    public final static String Doc_Cotags = "coTags";
    public final static String Doc_LdaTopic = "lda_topic";
    public final static String Doc_TitleWords = "splitTitle";
    public final static String Doc_NewsLenLevel = "newsLenLevel";

    public final static String Doc_Category = "category";

    public final static String Doc_Performance = "performance";

    public final static String Doc_ClickBait = "clickBait";

    public final static String Doc_ValidateCotags = "validateCotags";

    public final static String Doc_TimeSensitiveLevel = "timeSensitiveLevel";

    public final static String Doc_Distype = "disType";

    public final static String Doc_SpecialParam = "specialParam";




    /**
     * 符号 下划线
     */
    public static final String Symb_Underline = "_";
    /**
     * guid的长度 32位 md5值
     */
    public static final int GUID_Length = 32;

    /**
     * ikv查询的前缀，如果是数字开头，则拼接 cmpp_的前缀
     */
    public static final String IKV_Prefix = "cmpp_";

    /**
     * 查询hbase更新doc的最大耗时  200毫秒
     */
    public static final int MaxTimeOut_QueryHbaseDocOnline = 200;


    public static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+.*");

    /**
     * 新闻画像里的三个字段名称
     */
    public static final String FEATURE_TYPE = "type";
    public static final String FEATURE_WEIGHT = "weight";
    public static final String FEATURE_WORD = "word";

}
