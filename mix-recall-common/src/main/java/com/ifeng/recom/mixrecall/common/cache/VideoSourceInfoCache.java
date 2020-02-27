package com.ifeng.recom.mixrecall.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ifeng.recom.mixrecall.common.dao.mysql.dao.VideoSourceInfoDao;
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
public class VideoSourceInfoCache extends AbstractMapCache<String> {
    private final static Logger logger = LoggerFactory.getLogger(VideoSourceInfoCache.class);

    //    private static Cache<String, String> cache;
//
    @Autowired
    VideoSourceInfoDao videoSourceInfoDao;
//
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
//            List<SourceInfoItem> videoSourceList = videoSourceInfoDao.getVideoSourceInfo();
//
//            for (SourceInfoItem item : videoSourceList) {
//                cache.put(item.getManuscriptName(), item.getComEvalLevel());
//                logger.warn("init video SourceInfo:{} Level:{}", item.getManuscriptName(), item.getComEvalLevel());
//            }
//        }catch (Exception e){
//            logger.error("VideoSourceInfoCache error:{}",e);
//        }

    }

    public String getVideoSourceLevel(String sourceName) {
        return super.get(sourceName);
    }

    public Map<String, String> getVideoSourceLevel(List<String> sourceList) {
        return super.getAll(sourceList);
    }

    @Override
    public void loadAll(Map<String, String> cache) {
        List<SourceInfoItem> videoSourceList = videoSourceInfoDao.getVideoSourceInfo();

        for (SourceInfoItem item : videoSourceList) {
            cache.put(item.getManuscriptName(), item.getComEvalLevel());
            logger.warn("init video SourceInfo:{} Level:{}", item.getManuscriptName(), item.getComEvalLevel());
        }
    }

//    public static void checkStatus() {
//        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
//    }
}
