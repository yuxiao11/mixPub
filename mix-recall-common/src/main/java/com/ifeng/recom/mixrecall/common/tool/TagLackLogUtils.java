package com.ifeng.recom.mixrecall.common.tool;

import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.dao.redis.RedisUtils;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liligeng on 2019/3/21.
 * 输出统计内容缺失日志
 */
public class TagLackLogUtils {

    private static final Logger logger = LoggerFactory.getLogger(TagLackLogUtils.class);

    private static final Logger errLogger = LoggerFactory.getLogger(DocType.class);


    public static void info(String format, Object... argArray) {
        logger.info(format, argArray);
    }


    /**
     * 抽样5%的用户记录标签缺失信息
     *
     * @param mixRequestInfo 用户uid
     * @param docType        文章类型
     * @param tagNeedMap     用户需要召回数
     * @param tagLackMap     用户实际召回数
     */
    public static void recordLackInfo(MixRequestInfo mixRequestInfo, DocType docType, Map<String, Integer> tagNeedMap, Map<String, Integer> tagLackMap, Map<String, Integer> recallNumMap) {
        if (MapUtils.isEmpty(tagNeedMap) || MapUtils.isEmpty(tagLackMap)) {
            return;
        }

        Map<String, TagLackItem> lackItemMap = new HashMap<>();
        Set<String> allTagSet = new HashSet<>();

        //合并当前所有的召回标签
        allTagSet.addAll(tagNeedMap.keySet());
        allTagSet.addAll(tagLackMap.keySet());
        allTagSet.addAll(recallNumMap.keySet());


        for (String cotag : allTagSet) {
            //此处tag只截断coTag前面那个词
            String tag = cotag.substring(0, cotag.indexOf("-"));
            TagLackItem item = lackItemMap.get(tag);

            int needNum = tagNeedMap.getOrDefault(cotag, 0);
            int lackNum = tagLackMap.getOrDefault(cotag, 0);
            int recallNum = recallNumMap.getOrDefault(cotag, 0);

            if (item == null) {
                item = new TagLackItem(tag, needNum, lackNum, recallNum);
                lackItemMap.put(tag, item);
            } else {
                item.addAllNum(needNum, lackNum, recallNum);
            }
        }

        //记录缺失情况到日志
        for (TagLackItem item : lackItemMap.values()) {
            //tag标签 , c docType分类, n 需要数, l  缺失数, r 召回数
            logger.info("t:{}, c:{}, n:{}, l:{}, r:{}", item.getTag(), docType.getValue(), item.getNeedNum(), item.getLackNum(), item.getRecallNum());
        }

        //上报文章统计到redis
        reportLackInfo2Redis(mixRequestInfo, docType, lackItemMap);
    }


    /**
     * 上报标签缺失信息到redis
     */
    public static void reportLackInfo2Redis(MixRequestInfo mixRequestInfo, DocType docType, Map<String, TagLackItem> lackItemMap) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Jedis jedis = RedisUtils.getTagLackJedisClient();

        String dateStr = sdf.format(calendar.getTime());

        try {
            jedis.select(10);
            Pipeline pipeline = jedis.pipelined();

            //缺失数量，缺失比例上报到redis
            for (Map.Entry<String, TagLackItem> entry : lackItemMap.entrySet()) {
                StringBuilder sb = new StringBuilder();
                TagLackItem lackItem = entry.getValue();
                String key = sb.append(dateStr).append(":").append(docType.getValue()).append(":").append(entry.getKey()).toString();
                String lackNumStr = lackItem.getLackNum() + "";
                //增加统计人数
                pipeline.hincrBy(key, lackNumStr, 1);
                pipeline.hincrBy(key, "totalPerson", 1);
                pipeline.hincrBy(key, "totalLack", lackItem.getLackNum());
                pipeline.expire(key, 259200);

                if (needRecordNewUser(mixRequestInfo)) {
                    logger.warn("newuser: {} key:{}", mixRequestInfo.getUid(), key);
                    pipeline.hincrBy(key, "totalNewPerson", 1);
                    pipeline.hincrBy(key, "totalNewLack", lackItem.getLackNum());
                }

            }

            pipeline.sync();

        } catch (Exception e) {
            errLogger.error("report lack info to redis err:{}", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

    private static boolean needRecordNewUser(MixRequestInfo mixRequestInfo) {
        boolean check = false;
        UserModel userModel = mixRequestInfo.getUserModel();
        if (userModel.getUa_v() < 2) {
            try {
                String firstIn = userModel.getFirst_in();
                if (StringUtils.isNotBlank(firstIn)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    Date date = sdf.parse(firstIn);
                    //大于30天的用户不记录
                    if ((System.currentTimeMillis() - date.getTime()) > 30 * 24 * 3600 * 1000) {
                        return false;
                    }
                }
            } catch (Exception e) {
                logger.error("{} parse first_in err:{}", mixRequestInfo.getUid(), e);
            }
            return true;
        }

        return check;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class TagLackItem {

        //文章标签
        private String tag;

        //需要数量
        private int needNum;

        //召回数量
        private int recallNum;

        //缺失数量
        private int lackNum;

        public TagLackItem(String tag, int needNum, int lackNum, int recallNum) {
            this.tag = tag;
            this.needNum = needNum;
            this.lackNum = lackNum;
            this.recallNum = recallNum;
        }


        public void addAllNum(int needNum, int lackNum, int recallNum) {
            this.needNum += needNum;
            this.lackNum += lackNum;
            this.recallNum += recallNum;
        }

    }

}
