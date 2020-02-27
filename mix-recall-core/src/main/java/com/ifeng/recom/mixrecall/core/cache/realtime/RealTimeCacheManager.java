package com.ifeng.recom.mixrecall.core.cache.realtime;

import com.google.common.collect.Lists;
import com.ifeng.recom.mixrecall.common.util.MonitorTools;
import com.ifeng.recom.mixrecall.common.util.cache.CachePersist;
import com.ifeng.recom.mixrecall.core.cache.AbstractAsyncCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class RealTimeCacheManager {
    private static final Logger logger = LoggerFactory.getLogger(RealTimeCacheManager.class);

    //    public static DocumentCache documentCache;
//    public static GuidCache guidCache;
    //    public static SourceExploreRecordInfoCache sourceRecordCache;
    public static PreCoTagDocsCache preCoTagDocsCache;
    public static PreCoTagVideoCache preCoTagVideoCache;
    public static ExDocpicCache exDocpicCache;
    public static List<AbstractAsyncCache> cacheList = Lists.newArrayList();

    public static DocumentSyncCache documentSyncCache;

    public static final String DUMP_PATH = CachePersist.CACHE_PATH;

    private static final String fileName(Class clazz) {
        return DUMP_PATH + clazz.getSimpleName() + ".cache.dump";
    }

    private static void createDumpDir() throws IOException {
        if (!Files.exists(Paths.get(DUMP_PATH))) {
            Files.createDirectories(Paths.get(DUMP_PATH));
        }
    }

    static {

        try {
            createDumpDir();
        } catch (Exception e) {
            logger.error("dump dir create error, path:{}, {}", DUMP_PATH, e);
        }


//        guidCache = new GuidCache(AbstractAsyncCache.newConfigBuilder()
//                .setThreadNum(1)
//                .setMaxBatchNum(1)
//                .setQueueLength(1000)
//                .setMaxWaitMilliseconds(100)
//                .setDumpFlag(false)
//                .build()
//        );
//
//        documentCache = new DocumentCache(AbstractAsyncCache.newConfigBuilder()
//                .setThreadNum(1)
//                .setMaxBatchNum(100)
//                .setQueueLength(10000)
//                .setMaxWaitMilliseconds(100)
//                .setDumpFlag(true)
//                .setDumpPath(fileName(DocumentCache.class))
//                .build()
//                , guidCache
//        );
//        sourceRecordCache = new SourceExploreRecordInfoCache(AbstractCache.newConfigBuilder()
//                .setThreadNum(1)
//                .setMaxBatchNum(1)
//                .setQueueLength(1000)
//                .setMaxWaitMilliseconds(100)
//                .build()
//        );

        preCoTagDocsCache = new PreCoTagDocsCache(AbstractAsyncCache.newConfigBuilder()
                .setThreadNum(1)
                .setMaxBatchNum(1)
                .setQueueLength(2000)
                .setMaxWaitMilliseconds(100)
                .setDumpFlag(true)
                .setDumpPath(fileName(PreCoTagDocsCache.class))
                .build()
        );

        preCoTagVideoCache = new PreCoTagVideoCache(AbstractAsyncCache.newConfigBuilder()
                .setThreadNum(1)
                .setMaxBatchNum(1)
                .setQueueLength(2000)
                .setMaxWaitMilliseconds(100)
                .setDumpFlag(true)
                .setDumpPath(fileName(PreCoTagVideoCache.class))
                .build()
        );

        exDocpicCache = new ExDocpicCache(AbstractAsyncCache.newConfigBuilder()
                .setThreadNum(1)
                .setMaxBatchNum(1)
                .setQueueLength(1000)
                .setMaxWaitMilliseconds(100)
                .setDumpFlag(true)
                .setDumpPath(fileName(ExDocpicCache.class))
                .build()
        );

//        documentSyncCache = new DocumentSyncCache(documentCache);
//
//
//        cacheList.add(guidCache);
//        cacheList.add(documentCache);
//        cacheList.add(sourceRecordCache);
        cacheList.add(preCoTagDocsCache);
        cacheList.add(preCoTagVideoCache);
        cacheList.add(exDocpicCache);
    }

    /**
     * 1min打印一次状态
     */
    @Scheduled(fixedDelay = 60000,initialDelay = 60000)
    public void status() {
        MonitorTools.logger.info("status cache start");
        for (AbstractAsyncCache cache : cacheList) {
            try {
                MonitorTools.logger.info("status {} guidCache:{}", cache.getClass().getSimpleName(), cache.status());
            } catch (Exception e) {
            }
        }
        MonitorTools.logger.info("status cache end");
    }

    /**
     * 每10min进行一次dump, 第一次dump在启动10min执行
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 10 * 60 * 1000)
    public void dump() {
        MonitorTools.logger.info("status cache start");
        for (AbstractAsyncCache cache : cacheList) {
            try {
                long start = System.currentTimeMillis();
                logger.info("dump cache start, {}", cache.getClassName());
                cache.dump();
                logger.info("dump cache end, {}, {}, {}", cache.getClassName(), System.currentTimeMillis() - start, cache.status());
            } catch (Exception e) {
                logger.error("dump error, {}, {}", cache.getClassName(), e);
            }
        }
        MonitorTools.logger.info("status cache end");
    }

    @PostConstruct
    public void load() {
        logger.info("load cache start");
        for (AbstractAsyncCache cache : cacheList) {
            try {
                long start = System.currentTimeMillis();
                logger.info("load cache start, {}", cache.getClassName());
                cache.load();
                logger.info("load cache end, {}, {}, {}", cache.getClassName(), System.currentTimeMillis() - start, cache.status());
            } catch (Exception e) {
                logger.error("load error, {}, {}", cache.getClassName(), e);
            }
        }
        logger.info("load cache end");
    }
}
