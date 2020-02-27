package com.ifeng.recom.mixrecall.controller;

import com.google.gson.JsonSyntaxException;
import com.ifeng.recom.mixrecall.biz.impl.RecallBizImpl;
import com.ifeng.recom.mixrecall.common.constant.FlowTypeAsync;
import com.ifeng.recom.mixrecall.common.constant.FlowTypeSync;
import com.ifeng.recom.mixrecall.common.constant.LogFileName;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.LoggerUtils;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory.ListRecordInfo;
import static com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory.MapStringString;

/**
 * Created by liligeng on 2019/8/7.
 * 引擎实时调用召回http入口，引擎传入画像字段减少召回耗时
 * 控制耗时在50ms以下
 */
@RestController
@RequestMapping("/mixrecom")
public class RealTimeController {

    private static final Logger logger = LoggerFactory.getLogger(RecomController.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);
    private static final Logger accessLogger = LoggerUtils.Logger(LogFileName.ACCESS);

    @Autowired
    private RecallBizImpl recallBiz;

    @GetMapping("/recall")
    public String mixHome(String requestInfo, String userModel) {
        return postMixHome(requestInfo, userModel);
    }

    /**
     * 实时性召回入库，两个post参数，传入指定的画像信息
     * 主要用在召回首屏
     * @param requestInfo
     * @param userModel
     * @return
     */
    @PostMapping("/recall")
    public String postMixHome(String requestInfo, @RequestParam(required = false) String userModel) {

        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("total");

        String uid = null;
        String result = "";
        String flowType = null;

        try {
            MixRequestInfo mixRequestInfo = GsonUtil.json2Object(requestInfo, MixRequestInfo.class);

            uid = mixRequestInfo.getUid();
            flowType = mixRequestInfo.getFlowType();
            parseUserModel(mixRequestInfo, userModel, flowType, uid);


            accessLogger.info("uid:{} type:{} realTime request:{}", uid, flowType, requestInfo);

            if (StringUtils.isBlank(flowType)) {
                logger.warn("uid:{} flowType is null", uid);
                return result;
            }

            //各种组装逻辑策略统一返回string结果，方便后面兼容不同的数据结构
            result = recallBiz.doRecom(mixRequestInfo);

        } catch (Exception e) {
            logger.error("uid:{},controller {}", uid, e);
            e.printStackTrace();
        } finally {
            timer.addEndTime("total");
            timeLogger.info("mixRecom {} uid:{},flowType:{}", timer.getStaticsInfo(), uid, flowType);
            TimerEntityUtil.remove();
        }

        return result;

    }

    private void parseUserModel(MixRequestInfo mixRequestInfo, String userModel, String flowType, String uid){

        if(StringUtils.isBlank(userModel)) {
           return;
        }


        try {
            Map<String,String> userMap = GsonUtil.json2Object(userModel, MapStringString);
            UserModel model = new UserModel();
            model.setUserId(uid);
            if(flowType.equals(FlowTypeSync.toutiaoFirst)){
                //解析docpic_cotag
                String docpicCotagStr = userMap.get("docpic_cotag");
                if(StringUtils.isNotBlank(docpicCotagStr)) {
                    List<RecordInfo> docpicList = GsonUtil.json2Object(docpicCotagStr, ListRecordInfo);
                    model.setDocpic_cotag(docpicList);
                }

                //解析video_cotag
                String videoCotagStr = userMap.get("video_cotag");
                if(StringUtils.isNotBlank(videoCotagStr)){
                    List<RecordInfo> videoList = GsonUtil.json2Object(videoCotagStr, ListRecordInfo);
                    model.setVideo_cotag(videoList);
                }
            }

            if(flowType.equals(FlowTypeAsync.LastTopic)){
                //解析last_lda_topic
                String ldaTopicStr = userMap.get("last_lda_topic");
                if(StringUtils.isNotBlank(ldaTopicStr)){
                    List<RecordInfo> lastLdaTopicList = GsonUtil.json2Object(ldaTopicStr, ListRecordInfo);
                    model.setLastLdaTopicList(lastLdaTopicList);
                }
            }
            //若没有长期画像时用last_ucombineTag打底
            String lastUcombineTagStr = userMap.get("last_ucombineTag");
            if(StringUtils.isNotBlank(lastUcombineTagStr)) {
                List<RecordInfo> lastUcombineList = GsonUtil.json2Object(lastUcombineTagStr, ListRecordInfo);
                model.setLastCombineTagList(lastUcombineList);
            }
            mixRequestInfo.setUserModel(model);
        } catch (JsonSyntaxException e) {
            logger.error("uid:{} parse userModel err:{}", uid, userModel);
        }
    }
}
