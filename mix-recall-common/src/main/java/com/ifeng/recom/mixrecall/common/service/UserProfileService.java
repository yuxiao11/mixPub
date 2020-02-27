package com.ifeng.recom.mixrecall.common.service;

import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.common.util.JsonUtil;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import com.ifeng.userpf.client.UserProfileManager;
import com.ifeng.userpf.entity.ClientType;
import com.ifeng.userpf.upserver.UserProfileOnlineClient;
import org.apache.hadoop.hbase.shaded.com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ifeng.recom.mixrecall.common.service.UserProfile.transformMapToUserModel;

/**
 * Created by geyl on 2018/4/28.
 */
@Service
public class UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    private static final String[] LONGTERM_COLUMN = {"t1", "recent_t1", "ucombineTag", "recent_ucombineTag", "topic1_explore", "loc", "search", "general_search", "last_in", "general_ub", "group_ub", "ub", "locDetail", "general_locDetail", "ua_v", "first_in", "long_uTopic", "last_uTopic", "video_cotag", "recent_video_cotag", "docpic_cotag", "docpic_lda_topic", "recent_docpic_lda_topic"
            , "svideo_cotag", "video_cate", "docpic_cate", "user_cluster","recent_docpic_cotag","umos","e","s","ui","uk","user_cluster","docpic_subcate_period","video_subcate_period","general_likeVidR_weekends","general_likeVidR_dayInWork","general_likeVidR_nightInWork","general_doc_timeSensitive","general_vid_timeSensitive","user_group",
            "docpic_explore", "video_explore", "video_subcate", "docpic_subcate", "docpic_sc_explore", "video_sc_explore", "generalloc","svideo_cate","user_schannel","docpic_media","video_media","recent_docpic_media","recent_video_media","appUserInterest","last_cotagSim","recent_docpic_cotagSim","recent_video_cotagSim","recent_docpic_extCotag","recent_video_extCotag","docpic_extCotag","video_extCotag","daily_pullnum","last_lda_topic","last_docpic_lda_topic","last_video_cotag","fullness","recent_video_subcate","recent_docpic_subcate"};//Add daily_pullnum by YX 20190524

    private static final String[] REALTIME_COLUMN = {"last_t1", "last_ucombineTag", "topic1_explore", "last_dis_ucombine_tag","search","appUserInterest","last_lda_topic","last_docpic_lda_topic","last_video_cotag","last_video_subcate","last_video_cate","last_docpic_cate","last_docpic_subcate"};

    private static final Set<String> LONGTERM_KEYS = new HashSet<>(Arrays.asList(LONGTERM_COLUMN));
    private static final Set<String> REALTIME_KEYS = new HashSet<>(Arrays.asList(REALTIME_COLUMN));

    static {
        UserProfileManager.initOnline(600, ClientType.HOT, "UserCenter_Official");
    }

    public static UserModel getRealTimeUserModel(String uid) {
        if (uid == null || uid.isEmpty()) {
            logger.error("get user profile error, uid is null or empty");
            return null;
        }

        long startUserProfileRealTime = System.currentTimeMillis();
        try {
            Map<String, String> userProfileMap = UserProfileOnlineClient.searchPartRealtimeProfileByUid(uid, REALTIME_KEYS);

            if (userProfileMap != null && !userProfileMap.isEmpty()) {
                UserModel userModel = new UserModel();
                userModel.setUserId(uid);
                userModel = transformMapToUserModel(userProfileMap, userModel);

                long cost = System.currentTimeMillis() - startUserProfileRealTime;
                if (cost > 50) {
                    ServiceLogUtil.debug("profilerealtime {} cost:{}", uid, cost);
                }

                return userModel;
            }
        } catch (Exception e) {
            logger.error("get profile uid:" + uid + " error:" + e);
        }

        return null;
    }

    public static UserModel getLongTermUserProfileModel(String uid) {
        if (Strings.isNullOrEmpty(uid)) {
            logger.error("get user profile error, uid is null or empty");
            return null;
        }

        long startUserProfileRealTime = System.currentTimeMillis();
        try {
            Map<String, String> userProfileMap = UserProfileOnlineClient.searchPartOfflineProfileByUid(uid, LONGTERM_KEYS);

            if (userProfileMap != null && !userProfileMap.isEmpty()) {
                UserModel userModel = new UserModel();
                userModel.setUserId(uid);
                userModel = transformMapToUserModel(userProfileMap, userModel);

                long cost = System.currentTimeMillis() - startUserProfileRealTime;
                if (cost > 50) {
                    ServiceLogUtil.debug("profilelongterm {} cost:{}", uid, cost);
                }


                return userModel;
            }
        } catch (Exception e) {
            logger.error("get profile uid:" + uid + " error:" + e);
        }

        return null;
    }


    public static void main(String[] args) {
        String uid = "865969031431182"; //用户uid

//        UserModel userModel1 = getLongTermUserProfileModel(uid);
//        userModel1 = getLongTermUserProfileModel(uid);
//        userModel1 = getLongTermUserProfileModel(uid);
//        userModel1 = getLongTermUserProfileModel(uid);
//
//        System.out.println(JsonUtil.object2json(userModel1));

        UserModel usermodel2 = getRealTimeUserModel(uid);
        System.out.println(JsonUtil.object2jsonWithoutException(usermodel2));


        UserProfileManager.close(); //进程关闭时释放,必须执行
    }
}
