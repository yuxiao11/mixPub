package com.ifeng.recom.mixrecall.core.service;

import com.google.common.collect.Lists;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;

import java.util.*;
import java.util.stream.Collectors;

import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.primitives.Doubles.max;

public class RecallNumber4Cotag {
    private int recallNumber = 600;
    private int recallNumberHead = 400;
//    private int recallNumberMiddle = 150;
//    private int recallNumberTail = 150;

    private double sumWeight;
    private double sumWeightHead;
    private double sumWeightMiddle;
    private double sumWeightTail;
    private double fullness = 0.0;
    private double cotagSum = 0.0001;

//    private double headThreshold;
//    private double middleThreshold;


    private int size;
    private List<RecordInfo> list;
    private List<RecordInfo> listSection;
    private List<RecordInfo> listHead;
    private List<RecordInfo> listMiddle;
    private List<RecordInfo> listTail;
    private List<RecordInfo> listNew;
    private List<String> headTags;
    private List<String> middleTags;
    private List<String> tailTags;
    private List<String> cateHeadTags;
//    private List<Double> scoreRank;
    private Map<String, Double> tagWithWeightMap;
    private Map<String, Integer> tagClickMap;
    private Map<String, Double> cateWeightMap;
    private Map<String, Double> numberMap;
    private Map<String,Integer> cateNumberMap;
    private LinkedHashMap<String,List<String>> cateMap;
    private MixRequestInfo mixRequestInfo;
    private List<String> totalTags;

    private static final Logger logger = LoggerFactory.getLogger(RecallNumber4Cotag.class);


    public RecallNumber4Cotag(MixRequestInfo mixRequestInfo,List<RecordInfo> list, Map<String, Double> tagWithWeightMap, Map<String, Integer> tagClickMap,int recallNumber, double fullness) {
        this.list = list;
        this.tagWithWeightMap = tagWithWeightMap;
        this.tagClickMap = tagClickMap;
        this.recallNumber = recallNumber;
        this.recallNumberHead = (int) Math.round((recallNumber * 1.0));
//        this.recallNumberMiddle = (int) Math.round((recallNumber) * 0.7);
//        this.recallNumberTail = (int) Math.round((recallNumber) * 0.4);
        this.size = list.size();
        this.fullness = fullness;
        this.mixRequestInfo=mixRequestInfo;
        list.sort(new RecordInfo.RecordInfoWeightComparator());
        initNewC();

    }

