package com.ifeng.recom.mixrecall.negative;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserCluster;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.item.EvItem;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.UserProfile;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import com.ifeng.recom.mixrecall.template.behavior.IncreaseBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.util.*;

import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getCateAndSubcateFilterForDoc;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getCateAndSubcateFilterForVideo;

@Service
public class NegativeSupport {
    private static final Logger logger = LoggerFactory.getLogger(NegativeSupport.class);
//    private MixRequestInfo mixRequestInfo;
    @Autowired
    @Qualifier("SessionServiceImpl")
    SessionServiceImpl sessionService;




    public Map<String,Map<String,Double>> getNegativeTag(MixRequestInfo mixRequestInfo) {

        logger.info("check negative uid:{} step1",mixRequestInfo.getUid());
        UserModel userModel = mixRequestInfo.getUserModel();
        logger.info("check negative uid:{} step2",mixRequestInfo.getUid());
        List<EvItem> evInfos = mixRequestInfo.getEvItems();

        if (evInfos == null || evInfos.size() == 0) {
            return Maps.newHashMap();
        }

        Map<String, ItemProfile> itemProfileMap = sessionService.getClick(mixRequestInfo, evInfos);

        evInfos = sessionService.merge_add(evInfos, mixRequestInfo);//得到30分钟内的相关数据
        logger.info("check negative uid:{} step3",mixRequestInfo.getUid());

        List<EvItem.EvObj> evList = sessionService.getevListFromSessionList(evInfos); //保存ev信息 docid 是否点击等

        /**
         * 获取用户画像信息 包括 长期、短期、实时
         */

        List<RecordInfo> combineTagList = userModel.getRecentCombineTagList();
        List<RecordInfo> Docpic_cotagList = userModel.getDocpic_cotag();
        List<RecordInfo> Recent_docpic_cotagList = userModel.getRecent_docpic_cotag();
        List<RecordInfo> Video_cotagList = userModel.getVideo_cotag();
        List<RecordInfo> Recent_video_cotagList = userModel.getRecent_video_cotag();

        List<RecordInfo> last_t1_recordList = userModel.getLastt1RecordList();
        List<RecordInfo> last_t2_recordList = userModel.getLastt2RecordList();
        List<RecordInfo> last_t3_recordList = userModel.getLastt3RecordList();

        List<RecordInfo> recentInfoList = new ArrayList<>();
        List<RecordInfo> longInfoList = new ArrayList<>();
        List<RecordInfo> lastInfoList = new ArrayList<>();



        if(combineTagList != null && combineTagList.size() >= 1){
            longInfoList.addAll(combineTagList);
        }
        if(Docpic_cotagList != null && Docpic_cotagList.size() >= 1){
            longInfoList.addAll(Docpic_cotagList);
        }
        if(Video_cotagList != null && Video_cotagList.size() >= 1){
            longInfoList.addAll(Video_cotagList);
        }
        if(Recent_docpic_cotagList != null && Recent_docpic_cotagList.size() >= 1){
            recentInfoList.addAll(Recent_docpic_cotagList);
        }

        if(Recent_video_cotagList != null && Recent_video_cotagList.size() >= 1){
            recentInfoList.addAll(Recent_video_cotagList);
        }
        if(last_t1_recordList != null && last_t1_recordList.size() >= 1 ){
            lastInfoList.addAll(last_t1_recordList);
        }
        if(last_t2_recordList != null && last_t2_recordList.size() >= 1){
            lastInfoList.addAll(last_t2_recordList);
        }
        if(last_t3_recordList != null && last_t3_recordList.size() >= 1){
            lastInfoList.addAll(last_t3_recordList);
        }
        Map<String,Integer> longCotagMap = new HashMap<>();
        Map<String,Integer> recentCotagMap = new HashMap<>();
        longInfoList = longInfoList.subList(0,longInfoList.size()/2);
        recentInfoList = recentInfoList.subList(0,recentInfoList.size()/2);


        for(RecordInfo item : longInfoList){
            String cate = item.getRecordName().split("-")[0];
            if(!longCotagMap.containsKey(cate)){
                longCotagMap.put(cate,1);
            }else{
                longCotagMap.put(cate,longCotagMap.get(cate)+1);
            }
        }
        for(RecordInfo item : recentInfoList){
            String cate = item.getRecordName().split("-")[0];
            if(!recentCotagMap.containsKey(cate)){
                recentCotagMap.put(cate,1);
            }else{
                recentCotagMap.put(cate,recentCotagMap.get(cate)+1);
            }
        }


        Set<String> RecentCotagSets = new HashSet<>();
        Set<String> LongCotagSets = new HashSet<>();
        Map<String,Double> LastCotagMap = new HashMap<>();

        for(Map.Entry<String,Integer> item : longCotagMap.entrySet()){
            if(item.getValue() > longInfoList.size()/longCotagMap.keySet().size()){
                LongCotagSets.add(item.getKey());
            }
        }
        for(Map.Entry<String,Integer> item : recentCotagMap.entrySet()){
            if(item.getValue() > recentInfoList.size()/recentCotagMap.keySet().size()){
                RecentCotagSets.add(item.getKey());
            }
        }
        LongCotagSets.addAll(RecentCotagSets);
        logger.info("get longCotagSets:{} result:{}",mixRequestInfo.getUid(),LongCotagSets.toString());


        lastInfoList.forEach(item -> {
            if(item.getWeight() < 0.51){
                LastCotagMap.put(item.getRecordName(),(double)item.getReadFrequency()/item.getExpose());
            }
        });


        /**
         * Version 1.1 得到最近曝光的文章的 pv 、点击和 添加衰减因子 并根据时间进行降权 ，并在负反馈中应用相关的权重进行降权
         */
        Map<String, FeatureExposeClick> featureExposeClickMap = getExposeClickStatistic(evList, userModel);
        logger.info("check negative uid:{} step4",mixRequestInfo.getUid());
        //此处用于测试
//        if(featureExposeClickMap.size() >0){
//            for(Map.Entry<String,FeatureExposeClick> item : featureExposeClickMap.entrySet()){
//                logger.info("check featureExpose uid:{} type:{} expo:{} click:{} feature:{}",mixRequestInfo.getUid(),item.getValue().getType(),item.getValue().getExpose(),item.getValue().getClick(),item.getValue().getFeatureWord());
//
//            }
//        }


        Map<String, Map<String, Double>> negResult = ContentBasedRecallNew(mixRequestInfo, featureExposeClickMap,userModel,LongCotagSets,LastCotagMap);

        if (negResult == null) {
            return Maps.newHashMap();
        }
        return negResult;
    }



