package com.ifeng.recom.mixrecall.core.channel.excutor.usercf;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.UserProfile;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;
import com.ifeng.recom.mixrecall.core.channel.impl.UserCFDssmChannelImpl;

import java.util.List;

public class UserCFRecallExecutorDSSMTest {
    public static void main(String[] args) {
        CacheManager.init();
        String uid = "99001008030221";

        UserModel userModel = UserProfileCache.getUserModel(uid);

        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        mixRequestInfo.setUid(uid);
        mixRequestInfo.setUserModel(userModel);
        UserCFDssmChannelImpl userCFDssmChannel = new UserCFDssmChannelImpl();
        List<RecallResult> recallResults = userCFDssmChannel.doRecall(mixRequestInfo);

        for (RecallResult result : recallResults) {
            Document document = result.getDocument();
            System.out.print(result.getWhyReason()+"-->");
            System.out.println(document.getTopic1() + "\t" + result.getUserCFScore() + "\t" + document.getTitle()+"\t"+ document.getWhy());

        }
    }
}
