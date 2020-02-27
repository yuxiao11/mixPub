package com.ifeng.recom.mixrecall.core.schedule;

import com.ifeng.recom.mixrecall.common.cache.*;
import com.ifeng.recom.mixrecall.common.service.filter.MediaFilter;
import com.ifeng.recom.mixrecall.common.service.filter.SansuFilter;
import com.ifeng.recom.mixrecall.common.util.cache.CachePersist;
import com.ifeng.recom.mixrecall.common.util.http.HttpClientUtil;
import com.ifeng.recom.mixrecall.core.cache.DocCtrCache;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;
import com.ifeng.recom.mixrecall.core.cache.UserSearchCache;
import com.ifeng.recom.mixrecall.core.cache.feedback.CdmlVideoCache;
import com.ifeng.recom.mixrecall.core.cache.feedback.PositiveFeedDataCache;
import com.ifeng.recom.mixrecall.core.cache.mapping.EditorIdDocIdMappingCache;
import com.ifeng.recom.mixrecall.core.cache.mapping.SimIdDocIdMappingCache;
import com.ifeng.recom.mixrecall.core.cache.preload.CotagDocsNewCache;
import com.ifeng.recom.mixrecall.core.cache.preload.CotagVideoNewCache;
import com.ifeng.recom.mixrecall.core.cache.preload.ExcellentVideoCache;
import com.ifeng.recom.mixrecall.core.cache.preload.TrueDocsCache;
import com.ifeng.recom.mixrecall.core.cache.result.UserCFCache;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.ifeng.recom.mixrecall.common.cache.ScoreKeywordCache.putScoreKeywordToCache;

/**
 * Created by lilg1 on 2017/11/21.
 */
@Component
public class ScheduleManager {
    private final static Logger logger = LoggerFactory.getLogger(ScheduleManager.class);

    @Autowired
    SourceInfoCache sourceInfoCache;

    @Autowired
    VideoSourceInfoCache videoSourceInfoCache;

    @Autowired
    WeMediaSourceNameCache weMediaSourceNameCache;

    @Autowired
    FilterDocsCache filterDocsCache;

    @Autowired
    LowTagInfoCache lowTagInfoCache;

<<<<<<< HEAD

=======
    @Autowired
    MediaFilter mediaFilter;
>>>>>>> add7216e9597a942f2c2c4e74105441dd134a281

    /**
     * 每一个小时更新一次 FilterDocsCache
     */
    @Scheduled(cron = "0 0/60 * * * ? ")
    public void updateFilterDocIds() {
        filterDocsCache.init();
    }


    /**
     * 每小时更新一次过滤词
     */
    @PostConstruct
    @Scheduled(cron = "0 0/60 * * * ? ")
    public void updateScoreKeywordFilter() {
        putScoreKeywordToCache();
        SansuFilter.updateWordScores();
    }

    /**
     * 初始化DocsCache
     */
    @PostConstruct
    public void initDocsCache() {
        CachePersist.loadToCache(TrueDocsCache.cache, "TrueDocsCache");
        if(TrueDocsCache.cache.size()==0){
            TrueDocsCache.loadCache();
        }
    }

    /**
     * 每小时更新媒体信息
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void updateSourceInfo() {
        sourceInfoCache.init();
        videoSourceInfoCache.init();
        weMediaSourceNameCache.init();
        mediaFilter.loadCache();
    }


    /**
     * 缓存状态监控
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void checkCacheStat() {
        UserProfileCache.checkStatus();
        ExecutorThreadPool.checkPoolStatus();
        SimIdDocIdMappingCache.checkStatus();
//        FilterDocsCache.checkStatus();
//        SourceInfoCache.checkStatus();
//        VideoSourceInfoCache.checkStatus();
//        WeMediaSourceNameCache.checkStatus();
        DocCtrCache.checkStatus();
        UserSearchCache.checkStatus();
        TrueDocsCache.checkStatus();
        UserCFCache.checkStatus();
        DocPreloadCache.checkStatus();
//        LowTagInfoCache.checkStatus();
        CotagDocsNewCache.checkStatus();
        CotagVideoNewCache.checkStatus();
        CdmlVideoCache.checkStatus();
        CdmlVideoCache.checkStatusAb();
        PositiveFeedDataCache.checkStatus();
        EditorIdDocIdMappingCache.checkStatus();
        ExcellentVideoCache.checkStatus();
    }

    /**
     * 连接池状态监控
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void checkConnPoolStat() {
        HttpClientUtil.checkPoolStatus();
    }

    /**
     * Cache Persist
     */
    @Scheduled(cron = "0 0/120 * * * ? ")
    public void CachePersist() {
        CachePersist.writeToFile(DocCtrCache.cache, "DocCtrCache");
        CachePersist.writeToFile(SimIdDocIdMappingCache.cache, "SimIdDocIdMappingCache");
//        CachePersist.writeToFile(CotagVideoNewCache.cache, "CotagVideoNewCache");
//        CachePersist.writeToFile(CotagDocsNewCache.cache, "CotagDocsNewCache");
        CachePersist.writeToFileMore(DocPreloadCache.cache, "DocPreloadCache");
    }

//    @PostConstruct
//    public void CacheLoaderCotagViewCache() {
//        CachePersist.loadToCache(CotagVideoNewCache.cache, "CotagVideoNewCache");
//    }

//    @PostConstruct
//    public void CacheLoaderCotagDocCache() {
//        CachePersist.loadToCache(CotagDocsNewCache.cache, "CotagDocsNewCache");
//    }



    @Scheduled(cron = "0 0/90 * * * ? ")
    public void CachePersistForTrue() {
        CachePersist.writeToFile(TrueDocsCache.cache, "TrueDocsCache");
    }


    /**
     * Load File to Cache DocCtrCache
     */
    @PostConstruct
    public void CacheLoaderDocCtrCache() {
        CachePersist.loadToCache(DocCtrCache.cache, "DocCtrCache");
    }

    /**
     * Load File to Cache SimIdDocIdMappingCache
     */
    @PostConstruct
    public void CacheLoaderSimIdDocIdMappingCache() {
        CachePersist.loadToCache(SimIdDocIdMappingCache.cache, "SimIdDocIdMappingCache");
    }






    @Scheduled(cron = "0 0/60 * * * ? ")
    public void loadLowTagCache() {
        try{
            lowTagInfoCache.init();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("loadLowTagCache error:{}",e);
        }
    }
}
