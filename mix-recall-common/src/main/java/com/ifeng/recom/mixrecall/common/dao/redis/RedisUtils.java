package com.ifeng.recom.mixrecall.common.dao.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;


import java.util.HashSet;
import java.util.Set;

/**
 * Redis Utils by Jedis
 * Created by geyl on 2017/3/16.
 */
public class RedisUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    private static JedisPool jedisPoolPreload;

    private static JedisCluster jcUserProfile;

    private static JedisPool jedisPoolSourceRedis;

    //统计标签缺失上报redis
    private static JedisPool jedisPoolTagLack;

    //usercf cache
    private static JedisCluster jedisUserCFCluster;


    static {
        initJedisClusterForUserProfile();
        initJedisForPreload();
        initJedisForTagLack();
        initJedisPoolSourceRedis();
        initUserCfJedisCluster();
    }

    private static void initJedisPoolSourceRedis() {
        if (jedisPoolSourceRedis == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(100);
            config.setMaxIdle(30);
            config.setMaxWaitMillis(10000);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            config.setBlockWhenExhausted(true);
            jedisPoolSourceRedis = new JedisPool(config, "10.90.11.107", 6379, 10000);
        }
    }


    private static void initJedisForPreload() {
        if (jedisPoolPreload == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(30);
            config.setMaxIdle(5);
            config.setMaxWaitMillis(10000);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            config.setBlockWhenExhausted(true);
            jedisPoolPreload = new JedisPool(config, "10.90.11.105", 6380, 10000);
        }
    }

    private static void initJedisForTagLack() {
        if (jedisPoolTagLack == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(100);
            config.setMaxIdle(30);
            config.setMaxWaitMillis(10000);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            config.setBlockWhenExhausted(true);
            jedisPoolTagLack = new JedisPool(config, "10.90.11.105", 6379, 10000);
        }
    }


    private static void initJedisClusterForUserProfile() {
        if (jcUserProfile == null) {
            Set<HostAndPort> jedisClusterNodes = new HashSet<>();
            jedisClusterNodes.add(new HostAndPort("10.90.13.89", 7100));
            jedisClusterNodes.add(new HostAndPort("10.90.13.89", 7107));
            jedisClusterNodes.add(new HostAndPort("10.90.13.90", 7101));
            jedisClusterNodes.add(new HostAndPort("10.90.13.90", 7106));
            jedisClusterNodes.add(new HostAndPort("10.90.11.114", 7102));
            jedisClusterNodes.add(new HostAndPort("10.90.11.114", 7105));
            jedisClusterNodes.add(new HostAndPort("10.90.11.114", 7108));
            jedisClusterNodes.add(new HostAndPort("10.90.11.114", 7110));
            jedisClusterNodes.add(new HostAndPort("10.90.11.115", 7103));
            jedisClusterNodes.add(new HostAndPort("10.90.11.115", 7104));
            jedisClusterNodes.add(new HostAndPort("10.90.11.115", 7109));
            jedisClusterNodes.add(new HostAndPort("10.90.11.115", 7111));

            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxTotal(50);
            poolConfig.setTestOnBorrow(true);

            jcUserProfile = new JedisCluster(jedisClusterNodes, poolConfig);
        }
    }

    private static void initUserCfJedisCluster() {
        if (jedisUserCFCluster == null) {
            Set<HostAndPort> jedisClusterNodes = new HashSet<>();
            jedisClusterNodes.add(new HostAndPort("10.90.80.139", 6379));
            jedisClusterNodes.add(new HostAndPort("10.90.80.139", 6380));
            jedisClusterNodes.add(new HostAndPort("10.90.81.139", 6379));
            jedisClusterNodes.add(new HostAndPort("10.90.81.139", 6380));
            jedisClusterNodes.add(new HostAndPort("10.90.82.139", 6379));
            jedisClusterNodes.add(new HostAndPort("10.90.82.139", 6380));

            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxTotal(50);
            poolConfig.setTestOnBorrow(true);

            jedisUserCFCluster = new JedisCluster(jedisClusterNodes,10000,10000,5,"PHg36BOnE9uSfTAQ", poolConfig);
        }
    }


    public static JedisCluster getJedisUserCfAls(){
        if(jedisUserCFCluster!=null){
            return jedisUserCFCluster;
        }else{
            initUserCfJedisCluster();
            return jedisUserCFCluster;
        }
    }

    public static Jedis getPreloadJedisClient() {
        Jedis jedis = jedisPoolPreload.getResource();
        if (jedis != null) {
            return jedis;
        } else {
            return null;
        }
    }

    public static Jedis getTagLackJedisClient() {
        Jedis jedis = jedisPoolTagLack.getResource();
        if (jedis != null) {
            return jedis;
        } else {
            return null;
        }
    }

    public static JedisCluster getProfileJedisClusterClient() {
        if (jcUserProfile != null) {
            return jcUserProfile;
        } else {
            initJedisClusterForUserProfile();
            return jcUserProfile;
        }
    }


    public static Jedis getJedisPoolSourceRedis() {
        Jedis jedis = jedisPoolSourceRedis.getResource();
        if (jedis != null) {
            return jedis;
        } else {
            return null;
        }
    }


}