    /**
     * mixcotag召回
     * @param ucombinetag
     * @param weight   判定新兴趣阈值，小于该阈值走强插逻辑
     * @param featureExposeClickMap session内用户正负反馈信息
     * @return
     */
    public static Map<String,List<String>> ContentBasedRecall(Map<String, FeatureExposeClick> featureExposeClickMap) {

        //解析ucombinetag画像
//        JSONArray cotagarray = JSONObject.parseArray(ucombinetag);
        if (featureExposeClickMap == null || featureExposeClickMap.size() == 0) {

            return null;
        } else {
            Map<String, List<String>> filterTag = new HashMap<>();

            List<String> cotagList = new ArrayList<String>();
            List<String> cateList = new ArrayList<String>();
            List<String> subCateList = new ArrayList<String>();
            List<String> topicList = new ArrayList<String>();

            for (Map.Entry<String, FeatureExposeClick> entry : featureExposeClickMap.entrySet()) {
                String[] arr = entry.getKey().split("_");
                String type = arr[0];
                String tag = arr[1];

                if (type.equals("cotag")) {
                    Double click = entry.getValue().getClick();
                    Double expose = entry.getValue().getExpose();
                    Double ctr = click / expose;
                    if (expose >= 3 && ctr < 0.2) {
                        cotagList.add(tag);
                    }
                }

                if (type.equals("c")) {
                    Double click = entry.getValue().getClick();
                    Double expose = entry.getValue().getExpose();
                    Double ctr = click / expose;
                    if (expose >= 5 && ctr < 0.1) {
                        cateList.add(tag);
                    }

                }

                if (type.equals("sc")) {
                    Double click = entry.getValue().getClick();
                    Double expose = entry.getValue().getExpose();
                    Double ctr = click / expose;
                    if (expose >= 4 && ctr < 0.1) {
                        subCateList.add(tag);
                    }
                }

                if (type.equals("topic")) {
                    Double click = entry.getValue().getClick();
                    Double expose = entry.getValue().getExpose();
                    Double ctr = click / expose;
                    if (expose >= 3 && ctr < 0.1) {
                        topicList.add(tag);
                    }
                }


            }


            filterTag.put("cotag", cotagList);
            filterTag.put("C", cateList);
            filterTag.put("sc", subCateList);
            filterTag.put("topic", topicList);

            return filterTag;
        }
    }


