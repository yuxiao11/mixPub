package com.ifeng.recom.mixrecall.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ifeng.recom.mixrecall.common.dao.redis.RedisFailoverUtils;
import com.ifeng.recom.mixrecall.common.dao.redis.RedisUtils;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory.ListDocument;
import static com.ifeng.recom.mixrecall.core.cache.mapping.SimIdDocIdMappingCache.putSimIdDocId;

/**
 * Created by geyl on 2017/10/28.
 */
public class PreloadUtils {
    private static final Logger logger = LoggerFactory.getLogger(PreloadUtils.class);

    public static List<String> getDocumentId(String tag, int redisDb) {
        Jedis jedis = RedisUtils.getPreloadJedisClient();
        if (jedis != null) {
            jedis.select(redisDb);
        } else {
            return Collections.emptyList();
        }

        List<String> docIdList = new ArrayList<>(100);
        try {
            String rt = jedis.get(tag);
            if (rt == null) {
                return docIdList;
            }

            List<Document> documentList = GsonUtil.json2ObjectWithoutExpose(rt, ListDocument);

            if (documentList == null) {
                return docIdList;
            }

//            //媒体级别过滤,单个词的召回内容只出AB级别媒体
//            if (!tag.contains("-") && !tag.contains("source")) {
//                documentList = filterDocsByLevel(documentList, needLevel);
//            }

            for (Document d : documentList) {
                docIdList.add(d.getDocId());
//                putDocCache(d.getDocId(), d);
                putSimIdDocId(d.getSimId(), d.getDocId());
            }

            return docIdList;

        } catch (Exception e) {
            logger.error("get document id error: " + e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return docIdList;
    }


    /**
     * 新方法，预加载中将图文和视频拆分开
     *
     * @param tag
     * @param redisDb
     * @return
     */
    public static List<String> getDocIdNew(String tag, int redisDb) {
        Jedis jedis = RedisFailoverUtils.getPreloadJedisClient();

        List<String> docIdList = new ArrayList<>(100);
        try {
            if (jedis != null) {
                jedis.select(redisDb);
            } else {
                return Collections.emptyList();
            }
            String rt = jedis.get(tag);
            if (rt == null) {
                return docIdList;  //此处需要修改 BY YX
            }

            List<Document> documentList = GsonUtil.json2ObjectWithoutExpose(rt, ListDocument);

            if (documentList == null) {
                return docIdList;
            }


            for (Document d : documentList) {
                docIdList.add(d.getDocId());
                if(d!=null&&StringUtils.isNotBlank(d.getSimId())){
                    putSimIdDocId(d.getSimId(), d.getDocId());
                }
            }

            return docIdList;

        } catch (Exception e) {
            logger.error("get getDocIdNew id error:{} ", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return docIdList;
    }

    public static void main(String[] args) {
        CacheManager.init();
        getDocumentId("我军", 5);
    }

}
