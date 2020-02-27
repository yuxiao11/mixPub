package com.ifeng.recom.mixrecall.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ifeng.recom.mixrecall.common.dao.mysql.dao.SourceInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 自媒体名称缓存
 * Created by geyl on 2018/3/6.
 */
@Service
public class WeMediaSourceNameCache extends AbstractMapCache<Integer>{
    private final static Logger logger = LoggerFactory.getLogger(WeMediaSourceNameCache.class);

    //    public static Cache<String, Integer> cache;
    //cache标记
    private static final int checkFlag = 1;

    @Autowired
    private SourceInfoDao sourceInfoDao;

//    static {
//        initCacheIds();
//    }
//
//    private static void initCacheIds() {
//        cache = CacheBuilder
//                .newBuilder()
//                .concurrencyLevel(10)
//                .initialCapacity(200000)
//                .maximumSize(500000)
//                .build();
//    }

    @PostConstruct
    public void init() {
        load();
//        try{
//            List<String> weMediaSourceList = sourceInfoDao.getOrganizationMedia();
//            for (String source : weMediaSourceList) {
//                cache.put(source, checkFlag);
//                logger.warn("init organization Media:{}", source);
//            }
//        }catch (Exception e){
//            logger.error("WeMediaSourceNameCache error:{}",e);
//        }

    }

    public Map<String, Integer> checkNameExist(Set<String> sourceNames) {
//        return cache.getAllPresent(sourceNames);
        return super.getAll(sourceNames);
    }

    @Override
    public void loadAll(Map<String, Integer> cache) {
        List<String> weMediaSourceList = sourceInfoDao.getOrganizationMedia();
        for (String source : weMediaSourceList) {
            cache.put(source, checkFlag);
            logger.warn("init organization Media:{}", source);
        }
    }

//    public static void checkStatus() {
//        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
//    }

//    public static void main(String[] args) {
//        WeMediaSourceNameCache weMediaSourceNameCache = new WeMediaSourceNameCache();
//        System.out.println(weMediaSourceNameCache.cache.asMap());
//    }
}
