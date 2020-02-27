package com.ifeng.recom.mixrecall.negative;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.NewsPortraitRec;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import com.ifeng.recom.mixrecall.negative.cache.CacheEntity;
import com.ifeng.recom.mixrecall.negative.cache.CacheEntityUtil;
import com.ifeng.recom.mixrecall.negative.cache.ItemCacheCountUtil;
import com.ifeng.recom.mixrecall.negative.constant.GyConstant;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.hbase.client.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;




/**
 * 新闻内容画像查询
 * Created by jibin on 2017/5/4.
 */
@Service("HeadlineItemProfileService")
public class HeadlineItemProfileService implements IItemProfileService {
    private static final Logger logger = LoggerFactory.getLogger(HeadlineItemProfileService.class);
    private static final String LOGGER_MARKER = "HeadLineItemProfile";

    @Autowired
    private HeadlineItemProfileUtils headlineItemProfileUtils;

    @Autowired
    private CacheManager cacheManager;


    @Autowired
    private NewsPortraitShardsRedisUtil newsPortraitShardsRedisUtil;


    @Autowired
    private DocClient docClient;

    @Autowired
    private ItemCacheCountUtil itemCacheCountUtil;

    /**
     * 获取头条新闻的内容画像
     *
     * @param itemList
     * @return
     */
    @Override
    public Map<String, ItemProfile> getItemProfileModel(List<Item> itemList, MixRequestInfo requestInfo) {

        Map<String, ItemProfile> result = Maps.newHashMap();
        CacheEntity cacheEntity = CacheEntityUtil.getInstance();

        List<String> docIdList = Lists.newArrayList();
        ItemProfile itemProfileTemp = null;

        for (Item item : itemList) {

            itemProfileTemp = new ItemProfile(GyConstant.ItemProfileName.HeadLine_ITEM_PROFILE.getValue());
            
            //----------------从itemProfileEntity缓存 获取内容画像--------------------------------------------------------

            Document document = this.getItemProfileByCache(item);

            //----------------缓存没有添加到待请求docIdList---------------------------------------------------------------
            if (document == null) {
                docIdList.add(item.getId());
            }
            itemProfileTemp.setDocument(document);

            result.put(item.getId(), itemProfileTemp);
        }
        cacheEntity.addCacheNum("hitCacheItemProfileEntity", itemList.size() - docIdList.size());
        cacheEntity.addCacheNum("missCacheProfileEntity", docIdList.size());

        //----------------从ikv根据docIdList获取缓存中没有的内容画像-------------------------------------------------------
        if (CollectionUtils.isNotEmpty(docIdList)) {
            Map<String, Document> itemDocumentMap;

            itemDocumentMap = this.getItemProfileFromEs(docIdList);

            if (MapUtils.isEmpty(itemDocumentMap)) {

                for (String docId : docIdList) {
                    result.get(docId).setDocument(new Document());
                }
            } else {
                //----------------填充请求要的内容画像并更新缓存----------------------------------------------------------
                Cache cache = this.cacheManager.getCache(GyConstant.CacheName.ITEM_DOCUMENT_CACHEKEY.getValue());
                for (String docId : docIdList) {
                    Document document = itemDocumentMap.get(docId);
                    if (document == null) {
                        result.get(docId).setDocument(new Document());
                    } else {
                        result.get(docId).setDocument(document);
                        //--------更新缓存------
                        Element element = new Element(docId, document);
                        itemCacheCountUtil.addItems(docId);
                        cache.put(element);
                    }
                }
            }
        }


        return result;
    }

    //----------------private 方法---------------------------------------------------------------------

    /**
     * 从缓存查询用户画像，缓存没有返回null
     *
     * @param item
     * @return
     */
    private Document getItemProfileByCache(Item item) {
        Document document = null;
        Cache cache = this.cacheManager.getCache(GyConstant.CacheName.ITEM_DOCUMENT_CACHEKEY.getValue());

        Element element = cache.get(item.getId());
        if (element == null) {
            return null;
        } else {
            try {
                document = (Document) element.getObjectValue();
            } catch (Exception e) {
                logger.error("parse [{}] from cache error,uid:{} ,ERROR:{}", LOGGER_MARKER, item.getId(), e);
            }
        }
        return document;
    }

