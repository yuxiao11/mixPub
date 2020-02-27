package com.ifeng.recom.mixrecall.template.behavior;

import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecallThreadResult;
import com.ifeng.recom.mixrecall.common.model.item.MixResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.template.BaseTemplate;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.ifeng.recom.mixrecall.threadpool.ThreadUtils.getAsyncExecutorResult;

/**
 * 处理头条首页的实时召回，数据要尽可能简化
 * Created by jibin on 2017/12/25.
 */
@Service
public class ToutiaoFirstBehavior extends BaseTemplate<MixResult> {
    private final static Logger logger = LoggerFactory.getLogger(ToutiaoFirstBehavior.class);

    @Override
    public MixResult doRecom(MixRequestInfo mixRequestInfo) {
        TimerEntity timer = TimerEntityUtil.getInstance();

        String uid = mixRequestInfo.getUid();

        List<Future<RecallThreadResult>> recallResultList = new ArrayList<>();
        timer.addStartTime("recall");
        recallResultList.add(realtimeService.toutiaoFirst(mixRequestInfo));

        Map<RecallConstant.CHANNEL, List<RecallResult>> channelDocsMap = getAsyncExecutorResult(mixRequestInfo, recallResultList, GyConstant.timeout_Toutiao_Online);
        List<RecallResult> cotagList = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.COTAG, Collections.emptyList());
        timer.addEndTime("recall");

        logger.info("toutiao first before filter: uid:{} num:{}", uid, cotagList.size());
        cotagList = doCommonFilter(mixRequestInfo, cotagList, RecallConstant.CHANNEL.COTAG);
        logger.info("toutiao first after filter: uid:{} num:{}", uid, cotagList.size());

        List<RecallResult> result = outputChannelNumControl(mixRequestInfo, cotagList);


        return buildMixResult(mixRequestInfo, result, GyConstant.isNewResult);
    }


    private static List<RecallResult> outputChannelNumControl(MixRequestInfo mixRequestInfo, List<RecallResult> cotag) {
        List<RecallResult> results = new ArrayList<>();
        results.addAll(cotag.subList(0, Math.min(300, cotag.size())));
        return results;
    }
}
