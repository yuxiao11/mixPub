package com.ifeng.recom.mixrecall.core.cache.feedback;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.FlowTypeAsync;
import com.ifeng.recom.mixrecall.common.model.item.Index4User;
import com.ifeng.recom.mixrecall.common.model.item.LastDocBean;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.PositivefeedClient;
import joptsimple.internal.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 协同过滤正反馈结果的cache
 * Created by jibin on 2018/1/10.
 */
public class PositiveFeedDataCache {
    private final static Logger logger = LoggerFactory.getLogger(PositiveFeedDataCache.class);

    private static Cache<String, List<Index4User>> VideoCache;
    private static Cache<String, List<Index4User>> DocpicCache;

    static {
        initDocpicCache();
        initVideoCache();
    }

    private static void initVideoCache() {
        VideoCache = CacheBuilder
                .newBuilder()
                .concurrencyLevel(15)
                .recordStats()
                .expireAfterWrite(180, TimeUnit.MINUTES)
                .initialCapacity(1000000)
                .maximumSize(1000000).build();

    }

    private static void initDocpicCache() {
        DocpicCache = CacheBuilder
                .newBuilder()
                .concurrencyLevel(15)
                .recordStats()
                .expireAfterWrite(180, TimeUnit.MINUTES)
                .initialCapacity(1000000)
                .maximumSize(1000000)
                .build();
    }

    public static void checkStatus() {
        logger.debug("docpic hit_count:{} hit_rate:{} load_count:{} cache_size:{}", DocpicCache.stats().hitCount(), DocpicCache.stats().hitRate(), DocpicCache.stats().loadCount(), DocpicCache.size());
        logger.debug("video hit_count:{} hit_rate:{} load_count:{} cache_size:{}", VideoCache.stats().hitCount(), VideoCache.stats().hitRate(), VideoCache.stats().loadCount(), VideoCache.size());
    }

    public static Map<String, List<Index4User>> getBatchDocpic(Set<String> simIds) {
        Map<String, List<Index4User>> map = null;
        try {
            map = DocpicCache.getAllPresent(simIds);
        } catch (Exception e) {
            logger.error("getBatchDocpic cache", e);
        }
        if (MapUtils.isEmpty(map)) {
            map = Maps.newHashMap();
        }
        return map;
    }

    public static Map<String, List<Index4User>> getBatchVideo(Set<String> simIds) {
        Map<String, List<Index4User>> map = null;
        try {
            map = VideoCache.getAllPresent(simIds);
        } catch (Exception e) {
            logger.error("getBatchVideo cache", e);
        }
        if (MapUtils.isEmpty(map)) {
            map = Maps.newHashMap();
        }
        return map;
    }

    public static List<Index4User> getItemcfVideo(String simId) {
        try {
            return VideoCache.getIfPresent(simId);
        } catch (Exception e) {
            logger.error("video", e);
        }
        return Collections.emptyList();
    }


    public static Map<String, List<Index4User>> checkUpdateDocpic(MixRequestInfo mixRequestInfo) {

        List<LastDocBean> lastDocBeans = mixRequestInfo.getLastDocBeans();
        Set<String> simIds = Sets.newHashSet();
        lastDocBeans.forEach((bean) -> {
            if (!Strings.isNullOrEmpty(bean.getSimId())) {   //把上次点击文章的simID存在set里
                simIds.add(bean.getSimId());
            }
        });

        Map<String, List<Index4User>> resultImmutable = PositiveFeedDataCache.getBatchDocpic(simIds);  //从协同过滤正反馈结果中根据lastdoc的bean的simid 从缓存中 获取map key为sim id
        Map<String, List<Index4User>> result=new HashMap<>(resultImmutable);
        List<LastDocBean> lastDocBeans2Update = Lists.newArrayList();   //需要更新的last doc
        for (LastDocBean lastDocBean : lastDocBeans) {          //缓存中没有
            //如果result 中不含有这个simid 说明这个bean的
            if (!result.containsKey(lastDocBean.getSimId())) {  //缓存中没有这个simid
                lastDocBeans2Update.add(lastDocBean);  //把这个lastDocBeans 在后面查url加进去
            }
        }
        if (CollectionUtils.isNotEmpty(lastDocBeans2Update)) {
            try {

                //获取正反馈结果，结果为一个map，key为simId
                //调用URL 获取每一个simid 下的 index信息
                Map<String, List<Index4User>> indexResult = PositivefeedClient.getPositivefeedResult(mixRequestInfo, lastDocBeans2Update, DocType.DOCPIC, 500);
                if (MapUtils.isNotEmpty(indexResult)) {
                    DocpicCache.putAll(indexResult);
                    indexResult.forEach((k,v)->result.put(k,v));
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("{} checkUpdateDocpic ERROR:{}", mixRequestInfo.getUid(), e);
            }
        }
        return result;
    }


    public static Map<String, List<Index4User>> checkUpdateVideo(MixRequestInfo mixRequestInfo) {

        List<LastDocBean> lastDocBeans = mixRequestInfo.getLastDocBeans();
        Set<String> simIds = Sets.newHashSet();
        lastDocBeans.forEach((bean) -> {
            if (!Strings.isNullOrEmpty(bean.getSimId())) {
                simIds.add(bean.getSimId());
            }
        });

        Map<String, List<Index4User>> resultImmutable = PositiveFeedDataCache.getBatchVideo(simIds);
        Map<String, List<Index4User>> result=new HashMap<>(resultImmutable);
        List<LastDocBean> lastDocBeans2Update = Lists.newArrayList();
        for (LastDocBean lastDocBean : lastDocBeans) {
            if (!result.containsKey(lastDocBean.getSimId())) {
                lastDocBeans2Update.add(lastDocBean);
            }
        }

        if (CollectionUtils.isNotEmpty(lastDocBeans2Update)) {
            try {
                Map<String, List<Index4User>> indexResult = PositivefeedClient.getPositivefeedResult(mixRequestInfo, lastDocBeans2Update, DocType.VIDEO, 500);
                if (MapUtils.isNotEmpty(indexResult)) {
                    VideoCache.putAll(indexResult);
                    indexResult.forEach((k,v)->result.put(k,v));
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("{} checkUpdateVideo ERROR:{}", mixRequestInfo.getUid(), e);
            }

        }
        return result;
    }


    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            try {


                String uid = "geyalu";

                MixRequestInfo mixRequestInfo = new MixRequestInfo();
                Map<String, Boolean> userTypeMap = new HashMap<>();
                mixRequestInfo.setUserTypeMap(userTypeMap);
                mixRequestInfo.setUid(uid);

                //last doc
                List<LastDocBean> lastDocBeanList = Lists.newArrayList();
                LastDocBean lastDocBean1 = new LastDocBean("66559695", "clusterId_50244589","");
                LastDocBean lastDocBean2 = new LastDocBean("44732264", "clusterId_26802883","");
                lastDocBeanList.add(lastDocBean1);
                lastDocBeanList.add(lastDocBean2);
                mixRequestInfo.setLastDocBeans(lastDocBeanList);

                //user info
                userTypeMap.put("isWxb", false);
                userTypeMap.put("isLvsWhite", true);
                userTypeMap.put("isBeiJingUserNotWxb", true);
                mixRequestInfo.setDebugUser(true);

                mixRequestInfo.setFlowType(FlowTypeAsync.positiveFeedNew);
                Map<String, List<Index4User>> simId2Itemcf = PositiveFeedDataCache.checkUpdateDocpic(mixRequestInfo);
                System.out.println(new Gson().toJson(simId2Itemcf));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
