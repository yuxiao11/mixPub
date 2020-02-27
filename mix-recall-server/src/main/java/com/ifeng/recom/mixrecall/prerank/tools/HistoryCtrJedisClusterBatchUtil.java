package com.ifeng.recom.mixrecall.prerank.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisClusterException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author: ZhenBingLiu
 * @Description:
 * @Date: Created in 14:392017/11/28
 * @Modified By:
 */
@Component
public class HistoryCtrJedisClusterBatchUtil implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(HistoryCtrJedisClusterBatchUtil.class);

    @Autowired
    @Qualifier("historyCtrJedisClusterFactory")
    private HistoryCtrJedisClusterFactory historyCtrJedisClusterFactory;

    /**
     * 节点映射关系
     */
    private Map<String, JedisPool> nodeMap;

    /**
     * slot和host之间的映射
     */
    private TreeMap<Long, String> slotHostMap;


    /**
     * 初始化JedisNodeMap
     */
    private void initJedisNodeMap() {
        try {
            nodeMap = historyCtrJedisClusterFactory.getJedisCluster().getClusterNodes();
            String anyHost = nodeMap.keySet().iterator().next();
            initSlotHostMap(anyHost);
        } catch (JedisClusterException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 获取slot和host之间的对应关系
     *
     * @param anyHostAndPortStr
     * @return
     */
    private void initSlotHostMap(String anyHostAndPortStr) {
        TreeMap<Long, String> tree = new TreeMap<Long, String>();
        String parts[] = anyHostAndPortStr.split(":");
        HostAndPort anyHostAndPort = new HostAndPort(parts[0], Integer.parseInt(parts[1]));
        Jedis jedis = null;
        try {
            jedis = new Jedis(anyHostAndPort.getHost(), anyHostAndPort.getPort());
            jedis.auth("TBFVaNE5srcoUIJS");
            List<Object> list = jedis.clusterSlots();
            for (Object object : list) {
                List<Object> list1 = (List<Object>) object;
                List<Object> master = (List<Object>) list1.get(2);
                String hostAndPort = new String((byte[]) master.get(0)) + ":" + master.get(1);
                tree.put((Long) list1.get(0), hostAndPort);
                tree.put((Long) list1.get(1), hostAndPort);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        slotHostMap = tree;
    }

    public Map<String, JedisPool> getNodeMap() {
        return nodeMap;
    }

    public TreeMap<Long, String> getSlotHostMap() {
        return slotHostMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initJedisNodeMap();
    }
}