    /**
     * mixcotag召回
     * @param ucombinetag
     * @param weight   判定新兴趣阈值，小于该阈值走强插逻辑
     * @param featureExposeClickMap session内用户正负反馈信息
     * @return
     */
    public static Map<String,Map<String,Double>> ContentBasedRecallNew(MixRequestInfo mixRequestInfo,Map<String, FeatureExposeClick> featureExposeClickMap,UserModel userProfile,Set<String> longCotagSets,Map<String,Double> lastCotagMap) {

        if (featureExposeClickMap == null || featureExposeClickMap.size() == 0) {
            return Maps.newHashMap();
        } else {
            //得到最终的需要召回比例
            Map<String, Map<String, Double>> finalWightMap = Maps.newHashMap();

            Map<String, Double> cotagMap = Maps.newHashMap();
            Map<String, Double> cotag_feedMap = Maps.newHashMap();
            Map<String, Double> cotagMap_1 = Maps.newHashMap();
            Map<String, Double> cotagMap_2 = Maps.newHashMap();
            Map<String, Double> cotagMap_3 = Maps.newHashMap();
            Map<String, Double> cotagMap_4 = Maps.newHashMap();
            Map<String, String> cotagMap_5 = Maps.newHashMap();
            Map<String, String> cotagMap_6 = Maps.newHashMap();
            Map<String, String> cotagMap_7 = Maps.newHashMap();
            Map<String, String> cotagMap_8 = Maps.newHashMap();
            Map<String, String> cotagMap_9 = Maps.newHashMap();
            Map<String, Double> featureMap = Maps.newHashMap();
            Map<String, Double> featureMap_ratio = Maps.newHashMap();
            Map<String, Double> ldaMap = Maps.newHashMap();

            double filterThre = 3.0;

            double fullness;
            try {
                fullness = Double.parseDouble(mixRequestInfo.getUserModel().getFullness());
            } catch (Exception e) {
                fullness = 0.0;
            }
            if(fullness > 0.0  && fullness <= 0.4){
                filterThre = 2.2;
            }

//            Set<String> cScFilter_Doc = getCateAndSubcateFilterForDoc(mixRequestInfo.getUserModel());
//            Set<String> cScFilter_Video = getCateAndSubcateFilterForVideo(mixRequestInfo.getUserModel());
//            Set<String> cScFilter_Set = new HashSet<>();
//
//            if(cScFilter_Doc != null && cScFilter_Doc.size() >= 1){
//                cScFilter_Set.addAll(cScFilter_Doc);
//            }
//
//            if(cScFilter_Video != null && cScFilter_Video.size() >= 1){
//                cScFilter_Set.addAll(cScFilter_Video);
//            }

            for (Map.Entry<String, FeatureExposeClick> entry : featureExposeClickMap.entrySet()) {
                double weight = 1.0;

                String[] arr = entry.getKey().split("_");
                if(arr.length !=2){
                    logger.info("check arr:{}",arr);
                }
                String type = arr[0];
                String tag = arr[1];
                if (type.equals("c")) {
                    Double click = entry.getValue().getClick();
                    Double expose = entry.getValue().getExpose();
                    Double ctr = click / expose;

                    if (expose >= filterThre  && ctr < 0.25) {

                        if(longCotagSets.contains(tag)){
                            weight -= 0.12 * (expose - click);
                            cotagMap_1.put(tag,weight);
                        }else{
                            weight -= 0.17 * (expose - click);
                            cotagMap_2.put(tag,weight);
                        }
                        if (weight < 0.1) {
                            weight = 0.1;
                        }
                        cotagMap.put(tag, weight);
                    }

                    /**
                     * C 正反馈 增加曝光
                     */
                    if (expose >= filterThre && ctr > 0.6) {

                        if(longCotagSets.contains(tag)){
                            weight += 0.25 * click;
                        }else{
                            weight += 0.40 * click;
                        }
                        if (weight > 2.0) {
                            weight = 2.0;
                        }
                        cotagMap_5.put(tag,ctr+"_"+weight+"_"+expose+"_"+click);

                        cotag_feedMap.put(tag, weight);
                    }
                }

                if (type.equals("sc")) {
                    Double click = entry.getValue().getClick();
                    Double expose = entry.getValue().getExpose();
                    Double ctr = click / expose;

                    if (expose >= filterThre - 0.5  && ctr < 0.33) {
                        if(longCotagSets.contains(tag)){
                            weight -= 0.18 * (expose - click);
                            cotagMap_3.put(tag,weight);
                        }else{
                            weight -= 0.23 * (expose - click);
                            cotagMap_4.put(tag,weight);
                        }
                        if (weight < 0.1) {
                            weight = 0.1;
                        }
                        cotagMap.put(tag, weight);
                    }

                    /**
                     * SC 正反馈 增加曝光
                     */
                    if (expose >= filterThre -0.5 && ctr > 0.6) {

                        if(longCotagSets.contains(tag)){
                            weight += 0.25 * click;
                        }else{
                            weight += 0.40 * click;
                        }
                        if (weight > 2.0) {
                            weight = 2.0;
                        }
                        cotagMap_6.put(tag,ctr+"_"+weight+"_"+expose+"_"+click);

                        cotag_feedMap.put(tag, weight);
                    }
                }



                if (type.equals("featureWord")) {
                    Double click = entry.getValue().getClick();
                    Double expose = entry.getValue().getExpose();
                    Double ctr = click / expose;
                    /**
                     * 此处对媒体放宽过滤条件
                     */
                    Double filterFeature = filterThre;
                    if(tag.contains("(s)")){
                        filterFeature = filterThre + 1.0;
                    }

                    if (expose >= filterFeature && ctr < 0.1) {
                        if(lastCotagMap.containsKey(tag) && lastCotagMap.get(tag) < 0.3){
                            weight -= 0.20 * (expose - click);
                            if (weight < 0.1) {
                                weight = 0.1;
                            }
                            featureMap_ratio.put(tag,weight);
                        }else{
                            featureMap.put(tag, 1.0);

                        }

                    }

                    if (expose >= filterFeature - 1.0 && ctr >= 0.9) {
                        if(lastCotagMap.containsKey(tag)){
                            weight += 0.25 * click;
                        }else{
                            weight += 0.40 * click;
                            if (weight > 2.5) {
                                weight = 2.5;
                            }
                            cotagMap_7.put(tag,ctr+"_"+weight+"_"+expose+"_"+click);

                            featureMap_ratio.put(tag,weight);
                        }
                    }
                }

                if (type.equals("lda")) {
                    Double click = entry.getValue().getClick();
                    Double expose = entry.getValue().getExpose();
                    Double ctr = click / expose;
                    if (expose >= filterThre + 0.2 && ctr < 0.33) {
                        weight -= 0.15 * (expose - click);
                        if (weight < 0.1) {
                            weight = 0.1;
                        }
                        cotagMap_8.put(tag,ctr+"_"+weight+"_"+expose+"_"+click);

                        ldaMap.put(tag, weight);
                    }

                    if (expose >= filterThre - 0.2 && ctr > 0.75) {
                        weight += 0.2 * click;
                        if (weight > 2.0) {
                            weight = 2.0;
                        }

                        cotagMap_9.put(tag,ctr+"_"+weight+"_"+expose+"_"+click);

                        ldaMap.put(tag, weight);
                    }
                }
//                if(cScFilter_Set.contains(tag)){
//                    Double click = entry.getValue().getClick();
//                    Double expose = entry.getValue().getExpose();
//                    Double ctr = click / expose;
//                    if (ctr < 0.5) {
//                        weight = 0.0;
//                        cotagMap.put(tag, weight);
//                        logger.info("check here for uid:{} tag:{} weight:{}",mixRequestInfo.getUid(),tag,weight);
//
//                    }
//                }

            }



            finalWightMap.put("cotag",cotagMap);
            finalWightMap.put("cotag_posFeed",cotag_feedMap);
            finalWightMap.put("featureWord",featureMap);
            finalWightMap.put("featureWord_ratio",featureMap_ratio);
            finalWightMap.put("lda",ldaMap);



            logger.info("uid:{}  ______________NegativeFinalMap______________:{},cotag1:{},cotag2:{},cotag3:{},cotag4:{},cotag5:{},cotag6:{},cotag7:{},cotag8:{},cotag9:{},{},{},{} ",mixRequestInfo.getUid(),cotagMap,cotagMap_1,cotagMap_2,cotagMap_3,cotagMap_4,
                    cotagMap_5,cotagMap_6,cotagMap_7,cotagMap_8,cotagMap_9,featureMap,featureMap_ratio,ldaMap);
//            logger.info("uid:{}  ______________NegativeFinalMap______________:{},{},{} ",mixRequestInfo.getUid(),cotagMap,featureMap,ldaMap);

            return finalWightMap;
        }
    }

