package com.ifeng.recom.mixrecall.prerank.ctrmodel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jibin on 2017/5/30.
 */
@Service
public class CtrModelRedisClusterUtil {

    private static final Logger logger = LoggerFactory.getLogger(CtrModelRedisClusterUtil.class);

    @Resource(name="ctrModelJedisCluster")
    private CtrModelJedisClusterFactory ctrModelJedisClusterFactory;

//    public void updateJedisCluster() throws Exception {
//        ctrModelJedisClusterFactory.updateJedisCluster();
//    }

    public List<String> scanModelList(String key) {
        List<String> resultList = new ArrayList<>();
        ScanParams params = new ScanParams();
        params.count(100);
        ScanResult<String> scanResult = ctrModelJedisClusterFactory.getJedisCluster().sscan(key, ScanParams.SCAN_POINTER_START, params);

        String nextCursor = scanResult.getStringCursor();
        List<String> scanResultList = scanResult.getResult();

        while (true) {
            for (String str : scanResultList) {
                if (StringUtils.isNotBlank(str)) {
                    resultList.add(str);
                }
            }

            if (ScanParams.SCAN_POINTER_START.equals(nextCursor) || StringUtils.isBlank(nextCursor)) {
                break;
            }

            scanResult = ctrModelJedisClusterFactory.getJedisCluster().sscan(key, nextCursor, params);
            nextCursor = scanResult.getStringCursor();
            scanResultList = scanResult.getResult();
        }
        logger.info("CtrModel sscan  finish scan  key:{}  count:{} ", key, resultList.size());
        return resultList;
    }

    public Map<String, String> getMap(String key) {
        return ctrModelJedisClusterFactory.getJedisCluster().hgetAll(key);
    }

    public void set(String key, String value) {
        ctrModelJedisClusterFactory.getJedisCluster().set(key, value);
    }


    public Set<String> smembers(String key) {
        return ctrModelJedisClusterFactory.getJedisCluster().smembers(key);
    }

    public String get(String key) {
        return ctrModelJedisClusterFactory.getJedisCluster().get(key);
    }

    public long llen(String key) {
        return ctrModelJedisClusterFactory.getJedisCluster().llen(key);
    }

    public List<String> lrange(String key, long start, long end) {
        return ctrModelJedisClusterFactory.getJedisCluster().lrange(key, start, end);
    }

}
