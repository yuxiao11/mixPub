package com.ifeng.recom.mixrecall.common.dao.redis.jedisPool;

import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.RedisConstant;
import com.ifeng.recom.mixrecall.common.model.redis.JedisClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本机的redis连接池，处理本机缓存使用
 */
public class SimIdMappingJedisUtil {
    protected final static Logger log = LoggerFactory.getLogger(SimIdMappingJedisUtil.class);

    private static AtomicInteger local = new AtomicInteger(0);

    private static final List<JedisPool> jedisPoolList = new ArrayList<JedisPool>();


    //Redis服务器IP
    private static String addr = RedisConstant.SimIdMapping_redis_addr;

    //Redis的端口号
    private static String port = RedisConstant.SimIdMapping_redis_port;


    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int maxActive = RedisConstant.commonJedisPoolmaxActive;

    //控制一个pool最少有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int minIdle = RedisConstant.commonJedisPoolminIdle;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int maxIdle = RedisConstant.commonJedisPool_maxIdle;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int maxWait = RedisConstant.commonJedisPoolmaxWait;

    private static int timeOut = RedisConstant.commonJedisPooltimeOut;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean testOnBorrow = true;


    static {
        initRedis();
    }

    /**
     * 初始化redis集群
     */
    public static void initRedis() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();

            config.setMaxTotal(maxActive);
            config.setMaxIdle(maxIdle);
            config.setMinIdle(minIdle);
            config.setMaxWaitMillis(maxWait);
            config.setTestOnBorrow(testOnBorrow);
            String[] redisHost = addr.split(GyConstant.Symb_Comma);
            String[] redisPort = port.split(GyConstant.Symb_Comma);
            for (int i = 0; i < redisHost.length; i++) {
                addJedisPool(new JedisPool(config, redisHost[i], Integer.parseInt(redisPort[i]), timeOut));
            }
        } catch (Exception e) {
            log.error("redisInit Itemcf ERROR:{}", e);
            e.printStackTrace();
        }
    }


    /**
     * 轮播获取jedis客户端
     *
     * @return
     */
    private static JedisClient getJedisClient() {
        int localValue = local.getAndIncrement();
        int localTmpValue = 0;
        try {
            if (localValue < 0) {
                localValue = 0;
                local.set(0);
            }

            Jedis jedis = null;
            boolean isServerOK = false;
            int serverCount = jedisPoolList.size();
            for (int i = 0; i < serverCount; i++) {
                localTmpValue = localValue % serverCount;
                try {
                    jedis = getRedisClient(localTmpValue);
                    isServerOK = true;
                    break;
                } catch (Exception e) {
                    log.error("Itemcf getJedisClient error ! connection:redis {} 失败,现已故障转移,{}",
                            localTmpValue, e);
                    localValue++;  //容错
                }
            }
            if (!isServerOK) {
                log.error("Itemcf getJedisClient redis-server-all   均为不可用");
                return null;
            }
            JedisClient jesisReturn = new JedisClient();
            jesisReturn.setServerIndex(localTmpValue);
            jesisReturn.setJedis(jedis);
            return jesisReturn;
        } catch (Exception e) {
            log.error("Itemcf getJedisClient {}  error", localTmpValue, e);
        }
        return null;
    }


    private static void returnClient(JedisClient jedis) {
        if (jedis != null)
            returnClient(jedis.getServerIndex(), jedis.getJedis());
    }

    private static Jedis getRedisClient(int serverIndex) throws Exception {
        return jedisPoolList.get(serverIndex).getResource();
    }

    private static void returnClient(int serverIndex, Jedis jedis) {
        jedisPoolList.get(serverIndex).returnResource(jedis);
    }

    private static void addJedisPool(JedisPool jedisPool) {
        jedisPoolList.add(jedisPool);
    }


    /**
     * 更新redis并设置过期时间
     *
     * @param db
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public static boolean setex(int db, String key, int seconds, String value) {
        boolean check = false;
        JedisClient jedisClient = null;
        try {
            jedisClient = getJedisClient();
            Jedis jedis = jedisClient.getJedis();

            jedis.select(db);
            String result = jedis.setex(key, seconds, value);
            check = GyConstant.OK.equals(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("setex Error:{}, key:{},value:{}", e, key, value);
        } finally {
            if (jedisClient != null) {
                returnClient(jedisClient);
            }
        }
        return check;
    }


    /**
     * 批量获取
     *
     * @param db
     * @param key
     * @return
     */
    public static String get(int db, String key) {
        String result = null;
        JedisClient jedisClient = null;
        try {
            jedisClient = getJedisClient();
            Jedis jedis = jedisClient.getJedis();

            jedis.select(db);
            result = jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("mget Error:{}, key:{}", e, key);
        } finally {
            if (jedisClient != null) {
                returnClient(jedisClient);
            }
        }
        return result;
    }


    public static void main(String[] args) {
        System.out.println(get(1, "clusterId_52281856"));
        System.out.println(get(1, "clusterId_51989044"));
        System.out.println(get(1, "clusterId_50095439"));
        System.out.println(StringUtils.isBlank(get(1, "clusterId_500954391")));


        System.out.println("##########################");
        System.out.println(get(2, "clusterId_51989044"));
        System.out.println(setex(2, "clusterId_51989044", 100, "test"));
        System.out.println(get(2, "clusterId_51989044"));
        System.out.println(StringUtils.isBlank(get(2, "clusterId_51989044")));
    }

}
