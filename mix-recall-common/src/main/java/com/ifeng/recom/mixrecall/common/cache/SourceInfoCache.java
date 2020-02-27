package com.ifeng.recom.mixrecall.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.dao.mysql.dao.SourceInfoDao;
import com.ifeng.recom.mixrecall.common.model.SourceInfoItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by lilg1 on 2018/3/30.
 */
@Service
public class SourceInfoCache extends AbstractMapCache<String>{

    private static final Logger logger = LoggerFactory.getLogger(SourceInfoCache.class);

//    private static Cache<String, String> cache;

    @Autowired
    private SourceInfoDao sourceInfoDao;

//    static {
//        cache = CacheBuilder
//                .newBuilder()
//                .concurrencyLevel(10)
//                .initialCapacity(1000000)
//                .maximumSize(1000000)
//                .build();
//    }

    @PostConstruct
    public void init() {
        load();
//        try{
//            List<SourceInfoItem> sourceInfoList = sourceInfoDao.getSourceInfo();
//
//            for (SourceInfoItem item : sourceInfoList) {
//                cache.put(item.getManuscriptName(), item.getComEvalLevel());
//                logger.warn("init SourceInfo: {}, Level:{}", item.getManuscriptName(), item.getComEvalLevel());
//            }
//        }catch (Exception e){
//            logger.error("SourceInfoCache error:{}",e);
//        }

    }

    @Override
    public void loadAll(Map<String,String> cache) {
        List<SourceInfoItem> sourceInfoList = sourceInfoDao.getSourceInfo();
        for (SourceInfoItem item : sourceInfoList) {
            cache.put(item.getManuscriptName(), item.getComEvalLevel());
            logger.warn("init SourceInfo: {}, Level:{}", item.getManuscriptName(), item.getComEvalLevel());
        }
    }

    public String getSourceLevel(String sourceName) {
        return super.get(sourceName);
    }

    public Map<String, String> getSourceLevel(List<String> sourceNameList) {
        return super.getAll(sourceNameList);
    }

//    public static void checkStatus() {
//        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
//    }
}
