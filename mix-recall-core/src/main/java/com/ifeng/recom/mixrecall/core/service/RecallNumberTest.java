package com.ifeng.recom.mixrecall.core.service;

import com.ifeng.recom.mixrecall.common.model.RecordInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by geyl on 2017/11/9.
 */
public class RecallNumberTest {
    private int recallNumber = 500;
    private double sumWeight;
    private List<RecordInfo> list;
    private Map<String, Double> tagWithWeightMap=new HashMap<>();

    public RecallNumberTest(List<RecordInfo> list, int recallNumber) {
        this.list = list;
        this.recallNumber = recallNumber;
        init();
    }

    private void init() {
        int count = list.size();
        for (RecordInfo recordInfo : list) {
            double weight = Math.pow(count, 1.5);
            tagWithWeightMap.put(recordInfo.getRecordName(), weight);
            sumWeight += weight;
            count--;
        }
    }

    public int getRecallNumber(String tagName) {
        try {
            double s = (tagWithWeightMap.get(tagName)) / sumWeight;  //归一化后的tag权重
            return (int) (Math.round(s * recallNumber));
        } catch (Exception e) {
            return 10;
        }
    }


}
