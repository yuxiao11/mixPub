package com.ifeng.recom.mixrecall.common.dao.redis;

import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liligeng on 2019/2/27.
 */
public class NewStatisticsRedis {

    protected final static Logger logger = LoggerFactory.getLogger(NewStatisticsRedis.class);

    private static final String REDIS_NAMESPACE = "stats:";

    //近三天统计数据，使用近三天统计数据对文章进行排序
    private static final String LAST_P3D = "LAST_P3D";

    private static ShardedJedisPool shardedJedisPool;

    /**
     * 初始化redis集群
     */
    static {
        initRedis();
    }

    public static void initRedis() {
        logger.info("start init localhostJedisUtil.initRedis");
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            // 是否启用后进先出, 默认true
            poolConfig.setLifo(true);
            // 最大空闲连接数
            poolConfig.setMaxIdle(10);
            // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted), 如果超时就抛异常,小于零:阻塞不确定的时间
            poolConfig.setMaxWaitMillis(3000);
            // 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
            poolConfig.setMinEvictableIdleTimeMillis(1800000);
            // 最小空闲连接数, 默认0
            poolConfig.setMinIdle(0);
            // 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
            poolConfig.setNumTestsPerEvictionRun(3);
            // 对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断 (默认逐出策略)
            poolConfig.setSoftMinEvictableIdleTimeMillis(1800000);
            // 在获取连接的时候检查有效性, 默认false
            poolConfig.setTestOnBorrow(false);
            poolConfig.setMaxTotal(50000);
            // 在空闲时检查有效性, 默认false
            poolConfig.setTestWhileIdle(false);
            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>() {{
                add(new JedisShardInfo("10.80.81.140", 6379));
                add(new JedisShardInfo("10.80.81.140", 6380));
                add(new JedisShardInfo("10.80.77.141", 6379));
                add(new JedisShardInfo("10.80.77.141", 6380));
            }};
            // 构造池
            shardedJedisPool= new ShardedJedisPool(poolConfig, shards);
        } catch (Exception e) {
            logger.error("redisInit SpecialFilterUserJedisUtil ERROR:{}", e);
            e.printStackTrace();
        }
    }

    public static Map<String,Double> getCtrEvPv(String id){
        Map<String, Double> result = new HashMap<>();

        ShardedJedis shardedJedis = null;

        try{
            shardedJedis = shardedJedisPool.getResource();
            String key =  REDIS_NAMESPACE + id;
            String json = shardedJedis.hget(key, LAST_P3D);
            if(StringUtils.isNotBlank(json)) {
                Map<String, Object> map = GsonUtil.json2Object(json, Map.class);
                try {
                    double ev = (double) map.getOrDefault("ev",0d);
                    double pv = (double) map.getOrDefault("pv",0d);
                    double store = (double) map.getOrDefault("store",0d);
                    double share = (double) map.getOrDefault("share",0d);
                    if(ev>0){
                        double ctr = pv /ev;
                        result.put("ctr",ctr);
                    }else{
                        result.put("ctr",0d);
                    }
                    result.put("ev",ev);
                    result.put("pv",pv);
                    result.put("store",store);
                    result.put("share",share);
                } catch (Exception e) {
                    logger.error("query err:{}", e);
                }
            }

        }catch (Exception e){
            logger.error("query Statistic RedisUtil error:{}",e);
            returnBrokenResource(shardedJedis);
        }finally {
            returnResource(shardedJedis);
        }

        return result;
    }

    private static void returnBrokenResource(ShardedJedis shardedJedis) {
        try {
            shardedJedisPool.returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("returnBrokenResource error.", e);
        }
    }

    private static void returnResource(ShardedJedis shardedJedis) {
        try {
            shardedJedisPool.returnResource(shardedJedis);
        } catch (Exception e) {
            logger.error("returnResource error.", e);
        }
    }


}
