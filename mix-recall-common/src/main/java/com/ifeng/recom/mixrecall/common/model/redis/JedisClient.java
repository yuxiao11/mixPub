package com.ifeng.recom.mixrecall.common.model.redis;

import redis.clients.jedis.Jedis;

public class JedisClient {
	private int serverIndex;

	private Jedis jedis;

	public int getServerIndex() {
		return serverIndex;
	}

	public void setServerIndex(int serverIndex) {
		this.serverIndex = serverIndex;
	}

	public Jedis getJedis() {
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
}
