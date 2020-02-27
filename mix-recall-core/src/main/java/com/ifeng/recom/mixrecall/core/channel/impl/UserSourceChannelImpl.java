package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.cache.preload.SourceExploreCache;
import com.ifeng.recom.mixrecall.core.channel.excutor.UserSourceRecallExecutorN;
import com.ifeng.recom.mixrecall.core.threadpool.ExecutorThreadPool;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by lilg1 on 2018/1/18.
 */
@Service
public class UserSourceChannelImpl {

    private final static Logger logger = LoggerFactory.getLogger(UserSourceChannelImpl.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    private final static int timeout = 500;

    private List<RecordInfo> convertSoruceToRecallInfo(List<RecordInfo> sourceForExplores) {
        List<RecordInfo> results = new ArrayList<>();
        if (sourceForExplores == null || sourceForExplores.size() == 0) {
            return results;
        }
        RecordInfo recordInfo = null;
        for (RecordInfo s : sourceForExplores) {
            recordInfo = new RecordInfo(GyConstant.key_Source + s.getRecordName(), s.getWeight());
            results.add(recordInfo);
        }
        return results;
    }

    /**
     * 每个标签召回量写死3条，试验多样性
     * @param mixRequestInfo
     * @return
     */
    public List<RecallResult> doRecallN(MixRequestInfo mixRequestInfo) {

        UserModel userModel = mixRequestInfo.getUserModel();
        TimerEntity timer = TimerEntityUtil.getInstance();
        List<RecallResult> result = new ArrayList<>();
        timer.addStartTime("totalUserSource");
        try {
            LogicParams logicParams = mixRequestInfo.getLogicParams();
            timer.addStartTime("userGetSource");
            //获取用户媒体来源
            List<RecordInfo> sourceForExplores = SourceExploreCache.getFromCache(mixRequestInfo.getUid());
            //变更sourceName格式
            sourceForExplores = convertSoruceToRecallInfo(sourceForExplores);
            timer.addEndTime("userGetSource");

            timer.addStartTime("getSourceDoc");
            UserSourceRecallExecutorN userSourceRecallExecutorN = new UserSourceRecallExecutorN(mixRequestInfo, sourceForExplores);
            result = userSourceRecallExecutorN.call();
            timer.addEndTime("getSourceDoc");
        } catch (Exception e) {
            logger.error("uid:{},UserSourceChannelImpl thread error {}", mixRequestInfo.getUid(), e);
            e.printStackTrace();
        }
        timer.addEndTime("totalUserSource");
        timeLogger.info("ChannelLog UserSourceChannelImpl {} uid:{}", timer.getStaticsInfo(), userModel.getUserId());
        TimerEntityUtil.remove();
        return result;
    }


}
