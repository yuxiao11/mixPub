package com.ifeng.recom.mixrecall.core.channel.excutor.cotag;

import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.Why;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoTagVideoRecallForNewTagTest {
    public static void main(String[] args) throws Exception {
        CacheManager.init();
        String uid = "829584937ae04994b028008a3bdbab75";
        UserModel userModel = UserProfileCache.getUserModel(uid);

        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        mixRequestInfo.setUid(uid);
        CoTagVideoRecallForNewTag coTagRecallExecutor = new CoTagVideoRecallForNewTag(mixRequestInfo, userModel.getCombineTagList(), UserProfileEnum.TagPeriod.LONG, 200);
        List<RecallResult> recallResults = coTagRecallExecutor.call();

        System.out.println("total size:" + recallResults.size());

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
            System.out.println(document.getDocId() + "\t" + document.getTitle() + "\t" + document.getRecallTag() + "\t" + document.getDocType());

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


        for (Map.Entry<String, Integer> entry : recallTagMap.entrySet()) {
            int trueNum = trueMap.getOrDefault(entry.getKey(), 0);
            System.out.println(entry.getKey() + "\t" + entry.getValue() + "\t" + trueNum);
        }

    }

}
