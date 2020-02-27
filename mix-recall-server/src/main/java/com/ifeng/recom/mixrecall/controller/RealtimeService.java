package com.ifeng.recom.mixrecall.controller;

import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecallThreadResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.channel.impl.CoTagChannelFirstImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.LastCotagChannelImpl;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by liligeng on 2019/8/13.
 */
@Service
public class RealtimeService {

    private final static Logger logger = LoggerFactory.getLogger(RealtimeService.class);

    @Autowired
    private LastCotagChannelImpl lastCotagChannel;

    @Autowired
    private CoTagChannelFirstImpl coTagChannelFirst;

    @Async("realTime")
    public Future<RecallThreadResult> lastCotag(MixRequestInfo mixRequestInfo){
        List<RecallResult> rt = lastCotagChannel.doRecall(mixRequestInfo);
        RecallThreadResult recallThreadResult = new RecallThreadResult(RecallConstant.CHANNEL.LAST_COTAG, rt);
        return new AsyncResult<>(recallThreadResult);
    }

    @Async("realTime")
    public Future<RecallThreadResult> toutiaoFirst(MixRequestInfo mixRequestInfo){
        List<RecallResult> rt = coTagChannelFirst.doToutiaoFirstRecall(mixRequestInfo);
        RecallThreadResult recallThreadResult = new RecallThreadResult(RecallConstant.CHANNEL.COTAG, rt);
        return new AsyncResult<>(recallThreadResult);
    }

}
