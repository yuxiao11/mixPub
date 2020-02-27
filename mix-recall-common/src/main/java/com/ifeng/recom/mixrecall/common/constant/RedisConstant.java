package com.ifeng.recom.mixrecall.common.constant;

/**
 * Created by jibin on 2018/1/10.
 */
public class RedisConstant {
    //通用的redis连接池配置
    public static final int commonJedisPool_maxIdle = 100;
    public static final int commonJedisPoolminIdle = 8;
    public static final int commonJedisPoolmaxActive = 300;
    public static final int commonJedisPoolmaxWait = 200;
    public static final int commonJedisPooltimeOut = 200;

    //-----------------------------------------------------------
    //协同过滤的 redis连接池配置
    public static final String Itemcf_redis_addr = "10.90.7.54";
    public static final String Itemcf_redis_port = "6379";



    //simId转换 redis连接池配置
    public static final String SimIdMapping_redis_addr = "10.90.23.16";
    public static final String SimIdMapping_redis_port = "6379";
    public static final int SimIdMapping_redis_db = 1;
    //-----------------------------------------------------------



}
