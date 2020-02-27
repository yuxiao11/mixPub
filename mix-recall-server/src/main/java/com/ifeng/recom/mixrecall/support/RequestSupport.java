package com.ifeng.recom.mixrecall.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.config.NumRation;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.FlowTypeAsync;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.item.LastDocBean;
import com.ifeng.recom.mixrecall.common.model.request.DynamicParams;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams4videoUser;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.JsonUtil;
import com.ifeng.recom.mixrecall.common.util.flowType.FlowTypeUtils;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;
import com.ifeng.recom.mixrecall.core.cache.mapping.EditorIdDocIdMappingCache;
import com.ifeng.recom.mixrecall.prerank.executor.FeatureVectorExtractor;
import com.ifeng.recom.mixrecall.prerank.modelconfig.ModelConfigParser;
import com.ifeng.recom.mixrecall.prerank.tools.CtrSmoothParamsManager;
import com.ifeng.recom.mixrecall.prerank.tools.MediaEvalLevelCacheManager;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ifeng.recom.mixrecall.prerank.constant.CTRConstant;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



import static com.ifeng.recom.mixrecall.core.util.MathUtil.getNumByUid;

/**
 * 初始化请求信息
 * Created by jibin on 2018/1/19.
 */
@Service
public class RequestSupport {

    private static final Logger logger = LoggerFactory.getLogger(RequestSupport.class);

