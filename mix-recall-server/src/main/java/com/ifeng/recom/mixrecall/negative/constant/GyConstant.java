package com.ifeng.recom.mixrecall.negative.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jibin on 2017/5/5.
 */
public class GyConstant {

    /**多线程从ikv查询内容画像  总耗时
     *
     */
    public static final int MaxTimeOut_ikvItemProfile = Integer.valueOf(BaseConfig.getProperty("timeout_ikv_itemprofile"));


    /**
     * 多线程从hbase查询内容画像统计信息
     */

    public static final int MaxTimeOut_HbaseItemStatistic = Integer.valueOf(BaseConfig.getProperty("timeout_item_statistic"));


    /**
     * ikv查询的前缀，如果是数字开头，则拼接 cmpp_的前缀
     */
    public static final String IKV_Prefix="cmpp_";


    /**
     * guid的长度 36位 md5值
     */
    public static final int GUID_Length = 36;


    /**
     * 内容画像的标识
     */
    public enum ItemProfileName {

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



    public enum CacheName {

        /**
         * item内容画像 ehcache key
         */
        ITEM_DOCUMENT_CACHEKEY("documentEntity"),
        /**
         * 内容画像performance字段的ehcache key，过期时间与画像不一样
         */
        ITEM_PROFILE_FEATURES_CACHEKEY("itemProfileFeatures"),

        /**
         * item内容画像统计信息 ehcache key
         */
        ITEM_PROFILE_STATISTICS_CACHEKEY("itemProfileStatistics");

        private String value;

        CacheName(String value) {
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
