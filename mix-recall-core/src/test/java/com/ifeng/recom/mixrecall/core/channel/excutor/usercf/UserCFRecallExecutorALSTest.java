package com.ifeng.recom.mixrecall.core.channel.excutor.usercf;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserCF;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.UserProfile;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;

import java.util.List;

import static com.ifeng.recom.mixrecall.common.dao.hbase.UserCFClick.getNeighborClick;

public class UserCFRecallExecutorALSTest {
    public static void main(String[] args) {
        String uid = "99001008030221";

        UserModel userModel = UserProfileCache.getUserModel(uid);
        UserCF userCF = getNeighborClick(uid);

        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        mixRequestInfo.setUid(uid);
        mixRequestInfo.setUserModel(userModel);

        UserCFRecallExecutorALS userCFRecallExecutor = new UserCFRecallExecutorALS(mixRequestInfo, userCF);
        List<RecallResult> recallResults = userCFRecallExecutor.doRecall();

        for (RecallResult result : recallResults) {
            Document document = result.getDocument();
            System.out.println(document.getTopic1() + "\t" + result.getUserCFScore() + "\t" + document.getTitle());
        }
    }
}
