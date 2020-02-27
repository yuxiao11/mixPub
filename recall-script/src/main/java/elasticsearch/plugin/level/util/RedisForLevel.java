package elasticsearch.plugin.level.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis Utils by Jedis
 * Created by geyl on 2017/3/16.
 */
public class RedisForLevel {
    private static final Logger logger = LoggerFactory.getLogger(RedisForLevel.class);

    private static JedisPool jedisPoolSourceLevel;

    static {
        initJedisForSourceLevel();
    }

    private static void initJedisForSourceLevel() {
        if (jedisPoolSourceLevel == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(30);
            config.setMaxIdle(2);
            config.setMaxWaitMillis(10000);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            config.setBlockWhenExhausted(true);
            jedisPoolSourceLevel = new JedisPool(config, "10.90.3.55", 6379, 10000);
        }
    }


    public static Jedis getSourceLevelClient() {
        Jedis jedis = jedisPoolSourceLevel.getResource();
        if (jedis != null) {
            return jedis;
        } else {
            return null;
        }
    }


}