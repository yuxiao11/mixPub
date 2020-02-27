package com.ifeng.recom.mixrecall.core.service;

import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by geyl on 2017/11/9.
 */
public class RecallNumberControlWithNegativeBase {
    private static final Logger logger = LoggerFactory.getLogger(RecallNumberControlWithNegativeBase.class);

    private int recallNumber = 500;
    private double sumWeight;
    private List<RecordInfo> list;
    private Map<String,RecallCate> recallcateMap;
    private Map<String,Double> testParas;
    private MixRequestInfo mixRequestInfo;
    private Map<String, Double> tagWeightMap;




    public RecallNumberControlWithNegativeBase(MixRequestInfo mixRequestInfo, List<RecordInfo> list, Map<String, Double> tagWeightMap, int recallNumber) {
        this.mixRequestInfo = mixRequestInfo;
        this.list = list;
        this.recallNumber = recallNumber;
        this.tagWeightMap = tagWeightMap;
        init();
    }


    private void init() {
        recallcateMap = new HashMap<String,RecallCate>();

        for (RecordInfo recordInfo : list) {
            RecallCate recallcate = null;
            String recordName = recordInfo.getRecordName();
            String cate = recordName.split("-")[0];
            int recordEv = recordInfo.getExpose();
            int recordPv = recordInfo.getReadFrequency();

            double tag_weight = recordInfo.getWeight();

            if(recallcateMap.containsKey(cate)){
                recallcate = recallcateMap.get(cate);
                List<String>  recalltags = recallcate.getRecalltags();
                List<Double>  weights = recallcate.getWeights();
                int pv = recallcate.getC();
                int ev = recallcate.getE();
                recalltags.add(recordName);
                weights.add(tag_weight);
                ev += recordEv;
                pv += recordPv;
                recallcate.setC(pv);
                recallcate.setE(ev);
            }else{
                if((double)recordPv / recordEv > 0.25 || recordPv > 4){
                    recallcate  = new RecallCate();
                    List<String> recalltags = new ArrayList<>();
                    List<Double> weights = new ArrayList<>();
                    recalltags.add(recordName);
                    weights.add(tag_weight);
                    recallcate.setRecalltags(recalltags);
                    recallcate.setWeights(weights);
                    recallcate.setC(recordPv);
                    recallcate.setE(recordEv);
                    recallcateMap.put(cate,recallcate);
                }
            }
        }


        for(Map.Entry<String, RecallCate> entry : recallcateMap.entrySet()){
            String key = entry.getKey();
            RecallCate recallCate = entry.getValue();
            Double weight1 = recallCate.getWeights().get(0);
            int num = recallCate.getRecalltags().size();
            int pv = recallCate.getC();
            int ev = recallCate.getE();
//            Double score = 0.01;
            /**
             * 经过测试只选用Test1的召回公式进行cotag召回 log(0.55+weight1) * log(1+num) * log(5+ pv/num)
             */
            Double score = Math.log(weight1 + 0.55) * Math.log(num + 50) * Math.log(1.0 * pv/num + 5);

            recallCate.setScore(score);
            this.sumWeight += score;
        }

    }

    public int getRecallNumberWithNegative(String tagName) {

        try {

            String cateName = tagName.split("-")[0];
            if(!recallcateMap.keySet().contains(cateName)){
                return 0;
            }
            RecallCate recallCate = recallcateMap.get(cateName);


            double s = (recallCate.getScore()) / sumWeight;  //归一化后的tag权重

            int recallNum = (int)(Math.ceil(s * recallNumber * mixRequestInfo.getNegativeMap().getOrDefault("cotag",new HashMap<>()).getOrDefault(cateName,1.0) / recallCate.recalltags.size()));

            return recallNum;

        } catch (Exception e) {
            return 2;
        }
    }


    /**
     * 按大类的召回tags
     */
    public static class RecallCate{
        private List<String> recalltags;
        private List<Double> weights;
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