    /**
     * 解析user_cluster为一个map，key为一级分类
     * @param userProfile
     * @return
     */
    private Map<String, UserCluster> getCategoryUserClusterMap(UserModel userProfile) {
        if (userProfile == null) {
            return null;
        }

        List<UserCluster> userClusterFeature = userProfile.getUserClusterList();
        if (userClusterFeature == null) {
            return null;
        }
        Map<String, UserCluster> resultMap = new HashMap<>();
        for (UserCluster element : userClusterFeature) {
            if (element.getCate() != null) {
                resultMap.put(element.getCate(), element); //科技
            }
        }
        return resultMap;
    }

    /**
     * 统计evList中各个特征词的曝光点击数，存入Map，key为type_featureWord
     * 统计过程中考虑文章本身的CTR，CTR较高的文章对应的点击作降权（也就是曝光加权）
     * @param evList
     * @return
     */
    private static Map<String, FeatureExposeClick> getExposeClickStatistic(List<EvItem.EvObj> evList, UserModel userProfile) {
        Map<String, FeatureExposeClick> featureExposeClickMap = new HashMap<>();
        Map<Long,Map<String,Integer>> evPullnumMap = FeatureUtil.getEvpullnumMap(evList);


        /**
         * 此处添加cotagSets 对数据来源进行过滤 以保证过滤数据不被噪声影响 add by YX 20191016
         */
//        cotagSets.addAll(recentCotagSets);


        //按照时间戳 30分钟一个session 的文章进行排序 <time,Map<expo:1,cli:0,

        double decay = 1.0;
        if(userProfile == null || !StringUtils.isDouble(userProfile.getFullness())){
            decay = 0.3;
        }else{
            double fullness = Double.parseDouble(userProfile.getFullness());
            decay -= fullness * 0.3; //丰满度越高衰减因子越低 丰满度越低的用户对不感兴趣的文章更需要避免刷屏
        }
        List<String> check4weight = new ArrayList<>();

        for (EvItem.EvObj exposeInfo : evList) {
            boolean isClick = exposeInfo.isC();
            Long t = exposeInfo.getT();
            Map<String,Integer> valueMap = evPullnumMap.get(t);
            if(valueMap!=null){
                int pullnum = valueMap.get("pullnum");
                int clk = valueMap.get("clk");
                if(pullnum==1 && clk==0){//预载逻辑最近一刷特殊处理
                    continue;
                }
            }
            int pullnum = valueMap.get("pullnum");
            Set<String> cotags = exposeInfo.getCotags();
//            cotagSets.retainAll(cotags);
            double aic = 1.0;
//            if(cotagSets.size() != 0){
//                aic = 1.3;
//            }

            /**
             * 此处对于C和SC的过滤暂时注释 待之后需要对C以及SC通道进行过滤时使用 add by YX 2019-10-09
             */

            // c
            if(exposeInfo.getCategories() != null) {
                for (String category : exposeInfo.getCategories()) {
                    String key = "c" + "_" + category;
                    FeatureExposeClick featureExposeClick = featureExposeClickMap.get(key);
                    if (featureExposeClick == null) {
                        featureExposeClick = new FeatureExposeClick("c", category);
                        featureExposeClickMap.put(key, featureExposeClick);
                    }
                    featureExposeClick.addExpose(aic * Math.pow(decay,(1+pullnum)*0.5));
                    if (isClick) {
                        featureExposeClick.addClick(aic * Math.pow(decay,(1+pullnum)*0.5));
                    }
                }
            }

            // sc
            if(exposeInfo.getSubcates() != null ) {
                for (String subcate : exposeInfo.getSubcates()) {
                    if(subcate.length() >=1 ){
                        String key = "sc" + "_" + subcate;
                        FeatureExposeClick featureExposeClick = featureExposeClickMap.get(key);
                        if (featureExposeClick == null) {
                            featureExposeClick = new FeatureExposeClick("sc", subcate);
                            featureExposeClickMap.put(key, featureExposeClick);
                        }
                        featureExposeClick.addExpose(aic * Math.pow(decay,(1+pullnum)*0.5));
                        if (isClick) {
                            featureExposeClick.addClick(aic * Math.pow(decay,(1+pullnum)*0.5));
                        }
                    }
                }
            }

            //lda
            if(exposeInfo.getLdatopics() != null) {
                for (String lda : exposeInfo.getLdatopics()) {
                    String key = "lda" + "_" + lda;
                    FeatureExposeClick featureExposeClick = featureExposeClickMap.get(key);
                    if (featureExposeClick == null) {
                        featureExposeClick = new FeatureExposeClick("lda", lda);
                        featureExposeClickMap.put(key, featureExposeClick);
                    }
                    featureExposeClick.addExpose(aic * Math.pow(decay,(1+pullnum)*0.5));
                    if (isClick) {
                        featureExposeClick.addClick(aic * Math.pow(decay,(1+pullnum)*0.5) );
                    }
                }
            }

            //cotag
            if(exposeInfo.getCotags() != null) {
                Set<String> featureSet = new HashSet<>();

                for (String cotag : exposeInfo.getCotags()) {
                    String[] arr = cotag.split("-");
                    if(arr.length == 2){
                        String featureWord = "featureWord" + "_" + arr[1];
                        if(!featureSet.contains(arr[1])){
                            FeatureExposeClick featureExposeClick_featureWord = featureExposeClickMap.get(featureWord);
                            if (featureExposeClick_featureWord == null) {
                                featureExposeClick_featureWord = new FeatureExposeClick("featureWord", arr[1]);
                                featureExposeClickMap.put(featureWord, featureExposeClick_featureWord);
                            }

                            featureExposeClick_featureWord.addExpose(1.0);
                            if (isClick) {
                                featureExposeClick_featureWord.addClick(1.0);
                            }
                            featureSet.add(arr[1]);

                        }
                    }
                }
            }
        }
        return featureExposeClickMap;
    }

