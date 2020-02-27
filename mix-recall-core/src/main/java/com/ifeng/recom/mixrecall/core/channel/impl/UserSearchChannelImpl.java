package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.channel.excutor.UserSearchRecallExecutor;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by lilg1 on 2018/1/18.
 */
@Service
public class UserSearchChannelImpl {
    private final static Logger logger = LoggerFactory.getLogger(UserSearchChannelImpl.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    private final static int timeout = 400;

    public List<Document> doRecall(MixRequestInfo mixRequestInfo) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("total");

        UserModel userModel = mixRequestInfo.getUserModel();

        List<Document> result = new ArrayList<>();
        try {

            UserSearchRecallExecutor userSearchRecallExecutor = new UserSearchRecallExecutor(userModel);
            Future<List<Document>> searchFuture = ExecutorThreadPool.submitTaskForRecallDocument(userSearchRecallExecutor);
            result = searchFuture.get(600,TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            result=new ArrayList<>();
            logger.error("{} User Search thread error ",mixRequestInfo.getUid(), e);
        }
        StringBuilder stringBuilder = new StringBuilder();

        for (Document d:result){
            stringBuilder.append(d.getDocId()+":"+d.getTitle()+"--"+d.getRecallTag()+";");
        }
        if(stringBuilder.length()>0){
            logger.info("uid:{} userSearchDetail:{}",userModel.getUserId(),stringBuilder.toString());
        }
        timer.addEndTime("total");
        timeLogger.info("ChannelLog UserSearch {} uid:{}", timer.getStaticsInfo(), userModel.getUserId());

        return result;
    }


}


