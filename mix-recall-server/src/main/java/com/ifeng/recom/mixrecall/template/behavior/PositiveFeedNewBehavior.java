package com.ifeng.recom.mixrecall.template.behavior;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.config.BossUserConfig;
import com.ifeng.recom.mixrecall.common.config.constant.ApolloConstant;
import com.ifeng.recom.mixrecall.common.constant.FlowTypeAsync;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecallThreadResult;
import com.ifeng.recom.mixrecall.common.model.item.Index4User;
import com.ifeng.recom.mixrecall.common.model.item.LastDocBean;
import com.ifeng.recom.mixrecall.common.model.item.MixResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.template.BaseTemplate;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Future;

import static com.ifeng.recom.mixrecall.threadpool.ThreadUtils.getAsyncExecutorResult;

/**
 * 处理正反馈，数据要尽可能简化
 * Created by jibin on 2017/12/25.
 */
@Service
public class PositiveFeedNewBehavior extends BaseTemplate<Map<String, List<Index4User>>> {
    private static final Logger logger = LoggerFactory.getLogger(PositiveFeedNewBehavior.class);
    /**
     * 新闻正反馈和视频正反馈，单独作为引擎数据源
     * 引擎本地cache并进行本地布隆，不用过远程布隆
     *
     * @param mixRequestInfo
     * @return
     */
    @Override
    public Map<String, List<Index4User>> doRecom(MixRequestInfo mixRequestInfo) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        String flowType = mixRequestInfo.getFlowType();
        Map<String, List<Index4User>> result = null;

        try {
            if (FlowTypeAsync.positiveFeedNew.equals(flowType)) {

                timer.addStartTime("getDocpicPositiveFeed");
                result = getDocpicPositiveFeed(mixRequestInfo);
                timer.addEndTime("getDocpicPositiveFeed");
            } else if (FlowTypeAsync.positiveFeedVideoNew.equals(flowType)) {
                timer.addStartTime("getVideoPositiveFeed");
                result = getVideoPositiveFeed(mixRequestInfo);
                timer.addEndTime("getVideoPositiveFeed");
            }
        } catch (Exception e) {
            logger.error("{} PositiveFeedNewBehavior {} ERROR:{}", mixRequestInfo.getUid(), flowType, e);
        }
        return result;
    }

    /**
     * 只查询新闻正反馈
     *
     * @return
     */
    private Map<String, List<Index4User>> getDocpicPositiveFeed(MixRequestInfo mixRequestInfo) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        Map<String, List<Index4User>> result = Maps.newHashMap();
        List<Future<RecallThreadResult>> recallResultList = new ArrayList<>();
        recallResultList.add(recallService.positiveFeedDocpic(mixRequestInfo));
        Map<RecallConstant.CHANNEL, List<RecallResult>> channelDocsMap = getAsyncExecutorResult(mixRequestInfo, recallResultList, 500);
        List<RecallResult> posDocpics = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.PositiveFeedDocpic, Collections.emptyList());
        Map<String, List<RecallResult>> posResults = getPositiveFeedMapFromRecallResults(posDocpics);
        try {
             //正反馈补充来源的文章 boss用户不走补充来源  2018-11-29
            if(BossUserConfig.getBossUser(ApolloConstant.boss_users_key).contains(mixRequestInfo.getUid())){
                logger.info("{} bossUser come here,there is no supplySource!",mixRequestInfo.getUid());
            }else {

             //正反馈补充来源的文章
                timer.addStartTime("supplyRemainBeanlistDocpic");
                supplyRemainBeanlistDocpic(mixRequestInfo, posResults);
                timer.addEndTime("supplyRemainBeanlistDocpic");
            }
            timer.addStartTime("dealPosResults");
             for (Map.Entry<String, List<RecallResult>> entry : posResults.entrySet()) {
                 String originSimId = entry.getKey();
                 List<RecallResult> recomList = entry.getValue();
                 try {
                     recomList = doPosFeedFilter(mixRequestInfo, recomList, RecallConstant.CHANNEL.PositiveFeedDocpic);
                 } catch (Exception e) {
                     logger.error("filter error, {}, {}", mixRequestInfo.getUid(), e);
                     continue;
                 }
                 dealPositiveFeedStrategy(mixRequestInfo, recomList);
                 if (mixRequestInfo.isDebugUser()) {
                     for (RecallResult recallResult : recomList) {
                         logger.info("docId:{} simId:{} available:{} disType:{} times:{} hotboost:{} title:{} channel:{} debug:{}  source:{}", recallResult.getDocument().getDocId(), recallResult.getDocument().getSimId(), recallResult.getDocument().isAvailable(), recallResult.getDocument().getDistype(), recallResult.getDocument().getTimeSensitive().replace("\r\n", ""), recallResult.getDocument().getHotBoost(), recallResult.getDocument().getTitle(), recallResult.getWhyReason().getValue(), recallResult.getRecallTag(),recallResult.getDocument().getSource());
                     }
                 }
                 List<Index4User> index4UserList = docUtils.getIndex4UserList(mixRequestInfo, recomList, GyConstant.isOldResult);
                 result.put(originSimId, index4UserList);
             }
            timer.addEndTime("dealPosResults");
        }catch (Exception e){
             logger.error("uid:{},positive feedback supply source docpic error",mixRequestInfo.getUid(), e);
        }
        return result;
