package com.ifeng.recom.mixrecall.prerank;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.ctr.CtrResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.prerank.constant.CTRConstant;
import com.ifeng.recom.mixrecall.prerank.ctrmodel.CtrModelRedisClusterUtil;
import com.ifeng.recom.mixrecall.prerank.entity.BasicContext;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import com.ifeng.recom.mixrecall.prerank.tools.CtrSmoothParamsManager;
import com.ifeng.recom.mixrecall.prerank.tools.HistoryCtrRedisClusterUtil;
import com.ifeng.recom.mixrecall.prerank.tools.MediaEvalLevelCacheManager;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PreRank {

    private static final Logger logger = LoggerFactory.getLogger(PreRank.class);

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    CtrModelRedisClusterUtil ctrModelRedisClusterUtil;


    @Autowired
    @Qualifier("HistoryCtrRedisClusterUtil")
    private HistoryCtrRedisClusterUtil historyCtrRedisClusterUtil;


    /**
     * 模型文件名后缀
     */
    private static final String MODEL_NAME_NAME = ".model";
    private static final String FM_DELTA_MODEL_PREFIX = "delta-";


    public List<RecallResult> getPreRankResult(MixRequestInfo mixRequestInfo,List<RecallResult> results){
//        \ = null;
        CtrResult ctrResult = new CtrResult();


        try{
            TimerEntity timer = TimerEntityUtil.getInstance();

            timer.addStartTime("model");
            Model model = FMModelUtil.getModelByName(CTRConstant.FLAG_CTR_MODEL); //此处为模型名字 后期可以修改名字来控制模型
            timer.addEndTime("model");


            //组装特征信息
            timer.addStartTime("feature");
            List<FeatureContext> featureEntitys = getFeatureEntitys(mixRequestInfo,results); //这里获取用户画像信息 以及 内容画像信息
            timer.addEndTime("feature");


            // 获取用户id特征向量
            timer.addStartTime("uidVec");
            double[] uidVec = getUidVec("fm_1212_v8",mixRequestInfo.getUid());  //获取用户特征向量
            timer.addEndTime("uidVec");


            //计算ctr
            timer.addStartTime("calcCTR");
            ConcurrentHashMap<String, String> ctrMap = new ConcurrentHashMap<String, String>();

            try {
                CalcCtrUtils.calcCTR(ctrMap, results, model, featureEntitys, cacheManager, uidVec);
            } catch (Exception e) {
                logger.error("calcCTR ERROR:{} ", e.getMessage());
                e.printStackTrace();
            }

            ctrResult.setCtrMap(ctrMap);
//            result = JsonUtils.writeToJSON(ctrResult);


            /**
             * RecallResult 设置相应ctr分数
             */
            for(RecallResult item : results){
                item.setCtr(Double.parseDouble(ctrMap.get(item.getDocument().getDocId())));
            }

        }catch(Exception e){
            logger.error("get ctrResult doExperiment ERROR, uid = " + mixRequestInfo.getUid(), e);
        }


        /**
         * 获取模型
         */

        return results;

    }

    /**
     *  用户id特征向量，形式类似：0321-uid:3f83b7efcbf543f8939189f4b60040c4
     *  <模型代号后四位>-<UID特征id>:<UID>
     */
    public double [] getUidVec(String prefix, String uid) {
        String key = prefix + "-" + "uid:" + uid + ":1";
        String value = historyCtrRedisClusterUtil.getValue(key);
        double [] vec = null;
        if (org.apache.commons.lang.StringUtils.isEmpty(value)) {
            return vec;
        }
        String [] arr = value.split(",");
        vec = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            vec[i] = Double.parseDouble(arr[i]);
        }
        return vec;
    }

    public List<FeatureContext> getFeatureEntitys(MixRequestInfo mixRequestInfo,List<RecallResult> itemResults) {

        TimerEntity timer = TimerEntityUtil.getInstance();

        UserModel userProfile = mixRequestInfo.getUserModel(); //获取用户画像数据 可复用mix-recall部分的usermodel by yx

        /**
         * 添加BasicContext
         */

        BasicContext basiccontext = fillBasicContext(mixRequestInfo);

        List<FeatureContext> featureEntitys = new ArrayList<FeatureContext>();


        /**
         * 处理itemx信息
         */

        List<RecallResult> items = itemResults;
        //redis key set， 取文章跳出信息

        Set<String> keys = new HashSet<>();
        String prefix = "outrate_";
        for (RecallResult item : items) {
            basiccontext.setReason(item.getChannels().get(0).getValue()); //TODO
            FeatureContext featureContext = new FeatureContext();
            featureContext.setUserProfile(userProfile);
            featureContext.setBasicContext(basiccontext);
            if (itemResults != null && itemResults.size() > 0) {
                Document document = item.getDocument();
                featureContext.setItemDocument(document);
                //尽可能使用画像填充item信息，不需要调用方传递额外字段
                if (document != null ) {
                    if (document.getSimId() != null) {
                        keys.add(prefix + document.getSimId());
                    }
                }
            }

            featureContext.setRecallResult(item);
            // 设置文章媒体评级
            String source = null;
            String doctype = null;
            if (featureContext.getItemDocument() != null) {
                Document headLineItemProfile = featureContext.getItemDocument();
                source = headLineItemProfile.getSource();
                doctype = headLineItemProfile.getDocType();
                featureContext.setItemMedianDuration(headLineItemProfile.getLast1d_avg_duration());

            }
            String mediaEvalLevel = MediaEvalLevelCacheManager.getInstance().getMediaEvalLevel(doctype, source);
            featureContext.setItemMediaEvalLevel(mediaEvalLevel);

            // 设置历史CTR平滑参数
            featureContext.setCtrSmoothParamsNew(CtrSmoothParamsManager.getNewInstance()); //根据T 和 时间区间获取 贝叶斯平滑参数对ctr进行平滑 保证ctr置信 by yx

            featureEntitys.add(featureContext);
        }

        return featureEntitys;
    }



    public BasicContext fillBasicContext(MixRequestInfo mixRequestInfo) {

        UserModel userModel = mixRequestInfo.getUserModel();
        if (userModel == null) {
            return null;
        }

        BasicContext basiccontext = new BasicContext();

        basiccontext.setUid(mixRequestInfo.getUid());
        basiccontext.setChannel(mixRequestInfo.getRecomChannel());
        basiccontext.setUa(userModel.getUmt());
        basiccontext.setMos(userModel.getUmos());


        basiccontext.setPublishid(mixRequestInfo.getPublishid());
        basiccontext.setNet(userModel.getNet());


        String loc = userModel.getLoc();
        String[] retarray = {"-","-","-"};
        if (StringUtils.isNotBlank(loc)) {
            String[] locInfo = loc.split("_");

            int size = locInfo.length;
            if (size < 4) {
                //处理没有国籍的情况,size 为1到3
                //上海市_上海市_奉贤区
                //上海市_上海市
                if (size > 0) {
                    retarray[0] = locInfo[0].split("\\$")[0];
                }
                if (size > 1) {
                    retarray[1] = locInfo[1].split("\\$")[0];
                }
                if (size > 2) {
                    retarray[2] = locInfo[2].split("\\$")[0];
                }
            } else {
                //处理带有国籍的情况：
                //中国_北京市_北京市_朝阳区
                retarray[0] = locInfo[1].split("\\$")[0];
                retarray[1] = locInfo[2].split("\\$")[0];
                retarray[2] = locInfo[3].split("\\$")[0];
            }
        }
        basiccontext.setLoc1(retarray[0]);
        basiccontext.setLoc2(retarray[1]);
        basiccontext.setLoc3(retarray[2]);

        return basiccontext;
    }





//    public Model getFMModel(String flagName){
//        HashMap<String, FMModelEntity> modelMap = new HashMap<>();
//        Set<String> keys_all = ctrModelRedisClusterUtil.smembers(CTRConstant.KEY_CTR_FMMODEL_TO_ALL);
//        String MODEL_NAME_NAME = ".model";
//        if (keys_all != null && keys_all.size() > 0) {
//            for (String key : keys_all) {
//                if (key.endsWith(MODEL_NAME_NAME)) {
//                    String flowtag = key.substring(0, key.length()-MODEL_NAME_NAME.length());
//                    FMModelEntity model = readModelByRedisKey(key);
//                    if (model != null) {
//                        modelMap.put(flowtag, model);
//                    }
//                } else {
//                    logger.error("FM模型redis数据加载失败，文件名不合法  name:{}", key);
//                }
//            }
//            return modelMap;
//        } else {
//            throw new Exception("FM模型 加载失败，keys_all is wrong!\tkeys_all: " + keys_all.toString());
//        }
//
//    }


}
