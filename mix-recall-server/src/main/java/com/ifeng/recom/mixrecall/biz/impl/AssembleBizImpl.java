package com.ifeng.recom.mixrecall.biz.impl;

import com.ifeng.recom.mixrecall.biz.IAssembleBiz;
import com.ifeng.recom.mixrecall.common.constant.FlowTypeAsync;
import com.ifeng.recom.mixrecall.common.constant.MonitorKey;
import com.ifeng.recom.mixrecall.common.constant.MonitorType;
import com.ifeng.recom.mixrecall.common.model.item.MixResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.StringZipUtil;
import com.ifeng.recom.mixrecall.common.util.flowType.FlowTypeUtils;
import com.ifeng.recom.mixrecall.prerank.constant.CTRConstant;
import com.ifeng.recom.mixrecall.prerank.executor.FeatureVectorExtractor;
import com.ifeng.recom.mixrecall.prerank.modelconfig.ModelConfigParser;
import com.ifeng.recom.mixrecall.prerank.tools.CtrSmoothParamsManager;
import com.ifeng.recom.mixrecall.prerank.tools.MediaEvalLevelCacheManager;
import com.ifeng.recom.mixrecall.support.RequestSupport;
import com.ifeng.recom.mixrecall.template.behavior.*;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import com.ifeng.recom.tools.log.MonitorLog;
import com.ifeng.recom.tools.log.MonitorLogEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import javax.annotation.PostConstruct;
=======
import java.util.Map;
>>>>>>> add7216e9597a942f2c2c4e74105441dd134a281

/**
 * 考虑到后续的装备逻辑可能有多种，这里讲各个通道抽成一套公共模板
 */
@Service
public class AssembleBizImpl implements IAssembleBiz {
    private static final Logger logger = LoggerFactory.getLogger(AssembleBizImpl.class);

    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    @Autowired
    private PositiveFeedNewBehavior positiveFeedNewBehavior;
    @Autowired
    private IncreaseBehavior increaseBehavior;
    @Autowired
    private UserSubBehavior userSubBehavior;
    @Autowired
    private RequestSupport requestSupport;
    @Autowired
    private UserLastBehavior userLastBehavior;
    @Autowired
    private LastCotagBehavior lastCotagBehavior;

    /**
     * 新的http使用的接口,根据不同的flowType进行不同的操作
     *
     * @param mixRequestInfo
     * @return result String
     */
    @Override
    public String doRecom(MixRequestInfo mixRequestInfo) {
        long start = System.currentTimeMillis();
        Object result = null;
        String flowType = mixRequestInfo.getFlowType();
        TimerEntity timer = TimerEntityUtil.getInstance();

        requestSupport.init(mixRequestInfo);
        String uid = mixRequestInfo.getUid();
        if(mixRequestInfo.isDebugUser()){
            logger.info("uid:{} debugTest flowType:{}",uid,flowType);
        }
        String behavior;
        //新闻正反馈和视频正反馈走单独接口，结果不同
        if (FlowTypeAsync.positiveFeedNew.equals(flowType) || FlowTypeAsync.positiveFeedVideoNew.equals(flowType)) {
            timer.addStartTime("positiveFeedNewBehavior");
            result = positiveFeedNewBehavior.doRecom(mixRequestInfo);
            timer.addEndTime("positiveFeedNewBehavior");
            behavior = "positiveFeed";
            //订阅频道的处理是不一样的
        } else if (FlowTypeUtils.isMomentsnew(mixRequestInfo)) {
            // TODO 有流量 storm
            timer.addStartTime("userSubBehavior");
            result = userSubBehavior.doRecom(mixRequestInfo);
            timer.addEndTime("userSubBehavior");
            behavior = "userSub";
            //普通增量
        } else if (FlowTypeAsync.IncreasedateMerge.equals(flowType)) {
            timer.addStartTime("increaseBehavior");
            // TODO 有流量 storm
            result = increaseBehavior.doRecom(mixRequestInfo);
            timer.addEndTime("increaseBehavior");
            behavior = "incr";
        } else if(FlowTypeAsync.LastTopic.equals(flowType)){
            result = userLastBehavior.doRecom(mixRequestInfo);
            behavior = "lastTopic";
        } else if(FlowTypeAsync.FlowType_LastCotag.equals(flowType)){
            // TODO 有流量 engine
            result = lastCotagBehavior.doRecom(mixRequestInfo);
            behavior = "flowTypeLast";
        } else {
            behavior = "error";
        }
        if (result == null) {
            return null;
        }
        String str = GsonUtil.object2json(result);
        if (result instanceof MixResult) {
            MixResult t = (MixResult) result;
            int recallSize = t.getIndex4UserList() != null ? t.getIndex4UserList().size() : 0;
            MonitorLog.storeLogger(MonitorType.RECALL_BASIC.name(), MonitorType.RECALL_NAME.strMapper(behavior),
                    MonitorLogEntity.intEntity(MonitorKey.SIZE, recallSize),
                    MonitorLogEntity.longEntity(MonitorKey.RECOM_COST, System.currentTimeMillis() - start),
                    MonitorLogEntity.stringEntity(MonitorKey.UID, mixRequestInfo.getUid())
            );
        } else if (result instanceof Map) {
            Map t = (Map) result;
            int recallSize = t != null ? t.size() : 0;
            MonitorLog.storeLogger(MonitorType.RECALL_BASIC.name(), MonitorType.RECALL_NAME.strMapper(behavior),
                    MonitorLogEntity.intEntity(MonitorKey.SIZE, recallSize),
                    MonitorLogEntity.longEntity(MonitorKey.RECOM_COST, System.currentTimeMillis() - start),
                    MonitorLogEntity.stringEntity(MonitorKey.UID, mixRequestInfo.getUid()));

        }
        if (StringUtils.isNotBlank(str) && mixRequestInfo.isCompress()) {
            return StringZipUtil.compress(str);
        } else {
            return str;
        }
    }


}