//        return GsonUtil.object2json(result);
    }


    /**
     * 只查询视频正反馈
     *
     * @return
     */
    private Map<String, List<Index4User>> getVideoPositiveFeed(MixRequestInfo mixRequestInfo) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        Map<String, List<Index4User>> result = Maps.newHashMap();

        List<Future<RecallThreadResult>> recallResultList = new ArrayList<>();
        recallResultList.add(recallService.positiveFeedVideo(mixRequestInfo));

        Map<RecallConstant.CHANNEL, List<RecallResult>> channelDocsMap = getAsyncExecutorResult(mixRequestInfo, recallResultList, 500);
        List<RecallResult> posResultList = channelDocsMap.getOrDefault(RecallConstant.CHANNEL.PositiveFeedVideo, Collections.emptyList());
        Map<String, List<RecallResult>> posResults = getPositiveFeedMapFromRecallResults(posResultList);
        try {
            //正反馈补充来源的视频 boss用户不走补充来源  2018-11-29
            if(BossUserConfig.getBossUser(ApolloConstant.boss_users_key).contains(mixRequestInfo.getUid())){
                logger.info("{} bossUser come here,there is no supplySource!",mixRequestInfo.getUid());
            }else {
            //正反馈补充来源的视频
                timer.addStartTime("supplyRemainBeanlistVideo");
                supplyRemainBeanlistVideo(mixRequestInfo,posResults);
                timer.addEndTime("supplyRemainBeanlistVideo");
            }
            timer.addStartTime("dealPosResults");
            for (Map.Entry<String, List<RecallResult>> entry : posResults.entrySet()) {
                String originSimId = entry.getKey();
                List<RecallResult> recomList = entry.getValue();
                recomList = doPosFeedFilter(mixRequestInfo, recomList, RecallConstant.CHANNEL.PositiveFeedVideo);
                dealPositiveFeedStrategy(mixRequestInfo, recomList);

                if (mixRequestInfo.isDebugUser()) {
                    for (RecallResult recallResult : recomList) {
                        logger.info("docId:{} simId:{} available:{} disType:{} times:{} hotboost:{} title:{} channel:{} debug:{}", recallResult.getDocument().getDocId(), recallResult.getDocument().getSimId(), recallResult.getDocument().isAvailable(), recallResult.getDocument().getDistype(), recallResult.getDocument().getTimeSensitive().replace("\r\n", ""), recallResult.getDocument().getHotBoost(), recallResult.getDocument().getTitle(), recallResult.getWhyReason().getValue(), recallResult.getRecallTag());
                    }
                }
                List<Index4User> index4UserList = docUtils.getIndex4UserList(mixRequestInfo, recomList, GyConstant.isOldResult);
                result.put(originSimId, index4UserList);
            }
            timer.addEndTime("dealPosResults");
        } catch (Exception e) {
            logger.error("uid:" + mixRequestInfo.getUid() + ",positive feedback supply source video error.", e);
        }

        return result;
