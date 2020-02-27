package com.ifeng.recom.mixrecall.prerank.tools;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhaohh @ 2018-03-21 19:12
 **/
public class RedisUtil {

	/**
	 * 获取统计数据redis客户端
	 * @return
	 */
	public static JedisCluster getStatJedisCluster() {

		Set<HostAndPort> jedisClusterNodes = new HashSet<>();
		jedisClusterNodes.add(new HostAndPort("10.90.66.163", 6379));
		jedisClusterNodes.add(new HostAndPort("10.90.67.163", 6379));
		jedisClusterNodes.add(new HostAndPort("10.90.68.163", 6379));
		Integer timeout = 300000;
		Integer maxRedirections = 100;
		GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
		String password = "TBFVaNE5srcoUIJS";
		genericObjectPoolConfig.setMaxWaitMillis(-1);
		genericObjectPoolConfig.setMaxTotal(1000);
		genericObjectPoolConfig.setMinIdle(8);
		genericObjectPoolConfig.setMaxIdle(100);

		JedisCluster client = new JedisCluster(jedisClusterNodes, timeout, timeout, maxRedirections, password, genericObjectPoolConfig);

		return client;
	}

}
