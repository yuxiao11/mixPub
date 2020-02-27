package com.ifeng.recom.mixrecall.prerank.tools;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.util.JedisClusterCRC16;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @Author: ZhenBingLiu
 * @Description: 操作redis工具类
 * @Date: Created in 15:262017/11/7
 * @Modified By:
 */
@Service("HistoryCtrRedisClusterUtil")
public class HistoryCtrRedisClusterUtil {
    private static final Logger logger = LoggerFactory.getLogger(HistoryCtrRedisClusterUtil.class);

    @Autowired
    @Resource(name = "historyCtrJedisClusterFactory")
    private HistoryCtrJedisClusterFactory historyCtrJedisClusterFactory;

    @Autowired
    @Resource(name = "historyCtrJedisClusterBatchUtil")
    private HistoryCtrJedisClusterBatchUtil historyCtrJedisClusterBatchUtil;

    @Autowired
    private CacheManager cacheManager;


    public void updateJedisCluster() throws Exception {
        historyCtrJedisClusterFactory.updateJedisCluster();
    }

    public String getValue(String key) {
        return historyCtrJedisClusterFactory.getJedisCluster().get(key);
    }



    /**
     * 批量查询数据
     *
     * @param keys
     * @return
     */
    public Map<String, Object> getBatch(String... keys) {

        // 返回的结果，包括正确的keys的value的集合；和不存在的keys的集合
        Map<String, Object> result = new HashMap<>(2);

        // 正确的keys的value的集合
        Map<String, String> existResult = new HashMap<>(500);

        // 错误的key的集合
        Set<String> errorKeys = new HashSet<>(16);

        // 处理keys和jedisPool之间的对应关系 （全局保存）
        dealKeyJedisPoolMap(keys);


        // JedisPool和本次请求的Keys的映射关系
        Map<JedisPool,ArrayList<String>> jedisPoolKeysMap = new HashMap<JedisPool, ArrayList<String>>();


        // 初始化jedisPoolKeysMap
        for (JedisPool jedisPool : historyCtrJedisClusterBatchUtil.getNodeMap().values()){
            ArrayList<String> keysList = new ArrayList<>();
            jedisPoolKeysMap.put(jedisPool, keysList);
        }

        // jedisPoolKeysMap赋值
         Cache keyJedisPoolMapCache = cacheManager.getCache("keyJedisPoolMap");
        for (String key : keys) {

            JedisPool jedisPool = (JedisPool) keyJedisPoolMapCache.get(key).getObjectValue();

            if (jedisPool == null){
                continue;
            }

            ArrayList<String> keysList = jedisPoolKeysMap.get(jedisPool);

            keysList.add(key);
        }



        /**
         * 按jedisPool 来对key进行pipeline批量操作
         */
        for (JedisPool jedisPool : jedisPoolKeysMap.keySet()) {
            Jedis jedis = jedisPool.getResource();

            Pipeline pipeline = jedis.pipelined();
            try {
                if (pipeline != null) {
                    pipeline.clear();

                    ArrayList<String> keysList = jedisPoolKeysMap.get(jedisPool);
                    for (String key : keysList) {
                        pipeline.hget(key, "total");
                    }
                    List<Object> results = pipeline.syncAndReturnAll();

                    for (int index = 0; index < results.size(); index++) {
                        if (results.get(index) == null) {
                            errorKeys.add(keysList.get(index));
                        } else if (!results.get(index).toString().startsWith("redis.clients.jedis.exceptions.JedisMovedDataException")) {
                            existResult.put(keysList.get(index), results.get(index).toString());
                        }
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            } finally {
                /**
                 * 关闭连接
                 */
                if (pipeline != null ){
                    try {
                        pipeline.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        result.put("error", errorKeys);
        result.put("exist", existResult);
        return result;
    }

    /**
     * 批量查询数据
     *
     * @param keys
     * @return
     */
    public Map<String, double[]> getBatchItems(String... keys) {

        // 返回的结果，包括正确的keys的value的集合；和不存在的keys的集合
        Map<String, double[]> result = new HashMap<>();


        // 处理keys和jedisPool之间的对应关系 （全局保存）
        dealKeyJedisPoolMap(keys);


        // JedisPool和本次请求的Keys的映射关系
        Map<JedisPool,ArrayList<String>> jedisPoolKeysMap = new HashMap<JedisPool, ArrayList<String>>();


        // 初始化jedisPoolKeysMap
        for (JedisPool jedisPool : historyCtrJedisClusterBatchUtil.getNodeMap().values()){
            ArrayList<String> keysList = new ArrayList<>();
            jedisPoolKeysMap.put(jedisPool, keysList);
        }

        // jedisPoolKeysMap赋值
        Cache keyJedisPoolMapCache = cacheManager.getCache("keyJedisPoolMap");
        for (String key : keys) {

            JedisPool jedisPool = (JedisPool) keyJedisPoolMapCache.get(key).getObjectValue();

            if (jedisPool == null){
                continue;
            }

            ArrayList<String> keysList = jedisPoolKeysMap.get(jedisPool);

            keysList.add(key);
        }



        /**
         * 按jedisPool 来对key进行pipeline批量操作
         */
        for (JedisPool jedisPool : jedisPoolKeysMap.keySet()) {
            Jedis jedis = jedisPool.getResource();

            Pipeline pipeline = jedis.pipelined();
            try {
                if (pipeline != null) {
                    pipeline.clear();

                    ArrayList<String> keysList = jedisPoolKeysMap.get(jedisPool);
                    for (String key : keysList) {
                        pipeline.get(key);
                    }
                    List<Object> results = pipeline.syncAndReturnAll();

                    for (int index = 0; index < results.size(); index++) {
                        if (results.get(index) == null) {
                            result.put(keysList.get(index), null);
                        } else if (!results.get(index).toString().startsWith("redis.clients.jedis.exceptions.JedisMovedDataException")) {
                            String value = results.get(index).toString();
                            String [] arr = value.split(",");
                            double[] vec = new double[arr.length];
                            for (int i = 0; i < arr.length; i++) {
                                vec[i] = Double.parseDouble(arr[i]);
                            }
                            result.put(keysList.get(index).split(":")[1], vec);
                        }
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            } finally {
                /**
                 * 关闭连接
                 */
                if (pipeline != null ){
                    try {
                        pipeline.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return result;
    }


    /**
     * 通过key来获取对应的jedisPool对象
     *
     * @param key
     * @return
     */
    private JedisPool getJedisByKey(String key) {
        int slot = JedisClusterCRC16.getSlot(key);
        Map.Entry<Long, String> entry;
        entry = historyCtrJedisClusterBatchUtil.getSlotHostMap().lowerEntry(Long.valueOf(slot + 1));

        if(entry == null){
            logger.error("entry为空!!!!! key为：" + key + "，slot为：" + slot);
            return null;
        }
        return historyCtrJedisClusterBatchUtil.getNodeMap().get(entry.getValue());
    }


    /**
     * 处理请求的Key和JedisPool的对应关系
     * @param keys
     */
    private void dealKeyJedisPoolMap(String... keys){
        Cache keyJedisPoolMapCache = cacheManager.getCache("keyJedisPoolMap");
        for (String key : keys){
            if(keyJedisPoolMapCache.get(key) == null){
                Element element = new Element(key, getJedisByKey(key));
                keyJedisPoolMapCache.put(element);
            }
        }
    }


    /**
     * json字符串转换为map
     *
     * @param result
     * @return
     */
    private Map<String, Double> stringToMap(String result) {
        Map<String, String> tmpMap;
        /**
         * 先转换为Map<String, String>格式，删除无用数据
         */
        tmpMap = JSON.parseObject(result, new TypeReference<Map<String, String>>() {
        });

        // 删除无用数据
        tmpMap.remove("pdate");
        tmpMap.remove("simid");
        tmpMap.remove("job_date");
        tmpMap.remove("phour");

        result = JSON.toJSONString(tmpMap);
        Map<String, Double> innerMap = null;

        innerMap = JSON.parseObject(result, new TypeReference<Map<String, Double>>() {
        });

        return innerMap;
    }


}