    /**
     * 新增实验方法： 在按照类别分组的基础上考虑冷启动用户
     */
    public RecallNumber4Cotag(MixRequestInfo mixRequestInfo,List<RecordInfo> list, Map<String, Double> tagWithWeightMap, Map<String, Integer> tagClickMap,int recallNumber, double fullnessNum,String tag) {
        this.list = list;
        this.tagWithWeightMap = tagWithWeightMap;
        this.tagClickMap = tagClickMap;
        this.recallNumber = recallNumber;
        this.recallNumberHead = (int) Math.round((recallNumber * 1.0));
        this.size = list.size();
        this.fullness = fullnessNum;
        this.mixRequestInfo=mixRequestInfo;
//        logger.info("uid:{}------------- oldClass step1",mixRequestInfo.getUid());

        try{
            if(fullness < 0.3 ){
                if(mixRequestInfo.getRecomChannel() == "videoapp"){
                    initNewC4VideoUser();
                }else{
                    initNewC();
                }
            }else{
                initNew();

            }
//            logger.info("uid:{}------------- oldClass step2",mixRequestInfo.getUid());
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void initNew() {
        /**
         * 若tag数量大于150对其进行randomPick
         */

        cateMap = new LinkedHashMap<>();
        cateNumberMap = new HashMap<>();
        totalTags = new ArrayList<>();
        numberMap = new HashMap<>();
//        logger.info("uid:{}------------- oldClass step3",mixRequestInfo.getUid());


        /**
         * 此处获取头部、中部、尾部三部分的标签 用于分配相应的召回数量 即头部>中部>尾部
         */
        headTags = list.subList(0,(int)((double)size/3*1)).stream().map(RecordInfo::getRecordName).collect(Collectors.toList());
        middleTags = list.subList((int)((double)size/3*1),(int)((double)size/3*2)).stream().map(RecordInfo::getRecordName).collect(Collectors.toList());
        tailTags = list.subList((int)((double)size/3*2),size).stream().map(RecordInfo::getRecordName).collect(Collectors.toList());


//        logger.info("uid:{}------------- oldClass step4",mixRequestInfo.getUid());


        try{
            for(RecordInfo item : list){
                if(item.getReadFrequency() > 2 || item.getReadFrequency()/item.getExpose() > 0.25){
                    String tagName = item.getRecordName().split("-")[0];
                    if(cateMap.keySet().contains(tagName)){
                        cateMap.get(tagName).add(item.getRecordName());
                    }else{
                        ArrayList<String> subCate = new ArrayList<String>();
                        subCate.add(item.getRecordName());
                        cateMap.put(tagName,subCate);
                    }
                }
            }
//            logger.info("uid:{}------------- oldClass step5",mixRequestInfo.getUid());


            cateHeadTags = cateMap.keySet().stream().collect(Collectors.toList()).subList(0,(int)(cateMap.size()/2));
        }catch (Exception e){
            e.printStackTrace();
        }


        int recallNumAfterFilter = 0;

        /**
         * 这里 numberMap<大类名称，该大类子类所有score的加和> cateNumberMap<大类名称，该类需要召回的数量>
         */

//        logger.info("uid:{}------------- oldClass step6",mixRequestInfo.getUid());

        try{
            for(Map.Entry<String,List<String>> cate : cateMap.entrySet()){
                List<String> tags = cate.getValue();
                if(tags.size() >= 4){
                    cate.setValue(tags.subList(0,3));
                    List<String> otherTags = tags.subList(3,tags.size());
                    if(cateHeadTags.contains(cate.getKey())) {
                        cate.setValue(tags.subList(0, 4));
                        otherTags = tags.subList(4, tags.size());
                    }

                    Collections.shuffle(otherTags);
                    if(list.size() > 150*fullness && list.size() < 300*fullness ){
                        cate.getValue().addAll(otherTags.subList(0,(int)Math.round(otherTags.size()*0.8)));
                    }else if(list.size() >= 300*fullness && list.size() <= 500*fullness){
                        cate.getValue().addAll(otherTags.subList(0,(int)Math.round(otherTags.size()*0.6)));
                    }else if(list.size() > 500*fullness){
                        cate.getValue().addAll(otherTags.subList(0,(int)Math.round(otherTags.size()*0.5)));
                    }else{
                        cate.getValue().addAll(otherTags);

                    }
                }

                recallNumAfterFilter += cate.getValue().size();
            }
//            logger.info("uid:{}------------- oldClass step7",mixRequestInfo.getUid());


            cateMap.values().stream().forEach(item -> totalTags.addAll(item));

            for(Map.Entry<String,List<String>> item : cateMap.entrySet()){
                double score = 0.0;
                for(String s : item.getValue()){
                    score += (tagWithWeightMap.getOrDefault(s,0.5)-0.5);
                }
                cotagSum += score;
                numberMap.put(item.getKey(),score);
            }
//            logger.info("uid:{}------------- oldClass step8",mixRequestInfo.getUid());


            cateMap.clear();

        }catch (Exception e){
            e.printStackTrace();
        }


        if(numberMap.keySet().size() >= 70){
            recallNumber = recallNumber * 2;
        }else if(numberMap.keySet().size() >= 30 && numberMap.keySet().size() < 70){
            recallNumber = (int) (recallNumber * 1.5);
        }

        for(Map.Entry<String,Double> cotagItem : numberMap.entrySet()) {
            cateNumberMap.put(cotagItem.getKey(),(int) Math.round((0.011+Math.log1p(cotagItem.getValue()/cotagSum)) * recallNumber));
        }
//        logger.info("uid:{}------------- oldClass step9 cate:{} numberMap:{} cotagSum:{} recallNumber:{}",mixRequestInfo.getUid(),cateNumberMap.toString(),numberMap.toString(),cotagSum,recallNumber);



    }

    private void initNewC() {
        /**
         * 若tag数量大于150对其进行randomPick
         */

        listHead = new ArrayList<>();

        try{
            if(size > 300){
                listSection = list.subList(0,(int)(size*0.4));
            }else if(size >150 && size <= 300){
                listSection = list.subList(0,(int)(size*0.6));
            }else{
                listSection = list.subList(0,(int)(size*0.8));
            }

            for(RecordInfo item : listSection){
                if(item.getReadFrequency() > 2 || item.getReadFrequency()/item.getExpose() > 0.4 || listSection.size()<20){
                    listHead.add(item);
                }
            }

            headTags = listHead.stream().map(RecordInfo::getRecordName).collect(Collectors.toList());
            sumWeightHead = getSumWeight(listHead);

            listHead.clear();

           if(sumWeightHead/headTags.size() < 0.08){
                recallNumberHead = (int) (recallNumberHead * 0.5);
           }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initNewC4VideoUser() {
        /**
         * 若tag数量大于150对其进行randomPick
         */

        listHead = new ArrayList<>();

        try{
            if(size > 300){
                listSection = list.subList(0,(int)(size*0.7));
            }else if(size >150 && size <= 300){
                listSection = list.subList(0,(int)(size*0.9));
            }else{
                listSection = list;
            }

            for(RecordInfo item : listSection){
                if(item.getReadFrequency() > 1 || item.getReadFrequency()/item.getExpose() > 0.4){
                    listHead.add(item);
                }
            }

            headTags = listHead.stream().map(RecordInfo::getRecordName).collect(Collectors.toList());
            sumWeightHead = getSumWeight(listHead);

            listHead.clear();

            if(sumWeightHead/headTags.size() < 0.08){
                recallNumberHead = (int) (recallNumberHead * 0.8);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void init() {
        /**
         * 若tag数量大于150对其进行randomPick
         */
        //recordInfoList=recordInfoList.subList(0,size);
        listHead = list.subList(0,size/3*1);
        listMiddle = list.subList(size/3*1,size/3*2);
        listTail = list.subList(size/3*2,size);
//        headThreshold = listHead.get(listHead.size()-1).getWeight();
//        middleThreshold = listMiddle.get(listMiddle.size()-1).getWeight();

        Collections.shuffle(listHead);
        Collections.shuffle(listMiddle);
        Collections.shuffle(listTail);
        if(list.size() > 150 && list.size() < 300){
            listHead = listHead.subList(0,listHead.size()/10*8);
            listMiddle = listMiddle.subList(0,listMiddle.size()/10*8);
            listTail = listTail.subList(0,listTail.size()/10*8);
        }else if(list.size() >= 300 && list.size() <= 500){
            listHead = listHead.subList(0,listHead.size()/10*6);
            listMiddle = listMiddle.subList(0,listMiddle.size()/10*6);
            listTail = listTail.subList(0,listTail.size()/10*6);
        }else if(list.size() > 500){
            listHead = listHead.subList(0,listHead.size()/3);
            listMiddle = listMiddle.subList(0,listMiddle.size()/3);
            listTail = listTail.subList(0,listTail.size()/3);
        }
        headTags = listHead.stream().map(RecordInfo::getRecordName).collect(Collectors.toList());
        middleTags = listMiddle.stream().map(RecordInfo::getRecordName).collect(Collectors.toList());
        tailTags = listTail.stream().map(RecordInfo::getRecordName).collect(Collectors.toList());

        sumWeightHead = getSumWeight(listHead) * 1.37;
        sumWeightMiddle = getSumWeight(listMiddle) * 1.59;
        sumWeightTail = getSumWeight(listTail) * 1.83;
    }

    public double getSumWeight(List<RecordInfo> list){
        double result = 0.0;
        for(RecordInfo recordInfo : list) {
            result += (recordInfo.getWeight() - 0.5);
        }

        return result;
    }

    public void releaseMemory(){
        if(fullness < 0.3){
            headTags.clear();
            tagWithWeightMap.clear();
        }else{
            tagWithWeightMap.clear();
            totalTags.clear();
            numberMap.clear();
            headTags.clear();
            middleTags.clear();
            tailTags.clear();
            cateNumberMap.clear();
        }


        //            totalTags numberMap headTags middleTags tailTags cateNumberMap tagWithWeightMap


    }

    public int getRecallNumberNew(String tagName) {

        try {
            double tagScore =  tagWithWeightMap.get(tagName) - 0.5;
            double recallNumber;
            if(headTags.contains(tagName)){
                recallNumber =  tagScore / sumWeightHead * 1.10;  //归一化后的tag权重
                return Math.min(4,(int)(Math.round(Math.log1p(recallNumber) * recallNumberHead)));

            }else{
                return 0;
            }

        } catch (Exception e) {
            return 1;
        }
    }

    public int getRecallNumberNewC(String tagName) {
        try {
//            logger.info("uid:{}------------- oldClass innerstep1",mixRequestInfo.getUid());

//            logger.info("uid:{} fullness:{} tagName:{} cate:{}------------- oldClass innerstep2",mixRequestInfo.getUid(),fullness,tagName,cateNumberMap.toString());

            if(fullness < 0.3){
                return getRecallNumberNew(tagName);
            }
            if(totalTags.contains(tagName)){
                double tagScore =  tagWithWeightMap.get(tagName) - 0.5;

                if(headTags.contains(tagName)){
                    return Math.min(6,(int) Math.round(tagScore  / numberMap.get(tagName.split("-")[0]) * cateNumberMap.get(tagName.split("-")[0])));
                }else if(middleTags.contains(tagName)){
                    return Math.min(3,(int) Math.round(tagScore * 1.1 / numberMap.get(tagName.split("-")[0]) * cateNumberMap.get(tagName.split("-")[0])));
                }else if(tailTags.contains(tagName)){
                    return Math.min(2,(int) Math.round(tagScore * 1.1 / numberMap.get(tagName.split("-")[0]) * cateNumberMap.get(tagName.split("-")[0])));
                }else{
                    return 0;
                }
            }else{
                return 0;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2;
    }



    public int getRecallNumber(String tagName) {
        try {
            double s = (tagWithWeightMap.get(tagName) - 0.5) / sumWeight;  //归一化后的tag权重
            return (int) (Math.round(Math.log1p(s) * recallNumber));
        } catch (Exception e) {
            return 10;
        }
    }

    public int getRecallNumberCeil(String tagName) {
        try {
            double s = (tagWithWeightMap.get(tagName) - 0.5) / sumWeight;  //归一化后的tag权重
            return (int) (Math.ceil(Math.log1p(s) * recallNumber));
        } catch (Exception e) {
            return 10;
        }
    }

}
