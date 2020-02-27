package com.ifeng.recom.mixrecall.prerank.featureV1030;

import com.alibaba.fastjson.JSONObject;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.FeatureWord;
import com.ifeng.recom.mixrecall.prerank.FeatureItem;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import com.ifeng.recom.mixrecall.prerank.executor.Operator;
import com.ifeng.recom.mixrecall.prerank.tools.CtrSmoothParamsNew;
import com.ifeng.recom.mixrecall.prerank.tools.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.util.*;

public class FMItemPortraitOperator extends Operator {
    private static final long serialVersionUID = 5793391748460484896L;
    private static final Log logger = LogFactory.getLog(FMItemPortraitOperator.class);

    @Override
    public List<FeatureItem> compute(FeatureContext entity, Long featureId, String featureName, int type, String attrDimension) throws Exception {

        List<FeatureItem> featureItems = new ArrayList<FeatureItem>();
        // 文章t1特征词(特征词得分>0.5)
        addFeatureItems(featureItems, getItemFeaturesOfType("c", entity, "c"));
        // 文章t2特征词(特征词得分>0.5)
        addFeatureItems(featureItems, getItemFeaturesOfType("sc", entity, "sc"));
        // 文章LDA topic
        addFeatureItems(featureItems, getLdaTopicFeatures("ldaTopic", entity));
        // 文章稿源
        addSingleFeatureItem(featureItems, getItemSourceFeature("source", entity));

        CtrSmoothParamsNew ctrSmoothParams = entity.getCtrSmoothParamsNew();
        // 过去3h点击率
        addSingleFeatureItem(featureItems, getLast3hCtrFeature("last3hCTR", entity, ctrSmoothParams));
        // 过去1天点击率
        addSingleFeatureItem(featureItems, getLast1dCtrFeature("last1dCTR", entity, ctrSmoothParams));
        // today点击率
        addSingleFeatureItem(featureItems, getTodayCtrFeature("todayCTR", entity, ctrSmoothParams));
        // 总点击率
        addSingleFeatureItem(featureItems, getTotalCtrFeature("totalCTR", entity, ctrSmoothParams));

        // 媒体评级
        addSingleFeatureItem(featureItems, getSourceEvalLevelFeature("mediaLevel", entity));
        // 质量评分
        addSingleFeatureItem(featureItems, getItemQualityFeature("qualityLevel", entity));
        // 文章类型特征
        addSingleFeatureItem(featureItems, getDoctypeFeature("docType", entity));
        // 文章阅读时间平均数
        addSingleFeatureItem(featureItems, getItemAvgDurationFeature("avgduration", entity));
        // 文章cotag
        addFeatureItems(featureItems, getItemCotagFeatures("cotags", entity));
        // 文章id
        addSingleFeatureItem(featureItems, getSimid("simId", entity));
        // 标题实体词
        addFeatureItems(featureItems, getTitleFeature("entityInTitle", entity));
        // 文章长度
        addSingleFeatureItem(featureItems, getNewsLenFeature("length", entity));
        // 标题中是否有？号
        addSingleFeatureItem(featureItems, getTitleQuestionFeature("questionInTitle", entity));
        // 文章的时效性等级
        addSingleFeatureItem(featureItems, getTimeSensitiveLevelFeature("timeSensitiveLevel", entity));

        // 文章是否为精品池
        addSingleFeatureItem(featureItems, getDisTypeFeature("isJPpool", entity));
        // 文章是否为标题党
        addSingleFeatureItem(featureItems, getClickBaitFeature("isClickBait", entity));
        // TODO 文章跳出率暂时不用
//        addSingleFeatureItem(featureItems, getOutRateFeature("outrate", entity));
        // 视频清晰度
        addSingleFeatureItem(featureItems, getClarityFeature("clarity", entity));

        //图像对比度/清晰度 - sharpness
        addSingleFeatureItem(featureItems, getSharpnessFeature("sharpness", entity));

        //图像亮度 - constract_rms
        addSingleFeatureItem(featureItems, getContrastFeature("contract", entity));

        //图像色彩丰富度 - colorfulness
        addSingleFeatureItem(featureItems, getColorfulnessFeature("colorfulness", entity));

        //图像画面的自然程度 - naturalness
        addSingleFeatureItem(featureItems, getNaturalnessFeature("naturalness", entity));


        return featureItems;
    }

