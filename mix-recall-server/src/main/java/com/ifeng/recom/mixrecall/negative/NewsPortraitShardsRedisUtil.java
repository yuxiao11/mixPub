package com.ifeng.recom.mixrecall.negative;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.model.NewsPortraitRec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsPortraitShardsRedisUtil {
    private static ShardedJedisPool pool;
    private static final Logger logger = LoggerFactory.getLogger(NewsPortraitShardsRedisUtil.class);
    private static final String LOGGER_MARKER = "NewsPortraitRedisUtil";
    static {
        final List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>() {{
            add(new JedisShardInfo("10.80.81.140", 6379, 15000));
            add(new JedisShardInfo("10.80.81.140", 6380, 15000));
            add(new JedisShardInfo("10.80.77.141", 6379, 15000));
            add(new JedisShardInfo("10.80.77.141", 6380, 15000));
        }};
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(64);
        config.setMaxIdle(8);
        pool = new ShardedJedisPool(config, shards, Hashing.MURMUR_HASH);
    }

    public Map<String, String> hmget(String key, String... fields) {
        List<String> list = pool.getResource().hmget(key, fields);
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < fields.length; i++) {
            map.put(fields[i], list.get(i));
        }
        return map;
    }

    public Map<String, NewsPortraitRec> piplineHmgetNewsPortrait(List<String> simIdList, List<String> docTypeList) {
        Map<String, NewsPortraitRec> simIdToNewsPortraitRecMap = Maps.newHashMap();
        boolean tryFailed = true;
        int tryTimes = 0;
        while (tryFailed && tryTimes < 3) {
            ShardedJedis jedis = pool.getResource();
            try {
                ShardedJedisPipeline pipeline = jedis.pipelined();
                for (String simId : simIdList) {
//                    pipeline.hmget("stats:" + simId, "LAST_P1D", "LAST_PT3H");
                    pipeline.hmget("stats:" + simId, "LAST_PT3H", "LAST_P1D", "LAST_P3D", "total");
                }
                List<Object> resultList = pipeline.syncAndReturnAll();
                for (int i = 0; i < simIdList.size(); i++) {
                    List<String> results = (List<String>) resultList.get(i);
                    String docType = docTypeList.get(i);
                    NewsPortraitRec newsPortraitRec = parseInfo(results, docType);
                    simIdToNewsPortraitRecMap.put(simIdList.get(i), newsPortraitRec);
                }
                tryFailed = false;
            } catch (Exception e) {
                logger.error("tryTimes = {}, [{}], piplineHmgetNewsPortrait [ERROR]: {}", tryTimes, LOGGER_MARKER, e);
                tryTimes += 1;
            }finally {
                jedis.close();
            }
        }
        return simIdToNewsPortraitRecMap;
    }


    // 如果为null，返回默认值
    private String getStrValue(String value) {
        if (value == null) {
            return "0";
        } else {
            return value;
        }
    }
    private Double getDoubleValue(Double value) {
        if (value == null) {
            return 0.0;
        } else {
            return value;
        }
    }


    private NewsPortraitRec parseInfo(List<String> statInfos, String docType) {
        NewsPortraitRec newsPortraitRec = new NewsPortraitRec();
        if(statInfos == null) {
            return newsPortraitRec;
        }

        // last-3h
        JSONObject last3hObject = JSONObject.parseObject(statInfos.get(0));
        if (last3hObject != null) {
            newsPortraitRec.setLast3h_ev(getStrValue(last3hObject.getString("ev")));
            if("video".equals(docType)){
                newsPortraitRec.setLast3h_pv(getStrValue(last3hObject.getString("vv")));
            }else {
                newsPortraitRec.setLast3h_pv(getStrValue(last3hObject.getString("pv")));
            }
            newsPortraitRec.setLast3h_share(getStrValue(last3hObject.getString("share")));
            newsPortraitRec.setLast3h_store(getStrValue(last3hObject.getString("store")));
            newsPortraitRec.setLast3h_comment(getStrValue(last3hObject.getString("comment")));
        }

        // last-1d
        JSONObject last1dObject = JSONObject.parseObject(statInfos.get(1));
        if (last1dObject != null) {
            newsPortraitRec.setLast1d_ev(getStrValue(last1dObject.getString("ev")));
            if("video".equals(docType)){
                newsPortraitRec.setLast1d_pv(getStrValue(last1dObject.getString("vv")));
            }else {
                newsPortraitRec.setLast1d_pv(getStrValue(last1dObject.getString("pv")));
            }
            newsPortraitRec.setLast1d_share(getStrValue(last1dObject.getString("share")));
            newsPortraitRec.setLast1d_store(getStrValue(last1dObject.getString("store")));
            newsPortraitRec.setLast1d_comment(getStrValue(last1dObject.getString("comment")));

            // 设置过去24小时的时长和完成比统计
            Double duration = getDoubleValue(last1dObject.getDouble("duration"));
            Double duration_c = getDoubleValue(last1dObject.getDouble("duration_count"));
            Double v_duration = getDoubleValue(last1dObject.getDouble("v_duration"));
            Double v_duration_c = getDoubleValue(last1dObject.getDouble("v_duration_count"));
            Double readrate = getDoubleValue(last1dObject.getDouble("readrate"));
            Double readrate_c = getDoubleValue(last1dObject.getDouble("readrate_count"));
            Double v_readrate = getDoubleValue(last1dObject.getDouble("v_rate"));
            Double v_readrate_c = getDoubleValue(last1dObject.getDouble("v_rate_count"));
            if("video".equals(docType)){
                if (v_duration_c != 0.0) {
                    newsPortraitRec.setLast1d_avgduration(v_duration / v_duration_c);
                 }
                if (v_readrate_c != 0.0) {
                    newsPortraitRec.setLast1d_avgreadrate(v_readrate / v_readrate_c);
                }
            } else {
                if (duration_c != 0.0) {
                    newsPortraitRec.setLast1d_avgduration(duration / duration_c);
                }
                if (readrate_c != 0.0) {
                    newsPortraitRec.setLast1d_avgreadrate(readrate / readrate_c);
                }
            }
        }

        // last-3d
        JSONObject last3dObject = JSONObject.parseObject(statInfos.get(2));
        if (last3dObject != null) {
            newsPortraitRec.setLast3d_ev(getStrValue(last3dObject.getString("ev")));
            if("video".equals(docType)){
                newsPortraitRec.setLast3d_pv(getStrValue(last3dObject.getString("vv")));
            }else {
                newsPortraitRec.setLast3d_pv(getStrValue(last3dObject.getString("pv")));
            }
            newsPortraitRec.setLast3d_share(getStrValue(last3dObject.getString("share")));
            newsPortraitRec.setLast3d_store(getStrValue(last3dObject.getString("store")));
            newsPortraitRec.setLast3d_comment(getStrValue(last3dObject.getString("comment")));
        }

        // total
        JSONObject totalObject = JSONObject.parseObject(statInfos.get(3));
        if (totalObject != null) {
            newsPortraitRec.setTotal_ev(getStrValue(totalObject.getString("ev")));
            if("video".equals(docType)){
                newsPortraitRec.setTotal_pv(getStrValue(totalObject.getString("vv")));
            }else {
                newsPortraitRec.setTotal_pv(getStrValue(totalObject.getString("pv")));
            }
            newsPortraitRec.setTotal_share(getStrValue(totalObject.getString("share")));
            newsPortraitRec.setTotal_store(getStrValue(totalObject.getString("store")));
            newsPortraitRec.setTotal_comment(getStrValue(totalObject.getString("comment")));
        }

        // 设置today于last1d一样
        newsPortraitRec.setToday_ev(newsPortraitRec.getLast1d_ev());
        newsPortraitRec.setToday_pv(newsPortraitRec.getLast1d_pv());
        newsPortraitRec.setToday_share(newsPortraitRec.getLast1d_share());
        newsPortraitRec.setToday_store(newsPortraitRec.getLast1d_store());
        newsPortraitRec.setToday_comment(newsPortraitRec.getLast1d_comment());

        return newsPortraitRec;
    }


    /**
     * 批量查询文章的performance属性
     * 输入兼容有或者没有"cmpp_"前缀两种形式，如：[12345, cmpp_45678]
     * @param docIds
     * @return
     */
    public Map<String, String> getPerformaceBatch(List<String> docIds) {
        Map<String, String> docIdPerformanceMap = Maps.newHashMap();
        boolean tryFailed = true;
        int tryTimes = 0;
        while (tryFailed && tryTimes < 3) {
            ShardedJedis jedis = pool.getResource();
            try {
                ShardedJedisPipeline pipeline = jedis.pipelined();
                for (String docId : docIds) {
                    String key = "stats:" + docId;
                    if (!docId.startsWith("cmpp_")) {
                        key = "stats:cmpp_" + docId;
                    }
                    pipeline.hget(key, "performance");
                }
                List<Object> resultList = pipeline.syncAndReturnAll();
                for (int i = 0; i < docIds.size(); i++) {
                    String result = (String) resultList.get(i);
                    if (result != null) {
                        docIdPerformanceMap.put(docIds.get(i), result);
                    }
                }
                tryFailed = false;
            } catch (Exception e) {
                logger.error("tryTimes = {}, [{}], piplineGetPerformanceBatch [ERROR]: {}", tryTimes, LOGGER_MARKER, e);
                tryTimes += 1;
            }finally {
                jedis.close();
            }
        }
        return docIdPerformanceMap;
    }


    public static void main(String[] args) {
        NewsPortraitShardsRedisUtil newsPortraitShardsRedisUtil = new NewsPortraitShardsRedisUtil();
        List<String> simidList = new ArrayList<String>();
        List<String> docTypeList = new ArrayList<>();
//        simidList.add("clusterId_99841665");
        simidList.add("clusterId_91181429");
//        docTypeList.add("video");
        docTypeList.add(null);
        long start = System.currentTimeMillis();
        for(int i = 0; i < 1; i++) {
            newsPortraitShardsRedisUtil.piplineHmgetNewsPortrait(simidList,docTypeList);
        }
        long end = System.currentTimeMillis();
        System.out.println("elapsed: " + (end - start));
        System.out.println(newsPortraitShardsRedisUtil.hmget("stats:clusterId_99841665", "LAST_P1D", "LAST_PT3H"));
//        List<String> docIds = Lists.newArrayList();
//        docIds.add("74253959");
//        docIds.add("88610358");
//        docIds.add("42558808");
//        docIds.add("108923787");
//        System.out.println(newsPortraitShardsRedisUtil.getPerformaceBatch(docIds));
    }


}
