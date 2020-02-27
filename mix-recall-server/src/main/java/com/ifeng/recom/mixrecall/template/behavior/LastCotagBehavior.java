package com.ifeng.recom.mixrecall.template.behavior;

import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecallThreadResult;
import com.ifeng.recom.mixrecall.common.model.item.Index4User;
import com.ifeng.recom.mixrecall.common.model.item.MixResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.template.BaseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.ifeng.recom.mixrecall.threadpool.ThreadUtils.getAsyncExecutorResult;

/**
 * Created by liligeng on 2019/6/14.
 */
@Service
public class LastCotagBehavior extends BaseTemplate<MixResult> {

    private static final Logger logger = LoggerFactory.getLogger(UserLastBehavior.class);

    @Autowired
    private DocUtils docUtils;

    @Override
    public MixResult doRecom(MixRequestInfo mixRequestInfo) {
        if (mixRequestInfo.isDebugUser()) {
            logger.info("uid");
        }

        List<Future<RecallThreadResult>> recallResultList = new ArrayList<>();
        List<RecallResult> result;

        try {
            recallResultList.add(recallService.lastCotag(mixRequestInfo));
            Map<RecallConstant.CHANNEL, List<RecallResult>> channelDocsMap = getAsyncExecutorResult(mixRequestInfo, recallResultList, 200);
            result = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.LAST_COTAG, new ArrayList<>());
            result = removeDup(mixRequestInfo, result);

            if (mixRequestInfo.isDebugUser()) {
                for (RecallResult recallResult : result) {
                    logger.info("lastResultList uid:{} docId:{} simId:{} docType:{} available:{} disType:{} times:{} hotboost:{} title:{} channel:{} debug:{}", mixRequestInfo.getUid(), recallResult.getDocument().getDocId(), recallResult.getDocument().getSimId(), recallResult.getDocument().getDocType(), recallResult.getDocument().isAvailable(), recallResult.getDocument().getDistype(), recallResult.getDocument().getTimeSensitive().replace("\r\n", ""), recallResult.getDocument().getHotBoost(), recallResult.getDocument().getTitle(), recallResult.getWhyReason().getValue(), recallResult.getRecallTag());
                }
            }

            List<Index4User> index4UserList = docUtils.getIndex4UserList(mixRequestInfo, result, true);
            MixResult mixResult = new MixResult();
            mixResult.setIndex4UserList(index4UserList);
            mixResult.setAbtestMap(mixRequestInfo.getAbTestMap());
//            json = GsonUtil.object2json(mixResult);
            return mixResult;

        } catch (Exception e) {
            logger.error("get Err:{}", e);
        }

        return null;
    }


    public String doRealTimeRecom(MixRequestInfo mixRequestInfo) {
        String json = "";

        List<Future<RecallThreadResult>> recallResultList = new ArrayList<>();
        List<RecallResult> result;

        try {
            recallResultList.add(realtimeService.lastCotag(mixRequestInfo));
            Map<RecallConstant.CHANNEL, List<RecallResult>> channelDocsMap = getAsyncExecutorResult(mixRequestInfo, recallResultList, 200);
            result = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.LAST_COTAG, new ArrayList<>());
            result = removeDup(mixRequestInfo,result);

            if (mixRequestInfo.isDebugUser() && result!=null) {
                logger.info("uid:{} realTime lastCoTag result:{}", mixRequestInfo.getUid(), result.size());
            }

            List<Index4User> index4UserList = docUtils.getIndex4UserList(mixRequestInfo, result, true);
            MixResult mixResult = new MixResult();
            mixResult.setIndex4UserList(index4UserList);
            mixResult.setAbtestMap(mixRequestInfo.getAbTestMap());
            json = GsonUtil.object2json(mixResult);

        } catch (Exception e) {
            logger.error("get Err:{}", e);
        }

        return json;
    }
}
