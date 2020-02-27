package com.ifeng.recom.mixrecall.common.service;

import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.util.UserProfileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.ifeng.recom.mixrecall.common.constant.RecallConstant.PROFILE_CUT_OFF_WEIGHT;
import static com.ifeng.recom.mixrecall.common.util.UserProfileUtils.profileTagWeightFilter;

/**
 * Created by geyl on 2017/10/28.
 */
@Service
public class UserProfile {
    private static final Logger logger = LoggerFactory.getLogger(UserProfile.class);
<<<<<<< HEAD
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    private static final String TABLE_NAME_USERPROFILE = "UserCenter_Official";
    private static final String COLUMN_FAMILY_USERPROFILE = "info";
    private static final String[] HBASE_COLUMN = {"t1", "recent_t1", "last_t1", "ucombineTag", "recent_ucombineTag", "last_ucombineTag", "topic1_explore", "loc", "search", "general_search", "general_ub", "group_ub", "ub","user_cluster", "locDetail", "general_locDetail", "ua_v", "first_in", "docpic_lda_topic", "recent_docpic_lda_topic", "video_cate", "docpic_cate",
            "video_subcate", "docpic_subcate", "generalloc", "appUserInterest","last_lda_topic","last_docpic_lda_topic","daily_pullnum","last_video_cotag"};

    /**
     * 获取分区rowkey前缀
     *
     * @param uid 用户id
     * @return Hbase前缀+uid
     */
    private static String getRowKey(String uid) {
        if (Strings.isNullOrEmpty(uid)) {
            return null;
        }

        try {
            byte[] btInput = uid.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(btInput);
            byte[] resultByteArray = messageDigest.digest();
            int i = 0;
            for (byte aResultByteArray : resultByteArray) {
                i += Math.abs(aResultByteArray);
            }
            int prefix = 1000 + Math.abs(i) % 50;
            return prefix + "_" + uid;
        } catch (Exception e) {
            logger.error("get rowkey error", e);
        }

        return null;
    }

    /**
     * 根据用户uid查询其用户画像信息
     *
     * @param uid 用户id
     * @return map 保存字段及其值
     */
    private static Map<String, String> getUserProfileFromHbase(String uid) {
        Map<String, String> reMap = new HashMap<>();

        try {
            String rowKey = getRowKey(uid);
            if (rowKey != null) {
                reMap = getResultByColumns(TABLE_NAME_USERPROFILE, rowKey, COLUMN_FAMILY_USERPROFILE, HBASE_COLUMN);
            }
        } catch (Exception e) {
            logger.error("get from hbase error, uid:{}, error:{}", uid, e);
        }

        return reMap;
    }

    private static Map<String, String> getUserProfileMapFromRedis(String uid) {
        Map<String, String> map = new HashMap<>();

        try {
            JedisCluster jedisCluster = getProfileJedisClusterClient();
            List<String> userProfileList = jedisCluster.hmget(uid, HBASE_COLUMN);
            for (int i = 0; i < HBASE_COLUMN.length; i++) {
                map.put(HBASE_COLUMN[i], userProfileList.get(i));
            }
        } catch (Exception e) {
            logger.error("get profile from redis", e);
        }

        return map;
    }

    private static Map<String, String> getUserProfileMapFromRedisForColumns(String uid, String[] columns) {
        JedisCluster jedisCluster = getProfileJedisClusterClient();
        List<String> userProfileList = jedisCluster.hmget(uid, columns);

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            map.put(columns[i], userProfileList.get(i));
        }

        return map;
    }