    /**
     * 统计evList中各个特征词的曝光点击数，存入Map，key为type_featureWord
     * 统计过程中考虑文章本身的CTR，CTR较高的文章对应的点击作降权（也就是曝光加权）
     * @param evList
     * @return
     */
    private static Map<String, FeatureExposeClick> getExposeClickStatisticNew(List<EvItem.EvObj> evList, UserModel userProfile) {
        Map<String, FeatureExposeClick> featureExposeClickMap = new HashMap<>();
        Map<Long,Map<String,Integer>> evPullnumMap = FeatureUtil.getEvpullnumMap(evList);
        logger.info("get _______________evpullNumMap:{}",evPullnumMap.toString());

        //此处添加衰减因子
        double decay = 1.8;
        if(userProfile == null || !StringUtils.isDouble(userProfile.getFullness())){
            decay = 1.5;
        }else{
            double fullness = Double.parseDouble(userProfile.getFullness());
            decay += fullness * 1.1; //丰满度越高衰减因子越低 丰满度越低的用户对不感兴趣的文章更需要避免刷屏
        }


        for (EvItem.EvObj exposeInfo : evList) {
            boolean isClick = exposeInfo.isC();
            Long t = exposeInfo.getT();
            Map<String,Integer> valueMap = evPullnumMap.get(t);

            if(valueMap!=null){
                int pullnum = valueMap.get("pullnum");
                int clk = valueMap.get("clk");
                if(pullnum==1 && clk==0){//预载逻辑最近一刷特殊处理
                    continue;
                }
            }

            int pullnum = valueMap.get("pullnum");
            // c
            if(exposeInfo.getCategories() != null) {
                for (String category : exposeInfo.getCategories()) {
                    String key = "c" + "_" + category;
                    FeatureExposeClick featureExposeClick = featureExposeClickMap.get(key);
                    if (featureExposeClick == null) {
                        featureExposeClick = new FeatureExposeClick("c", category);
                        featureExposeClickMap.put(key, featureExposeClick);
                    }
                    featureExposeClick.addExpose(0.58 * Math.pow(decay, -1-(0.1*pullnum)));
                    if (isClick) {
                        featureExposeClick.addClick(0.58 * Math.pow(decay, -1-(0.1*pullnum)));
                    }
                }
            }
            // sc
            if(exposeInfo.getSubcates() != null) {
                for (String subcate : exposeInfo.getSubcates()) {
                    String key = "sc" + "_" + subcate;
                    FeatureExposeClick featureExposeClick = featureExposeClickMap.get(key);
                    if (featureExposeClick == null) {
                        featureExposeClick = new FeatureExposeClick("sc", subcate);
                        featureExposeClickMap.put(key, featureExposeClick);
                    }
                    featureExposeClick.addExpose(0.58 *  Math.pow(decay, -1-(0.1*pullnum)));
                    if (isClick) {
                        featureExposeClick.addClick(0.58 * Math.pow(decay, -1-(0.1*pullnum)));
                    }
                }
            }

            //lda
            if(exposeInfo.getLdatopics() != null) {
                for (String lda : exposeInfo.getLdatopics()) {
                    String key = "lda" + "_" + lda;
                    FeatureExposeClick featureExposeClick = featureExposeClickMap.get(key);
                    if (featureExposeClick == null) {
                        featureExposeClick = new FeatureExposeClick("lda", lda);
                        featureExposeClickMap.put(key, featureExposeClick);
                    }
                    featureExposeClick.addExpose(0.5 * Math.pow(decay, -1-(0.1*pullnum)));
                    if (isClick) {
                        featureExposeClick.addClick(0.5 *Math.pow(decay, -1-(0.1*pullnum)));
                    }
                }
            }

            //cotag
            if(exposeInfo.getCotags() != null) {
                for (String cotag : exposeInfo.getCotags()) {
                    String[] arr = cotag.split("-");
                    String key = "cotag" + "_" + arr[0];
                    String featureWord = "featureWord" + "_" + arr[1];
                    FeatureExposeClick featureExposeClick_cotag = featureExposeClickMap.get(key);
                    FeatureExposeClick featureExposeClick_featureWord = featureExposeClickMap.get(key);

                    if (featureExposeClick_cotag == null) {
                        featureExposeClick_cotag = new FeatureExposeClick("cotag", cotag);
                        featureExposeClickMap.put(key, featureExposeClick_cotag);
                    }
                    featureExposeClick_cotag.addExpose(1.0);

                    if (featureExposeClick_featureWord == null) {
                        featureExposeClick_featureWord = new FeatureExposeClick("featureWord", cotag);
                        featureExposeClickMap.put(key, featureExposeClick_featureWord);
                    }
                    featureExposeClick_featureWord.addExpose(1.0);
                    if (isClick) {
                        featureExposeClick_cotag.addClick(1.0);
                        featureExposeClick_featureWord.addClick(1.0);
                    }
                }
            }
        }
        return featureExposeClickMap;
    }
}
