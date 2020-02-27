package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;

import java.util.List;

public class UserSearchRecallExecutorTest {

    public static void main(String[] args) throws Exception {
        String uid = "867305035296545";

        UserModel userModel = UserProfileCache.getUserModel(uid);
        UserSearchRecallExecutor userSearchRecallExecutor = new UserSearchRecallExecutor(userModel);

        List<Document> docs = userSearchRecallExecutor.call();

        for (Document document : docs) {
            System.out.println(document.getTitle());
        }
    }
}
