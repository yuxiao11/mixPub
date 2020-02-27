package com.ifeng.recom.mixrecall.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import com.ifeng.recom.mixrecall.common.dao.mysql.dao.SourceInfoDao;
import com.ifeng.recom.mixrecall.common.model.LowTagInfoItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pandeng on 2018/11/8.
 */
@Service
public class LowTagInfoCache extends AbstractMapCache<String> {

    private static final Logger logger = LoggerFactory.getLogger(LowTagInfoCache.class);

//    private static Cache<String, String> cache;

    @Autowired
    private SourceInfoDao sourceInfoDao;

    private static  Set<String> lowSets= Sets.newHashSet();
    static {
//        cache = CacheBuilder
//                .newBuilder()
//                .concurrencyLevel(10)
//                .initialCapacity(200000)
//                .maximumSize(500000)
//                .build();

        lowSets.add("血腥");
        lowSets.add("三俗");
        lowSets.add("色情");
        lowSets.add("政治敏感");
    }

    @PostConstruct
    public void init() {
        load();
//        long start=System.currentTimeMillis();
//        try{
//            List<LowTagInfoItem> lowTagInfoList = sourceInfoDao.getLowTagInfo();
//            for (LowTagInfoItem item : lowTagInfoList) {
//                String auditTags=item.getAuditTags();
//                if(StringUtils.isNotBlank(auditTags)){
//                    for(String lowStr:lowSets){
//                        if(auditTags.contains(lowStr)){
//                            cache.put(item.getRecomId(),item.getAuditTags());
//                        }
//                    }
//                }
//            }
//            logger.info("LowTagInfoCache cache size:{},cost:{}",cache.size(),System.currentTimeMillis()-start);
//        }catch (Exception e){
//            logger.error("LowTagInfoCache error:{}",e);
//        }

    }

    public Map<String, String> getLowTags(Set<String> ids) {
        return super.getAll(ids);
    }


    @Override
    public void loadAll(Map<String, String> cache) {
        List<LowTagInfoItem> lowTagInfoList = sourceInfoDao.getLowTagInfo();
        for (LowTagInfoItem item : lowTagInfoList) {
            String auditTags=item.getAuditTags();
            if(StringUtils.isNotBlank(auditTags)){
                for(String lowStr:lowSets){
                    if(auditTags.contains(lowStr)){
                        cache.put(item.getRecomId(),item.getAuditTags());
                    }
                }
            }
        }
    }

//    public static Map<String, String> getLowTags(Set<String> ids) {
//        return cache.getAllPresent(ids);
//    }


//    public static void checkStatus() {
//        logger.debug("hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
//    }
}
