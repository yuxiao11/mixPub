package com.ifeng.recom.mixrecall.core.cache.feedback;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.ifeng.recom.mixrecall.common.dao.redis.jedisPool.ItemcfJedisUtil;
import com.ifeng.recom.mixrecall.common.model.item.CdmlVideoItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by liligeng on 2019/5/22.
 */
public class CdmlVideoCache {

    private final static Logger logger = LoggerFactory.getLogger(CdmlVideoCache.class);

    //cdml文章缓存
    private static Cache<String, List<CdmlVideoItem>> cdmlVideoCache;

    //cdml文章查询为空缓存，十分钟之内不再查
    private static Cache<String, Boolean> cdmlVideoAbsentCache;

    private static CdmlVideoItem.CdmlVideoItemComparator cdmlComparator = new CdmlVideoItem.CdmlVideoItemComparator();

    private static final int dbNum = 6;

    static {
        initCdmlCache();
    }

    private static void initCdmlCache() {
        cdmlVideoCache = CacheBuilder
                .newBuilder()
                .concurrencyLevel(15)
                .recordStats()
                .expireAfterWrite(180, TimeUnit.MINUTES)
                .initialCapacity(1000000)
                .maximumSize(1000000).build();

        cdmlVideoAbsentCache = CacheBuilder
                .newBuilder()
                .concurrencyLevel(15)
                .recordStats()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .initialCapacity(200000)
                .maximumSize(200000).build();
    }

    public static List<CdmlVideoItem> getFromCache(String guid) {
        List<CdmlVideoItem> cdmlVideoItemList = cdmlVideoCache.getIfPresent(guid);
        return cdmlVideoItemList;
    }


    /**
     * @param guid
     * @param result
     * @return
     */
    public static Boolean checkCacheNeedUpdate(String guid, List<CdmlVideoItem> result) {
        List<CdmlVideoItem> cacheItem = cdmlVideoCache.getIfPresent(guid);
        if (cacheItem != null && !cacheItem.isEmpty()) {
            result.addAll(cacheItem);
            return false;
        }

        Boolean exist = cdmlVideoAbsentCache.getIfPresent(guid);
        if (exist != null) {
            return false;
        }

        return true;
    }

    public static Map<String, List<CdmlVideoItem>> batchQueryCdmlItem(List<String> id2Query, Map<String, String> guidSimIdMapping){
        Map<String, List<CdmlVideoItem>> result = new HashMap<>();

        for(String guid : id2Query){
            List<CdmlVideoItem> idQueryResult = new ArrayList<>();
            Boolean needUpdate = checkCacheNeedUpdate(guid, idQueryResult);
            if(needUpdate){
                idQueryResult = queryAndUpdateCache(guid);
            }

            String simId = guidSimIdMapping.get(guid);
            result.put(simId, idQueryResult);
        }
        return result;
    }

    /**
     * @param guid
     * @return
     */
    public static List<CdmlVideoItem> queryAndUpdateCache(String guid) {
        String cdmlStr = ItemcfJedisUtil.get(dbNum, guid);
        if (StringUtils.isBlank(cdmlStr)) {
            return new ArrayList<>();
        }

        List<CdmlVideoItem> result = new ArrayList<>();

        String[] cdmlArr = cdmlStr.split(",");
        for (String cdmlPair : cdmlArr) {
            String[] pairArr = cdmlPair.split("#");
            if (pairArr != null && pairArr.length > 2) {
                String simId = pairArr[0];
                String reGuid = pairArr[1];
                Double score = Double.valueOf(pairArr[2]);

                CdmlVideoItem item = new CdmlVideoItem(simId, reGuid, score);
                result.add(item);
            }
        }


        return result;
    }

    public static void checkStatus() {
        status(cdmlVideoCache, "video");
    }

    public static void checkStatusAb() {
        status(cdmlVideoAbsentCache,"absent");
    }

    private static void status(Cache cache,String str) {
        CacheStats stats = cache.stats();
        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}, {}", stats.hitCount(), stats.hitRate(),
                stats.loadCount(), cache.size(), str);
    }

}
