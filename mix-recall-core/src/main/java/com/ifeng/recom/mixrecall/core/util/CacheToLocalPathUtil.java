package com.ifeng.recom.mixrecall.core.util;

import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.cache.CachePersist;
import com.ifeng.recom.mixrecall.common.util.threadUtil.BaseThreadPool;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@Service
public class CacheToLocalPathUtil {

    protected final static Logger log = LoggerFactory.getLogger(CacheToLocalPathUtil.class);

    @PostConstruct
    public void init() {
        long start = System.currentTimeMillis();
        try {
            readlocalfile();
            if(DocPreloadCache.cache.size()==0){
                CachePersist.loadToCache(DocPreloadCache.cache, "DocPreloadCache");
            }
        } catch (Exception e) {
            log.error("readlocalfile init ERROR:{}", e);
        }
        log.info("readlocalfile init cost:{}", System.currentTimeMillis() - start);
    }

    private void readlocalfile() {
        try {
            File file = new File(GyConstant.localCacheDir);
            //判断目标文件所在的目录是否存在
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    log.error("readlocalfile 创建目标文件所在目录失败:{}", GyConstant.localCacheDir);
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
            }

            CountDownLatch countDownLatch = new CountDownLatch(GyConstant.doc_txt_Num);
            for (int i = 0; i < GyConstant.doc_txt_Num; i++) {
                String fileName = GyConstant.personalcacheOfpath + GyConstant.Symb_Underline + i;
                BaseThreadPool.THREAD_POOL_UpdateIndex.execute(new DealWorker(fileName, countDownLatch));
            }

            try {
                countDownLatch.await(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                //此处简化处理，非正常中断应该抛出异常或返回错误结果
                e.printStackTrace();
                log.error("readlocalfile wait ERROR:{}", e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("readlocalfile ERROR:{}", e);
        }
    }


    /**
     * 线程请求处理类
     */
    private class DealWorker implements Runnable {

        /**
         * 正在运行的线程数
         */
        private CountDownLatch countDownLatch;

        private String fileName;


        /**
         * 构造函数
         *
         * @param fileName         待处理文件名
         * @param countDownLatch 正在运行的线程数
         */
        private DealWorker(String fileName, CountDownLatch countDownLatch) {
            this.fileName = fileName;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                readTxtFiletoCache(fileName);
            } finally {
                //当前线程处理完成，runningThreadNum线程数减1，此操作必须在finally中完成，避免处理异常后造成runningThreadNum线程数无法清0
                this.countDownLatch.countDown();
            }
        }
    }


    /**
     * 读取缓存
     *
     * @param filePath
     */
    public void readTxtFiletoCache(String filePath) {
        long start = System.currentTimeMillis();
        try {
            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String lineTxt = "";

                long start1 = System.currentTimeMillis();
                try {
                    Document oneDocu = null;

                    while ((lineTxt = br.readLine()) != null) {
                        try {
                            if (StringUtils.isBlank(lineTxt)) {
                                continue;
                            }

                            oneDocu =GsonUtil.json2Object(lineTxt, Document.class);

                            if(StringUtils.isNotBlank(oneDocu.getDocId())){
                                DocPreloadCache.putDocCache(oneDocu.getDocId(),oneDocu);
                            }
                        } catch (Exception e) {
                            log.error("into readlocalfile ERROR:{}", e);
                        }
                    }
                } catch (Exception e) {
                    log.error("into readlocalfile ERROR:{}", e);
                } finally {
                    br.close();
                }

                long cost1 = System.currentTimeMillis() - start1;
                log.info("{} into readlocalfile cachesize={},cost={}", filePath, DocPreloadCache.cache.size(), cost1);

            }
        } catch (Exception e) {
            log.error("{} into readlocalfile Exception={}", filePath, e);
        }

        log.info("{} readlocalfile init cost:{}", filePath, System.currentTimeMillis() - start);
    }


    public static  void  main(String[] args){
        CacheToLocalPathUtil c=new CacheToLocalPathUtil();
        c.readTxtFiletoCache("d:/data/DocPreloadCache.txt_0");
    }
}