    private void addFeatureItems(List<FeatureItem> featureItems, List<FeatureItem> subList) {
        if (subList != null && subList.size() > 0) {
            featureItems.addAll(subList);
        }
    }

    private void addSingleFeatureItem(List<FeatureItem> featureItems, FeatureItem featureItem) {
        if (featureItem != null) {
            featureItems.add(featureItem);
        }
    }


    /**
     * 解析文章cotag
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private List<FeatureItem> getItemCotagFeatures(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        Set<String> cotags = headLineItemProfile.getcoTagSet();
        if (cotags == null || cotags.size() == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 3);
        }
        List<FeatureItem> result = new ArrayList<>();
        for (String cotag : cotags) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(3);
            featureItem.setValueStr(cotag.replace("\n", "").trim());
            result.add(featureItem);
        }
        return result;
    }

    /**
     * 获取Id特征
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getSimid(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(3);
        String simid = headLineItemProfile.getSimId();
        featureItem.setValueStr(simid);
        return featureItem;
    }


    /**
     * 构造lda topic特征
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private List<FeatureItem> getLdaTopicFeatures(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        List<String> ldaTopicList = headLineItemProfile.getLdaTopicList();
        if (ldaTopicList == null || ldaTopicList.size() == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 3);
        }
        List<FeatureItem> result = new ArrayList<>();
        for (String ldaTopic : ldaTopicList) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(3);
            featureItem.setValueStr(ldaTopic);
            result.add(featureItem);
        }
        return result;
    }


    /**
     * 获取文章的阅读时间中位数并离散化
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getItemAvgDurationFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        double medianDuration = headLineItemProfile.getLast1d_avg_duration();
        if (medianDuration < 0) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        int durationInterval = 5;        // 按照5秒划分时间区间
        if (medianDuration >= 300) {    // 大于5分钟的点击认为没有区别
            medianDuration = 300;
        }
        int durationLevel;
        if (medianDuration == 0) {
            durationLevel = 0;
        } else {
            durationLevel = ((int) medianDuration) / durationInterval + 1;
        }

        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(3);
        featureItem.setValueStr(String.valueOf(durationLevel));
        return featureItem;
    }


    /**
     * 获取文章特征词
     *
     * @param featureName
     * @param types       特征词类型
     * @return
     */
    private List<FeatureItem> getItemFeaturesOfType(String featureName, FeatureContext featureContext, String... types) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        Map<String, List<FeatureWord>> alFeatureTypeMap = headLineItemProfile.getAlFeaturesTypeMap();
        if (types.length == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 3);
        }
        if (alFeatureTypeMap == null) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 3);
        }
        List<FeatureItem> subList = new ArrayList<>();
        for (String type : types) {
            List<FeatureWord> tmpFeatureWords = alFeatureTypeMap.get(type);
            if (tmpFeatureWords == null || tmpFeatureWords.size() == 0) {
                continue;
            }
            for (FeatureWord featureWord : tmpFeatureWords) {
                // 权重小于0.5的不考虑
                if (featureWord == null || featureWord.word == null || Math.abs(featureWord.weight) <= 0.5) {
                    continue;
                }

                FeatureItem featureItem = new FeatureItem();
                featureItem.setFeatureName(featureName);
                featureItem.setFeatureType(3);
                featureItem.setValueStr(featureWord.word.replace("\t", "").trim().toLowerCase());
                subList.add(featureItem);
            }
        }
        return subList;
    }

    /**
     * 获取稿源特征
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getItemSourceFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String source = headLineItemProfile.getSource();
        if (StringUtils.isNullString(source)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }

        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(3);
        featureItem.setValueStr(source.replace("\t", "").trim());
        return featureItem;
    }

    /**
     * 获取平滑后的最近三小时CTR
     *
     * @param featureName
     * @param featureContext
     * @param ctrSmoothParams
     * @return
     */
    private FeatureItem getLast3hCtrFeature(String featureName, FeatureContext featureContext, CtrSmoothParamsNew ctrSmoothParams) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String last3h_ev = headLineItemProfile.getLast3h_ev();
        String last3h_pv = headLineItemProfile.getLast3h_pv();
        if (StringUtils.isNumeric(last3h_ev) && StringUtils.isNumeric(last3h_pv)) {
            double ev = Double.parseDouble(last3h_ev);
            double pv = Double.parseDouble(last3h_pv);
            if (ev < 0 || pv < 0) {
                return FeatureExtractUtils.getNoneFeature(featureName, 3);
            }

            // 查找对应的平滑参数
            List<FeatureWord> topCategories = null;
            if (headLineItemProfile.getAlFeaturesTypeMap() != null) {
                topCategories = headLineItemProfile.getAlFeaturesTypeMap().get("c");
            }
            double[] smoothParams = ctrSmoothParams.getLast3hAlphaAndBeta("OverAll");
            if (topCategories != null && topCategories.size() > 0) {
                smoothParams = ctrSmoothParams.getLast3hAlphaAndBeta(topCategories.get(0).word);
            }

            return getCtrFeatureItem(featureName, ev, pv, smoothParams);
        } else {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
    }

    /**
     * 获取平滑后的最近一天CTR
     *
     * @param featureName
     * @param featureContext
     * @param ctrSmoothParams
     * @return
     */
    private FeatureItem getLast1dCtrFeature(String featureName, FeatureContext featureContext, CtrSmoothParamsNew ctrSmoothParams) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String last1d_ev = headLineItemProfile.getLast1d_ev();
        String last1d_pv = headLineItemProfile.getLast1d_pv();
        if (StringUtils.isNumeric(last1d_ev) && StringUtils.isNumeric(last1d_pv)) {
            double ev = Double.parseDouble(last1d_ev);
            double pv = Double.parseDouble(last1d_pv);
            if (ev < 0 || pv < 0) {
                return FeatureExtractUtils.getNoneFeature(featureName, 3);
            }

            // 查找对应的平滑参数
            List<FeatureWord> topCategories = null;
            if (headLineItemProfile.getAlFeaturesTypeMap() != null) {
                topCategories = headLineItemProfile.getAlFeaturesTypeMap().get("c");
            }
            double[] smoothParams = ctrSmoothParams.getLast1dAlphaAndBeta("OverAll");
            if (topCategories != null && topCategories.size() > 0) {
                smoothParams = ctrSmoothParams.getLast1dAlphaAndBeta(topCategories.get(0).word);
            }

            return getCtrFeatureItem(featureName, ev, pv, smoothParams);
        } else {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
    }

    /**
     * 获取平滑后的当日CTR
     *
     * @param featureName
     * @param featureContext
     * @param ctrSmoothParams
     * @return
     */
    private FeatureItem getTodayCtrFeature(String featureName, FeatureContext featureContext, CtrSmoothParamsNew ctrSmoothParams) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String today_ev = headLineItemProfile.getToday_ev();
        String today_pv = headLineItemProfile.getToday_pv();
        if (StringUtils.isNumeric(today_ev) && StringUtils.isNumeric(today_pv)) {
            double ev = Double.parseDouble(today_ev);
            double pv = Double.parseDouble(today_pv);
            if (ev < 0 || pv < 0) {
                return FeatureExtractUtils.getNoneFeature(featureName, 3);
            }

            // 查找对应的平滑参数
            List<FeatureWord> topCategories = null;
            if (headLineItemProfile.getAlFeaturesTypeMap() != null) {
                topCategories = headLineItemProfile.getAlFeaturesTypeMap().get("c");
            }
            double[] smoothParams = ctrSmoothParams.getLast1dAlphaAndBeta("OverAll");
            if (topCategories != null && topCategories.size() > 0) {
                smoothParams = ctrSmoothParams.getLast1dAlphaAndBeta(topCategories.get(0).word);
            }

            return getCtrFeatureItem(featureName, ev, pv, smoothParams);
        } else {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
    }

    /**
     * 获取平滑后总CTR
     *
     * @param featureName
     * @param featureContext
     * @param ctrSmoothParams
     * @return
     */
    private FeatureItem getTotalCtrFeature(String featureName, FeatureContext featureContext, CtrSmoothParamsNew ctrSmoothParams) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String total_ev = headLineItemProfile.getTotal_ev();
        String total_pv = headLineItemProfile.getTotal_pv();
        if (StringUtils.isNumeric(total_ev) && StringUtils.isNumeric(total_pv)) {
            double ev = Double.parseDouble(total_ev);
            double pv = Double.parseDouble(total_pv);
            if (ev < 0 || pv < 0) {
                return FeatureExtractUtils.getNoneFeature(featureName, 3);
            }

            // 查找对应的平滑参数
            List<FeatureWord> topCategories = null;
            if (headLineItemProfile.getAlFeaturesTypeMap() != null) {
                topCategories = headLineItemProfile.getAlFeaturesTypeMap().get("c");
            }
            double[] smoothParams = ctrSmoothParams.getLast1dAlphaAndBeta("OverAll");
            if (topCategories != null && topCategories.size() > 0) {
                smoothParams = ctrSmoothParams.getLast1dAlphaAndBeta(topCategories.get(0).word);
            }

            return getCtrFeatureItem(featureName, ev, pv, smoothParams);
        } else {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
    }

    private FeatureItem getCtrFeatureItem(String featureName, double ev, double pv, double[] smoothParams) {
        double alpha = smoothParams[0];
        double beta = smoothParams[1];
        // 平滑后对CTR作离散化
        double smoothCtr = (pv + alpha) / (ev + alpha + beta);
        int ctrIndex = getCtrIndex(smoothCtr);
        if (ctrIndex > 100) {
            return null;
        }

        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(3);
        featureItem.setValueStr(String.valueOf(ctrIndex));

        return featureItem;
    }

    /**
     * 暂时先定为0~1分为100份
     * 看有没有太高的ctr因为训练样本不足得不到充分训练的问题
     *
     * @param ctr
     * @return
     */
    private int getCtrIndex(double ctr) {
        double step = 0.01;
        return (int) (ctr / step);
    }

    /**
     * 文章跳出率特征
     *
     * @param featureName
     * @param featureContext
     * @return
     */