    /**
     * 从HBase中批量获取新闻画像
     * @param docIds
     * @return
     */
    public Map<String, Document> getItemProfileFromEs(final List<String> docIds) {
        Future<Map<String, Document>> future = ExecutorThreadPool.threadPool.submit(new Callable<Map<String, Document>>() {
            @Override
            public Map<String, Document> call() throws Exception {

                Table cntTable = null;
                Table indexTable = null;
                Map<String, Document> tempMap;
                try {

                    tempMap = docClient.getDocBatch(docIds);
                } catch (Exception e) {
                    logger.error("get item profile batch from HBase failed: {}", e);
                    tempMap = new HashMap<>();
                }

                if (tempMap == null) {
                    return new HashMap<>();
                }
                return tempMap;
            }
        });


        Map<String, Document> resultMap = null;
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("itemHbase");

        try {
            resultMap = future.get(150, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("get item profile from hbase currentThread interrupted!:{}", e);
        } catch (TimeoutException e) {
            logger.error("get item profile from hbase timeout!:{}", e);
        } catch (Exception e) {
            logger.error("get item profile from hbase error!:{}", e);
        } finally {
            future.cancel(true);
        }
        timer.addEndTime("itemHbase");

        if (resultMap == null) {
            resultMap = new HashMap<>();
        }

        // 统计没查到的画像
        int emptyItemProfileCount = 0;
        for (String docId : docIds) {
            Document itemProfile = resultMap.get(docId);
            if (itemProfile == null) {
                emptyItemProfileCount += 1;
            }
        }
        if (emptyItemProfileCount > 0) {
            logger.error("!!! not all item profiles fetched, empty item profile = {}, all items = {}", emptyItemProfileCount, docIds.size());
        }
        //TODO 此处有可能获取不到用户画像

        return resultMap;
    }


    /**
     * 去掉不需要的画像字段
     * @param headLineItemProfile
     */
    private void resetItemProfile(HeadLineItemProfile headLineItemProfile) {
        headLineItemProfile.setCombineTagList(null);
        headLineItemProfile.setLatentTopic(null);
        headLineItemProfile.setPicFingerprint(null);
        headLineItemProfile.setThumbnailpic(null);
        headLineItemProfile.setFeatures2(null);
        headLineItemProfile.setHotEvent(null);
        headLineItemProfile.setCategory(null);
        headLineItemProfile.setTopic(null);
        headLineItemProfile.setLoclist(null);
        headLineItemProfile.setSplitContent(null);
        headLineItemProfile.setSplitTitle(null);
    }


    /**
     * 从缓存获取内容画像统计数据，缓存没有返回null
     *
     * @param docId
     * @return
     */
    private NewsPortraitRec getNewsPortraitRecByCache(String docId) {
        NewsPortraitRec newsPortraitRec = null;
        Cache cache = this.cacheManager.getCache(GyConstant.CacheName.ITEM_PROFILE_STATISTICS_CACHEKEY.getValue());
        Element element = cache.get(docId);
        if (element == null) {
            return null;
        } else {
            try {
                newsPortraitRec = (NewsPortraitRec) element.getObjectValue();
            } catch (Exception e) {
                logger.error("parse [HeadLineItemProfile] from cache error,uid:{} ,ERROR:{}", docId, e);
            }
        }
        return newsPortraitRec;
    }


    /**
     * 从redis获取内容画像统计数据，redis查询为空，返回null
     *
     * @param simIdList
     * @return
     */
    private Map<String, NewsPortraitRec> getNewsPortraitRecByRedisBatch(final List<String> simIdList, final List<String> docTypeList) {
        Map<String, NewsPortraitRec> newsProtraitRecmap = null;

        TimerEntity timer = TimerEntityUtil.getInstance();
        if (simIdList.size() == 0) {
            return newsProtraitRecmap;
        }
        timer.addStartTime("TjInfo");
        Future<Map<String, NewsPortraitRec>> future = ExecutorThreadPool.threadPool.submit(new Callable<Map<String, NewsPortraitRec>>() {
            @Override
            public Map<String, NewsPortraitRec> call() throws Exception {
                return newsPortraitShardsRedisUtil.piplineHmgetNewsPortrait(simIdList, docTypeList);

            }
        });

        int timeout = 300;

        try {
            newsProtraitRecmap = future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("[{}], get item statistic from redis currentThread interrupted [Error]: {}", LOGGER_MARKER, e);
        } catch (ExecutionException e) {
            logger.error("[{}], get item statistic from redis ExecutionException [Error]: {}", LOGGER_MARKER, e);
        } catch (TimeoutException e) {
            logger.error("[{}], get item statistic from redis TimeoutException [Error]: {}", LOGGER_MARKER, e);
        } catch (Exception e) {
            logger.error("[{}], get item statistic from redis Exception [Error]: {}", LOGGER_MARKER, e);
        } finally {
            future.cancel(true);
        }

        timer.addEndTime("TjInfo");
        if (MapUtils.isEmpty(newsProtraitRecmap)) {
            return null;
        }
        return newsProtraitRecmap;

    }
}
