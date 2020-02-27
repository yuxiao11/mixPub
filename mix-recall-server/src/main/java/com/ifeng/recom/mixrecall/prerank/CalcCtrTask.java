package com.ifeng.recom.mixrecall.prerank;


import com.ifeng.recom.mixrecall.prerank.constant.CTRConstant;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import com.ifeng.recom.mixrecall.prerank.executor.FeatureVectorExtractor;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by jibin on 2017/5/23.
 */
public class CalcCtrTask implements Callable<Map<String, String>> {

    private static final Logger logger = LoggerFactory.getLogger(CalcCtrTask.class);

    /**
     * Item队列的起始下标
     */
    private int INDEX_START;

    /**
     * Item队列的结束下标
     */
    private int INDEX_END;

    /**
     * 模型
     */
    private Model MODEL;

    /**
     * 检索ID
     */
    private String SID;

    /**
     * 是否进行debug日志输出
     */
    private boolean isDebug;

    /**
     * feature实体
     */
    private List<FeatureContext> FEATURECONTEXTS;


    /**
     * ehcache 缓存 cacheManager
     */
    private CacheManager cacheManager;

    /**
     * 用户ID特征向量
     */
    private double [] uidVec;

    /**
     * 内容ID特征向量
     */
    private Map<String, double[]> itemVecs;


    /**
     * 线程计算 指定区域的item 预估点击率
     */
    public CalcCtrTask(int start, int end, Model model, List<FeatureContext> featureContexts,  CacheManager cacheManager, double [] uidVec) {
        INDEX_START = start;
        INDEX_END = end;
        MODEL = model;
        FEATURECONTEXTS = featureContexts;
        this.cacheManager = cacheManager;
        this.uidVec = uidVec;
    }

    /**
     * 计算单个item的预估点击率
     *
     * @return 返回通过模型计算的ctr预估值
     */
    private double getCtr(FeatureContext entity,boolean isDebug) {
        double result = MODEL.getDefaultValue();
        try {

            List<FeatureItem> featureValues = FeatureVectorExtractor.extractor(entity, CTRConstant.Feature_Version_Name, "FM");
            List<FeatureItem> featureItemList = new ArrayList<>();
            for(FeatureItem featureItem: featureValues){
                if(!featureItem.getValueStr().equals("none")) {
                    featureItemList.add(featureItem);
                }
            }
            if (null == featureItemList) {
                return MODEL.getDefaultValue();
            }
            if(Math.random() <= 0.01){
                logger.info(listToString(featureItemList));
            }

            result = MODEL.calculate(uidVec, featureItemList);

        } catch (Exception e) {
            logger.error("getCtr ERROR:{}", e);
        }


        return result;
    }


    @Override
    public Map<String, String> call() {
        Map<String, String> result = new HashMap<String, String>(INDEX_END - INDEX_START);
        FeatureContext featureContext = null;
        for (int i = INDEX_START; i < INDEX_END; i++) {
            try {
                featureContext = FEATURECONTEXTS.get(i);
                double ctr = getCtr(featureContext,this.isDebug);
                String ctrStr = String.format(CTRConstant.Ctr_Format, ctr);
//                featureContext.getRecallResult().setCtr(Double.parseDouble(ctrStr));
                result.put(featureContext.getItemDocument().getId(), ctrStr);
            } catch (Exception e) {
                logger.error("CTR预估发生错误: " + e.getMessage(), e);
            }
        }
        return result;
    }

//    /**
//     * 添加adNature到 debug缓存
//     * @param adNature
//     */
//    private void addResultToDebugCache(AdNatureEntity adNature){
//        Cache debugCache = cacheManager.getCache(CTRConstant.CacheName.UIDANDDOCID_RESULT_CACHE.getValue());
//        String cacheKey = CalcCtrTask.buildDebugCacheKey(CTX.getTrackId(),adNature.getItemId());
//        DebugCacheBean debugCacheBean = new DebugCacheBean(IPUtil.getLinuxLocalIp(),CTX.getTrackId(),adNature);
//        Element element = new Element(cacheKey,debugCacheBean);
//        debugCache.put(element);
//    }

    /**
     * build debug cache key by uid and docId
     * @param uid
     * @param docId
     * @return
     */
    public static String buildDebugCacheKey(String uid,String docId){
        StringBuilder sb = new StringBuilder().append(uid).append(CTRConstant.Symb_PoundSign).append(docId);
        return sb.toString();
    }

    /**
     * *将list转换为字符串，方便打印
     * @param list
     * @param <T>
     * @return
     */
    public static <T> String listToString(List<T> list) {
        StringBuilder sb = new StringBuilder();
        for (T item : list) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(item.toString());
        }
        return sb.toString();
    }



}
