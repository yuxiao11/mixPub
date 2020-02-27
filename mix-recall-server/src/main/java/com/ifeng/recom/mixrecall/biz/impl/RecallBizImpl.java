package com.ifeng.recom.mixrecall.biz.impl;

import com.ifeng.recom.mixrecall.biz.IAssembleBiz;
import com.ifeng.recom.mixrecall.common.constant.FlowTypeAsync;
import com.ifeng.recom.mixrecall.common.constant.FlowTypeSync;
import com.ifeng.recom.mixrecall.common.constant.MonitorKey;
import com.ifeng.recom.mixrecall.common.constant.MonitorType;
import com.ifeng.recom.mixrecall.common.model.item.MixResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.template.behavior.LastCotagBehavior;
import com.ifeng.recom.mixrecall.template.behavior.ToutiaoFirstBehavior;
import com.ifeng.recom.mixrecall.template.behavior.UserLastBehavior;
import com.ifeng.recom.tools.log.MonitorLog;
import com.ifeng.recom.tools.log.MonitorLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by liligeng on 2019/8/7.
 * 实时http接口业务组装类
 *
 */
@Service
public class RecallBizImpl implements IAssembleBiz {


    @Autowired
    private ToutiaoFirstBehavior toutiaoFirstBehavior;

    @Autowired
    private LastCotagBehavior lastCotagBehavior;

    @Autowired
    private  UserLastBehavior userLastBehavior;

    @Override
    public String doRecom(MixRequestInfo mixRequestInfo) {
        Object result = null;
//        String result = null;
        long start = System.currentTimeMillis();
        String behavior="";
        if(FlowTypeSync.toutiaoFirst.equals(mixRequestInfo.getFlowType())){
            // TODO 有流量 engine
            result = toutiaoFirstBehavior.doRecom(mixRequestInfo);
            behavior = "toutiaoFirst";
        }else if(FlowTypeSync.lastCotag.equals(mixRequestInfo.getFlowType())){
            result = lastCotagBehavior.doRealTimeRecom(mixRequestInfo);
            behavior = "lastCotag";
        }else if(FlowTypeAsync.LastTopic.equals(mixRequestInfo.getFlowType())){
            // TODO 有流量 engine
            result = userLastBehavior.doRecom(mixRequestInfo);
            behavior = "lastTopic";
        }
        if (result instanceof MixResult) {
            MixResult t = (MixResult) result;
            int recallSize = t.getIndex4UserList() != null ? t.getIndex4UserList().size() : 0;
            MonitorLog.storeLogger(MonitorType.RECALL_BASIC.name(), MonitorType.RECALL_NAME.strMapper(behavior),
                    MonitorLogEntity.intEntity(MonitorKey.SIZE, recallSize),
                    MonitorLogEntity.longEntity(MonitorKey.RECOM_COST, System.currentTimeMillis() - start),
                    MonitorLogEntity.stringEntity(MonitorKey.UID, mixRequestInfo.getUid())
            );
        }
        return GsonUtil.object2json(result);
    }

}
