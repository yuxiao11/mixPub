package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.RecomChannelEnum;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.channel.excutor.UserSubRecallExecutor;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilg1 on 2018/1/18.
 */
@Service
public class UserSubChannelImpl {

    private final static Logger logger = LoggerFactory.getLogger(UserSubChannelImpl.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    private final static int timeout = 400;

    /**
     * 用户订阅通道
     * @param mixRequestInfo
     * @return
     */
    public List<Document> doRecall(MixRequestInfo mixRequestInfo) {

        UserModel userModel = mixRequestInfo.getUserModel();
        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("total");

        LogicParams logicParams = mixRequestInfo.getLogicParams();

        List<Document> result = new ArrayList<>();
        try {
            UserSubRecallExecutor userSubRecallExecutor = new UserSubRecallExecutor(mixRequestInfo, logicParams);
            result = userSubRecallExecutor.call();
        } catch (Exception e) {
            logger.error("User Sub thread error {}", e);
            e.printStackTrace();
        }
        timer.addEndTime("total");
        timeLogger.info("ChannelLog UserSubChannel {} uid:{}", timer.getStaticsInfo(), userModel.getUserId());
        TimerEntityUtil.remove();
        return result;
    }


    private int getNumToAdd(MixRequestInfo mixRequestInfo) {
        int numToAdd = GyConstant.num_Sub;

        if (RecomChannelEnum.momentsnew.getValue().equals(mixRequestInfo.getRecomChannel())) {
            numToAdd = GyConstant.num_Sub_Momentsnew;
        }
        return numToAdd;
    }
}
