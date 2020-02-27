package com.ifeng.recom.mixrecall.prerank.tools;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @Author: ZhenBingLiu
 * @Description:
 * @Date: Created in 15:262017/11/7
 * @Modified By:
 */

@Component("historyCtrJedisClusterFactory")
public class HistoryCtrJedisClusterFactory implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(HistoryCtrJedisClusterFactory.class);


    private JedisCluster jedisCluster;


    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }


    public void updateJedisCluster() throws Exception {
        Set<HostAndPort> hostAndPorts = this.parseHostAndPort();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

//        jedisCluster = new JedisCluster(hostAndPorts, timeout, maxRedirections,genericObjectPoolConfig);
        jedisCluster = new JedisCluster(hostAndPorts, 300000, 300000, 100, "TBFVaNE5srcoUIJS", poolConfig);
        logger.info("update jedisCluster success!");
    }

    /**
     * 解析主机IP和端口
     * @return
     * @throws Exception
     */

    private Set<HostAndPort> parseHostAndPort() throws Exception {
        try {
            Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
            jedisClusterNodes.add(new HostAndPort("10.90.66.163", 6379));
            jedisClusterNodes.add(new HostAndPort("10.90.67.163", 6379));
            jedisClusterNodes.add(new HostAndPort("10.90.68.163", 6379));

            return jedisClusterNodes;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new Exception("解析 redis lianjie chucuo 配置文件失败", ex);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<HostAndPort> hostAndPorts = this.parseHostAndPort();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        jedisCluster = new JedisCluster(hostAndPorts, 300000, 300000, 100, "TBFVaNE5srcoUIJS", poolConfig);
        logger.info("JedisClusterFactory init success!");
    }


}
