package com.ifeng.recom.mixrecall.core.channel.excutor.cotag;

import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.Why;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;
import com.ifeng.userpf.client.UserProfileManager;
import com.ifeng.userpf.entity.ClientType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ifeng.recom.mixrecall.common.constant.RecallConstant.PROFILE_CUT_OFF_WEIGHT;
import static com.ifeng.recom.mixrecall.common.util.UserProfileUtils.profileTagWeightFilter;


/**
 * Created by geyl on 2017/11/13.
 */
public class CoTagDRecallTest {
    private static final Logger logger = LoggerFactory.getLogger(CoTagDRecallTest.class);


    public static void main(String[] args) throws Exception {
        CacheManager.init();
//        String uid = "62a8cb093b634d659b90253de42b6ce9";
        String uid = "866654030878588";

        UserProfileManager.initOnline(0, ClientType.BATCH, "UserCenter_Official");
        UserModel u = UserProfileCache.getUserModel(uid);

        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        mixRequestInfo.setUid(uid);


//        CoTagDRecall coTagRecallExecutor = new CoTagDRecall(mixRequestInfo, profileTagWeightFilter(u.getDocpic_cotag(), PROFILE_CUT_OFF_WEIGHT), TagPeriod.RECENT, 800);
        CoTagDRecall coTagRecallExecutor = new CoTagDRecall(mixRequestInfo, profileTagWeightFilter(u.getLastCombineTagList(), PROFILE_CUT_OFF_WEIGHT), TagPeriod.RECENT, 800);

        List<RecallResult> recallResults = coTagRecallExecutor.call();

        List<Document> docs = new ArrayList<>();
        for (RecallResult result : recallResults) {
            System.out.println(result.getDocument().getTitle() + " " + result.getRecallTag() + " " + result.getWhyReason().getValue());
//            System.out.println(result.getDocument().getDocId());

            Why why = new Why();
            why.setReason(result.getWhyReason());
            result.getDocument().setWhy(why);

            result.getDocument().setRecallTag(result.getRecallTag());

            docs.add(result.getDocument());
        }


        Map<String, Integer> recallTagMap = new HashMap<>();
        Map<String, Integer> trueMap = new HashMap<>();
        for (Document document : docs) {
            System.out.println(document.getDocId() + "\t" + document.getTitle() + "\t" + document.getRecallTag() + "\t");

            String recallTag = document.getRecallTag();

            if (recallTagMap.containsKey(recallTag)) {
                int num = recallTagMap.get(recallTag);
                recallTagMap.put(recallTag, ++num);
            } else {
                recallTagMap.put(recallTag, 1);
            }

            String why = document.getWhy().getReason().getValue();

            if (!why.contains("supply")) {
                if (trueMap.containsKey(recallTag)) {
                    int num = trueMap.get(recallTag);
                    trueMap.put(recallTag, ++num);
                } else {
                    trueMap.put(recallTag, 1);
                }
            }
        }

    }

}
