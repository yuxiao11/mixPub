package com.ifeng.recom.mixrecall.core.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.JsonUtil;
import com.ifeng.recom.mixrecall.core.channel.impl.SingleTagChannelImpl;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import com.ifeng.userpf.client.UserProfileManager;
import com.ifeng.userpf.entity.ClientType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.ifeng.recom.mixrecall.common.service.UserProfileService.getLongTermUserProfileModel;
import static com.ifeng.recom.mixrecall.common.service.UserProfileService.getRealTimeUserModel;

/**
 * 缓存用户画像 <uid,UserModel>
 **/

public class UserProfileCache {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileCache.class);
    public static LoadingCache<String, UserModel> cache;
    private static LoadingCache<String, UserModel> realTimeCache;



    static {
        initCache();
    }


    /**
     * 处理画像超时问题，如果超时，3分钟内不再查询
     */
    private static LoadingCache<String, Long> UserSyncHistory = CacheBuilder.newBuilder()
            .maximumSize(200000)
            .initialCapacity(200000)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Long>() {
                        public Long load(String key) throws Exception {
                            return System.currentTimeMillis();
                        }
                    });




    private static void initCache() {

        cache = CacheBuilder
                .newBuilder()
                .recordStats()
                .concurrencyLevel(15)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .initialCapacity(100000)
                .maximumSize(100000)
                .build(new CacheLoader<String, UserModel>() {
                    @Override
                    public UserModel load(String uid) throws Exception {
                        return getUserModelLong(uid, "longCache");
                    }
                });

        realTimeCache = CacheBuilder
                .newBuilder()
                .recordStats()
                .concurrencyLevel(15)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .initialCapacity(10000)
                .maximumSize(100000)
                .build(new CacheLoader<String, UserModel>() {
                    @Override
                    public UserModel load(String uid) throws Exception {
                        UserModel userModel = null;
                        long start = System.currentTimeMillis();
                        try {
                            Future<UserModel> future = ExecutorThreadPool.threadPool.submit(new Callable<UserModel>() {
                                public UserModel call() throws Exception {
                                    UserModel userModel = getRealTimeUserModel(uid);
                                    return userModel;
                                }
                            });

                            try {
                                userModel = future.get(GyConstant.timeout_UserModel_last, TimeUnit.MILLISECONDS);
                            } catch (Exception e) {
                                logger.error("{} getLongTermUserProfileModel ERROR :{}", uid, e);
                                future.cancel(true);
                            }
                        } catch (Exception e) {
                            logger.error("{} getLongTermUserProfileModel ERROR :{}", uid, e);
                        }

                        long cost = System.currentTimeMillis() - start;
                        if (cost > 50) {
                            ServiceLogUtil.debug("UserProfileRealTime {} cost:{}", uid, cost);
                        }
                        if (userModel != null) {
                            return userModel;
                        } else {
                            return new UserModel(uid);
                        }
                    }
                });
    }


    /**
     * 建议直接查询cache
     * @param uid
     * @param logType
     * @return
     */
    @Deprecated
    private static UserModel getUserModelLong(String uid, String logType) {
        long start = System.currentTimeMillis();
        UserModel userModel = null;
        try {
            Future<UserModel> future = ExecutorThreadPool.threadPool.submit(new Callable<UserModel>() {
                public UserModel call() throws Exception {
                    UserModel userModel = getLongTermUserProfileModel(uid);
                    return userModel;
                }
            });
            try {
                userModel = future.get(GyConstant.timeout_UserModel_long, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logger.error("{} {} getLongTermUserProfileModel ERROR :{}", uid, logType, e);
                future.cancel(true);
            }
        } catch (Exception e) {
            logger.error("{} {} getLongTermUserProfileModel ERROR :{}", uid, logType, e);
        }
        long cost = System.currentTimeMillis() - start;
        if (cost > 50) {
            ServiceLogUtil.debug("UserProfile {} {} cost:{}", uid, logType, cost);
        }
        if (userModel != null) {
            return userModel;
        } else {
            logger.warn("{} get UserModel Null", uid);
            return new UserModel(uid);
        }
    }





    public static UserModel getUserModel(String uid) {
        try {

            long nowtime = System.currentTimeMillis();
            long cacheHistoryTime = UserSyncHistory.get(uid);

            UserModel longTermUserModel = cache.getIfPresent(uid);

            if (userModelLongNeedRefresh(longTermUserModel)) {
                //如果当期时间大于cache时间（cache时间未过期），则认为是时间窗口以内

                boolean notTimeLimit = (nowtime <= cacheHistoryTime);
                if (notTimeLimit) {
                    longTermUserModel = getUserModelLong(uid, "LongRefresh");
                    cache.put(uid, longTermUserModel);
                }

                if (longTermUserModel == null) {
                    longTermUserModel = new UserModel(uid);
                    logger.error("{} get longTermUserModel Null", uid);
                }

                if (userModelLongNeedRefresh(longTermUserModel)) {
                    logger.warn("{} UserModelResultNull:{},cotag:{}", uid, notTimeLimit, JsonUtil.object2jsonWithoutException(longTermUserModel.getCombineTagList()));
                }
            }


            UserModel realTimeUserModel = null;
            try {
                realTimeUserModel = realTimeCache.get(uid);
            } catch (ExecutionException e) {
                logger.error("query realTime profile error");
            }

            if (realTimeUserModel!=null && !(realTimeUserModel.getLastCombineTagList() == null || realTimeUserModel.getLastCombineTagList().isEmpty())) {
                if(uid.equals("865969031431182")){
                    logger.info("{} getRealTimeUserModel :{}",uid, GsonUtil.object2json(realTimeUserModel));
                }
                longTermUserModel.setLastCombineTagList(realTimeUserModel.getLastCombineTagList());
                longTermUserModel.setLastLdaDocpicTopicList(dealErrorLastDopic(realTimeUserModel.getLastLdaDocpicTopicList()));
                longTermUserModel.setTopic1Explore(realTimeUserModel.getTopic1Explore());
                longTermUserModel.setLastt1RecordList(realTimeUserModel.getLastt1RecordList());
                longTermUserModel.setSearch(realTimeUserModel.getSearch());
                longTermUserModel.setLastLdaTopicList(realTimeUserModel.getLastLdaTopicList());
                longTermUserModel.setLastVideoCotag(realTimeUserModel.getLastVideoCotag());
                longTermUserModel.setLastVideoCate(realTimeUserModel.getLastVideoCate());
                longTermUserModel.setLastVideoSubCate(realTimeUserModel.getLastVideoSubCate());
                longTermUserModel.setLast_docpic_cate(realTimeUserModel.getLast_docpic_cate());
                longTermUserModel.setLast_docpic_subcate(realTimeUserModel.getLast_docpic_subcate());
            }

            if (CollectionUtils.isEmpty(longTermUserModel.getTopic1Explore())) {
                logger.warn("{} addDefaultExp", uid);
                longTermUserModel.setTopic1Explore(SingleTagChannelImpl.defaultExploreList);
            }

            return longTermUserModel;

        } catch (Exception e) {
            logger.error("{} user profile cache {}", uid, e);
        }
        UserModel userModel = new UserModel();
        userModel.setUserId(uid);
        return userModel;
    }

    private static List<RecordInfo> dealErrorLastDopic(List<RecordInfo> recordInfoList){
        try{
            if(CollectionUtils.isEmpty(recordInfoList)){
                return recordInfoList;
            }
            recordInfoList.removeIf(x->!x.getRecordName().contains("topic"));
        }catch (Exception e){
            logger.error("dealErrorLastDopic error:{},{}",e,JsonUtil.object2jsonWithoutException(recordInfoList));
        }
        return recordInfoList;
    }
    /**
     * 检测长期画像是否需要更新
     *
     * @param userModel
     * @return
     */
    private static boolean userModelLongNeedRefresh(UserModel userModel) {
        boolean check = false;
        if (userModel == null || userModel.getCombineTagList() == null || userModel.getCombineTagList().isEmpty()) {
            check = true;
        }
        return check;
    }


    public static void checkStatus() {
        logger.debug("long hit_count:{} hit_rate:{} load_count:{} cache_size:{}", cache.stats().hitCount(), cache.stats().hitRate(), cache.stats().loadCount(), cache.size());
        logger.debug("real hit_count:{} hit_rate:{} load_count:{} cache_size:{}", realTimeCache.stats().hitCount(), realTimeCache.stats().hitRate(), realTimeCache.stats().loadCount(), realTimeCache.size());
    }

    public static void main(String[] args) {
        UserProfileManager.initOnline(600, ClientType.HOT, "UserCenter_Official");
//        UserModel userModel = getUserModel("27d7344be6c44fc880db8ec6de6fc7a5");
        UserModel userModel = new UserModel();
        userModel = getUserModel("99001008030221");
        userModel = getUserModel("99001008030221");
        userModel = getUserModel("99001008030221");
        System.out.println(JsonUtil.object2jsonWithoutException(userModel));

        System.out.println(userModel.toString());
    }
}