=======
>>>>>>> add7216e9597a942f2c2c4e74105441dd134a281
    static UserModel transformMapToUserModel(Map<String, String> userProfileMap, UserModel userModel) {
        try {
            userModel.setUa_v(Integer.parseInt(StringUtils.isBlank(userProfileMap.get("ua_v")) ? "1" : userProfileMap.get("ua_v")));

            if (StringUtils.isNotBlank(userProfileMap.get("first_in"))) {
                userModel.setFirst_in(userProfileMap.get("first_in"));
            }

            /**
             * Add PullNum By YX 20190523
             */
            userModel.setDaily_pullNum(userProfileMap.get("daily_pullnum"));

            /**
             * fm模型相关特征 By YX 20191227
             */
            userModel.setUmos(userProfileMap.get("umos"));
            userModel.setE(userProfileMap.get("e"));
            userModel.setS(userProfileMap.get("s"));
            userModel.setUb_fm(userProfileMap.get("ub"));
            userModel.setGeneral_ub_fm(userProfileMap.get("general_ub"));
            userModel.setUi_fm(userProfileMap.get("ui"));
            userModel.setUk_fm(userProfileMap.get("uk"));
            userModel.setSearch_fm(userProfileMap.get("search"));
            userModel.setGeneral_Search_fm(userProfileMap.get("general_search"));
            userModel.setUser_cluster(userProfileMap.get("user_cluster"));
            userModel.setDocpic_sc_period("docpic_subcate_period");
            userModel.setVideo_sc_period("video_subcate_period");
            userModel.setGroup_ub("group_ub");
            userModel.setGeneral_likeVidR_weekends("general_likeVidR_weekends");
            userModel.setGeneral_likeVidR_dayInWork("general_likeVidR_dayInWork");
            userModel.setGeneral_likeVidR_nightInWork("general_likeVidR_nightInWork");

            userModel.setGeneral_doc_timeSensitive("general_doc_timeSensitive");
            userModel.setGeneral_vid_timeSensitive("general_vid_timeSensitive");
            userModel.setUser_group("user_group");



            userModel.setUserChannel(userProfileMap.get("user_schannel"));
            userModel.setLoc(userProfileMap.get("loc"));
            userModel.setGeneralLoc(userProfileMap.get("generalloc"));
            userModel.setFullness(userProfileMap.get("fullness"));

            userModel.setCombineTagList(profileTagWeightFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("ucombineTag")), PROFILE_CUT_OFF_WEIGHT));
            userModel.setDocpic_cotag(profileTagWeightFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("docpic_cotag")), PROFILE_CUT_OFF_WEIGHT));

            //更新小视频的画像
            userModel.setSvideo_cotagList(profileTagWeightFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("F")), PROFILE_CUT_OFF_WEIGHT));
            userModel.setSvideo_cate(profileTagWeightFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("svideo_cate")), PROFILE_CUT_OFF_WEIGHT));


            //以下两个字段取全量 用来MixcCotag通道召回进行过滤使用 此处注意在 C 和 SC相关通道进行外部处理 将权重小于0.5的数据进行过滤
            userModel.setVideo_cate(UserProfileUtils.extractCombineTag(userProfileMap.get("video_cate")));
            userModel.setDocpic_cate(UserProfileUtils.extractCombineTag(userProfileMap.get("docpic_cate")));

            userModel.setLastCombineTagList(profileTagWeightFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("last_ucombineTag")), PROFILE_CUT_OFF_WEIGHT));
            userModel.setLastCombineTagList(profileTagWeightFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("last_dis_ucombine_tag")), PROFILE_CUT_OFF_WEIGHT));
            userModel.setRecentCombineTagList(profileTagWeightFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("recent_ucombineTag")), PROFILE_CUT_OFF_WEIGHT));
            userModel.setLastLdaTopicList(profileTagWeightFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("last_lda_topic")), PROFILE_CUT_OFF_WEIGHT));
            userModel.setLastVideoCotag(profileTagWeightFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("last_video_cotag")), PROFILE_CUT_OFF_WEIGHT));

            userModel.setLong_uTopic(profileTagWeightFilter(UserProfileUtils.extractUTopic(userModel.getUserId(), userProfileMap.get("long_uTopic")), PROFILE_CUT_OFF_WEIGHT));
            userModel.setLast_uTopic(profileTagWeightFilter(UserProfileUtils.extractUTopic(userModel.getUserId(), userProfileMap.get("last_uTopic")), PROFILE_CUT_OFF_WEIGHT));
            userModel.setVideo_cotag(profileTagWeightFilter(UserProfileUtils.extractUTopic(userModel.getUserId(), userProfileMap.get("video_cotag")), PROFILE_CUT_OFF_WEIGHT));
            userModel.setRecent_video_cotag(profileTagWeightFilter(UserProfileUtils.extractUTopic(userModel.getUserId(), userProfileMap.get("recent_video_cotag")), PROFILE_CUT_OFF_WEIGHT));
            userModel.setRecent_docpic_cotag(profileTagWeightFilter(UserProfileUtils.extractUTopic(userModel.getUserId(), userProfileMap.get("recent_docpic_cotag")), PROFILE_CUT_OFF_WEIGHT));

            userModel.setLastt1RecordList(profileTagWeightFilter(UserProfileUtils.extractRecordListFromStrOnlyWeight(userProfileMap.get("last_t1")), PROFILE_CUT_OFF_WEIGHT));

            userModel.setRecentt1RecordList(profileTagWeightFilter(UserProfileUtils.extractRecordListFromStrOnlyWeight(userProfileMap.get("recent_t1")), PROFILE_CUT_OFF_WEIGHT));

            userModel.setT1RecordList(profileTagWeightFilter(UserProfileUtils.extractRecordListFromStrOnlyWeight(userProfileMap.get("t1")), PROFILE_CUT_OFF_WEIGHT));


            userModel.setTopic1Explore(UserProfileUtils.extractCombineTag(userProfileMap.get("topic1_explore")));

            userModel.setDocpic_explore(UserProfileUtils.profileTagNumFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("docpic_explore")), RecallConstant.PROFILE_CUT_OFF_Num));
            userModel.setVideo_explore(UserProfileUtils.profileTagNumFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("video_explore")), RecallConstant.PROFILE_CUT_OFF_Num));
            userModel.setDocpic_sc_explore(UserProfileUtils.profileTagNumFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("docpic_sc_explore")), RecallConstant.PROFILE_CUT_OFF_NUM_SC));
            userModel.setVideo_sc_explore(UserProfileUtils.profileTagNumFilter(UserProfileUtils.extractCombineTag(userProfileMap.get("video_sc_explore")), RecallConstant.PROFILE_CUT_OFF_NUM_SC));





            userModel.setUb(UserProfileUtils.extractUserSub(userProfileMap.get("ub"), userProfileMap.get("group_ub"), userModel.getUserId()));
            userModel.setSearch(UserProfileUtils.extractSearch(userProfileMap.get("search")));
            userModel.setLastIn(userProfileMap.get("last_in"));

            userModel.setAppUserInterest(UserProfileUtils.extractAppUserList(userProfileMap.get("appUserInterest")));
            userModel.setUserClusterList(UserProfileUtils.extractUserCluster(userModel.getUserId(), userProfileMap.get("user_cluster")));

            userModel.setLastCotagSim(UserProfileUtils.extractBySim(userProfileMap.get("last_cotagSim")));
            userModel.setRecentCotagDSim(UserProfileUtils.extractBySim(userProfileMap.get("recent_docpic_cotagSim")));
            userModel.setRecentCotagVSim(UserProfileUtils.extractBySim(userProfileMap.get("recent_video_cotagSim")));

            /**
             * 添加 recent_docpic_extCotag和recent_video_extCotag by yx 20191128
             */
            userModel.setRecentCotagDGraph(UserProfileUtils.extractByGraph(userProfileMap.get("recent_docpic_extCotag")));
            userModel.setRecentCotagVGraph(UserProfileUtils.extractByGraph(userProfileMap.get("recent_video_extCotag")));

            /**
             * 添加 long_docpic_extCotag和long_video_extCotag by yx 20191128
             */
            userModel.setLongCotagDGraph(UserProfileUtils.extractByGraph(userProfileMap.get("docpic_extCotag")));
            userModel.setLongCotagVGraph(UserProfileUtils.extractByGraph(userProfileMap.get("video_extCotag")));


            //以下几个字段 用来给赵宏宏查询ffm透传用 所以不以0.5截取 全部取出 mix内部使用 需注意
            userModel.setRecentVideoSubCate(UserProfileUtils.extractCombineTag(userProfileMap.get("recent_video_subcate")));
            userModel.setLastVideoCate(UserProfileUtils.extractCombineTag(userProfileMap.get("last_video_cate")));
            userModel.setLastVideoSubCate(UserProfileUtils.extractCombineTag(userProfileMap.get("last_video_subcate")));
            userModel.setVideo_subcate(UserProfileUtils.extractCombineTag(userProfileMap.get("video_subcate")));
            userModel.setDocpic_subcate(UserProfileUtils.extractCombineTag(userProfileMap.get("docpic_subcate")));
            userModel.setDocpicLdaTopic(UserProfileUtils.extractCombineTag(userProfileMap.get("docpic_lda_topic")));
            userModel.setRecentDocpicLdaTopic(UserProfileUtils.extractCombineTag(userProfileMap.get("recent_docpic_lda_topic")));
            userModel.setLastLdaDocpicTopicList(UserProfileUtils.extractCombineTag(userProfileMap.get("last_docpic_lda_topic")));
            userModel.setRecent_docpic_subcate(UserProfileUtils.extractCombineTag(userProfileMap.get("recent_docpic_subcate")));
            userModel.setLast_docpic_cate(UserProfileUtils.extractCombineTag(userProfileMap.get("last_docpic_cate")));
            userModel.setLast_docpic_subcate(UserProfileUtils.extractCombineTag(userProfileMap.get("last_docpic_subcate")));
            userModel.setDocpic_media(UserProfileUtils.extractCombineTag(userProfileMap.get("docpic_media")));
            userModel.setVideo_media(UserProfileUtils.extractCombineTag(userProfileMap.get("video_media")));
            userModel.setDocpic_media(UserProfileUtils.extractCombineTag(userProfileMap.get("recent_docpic_media")));
            userModel.setVideo_media(UserProfileUtils.extractCombineTag(userProfileMap.get("recent_video_media")));


            Map<String, String> locationMap = UserProfileUtils.extractUserLocation(userProfileMap.get("locDetail"), userProfileMap.get("general_locDetail"));
            if (locationMap != null) {
                userModel.setCity(locationMap.get("City"));
                userModel.setState(locationMap.get("State"));
                userModel.setSubLocality(locationMap.get("SubLocality"));
                userModel.setStreet(locationMap.get("Street"));
                userModel.setLng(locationMap.get("Lng"));
                userModel.setLat(locationMap.get("Lat"));

            }
        } catch (Exception e) {
            logger.error("trans profile uid:{} ERROR:{}", userModel.getUserId(), e);
        }

        return userModel;
    }
}
