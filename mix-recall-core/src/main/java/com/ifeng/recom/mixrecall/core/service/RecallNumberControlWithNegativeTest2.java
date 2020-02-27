package com.ifeng.recom.mixrecall.core.service;

import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by geyl on 2017/11/9.
 */
public class RecallNumberControlWithNegativeTest2 {
    private static final Logger logger = LoggerFactory.getLogger(RecallNumberControlWithNegativeTest2.class);

    private int recallNumber = 500;
    private double sumWeight;
    private List<RecordInfo> list;
    private Map<String,RecallCate> recallcateMap;
    private Map<String,Double> testParas;
    private MixRequestInfo mixRequestInfo;
    private Map<String, Double> tagWeightMap;
    private List<String> tagFilterList;




    public RecallNumberControlWithNegativeTest2(MixRequestInfo mixRequestInfo, List<RecordInfo> list, Map<String, Double> tagWeightMap, int recallNumber) {
        this.mixRequestInfo = mixRequestInfo;
        this.list = list;
        this.recallNumber = recallNumber;
        this.tagWeightMap = tagWeightMap;
        init();
    }


    private void init() {
        recallcateMap = new HashMap<String,RecallCate>();
        tagFilterList = new ArrayList<>();

        for (RecordInfo recordInfo : list) {
            RecallCate recallcate = null;
            String recordName = recordInfo.getRecordName();
            String cate = recordName.split("-")[0];
            int recordEv = recordInfo.getExpose();
            int recordPv = recordInfo.getReadFrequency();
            if(recordPv == 1 && recordPv/recordEv < 0.333){
                tagFilterList.add(recordName);
                continue;
            }

            double tag_weight = recordInfo.getWeight();

            if(recallcateMap.containsKey(cate)){
                recallcate = recallcateMap.get(cate);
                List<String>  recalltags = recallcate.getRecalltags();
                List<Double>  weights = recallcate.getWeights();
                List<Integer> pvs = recallcate.getPvs();
                int pv = recallcate.getC();
                int ev = recallcate.getE();
                recalltags.add(recordName);
                weights.add(tag_weight);
                pvs.add(pv);
                ev += recordEv;
                pv += recordPv;
                recallcate.setC(pv);
                recallcate.setE(ev);
                recallcate.setPvs(pvs);
            }else if((double)recordPv / recordEv > 0.25 || recordPv > 2){
                recallcate  = new RecallCate();
                List<String> recalltags = new ArrayList<>();
                List<Double> weights = new ArrayList<>();
                List<Integer> pvs = new ArrayList<>();
                recalltags.add(recordName);
                weights.add(tag_weight);
                pvs.add(recordPv);
                recallcate.setRecalltags(recalltags);
                recallcate.setWeights(weights);
                recallcate.setPvs(pvs);
                recallcate.setC(recordPv);
                recallcate.setE(recordEv);
                recallcateMap.put(cate,recallcate);

            }
        }



        Map<String,Double> negativeMap = mixRequestInfo.getNegativeMap().getOrDefault("cotag",new HashMap<>());
        Iterator<Map.Entry<String,RecallCate>> iterator = recallcateMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,RecallCate> entry = iterator.next();
            String key = entry.getKey();
            RecallCate recallCate = entry.getValue();
            int num = recallCate.getRecalltags().size();
            int oldNum = recallCate.getRecalltags().size();
            int pv = recallCate.getC();
            List<String> tagBefore = recallCate.getRecalltags();

            if(negativeMap.containsKey(key)){
                recallCate.setRecalltags(recallCate.getRecalltags().subList(0,(int)(negativeMap.get(key)*num)));
                num = recallCate.getRecalltags().size();
                if(num == 0){
                    iterator.remove();
                    continue;
                }
                pv = recallCate.getPvs().subList(0,num).stream().mapToInt(Integer::intValue).sum();
                logger.info("TagSize After Filter uid:{} before:{} after:{} tagBefore:{} tagAfter:{}",mixRequestInfo.getUid(),oldNum,num,tagBefore,recallCate.getRecalltags());
            }

            Double weight1 = recallCate.getWeights().get(0);
            int ev = recallCate.getE();

            /**
             * 经过测试只选用Test1的召回公式进行cotag召回 log(0.55+weight1) * log(50+num) * log(5+ pv/num)
             */
            Double score = Math.log(weight1 + 0.51) * Math.log(num + 50) * Math.log(1.0 * pv/num + 5);


            /**
             * 以下公式用来进行ABTest 观察test 和Base1实验组数据的变化用
             */
//            score = Math.log10(weight1 + 0.5) * Math.log10(num + 0.5) * Math.log10(1.0 * pv/num + 0.1) * Math.log10(1.0*pv/ev + 2);
//            logger.info("check info uid:{} weight1:{} num:{} pv:{} ev:{} step1:{} step2:{} step3:{} step4:{} score:{}",mixRequestInfo.getUid(),weight1,num,pv,ev,Math.log10(weight1 + 0.55), Math.log10(num + 1)
//                    ,Math.log10(1.0 * pv/num +2), Math.log10(1.0*pv/ev + 2),score);


            recallCate.setScore(score);
            this.sumWeight += score;
        }
//        for(Map.Entry<String,RecallCate> item : recallcateMap.entrySet()){
//            logger.info("checkInfo uid:{} detail:{}____{}_{}_{}",mixRequestInfo.getUid(),item.getKey(),item.getValue().getRecalltags());
//
//        }
    }

    public int getRecallNumberWithNegative(String tagName) {
        try {
            if(tagName.split("-").length != 2){
                return 0;
            }
            String cateName = tagName.split("-")[0];
            String featureName = tagName.split("-")[1];
            if(!recallcateMap.keySet().contains(cateName) || tagFilterList.contains(tagName)){
                return 0;
            }
            RecallCate recallCate = recallcateMap.get(cateName);

            double s = (recallCate.getScore()) / sumWeight;  //归一化后的tag权重

            double ratio = 1.0;

            if(mixRequestInfo.getNegativeMap().getOrDefault("cotag_posFeed",new HashMap<>()).containsKey(cateName)){
                ratio = mixRequestInfo.getNegativeMap().get("cotag_posFeed").get(cateName);
            }
            if(mixRequestInfo.getNegativeMap().getOrDefault("featureWord_ratio",new HashMap<>()).containsKey(featureName)){
                ratio = mixRequestInfo.getNegativeMap().get("featureWord_ratio").get(featureName);
//                logger.info("check feature:{},{},{} score:{} number:{} size:{}",mixRequestInfo.getUid(),featureName,ratio,s,recallNumber,recallCate.recalltags.size());
            }

            int recallNum = (int)(Math.round(Math.ceil(s * recallNumber / recallCate.recalltags.size()) * ratio));
            int oldrecallNum = (int)(Math.ceil(s * recallNumber / recallCate.recalltags.size()));
            if(recallNum > oldrecallNum){
                logger.info("ratio num uid:{} cate:{} tag:{} oldRecallNum:{} recallNum:{}",mixRequestInfo.getUid(),cateName,featureName,oldrecallNum,recallNum);

            }
            if(recallNum < oldrecallNum){
                logger.info("ratio lower num uid:{} cate:{} tag:{} oldRecallNum:{} recallNum:{}",mixRequestInfo.getUid(),cateName,featureName,oldrecallNum,recallNum);

            }



            return recallNum;

        } catch (Exception e) {
            logger.error("Get inner recall[Negative] Number  Error:", e);
            return 2;
        }
    }


    /**
     * 按大类的召回tags
     */
    public static class RecallCate{
        private List<String> recalltags;
        private List<Double> weights;
        private List<Integer> pvs;
        private int e;
        private int c;

        private double score;

        public List<String> getRecalltags() {
            return recalltags;
        }

        public void setRecalltags(List<String> recalltags) {
            this.recalltags = recalltags;
        }

        public List<Double> getWeights() {
            return weights;
        }

        public void setWeights(List<Double> weights) {
            this.weights = weights;
        }

        public List<Integer> getPvs() {
            return pvs;
        }

        public void setPvs(List<Integer> pvs) {
            this.pvs = pvs;
        }

        public int getE() {
            return e;
        }

        public void setE(int e) {
            this.e = e;
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }


}
