package com.ifeng.recom.mixrecall.common.dao.redis;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liligeng on 2019/6/3.
 */
public class RedisFailoverUtils {

    private static Logger logger = LoggerFactory.getLogger(RedisFailoverUtils.class);

    //初始化时包含的所有redis连接池对象
    private static Map<String, JedisPool> preloadJedisGroup = new ConcurrentHashMap<>();

    //当前可用的redis连接池对象
    private static Map<String, JedisPool> jedisPoolAvailable = new ConcurrentHashMap<>();

    //出现故障的redis连接池，需要定时check
    private static Map<String, JedisPool> jedisPoolFail = new ConcurrentHashMap<>();

    //每分钟检查一次
    private static int checkInterval = 60 * 1000;

    //是否已经启动守护进程标识
    private static boolean isStartDaemon = false;

    private static String masterHost = "10.80.82.141";

    private static String slaveHost = "10.80.90.141";

    static {
        initPreloadFailoverJedis();
    }


    private static void initPreloadFailoverJedis() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(50);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(10000);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setBlockWhenExhausted(true);
        JedisPool master = new JedisPool(config, masterHost, 6380, 3000);
        JedisPool slave = new JedisPool(config, slaveHost, 6380, 3000);
        preloadJedisGroup.put(masterHost, master);
        preloadJedisGroup.put(slaveHost, slave);
        jedisPoolAvailable.putAll(preloadJedisGroup);
    }

    public static Jedis getPreloadJedisClient() {

        Iterator<Map.Entry<String, JedisPool>> iterator = jedisPoolAvailable.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, JedisPool> entry = iterator.next();
            String name = entry.getKey();
            JedisPool pool = entry.getValue();
            try {
                Jedis jedis = pool.getResource();
                jedis.ping();
                return jedis;
            } catch (Exception e) {
                jedisPoolFail.put(name, pool);
                jedisPoolAvailable.remove(name);
                logger.error(" {} redis 连接池无法获取连接: avail:{} ,fail:{} 已启动故障转移", name, jedisPoolAvailable.keySet(),
                        jedisPoolFail.keySet(), e);
                startDaemon();
            }
        }
        return null;
    }


    /**
     * 启动守护进程，如果发现那个redis发生宕机~ 启动守护进程，
     * 每隔一分钟检查一下宕机redis的状态，机器全宕每三秒检查一次，恢复正常后重新加入
     * 连接池列表
     */
    private static void startDaemon() {
        if (isStartDaemon) {
            return;
        }

        synchronized (PoolDaemonThread.class) {
            if(isStartDaemon){
                //double check, 避免成功创建守护线程后重复创建
                return;
            }
            isStartDaemon = true;
            PoolDaemonThread thread = new PoolDaemonThread();
            new Thread(thread).start();
        }
    }

    private static class PoolDaemonThread implements Runnable {

        @Override
        public void run() {
            while (jedisPoolFail != null && !jedisPoolFail.isEmpty()) {
                Iterator<Map.Entry<String, JedisPool>> iterator = jedisPoolFail.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, JedisPool> entry = iterator.next();
                    String name = entry.getKey();
                    JedisPool pool = entry.getValue();
                    try {
                        Jedis jedis = pool.getResource();
                        String result = jedis.ping();
                        if (StringUtils.isNotBlank(result) && "PONG".equals(result)) {
                            jedisPoolAvailable.put(name, pool);
                            jedisPoolFail.remove(name);
                            logger.info("{} redis 连接池恢复 avail:{} fail:{}", name, jedisPoolAvailable.keySet(), jedisPoolFail.keySet());
                        }
                    } catch (Exception e) {
                        logger.error("{} redis 连接池暂未恢复 avail:{} fail:{} 轮询检查中：{}", name, jedisPoolAvailable.keySet(),
                                jedisPoolFail.keySet(), e);

                    } finally {
                        if (jedisPoolFail != null && jedisPoolFail.isEmpty()) {
                            isStartDaemon = false;
                            logger.info("{} redis 连接池全部恢复，退出守护进程 avail:{} fail:{}", name, jedisPoolAvailable.keySet(), jedisPoolFail.keySet());
                            break;
                        }
                    }
                }

                try {
                    if (jedisPoolAvailable != null && !jedisPoolAvailable.isEmpty()) {
                        Thread.sleep(checkInterval);
                    } else {
                        //无可用redis连接池每三秒检查一次
                        Thread.sleep(3000);
                    }
                } catch (Exception e) {
                    logger.error("Pool Daemon Thread Sleep Err {}", e);
                }
            }
        }
    }
}