    /**
     * 通用的初始化方法，所有的初始化方法都收到一个地方
     *
     * @param mixRequestInfo
     */
    public void init(MixRequestInfo mixRequestInfo) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        String uid = mixRequestInfo.getUid();
        try{
            timer.addStartTime("profile");
            UserModel userModel = UserProfileCache.getUserModel(uid);
            mixRequestInfo.setUserModel(userModel);
            timer.addEndTime("profile");

            if (mixRequestInfo.isDebugUser()) {
                logger.warn("{} userModel is:{}", uid, JsonUtil.object2jsonWithoutException(userModel));
            }

            if(userModel==null||userModel.getCombineTagList()==null){
                logger.info("{} userModel is null",uid);
            }

            /**
             * 为用户画像添加ctr部分信息 add by yx 20191228
             */
            userModel.fillUserModel();

            initLastDoc(mixRequestInfo);
            initLogicParam(mixRequestInfo);
//            /**
//             * 此处添加媒体评级缓存 计算CTR by yx 20191227
//             */
//
//            // 初始化媒体评级
//            MediaEvalLevelCacheManager.init();
//            // 初始化历史CTR平滑参数信息
//            CtrSmoothParamsManager.init();
//
//            //初始化模型配置
//            ModelConfigParser.loadModelConfigs(CTRConstant.MODEL_CONFIG_PATH);
////            initABTestParam(mixRequestInfo);
//
//            //初始化特征
//            FeatureVectorExtractor.loadFeatureConfigs(CTRConstant.FEATURE_PATH);


        }catch (Exception e){
            e.printStackTrace();
            logger.error("uid:{},Request init error:{}",uid,e);
        }

    }

    /**
     * 此处添加ABtest初始化流程
     * abtestMap参数为 Abtest实验名和Abtest分组名 例如Filter_test_20  20为流量数 当前取模10000 所以20%流量为<2000
     * getNumByUid函数 参数为：uid，groupName 此处groupName为实验组名
     * @param mixRequestInfo
     */

    public void initABTestParam(MixRequestInfo mixRequestInfo){
        Map<String,String> abtestMap = new HashMap<>();
        long abtestFlag = getNumByUid( mixRequestInfo.getUid(),"CateFilterTest"); // 此处按照
        if(abtestFlag < 2000){
            abtestMap.put("CateFilterTest","Filter_test_20");

        }else if(abtestFlag < 4000) {
            abtestMap.put("CateFilterTest","Filter_base1_20");

        }else if(abtestFlag < 6000) {
            abtestMap.put("CateFilterTest","Filter_base2_20");

        }else if(abtestFlag < 8000) {
            abtestMap.put("GraphCotagTest","Graph_Cotag_test_20");

        }else if(abtestFlag < 10000) {
            abtestMap.put("GraphCotagTest","Graph_Cotag_base_20");

        }
        mixRequestInfo.setAbTestMap(abtestMap);
    }

    public void initDynamic(MixRequestInfo mixRequestInfo) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        String uid = mixRequestInfo.getUid();
        try{
            timer.addStartTime("profile");
            UserModel userModel = UserProfileCache.getUserModel(uid);

            mixRequestInfo.setUserModel(userModel);
            timer.addEndTime("profile");

            if (mixRequestInfo.isDebugUser()) {
                logger.warn("{} userModel is:{}", uid, JsonUtil.object2jsonWithoutException(userModel));
            }

            if(userModel==null||userModel.getCombineTagList()==null){
                logger.info("{} userModel is null",uid);
            }
            initLastDoc(mixRequestInfo);
            initDynamicParam(mixRequestInfo);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("uid:{},Request init error:{}",uid,e);
        }

    }

    /**
     * 初始化召回数量配置，后续待补充
     * 计划后面改成动态控制的
     *
     * @param mixRequestInfo
     */
    private void initLogicParam(MixRequestInfo mixRequestInfo) {
        LogicParams logicParams = null;
        if(mixRequestInfo.getRecomChannel() == "videoapp"){
            logicParams = new LogicParams4videoUser();
        }else{
            logicParams = new LogicParams();

        }

        mixRequestInfo.setLogicParams(logicParams);
        UserModel userModel=mixRequestInfo.getUserModel();

        //初始化总条数，默认值200
        if (FlowTypeAsync.IncreasedateMerge.equals(mixRequestInfo.getFlowType())) {
            logicParams.setResult_size(GyConstant.ResultSize_IncreasedateMerge);

            if (userModel == null || userModel.getUa_v() < 6) {
                logicParams.setExpDocNum(300);
                logicParams.setExpVideoNum(200);
            } else {
                logicParams.setExpDocNum(100);
                logicParams.setExpVideoNum(50);
            }


        }

        if (FlowTypeUtils.isMomentsnew(mixRequestInfo)) {
            updateUserSub(mixRequestInfo);
        }

        if (FlowTypeUtils.isHeadline(mixRequestInfo)) {
            updateCotagLvs(mixRequestInfo);
        }
    }

    /**
     * 初始化召回数量配置，后续待补充
     * 计划后面改成动态控制的
     *
     * @param mixRequestInfo
     */
    private void initDynamicParam(MixRequestInfo mixRequestInfo) {
        LogicParams logicParams = new DynamicParams();

        NumRation numRatio = new NumRation();
        makeDiffRatio(mixRequestInfo,numRatio,logicParams);
        mixRequestInfo.setLogicParams(logicParams);
        UserModel userModel=mixRequestInfo.getUserModel();

        //初始化总条数，默认值200
        if (FlowTypeAsync.IncreasedateMerge.equals(mixRequestInfo.getFlowType())) {
            logicParams.setResult_size(GyConstant.ResultSize_IncreasedateMerge);

            if (userModel == null || userModel.getUa_v() < 6) {
                logicParams.setExpDocNum(300);
                logicParams.setExpVideoNum(200);
            } else {
                logicParams.setExpDocNum(100);
                logicParams.setExpVideoNum(50);
            }


        }

        if (FlowTypeUtils.isMomentsnew(mixRequestInfo)) {
            updateUserSub(mixRequestInfo);
        }

        if (FlowTypeUtils.isHeadline(mixRequestInfo)) {
            updateCotagLvs(mixRequestInfo);
        }
    }


    /**
     * 更新用户关注频道的数据参数配置
     *
     * @param mixRequestInfo
     */
    private void updateUserSub(MixRequestInfo mixRequestInfo) {
        LogicParams logicParams = mixRequestInfo.getLogicParams();
        logicParams.setNumEachUserSub(GyConstant.eacheNum_Sub_Momentsnew);
        logicParams.setNumToAddUserSub(GyConstant.num_Sub_Momentsnew);
    }

    /**
     * 更新cotagS的召回数量
     *
     * @param mixRequestInfo
     */
    private void updateCotagLvs(MixRequestInfo mixRequestInfo) {
        LogicParams logicParams = mixRequestInfo.getLogicParams();
        logicParams.setCotagSTotalNum(GyConstant.cotags_Total_Headline);
    }

    /**
     * 初始化lastDoc， 处理客户端回传的lastDoc中的id问题，预处理一次，后面都不用再次处理
     *
     * @param mixRequestInfo
     */
    private void initLastDoc(MixRequestInfo mixRequestInfo) {
        List<LastDocBean> lastDocBeans = mixRequestInfo.getLastDocBeans();
        if (CollectionUtils.isEmpty(lastDocBeans)) {
            lastDocBeans = Lists.newArrayList();
            mixRequestInfo.setLastDocBeans(lastDocBeans);
            return;
        }

        Set<String> normalIdSet = Sets.newHashSet();
        for (LastDocBean lastDocBean : lastDocBeans) {
            String normalId = lastDocBean.getDocId();
            if (!StringUtils.isBlank(normalId)) {
                normalIdSet.add(normalId);
            }
        }

        //id 转换
        Map<String, String> normalId2DocId = EditorIdDocIdMappingCache.getBatchDocIds(normalIdSet);


        Set<String> docId2query = Sets.newHashSet();
        for (LastDocBean lastDocBean : lastDocBeans) {
            String normalId = lastDocBean.getDocId();
            if (StringUtils.isBlank(normalId)) {
                continue;
            }
            String docId = normalId2DocId.get(normalId);
            lastDocBean.setDocId(docId);

            //simid或者source为空需要补足
            if (StringUtils.isBlank(lastDocBean.getSimId()) || StringUtils.isBlank(lastDocBean.getSource())) {
                docId2query.add(docId);
            }
        }


        Map<String, Document> docMap= Maps.newHashMap();
        if(docId2query!=null&&docId2query.size()>0){
            docMap=DocPreloadCache.getBatchDocsWithQuery(docId2query);
        }

        for (LastDocBean lastDocBean : lastDocBeans) {
            String simId = lastDocBean.getSimId();
            if (StringUtils.isBlank(simId)) {
                Document doc = docMap.get(lastDocBean.getDocId());
                if (doc != null && doc.getDocId() != null) {
                    simId = doc.getSimId();
                    lastDocBean.setSimId(simId);
                }
            }
        }
        //mixRequestInfo中的lastbean不含有sources字段 需将source补充进去
        for(LastDocBean lastDocBean : lastDocBeans){
            String source = lastDocBean.getSource();
            if(StringUtils.isBlank(source)){
                Document doc = docMap.get(lastDocBean.getDocId());
                if (doc != null && doc.getDocId() != null) {
                    source = doc.getSource();
                    lastDocBean.setSource(source);
                }
            }
        }

        if(mixRequestInfo.isDebugUser()){
            logger.info("uid:{} debugTest lastDocBeans:{}",mixRequestInfo.getUid(),GsonUtil.object2json(lastDocBeans));
        }
    }
    /**
     * 新方法 用于设置动态的召回参数 By ZGX YX  20190526
     */
    private void makeDiffRatio(MixRequestInfo mixRequestInfo,NumRation numRatio,LogicParams logicParamsNew){
        Map<String,Map<String,String>> ratioMap= numRatio.getValue();
        Integer pullCount = mixRequestInfo.getPullCount();
        Integer daily_pullNum = Integer.valueOf(mixRequestInfo.getUserModel().getDaily_pullNum());
        if(daily_pullNum <= 7 ){ //userType 1
            Map<String,String> numRatioMap = ratioMap.get("userType1-pullGroup"+String.valueOf(pullCount));
//            logicParamsNew.setRatio(numRatioMap);
        }else if(daily_pullNum >= 8 && daily_pullNum <= 15 ){ //userType 2
            Map<String,String> numRatioMap = ratioMap.get("userType2-pullGroup"+String.valueOf(pullCount));
        }else if(daily_pullNum >=16 && daily_pullNum <= 30){ //userType 3
            Map<String,String> numRatioMap = ratioMap.get("userType3-pullGroup"+String.valueOf(pullCount));
        }else{  //userType 4
            Map<String,String> numRatioMap = ratioMap.get("userType4-pullGroup"+String.valueOf(pullCount));
        }

    }



}