//    private FeatureItem getOutRateFeature(String featureName, FeatureContext featureContext) {
//        if (!hasItemprofile(featureContext)) {
//            return FeatureExtractUtils.getNoneFeature(featureName, 3);
//        }
//        Document headLineItemProfile = featureContext.getItemDocument();
//        String infos = headLineItemProfile.getOutinfos();
//        if (infos != null) {
//            String[] tmp = infos.split(",");
//            double out = Double.parseDouble(tmp[0]);
//            double ev = Double.parseDouble(tmp[1]);
//            if (ev > 200) {
//                double outrate = out / ev;
//                int index = getCtrIndex(outrate);
//                FeatureItem featureItem = new FeatureItem();
//                featureItem.setFeatureName(featureName);
//                featureItem.setFeatureType(3);
//                featureItem.setValueStr(String.valueOf(index));
//                return featureItem;
//            }
//        }
//        return FeatureExtractUtils.getNoneFeature(featureName, 3);
//    }

    /**
     * 媒体评级：S A B C D E
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getSourceEvalLevelFeature(String featureName, FeatureContext featureContext) {
        String sourceEvalLevel = featureContext.getItemMediaEvalLevel();
        if (sourceEvalLevel == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        int evalCode;
        if (sourceEvalLevel.equals("S")) {
            evalCode = 0;
        } else if (sourceEvalLevel.equals("A")) {
            evalCode = 1;
        } else if (sourceEvalLevel.equals("B")) {
            evalCode = 2;
        } else if (sourceEvalLevel.equals("C")) {
            evalCode = 3;
        } else if (sourceEvalLevel.equals("D")) {
            evalCode = 4;
        } else if (sourceEvalLevel.equals("E")) {
            evalCode = 5;
        } else {
            evalCode = -1;
        }

        if (evalCode >= 0) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(3);
            featureItem.setValueStr(String.valueOf(evalCode));
            return featureItem;
        } else {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
    }

    /**
     * 获取文章质量评级特征：对质量得分离散化
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getItemQualityFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String qualityEvalLevel = headLineItemProfile.getQualityEvalLevel();
        if (StringUtils.isNullString(qualityEvalLevel)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        double quality = Double.parseDouble(qualityEvalLevel);
        int qualityCode;
        if (quality < 0) {
            qualityCode = 0;
        } else {
            double step = 0.5;
            qualityCode = ((int) (quality / step)) + 1;
        }
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(3);
        featureItem.setValueStr(String.valueOf(qualityCode));
        return featureItem;
    }

    /**
     * 文章类型 video, doc, docpic,	slide, topic, live,
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getDoctypeFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String doctype = headLineItemProfile.getDocType();
        if (StringUtils.isNullString(doctype)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        int typeCode = -1;
        if (doctype.equals("video")) {
            typeCode = 0;
        } else if (doctype.equals("doc")) {
            typeCode = 1;
        } else if (doctype.equals("docpic")) {
            typeCode = 2;
        } else if (doctype.equals("slide")) {
            typeCode = 3;
        } else if (doctype.equals("topic")) {
            typeCode = 4;
        } else if (doctype.equals("live")) {
            typeCode = 5;
        }
        if (typeCode >= 0) {
            FeatureItem featureItem = new FeatureItem();
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(3);
            featureItem.setValueStr(String.valueOf(typeCode));

            return featureItem;
        }
        return FeatureExtractUtils.getNoneFeature(featureName, 3);
    }

    /**
     * 标题实体词
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private List<FeatureItem> getTitleFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String splitTitle = headLineItemProfile.getSplitTitle();
        if (splitTitle == null || StringUtils.isNullString(splitTitle)) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 3);
        }
        String[] words = splitTitle.split(" ");
        ArrayList<String> posList = new ArrayList<>();
        posList.add("nr");
        posList.add("nz");
        posList.add("x");
        ArrayList<FeatureItem> subList = new ArrayList<>();
        Set<String> featureWords = new HashSet<>();
        if (words == null || words.length == 0) {
            return FeatureExtractUtils.getNoneFeatures(featureName, 3);
        }
        for (String word : words) {
            String[] tmp = word.split("_");
            if (tmp.length == 2) {
                String w = tmp[0];
                String pos = tmp[1];
                if (posList.contains(pos)) {
                    String v = w.replace("\t", "").trim().toLowerCase();
                    if (!featureWords.contains(v)) {
                        FeatureItem featureItem = new FeatureItem();
                        featureItem.setFeatureName(featureName);
                        featureItem.setFeatureType(3);
                        featureItem.setValueStr(v);
                        subList.add(featureItem);
                        featureWords.add(v);
                    }
                }
            }

        }
        return subList;
    }


    /**
     * 文章长度特征
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getNewsLenFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        int newslenlevel = headLineItemProfile.getNewslenlevel();
        if (newslenlevel < 1) {
            newslenlevel = 1;
        }
        if (newslenlevel > 5) {
            newslenlevel = 5;
        }
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(3);
        featureItem.setValueStr(String.valueOf(newslenlevel));
        return featureItem;
    }


    /**
     * 标题中是否有问号
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getTitleQuestionFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String title = headLineItemProfile.getTitle();
        if (StringUtils.isNullString(title)) {
            return null;
        }
        String valueStr = "-1";
        if (title.contains("?") || title.contains("？")) {
            valueStr = "1";
        }
        FeatureItem featureItem = new FeatureItem();
        featureItem.setValueStr(valueStr);
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(3);
        return featureItem;
    }

    /**
     * 文章时效性特征
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getTimeSensitiveLevelFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        if (StringUtils.isNullString(headLineItemProfile.getTimeSensitiveLevel())) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(3);
        featureItem.setValueStr(headLineItemProfile.getTimeSensitiveLevel());
        return featureItem;
    }

    /**
     * 是否为精品池文章
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getDisTypeFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String disType = headLineItemProfile.getDisType();
        String isJppool = "true";
        if (disType == null || !disType.equals("jppool")) {
            isJppool = "false";
        }
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(3);
        featureItem.setValueStr(isJppool);
        return featureItem;

    }

    /**
     * 标题党特征
     *
     * @param featureName
     * @param featureContext
     * @return
     */
    private FeatureItem getClickBaitFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        String clickBaits = "true";
        if (headLineItemProfile.getSpecialParam() == null || !headLineItemProfile.getSpecialParam().contains("标题党")) {
            clickBaits = "false";
        }
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFeatureName(featureName);
        featureItem.setFeatureType(3);
        featureItem.setValueStr(clickBaits);
        return featureItem;
    }


    private FeatureItem getClarityFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        if (headLineItemProfile.getSpecialParam() == null || !"video".equals(headLineItemProfile.getDocType())) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        String clarity = getVideoClarity(headLineItemProfile.getSpecialParam());
        if (clarity != null && StringUtils.isDouble(clarity)) {
            int value = (int) Double.parseDouble(clarity);
            FeatureItem featureItem = new FeatureItem();
            featureItem.setValueStr(String.valueOf(value));
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(3);
            return featureItem;
        }
        return FeatureExtractUtils.getNoneFeature(featureName, 3);
    }


    private String getVideoClarity(String specialParam) {
        try {
            JSONObject object = JSONObject.parseObject(specialParam);
            if (object.containsKey("videoLearningLevel")) {
                return object.getString("videoLearningLevel");
            } else {
                return null;
            }
        }catch (Exception e){
            return null;
        }

    }

    public boolean hasItemprofile(FeatureContext featureContext) {
        if (featureContext == null || featureContext.getItemDocument() == null ) {
            
            return false;
        }
        return true;
    }

    private FeatureItem getSharpnessFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        if (headLineItemProfile.getLowImgFeature() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        String sharpness = getLowImgFeature(headLineItemProfile, "sharpness");
        if (sharpness != null && StringUtils.isDouble(sharpness)) {
            int value = (int) (Double.parseDouble(sharpness) / 0.1);
            FeatureItem featureItem = new FeatureItem();
            featureItem.setValueStr(String.valueOf(value));
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(3);
            return featureItem;
        }
        return FeatureExtractUtils.getNoneFeature(featureName, 3);
    }

    private FeatureItem getContrastFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        if (headLineItemProfile.getLowImgFeature() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        String contrast = getLowImgFeature(headLineItemProfile, "contrast_rms");
        if (contrast != null && StringUtils.isDouble(contrast)) {
            int value = (int) (Double.parseDouble(contrast) / 0.1);
            FeatureItem featureItem = new FeatureItem();
            featureItem.setValueStr(String.valueOf(value));
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(3);
            return featureItem;
        }
        return FeatureExtractUtils.getNoneFeature(featureName, 3);
    }

    private FeatureItem getColorfulnessFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        if (headLineItemProfile.getLowImgFeature() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        String colorfulness = getLowImgFeature(headLineItemProfile, "colorfulness");
        if (colorfulness != null && StringUtils.isDouble(colorfulness)) {
            int value = (int) (Double.parseDouble(colorfulness) / 0.1);
            FeatureItem featureItem = new FeatureItem();
            featureItem.setValueStr(String.valueOf(value));
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(3);
            return featureItem;
        }
        return FeatureExtractUtils.getNoneFeature(featureName, 3);
    }

    private FeatureItem getNaturalnessFeature(String featureName, FeatureContext featureContext) {
        if (!hasItemprofile(featureContext)) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        Document headLineItemProfile = featureContext.getItemDocument();
        if (headLineItemProfile.getLowImgFeature() == null) {
            return FeatureExtractUtils.getNoneFeature(featureName, 3);
        }
        String colorfulness = getLowImgFeature(headLineItemProfile, "naturalness");
        if (colorfulness != null && StringUtils.isDouble(colorfulness)) {
            int value = (int) (Double.parseDouble(colorfulness) / 0.1);
            FeatureItem featureItem = new FeatureItem();
            featureItem.setValueStr(String.valueOf(value));
            featureItem.setFeatureName(featureName);
            featureItem.setFeatureType(3);
            return featureItem;
        }
        return FeatureExtractUtils.getNoneFeature(featureName, 3);
    }


    private String getLowImgFeature(Document headLineItemProfile, String text) {
        Map<String, String> lowFeatureMap = headLineItemProfile.getLowFeatureMap();
        if (lowFeatureMap == null || !lowFeatureMap.containsKey(text)) {
            return null;
        }
        return lowFeatureMap.get(text);
    }



}