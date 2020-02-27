package com.ifeng.recom.mixrecall.prerank.ctrmodel;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

@Component("ctrModelJedisCluster")
public class CtrModelJedisClusterFactory implements InitializingBean {
	
	private static final Logger logger = LoggerFactory.getLogger(CtrModelJedisClusterFactory.class);

    private JedisCluster jedisCluster;


    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }


    private Set<HostAndPort> parseHostAndPort() throws Exception {
        try {
            Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
            jedisClusterNodes.add(new HostAndPort("10.90.11.95", 7000));
            jedisClusterNodes.add(new HostAndPort("10.90.11.96", 7003));
            jedisClusterNodes.add(new HostAndPort("10.90.11.96", 7004));
            jedisClusterNodes.add(new HostAndPort("10.90.11.106", 7007));
            jedisClusterNodes.add(new HostAndPort("10.90.11.106", 7008));

            return jedisClusterNodes;
        } catch (IllegalArgumentException ex) {  
            throw ex;  
        } catch (Exception ex) {  
            throw new Exception("解析 redis lianjie chucuo 配置文件失败", ex);
        }  
    }  
      
    @Override
    @PostConstruct
    public void afterPropertiesSet() throws Exception {  
        Set<HostAndPort> hostAndPorts = this.parseHostAndPort();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

        jedisCluster = new JedisCluster(hostAndPorts, 300000, 300000, 100, "8KQmAopiYOwPS0D3", poolConfig);
        
        logger.info("JedisClusterFactory init success!");
          
    }

  
}