//        return GsonUtil.object2json(result);
    }


    /**
     * PositiveFeedBehavior 这个类供头条正反馈强插使用，加上特殊的Strategy 标签，方便统计对比分析，也方便头条特殊处理
     *
     * @param mixRequestInfo
     * @param recomList
     */
    private void dealPositiveFeedStrategy(MixRequestInfo mixRequestInfo, List<RecallResult> recomList) {
        for (RecallResult recallResult : recomList) {
            recallResult.setStrategy(GyConstant.strategyPositiveFeed);
        }
    }

    /**
     * 转换RecallResults->Map(RecallTag,List<RecallResult>)
     *
     * @param recallResults
     * @return
     */
    private Map<String, List<RecallResult>> getPositiveFeedMapFromRecallResults(List<RecallResult> recallResults) {
        Map<String, List<RecallResult>> posResults = new HashMap<>();
        for (RecallResult recallResult : recallResults) {
            String originSimId = recallResult.getRecallTag();

            if (posResults.get(originSimId) != null) {
                posResults.get(originSimId).add(recallResult);
            } else {
                List<RecallResult> results = new ArrayList<>();
                results.add(recallResult);
                posResults.put(originSimId, results);
            }
        }
        return posResults;
    }

    private List<LastDocBean> getRemainBeanList(MixRequestInfo mixRequestInfo,Map<String, List<RecallResult>> posResults){
        List<LastDocBean> lastDocBeans = mixRequestInfo.getLastDocBeans();
        List<LastDocBean> remainBeanlist = new ArrayList<>();
        if (CollectionUtils.isEmpty(lastDocBeans)){
            return remainBeanlist;
        }
        try{
            Set<String> simIdSet = posResults.keySet();
            for(LastDocBean bean : lastDocBeans){
                String simId = bean.getSimId();
                if(simIdSet.contains(simId)){
                    continue;
                }else{
                    remainBeanlist.add(bean);
                }
            }
        }catch (Exception e){
            logger.error("uid:{} getRemainBeanList error:{}",mixRequestInfo.getUid(),e);
        }
        return remainBeanlist;
    }

    private void supplyRemainBeanlistVideo(MixRequestInfo mixRequestInfo,Map<String, List<RecallResult>> posResults){
        List<LastDocBean> lastDocBeans = mixRequestInfo.getLastDocBeans();
        List<Future<RecallThreadResult>> recallResultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(lastDocBeans)){
            return;
        }
        List<LastDocBean> remainBeanlist = getRemainBeanList(mixRequestInfo,posResults);
        if(CollectionUtils.isNotEmpty(remainBeanlist)){
            recallResultList.add(recallService.positiveFeedVideoFromSource(mixRequestInfo, remainBeanlist));
            Map<RecallConstant.CHANNEL, List<RecallResult>> channelDocsMapFromSource = getAsyncExecutorResult(mixRequestInfo, recallResultList, 500);
            List<RecallResult> posVideoFromSource = channelDocsMapFromSource.getOrDefault(RecallConstant.CHANNEL.PositiveFeedVideoFromSource, Collections.emptyList());
            Map<String, List<RecallResult>> sourcePosResults = getPositiveFeedMapFromRecallResults(posVideoFromSource);
            posResults.putAll(sourcePosResults);
        }
    }


    private void supplyRemainBeanlistDocpic(MixRequestInfo mixRequestInfo,Map<String, List<RecallResult>> posResults ){
        List<LastDocBean> lastDocBeans = mixRequestInfo.getLastDocBeans();
        if (CollectionUtils.isEmpty(lastDocBeans)){
            return;
        }
        //根据点击记录 获取点击记录中，posResults中未出现的simId
        List<LastDocBean> remainBeanlist = getRemainBeanList(mixRequestInfo,posResults);
        List<Future<RecallThreadResult>> recallResultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(remainBeanlist)) {
            recallResultList.add(recallService.positiveFeedDocpicFromSource(mixRequestInfo, remainBeanlist));
            Map<RecallConstant.CHANNEL, List<RecallResult>> channelDocsMapFromSource = getAsyncExecutorResult(mixRequestInfo, recallResultList, 500);
            List<RecallResult> posDocpicsFromSource = channelDocsMapFromSource.getOrDefault(RecallConstant.CHANNEL.PositiveFeedDocpicFromSource, Collections.emptyList());
            Map<String, List<RecallResult>> sourcePosResults = getPositiveFeedMapFromRecallResults(posDocpicsFromSource);
            posResults.putAll(sourcePosResults);
        }
    }
}
