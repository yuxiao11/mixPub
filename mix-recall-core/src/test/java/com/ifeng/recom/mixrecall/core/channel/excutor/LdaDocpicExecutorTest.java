package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;

import java.util.List;

public class LdaDocpicExecutorTest {

    public static void main(String[] args) {
        CacheManager.init();
        String uid = "bf878319fa064f5085c931cd2144da78";
        UserModel userModel = UserProfileCache.getUserModel(uid);

        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        mixRequestInfo.setUid(uid);
        LdaDocpicExecutor ldaDocpicExecutor = new LdaDocpicExecutor(mixRequestInfo, userModel.getDocpicLdaTopic(), UserProfileEnum.TagPeriod.LONG, 200);
        List<RecallResult> recallResults = null;
        try {
            recallResults = ldaDocpicExecutor.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (RecallResult result : recallResults) {
            System.out.println(result.getDocument().getTitle() + " " + result.getRecallTag() + " " + result.getWhyReason().getValue());
        }

        System.out.println("total size:" + recallResults.size());

    }
}
