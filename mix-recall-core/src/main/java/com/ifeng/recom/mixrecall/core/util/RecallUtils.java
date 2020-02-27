package com.ifeng.recom.mixrecall.core.util;


import com.ifeng.recom.mixrecall.common.config.ApplicationConfig;
import com.ifeng.recom.mixrecall.common.constant.ApolloConstant;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.model.*;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import com.ifeng.recom.mixrecall.core.channel.excutor.cotag.CoTagDRecallNew;
import com.ifeng.recom.mixrecall.core.service.*;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static com.ifeng.recom.mixrecall.common.service.BloomFilter.filterSimIdByBloomFilter;

/**
 * Created by geyl on 2017/11/21.
 */
public class RecallUtils {
    private static final Logger logger = LoggerFactory.getLogger(RecallUtils.class);

    public static Document.HotBoostComparator hotBoostComparator = new Document.HotBoostComparator();

    /**
     * 根据画像记录获取 标签和权重的map
     *
     * @param recordInfoList
     * @return
     */
    public static Map<String, Double> getTagAndWeightMap(List<RecordInfo> recordInfoList) {

        if (recordInfoList == null) {
            return Collections.emptyMap();
        }

        Map<String, Double> tagAndWeightMap = new HashMap<>(); //画像标签和权重
        for (RecordInfo recordInfo : recordInfoList) {
            try {
                tagAndWeightMap.put(recordInfo.getRecordName(), recordInfo.getWeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tagAndWeightMap;
    }

    /**
     * 根据画像记录获取 标签和权重的map
     *
     * @param recordInfoList
     * @return
     */
    public static Map<String, Integer> getTagAndClick(List<RecordInfo> recordInfoList) {

        if (recordInfoList == null) {
            return Collections.emptyMap();
        }

        Map<String, Integer> tagAndClick = new HashMap<>(); //画像标签和权重
        for (RecordInfo recordInfo : recordInfoList) {
            try {
                tagAndClick.put(recordInfo.getRecordName(), recordInfo.getReadFrequency());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tagAndClick;
    }


    public static Map<String, Double> getTagAndWeightMapTop(List<RecordInfo> recordInfoList, int minSize, long checkNum) {
        if (recordInfoList == null) {
            return Collections.emptyMap();
        }
        recordInfoList.sort(new RecordInfo.RecordInfoWeightComparator());
        //取标签前2/3
        int size = 0;
        if (checkNum < 0) {
            size = recordInfoList.size() / 3 * 2;
        } else {
            size = recordInfoList.size();
        }


        recordInfoList = recordInfoList.subList(0, size);
        if (recordInfoList == null || recordInfoList.size() == 0) {
            return Collections.emptyMap();
        }
        Map<String, Double> tagAndWeightMap = new HashMap<>(); //画像标签和权重
        for (RecordInfo recordInfo : recordInfoList) {
            try {
                tagAndWeightMap.put(recordInfo.getRecordName(), recordInfo.getWeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tagAndWeightMap;
    }


    /**
     * 根据画像记录获取 标签和标签在画像中的位置的map
     *
     * @param recordInfoList
     * @return
     */
    public static Map<String, Double> getTagPositionMap(List<RecordInfo> recordInfoList) {
        if (recordInfoList == null) {
            return Collections.emptyMap();
        }

        int totolNum = recordInfoList.size();

        Map<String, Double> tagAndPositionMap = new HashMap<>(); //画像标签和权重
        double position = 1d;
        for (RecordInfo recordInfo : recordInfoList) {
            try {
                tagAndPositionMap.put(recordInfo.getRecordName(), position++ / totolNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tagAndPositionMap;
    }

    /**
     * 根据画像记录获取 标签和标签在画像中的位置的map
     *
     * @param recordInfoList
     * @return
     */
    public static Set<String> getCateAndSubcateFilterForDoc(UserModel userModel) {
        if (userModel == null) {
            return Collections.emptySet();
        }

        Set<String> filterSet = new HashSet<>();

        List<RecordInfo> recordInfoList = new ArrayList<>();
        if(userModel.getDocpic_cate() != null){
            recordInfoList.addAll(userModel.getDocpic_cate());
        }

        if(userModel.getDocpic_subcate() != null){
            recordInfoList.addAll(userModel.getDocpic_subcate());
        }

        for (RecordInfo recordInfo : recordInfoList) {
            try {
                if(recordInfo.getWeight() <= 0.5){
                    filterSet.add(recordInfo.getRecordName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return filterSet;
    }

    public static Set<String> getCateAndSubcateFilterForVideo(UserModel userModel) {
        if (userModel == null) {
            return Collections.emptySet();
        }

        Set<String> filterSet = new HashSet<>();

        List<RecordInfo> recordInfoList = new ArrayList<>();
        if(userModel.getVideo_cate() != null){
            recordInfoList.addAll(userModel.getVideo_cate());
            logger.info("check uid:{} cate:{} recordInfoList:{}",userModel.getUserId(),userModel.getVideo_cate(),recordInfoList.toString());
        }

        if(userModel.getVideo_subcate() != null){
            recordInfoList.addAll(userModel.getVideo_subcate());
        }

        for (RecordInfo recordInfo : recordInfoList) {
            try {
                if(recordInfo.getWeight() <= 0.5){
                    filterSet.add(recordInfo.getRecordName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return filterSet;
    }


    /**
     * 根据TagWeightMap获取数据 防止出现数据个数不同的情况
     *
     * @param tagWeightMap
     * @return
     */
    public static Map<String, Double> getTagPositionMap(Map<String, Double> tagWeightMap) {
        if (tagWeightMap == null) {
            return Collections.emptyMap();
        }

        int totolNum = tagWeightMap.size();


        Map<String, Double> tagAndPositionMap = new HashMap<>(); //画像标签和权重
        double position = 1d;
        for (String tagName : tagWeightMap.keySet()) {
            try {
                tagAndPositionMap.put(tagName, position++ / totolNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tagAndPositionMap;
    }

    public static Map<String, Double> getTagPositionForSim(List<String> recordInfoList) {
        if (recordInfoList == null) {
            return Collections.emptyMap();
        }

        int totolNum = recordInfoList.size();

        Map<String, Double> tagAndPositionMap = new HashMap<>(); //画像标签和权重
        double position = 1d;
        for (String tag : recordInfoList) {
            try {
                tagAndPositionMap.put(tag, position++ / totolNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tagAndPositionMap;
    }

    public static Map<String, Integer> getTagRecallNumberOLDTEST(MixRequestInfo mixRequestInfo, TagPeriod tagPeriod, int totalNumber, List<RecordInfo> recordInfos, Map<String, Double> tagWeights) {
        Set<String> tags = tagWeights.keySet();
        Map<String, Integer> tagRecallNum = new HashMap<>();
        List<String> tagTest = new ArrayList<>();

        try {

            RecallNumberControl recallNumberControl = new RecallNumberControl(recordInfos, tagWeights, totalNumber);
            for (String tag : tags) {

                int number = recallNumberControl.getRecallNumber(tag); //当前tag应召回的数量
                tagTest.add(tag + ":" + number);
                if (number > 0) {
                    tagRecallNum.put(tag, number);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagRecallNum;
    }

    public static Map<String, Integer> getTagRecallNumber(int totalNumber, List<RecordInfo> recordInfos, Map<String, Double> tagWeights) {
        Set<String> tags = tagWeights.keySet();
        Map<String, Integer> tagRecallNum = new HashMap<>();


        try {

            RecallNumberControl recallNumberControl = new RecallNumberControl(recordInfos, tagWeights, totalNumber);
            for (String tag : tags) {
                int number = recallNumberControl.getRecallNumber(tag); //当前tag应召回的数量
                if (number > 0) {
                    tagRecallNum.put(tag, number);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagRecallNum;
    }

    public static Map<String, Integer> getTagRecallNumberWithNegative_CsC(int totalNumber, List<RecordInfo> recordInfos, Map<String, Double> tagWeights,MixRequestInfo mixRequestInfo) {
        Set<String> tags = tagWeights.keySet();
        Map<String, Integer> tagRecallNum = new HashMap<>();


        try {
            RecallNumberControl recallNumberControl = new RecallNumberControl(recordInfos, tagWeights, totalNumber);
            for (String tag : tags) {
                int number = 0; //当前tag应召回的数量

                try{
                    number = (int)(Math.round(recallNumberControl.getRecallNumber(tag) * mixRequestInfo.getNegativeMap().getOrDefault("cotag",new HashMap<>()).getOrDefault(tag,1.0))); //当前tag应召回的数量

                }catch(Exception e){
                    number = recallNumberControl.getRecallNumber(tag); //当前tag应召回的数量
                }
                if (number > 0) {
                    tagRecallNum.put(tag, number);
                }
            }
        } catch (Exception e) {
            logger.info("check weightMap:{}_{}",mixRequestInfo.getUid(),mixRequestInfo.getNegativeMap().get("cotag"));
            e.printStackTrace();

        }
        return tagRecallNum;
    }

    public static Map<String, Integer> getTagRecallNumber(int totalNumber, List<RecordInfo> recordInfos, Map<String, Double> tagWeights, String cScType) {
        // 针对c sc召回，用的也是cotag的redis，需要在cate或subcate前加c-或sc-
        Set<String> tags = tagWeights.keySet();
        Map<String, Integer> tagRecallNum = new HashMap<>();
        try {

            RecallNumberControl recallNumberControl = new RecallNumberControl(recordInfos, tagWeights, totalNumber);
            for (String tag : tags) {
                int number = recallNumberControl.getRecallNumber(tag); //当前tag应召回的数量
                if (number > 0) {
                    if ("c".equals(cScType)) {
                        tagRecallNum.put("c-" + tag, number);
                    } else if ("sc".equals(cScType)) {
                        tagRecallNum.put("sc-" + tag, number);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagRecallNum;
    }

    //Add By YX at 2019-06-10
    public static Map<String, Integer> getTagRecallNumberNew(MixRequestInfo mixRequestInfo, int totalNumber, List<
            RecordInfo> recordInfos, Map<String, Double> tagWeights, Map<String, Integer> tagClicks, Double fullness) {
        Set<String> tags = tagWeights.keySet();
        Map<String, Integer> tagRecallNum = new HashMap<>();

        try {

            //Add By YX at 2019-09-21 负反馈相关召回策略 为了避免召回文章扎堆问题
//            RecallNumber4Cotag recallNumber4Cotag = new RecallNumberWithNegative(mixRequestInfo, recordInfos, tagWeights, tagClicks, totalNumber, fullness, "withColder");

            RecallNumber4Cotag recallNumber4Cotag = new RecallNumber4Cotag(mixRequestInfo, recordInfos, tagWeights, tagClicks, totalNumber, fullness, "withColder");
            /**
             * 将以下代码进行注释 By YX 20190805
             */

            for (String tag : tags) {
                int number = recallNumber4Cotag.getRecallNumberNewC(tag); //当前tag应召回的数量
                if (number > 0) {
                    tagRecallNum.put(tag, number);
                }
            }

            recallNumber4Cotag.releaseMemory();
            logger.info("tag resultTag uid:{} tags：{}", mixRequestInfo.getUid(), tagRecallNum.size());

        } catch (Exception e) {
            logger.error("Get recall Number Error:", e);
            e.printStackTrace();
        }
        return tagRecallNum;
    }

    //Add By YX at 2019-09-21 负反馈相关召回策略 为了避免召回文章扎堆问题
    public static Map<String, Integer> getTagRecallNumberWithNegativeTest2(MixRequestInfo mixRequestInfo, int totalNumber, List<RecordInfo> recordInfos, Map<String, Double> tagWeights,String flag) {
        Set<String> tags = tagWeights.keySet();
        Map<String, Integer> tagRecallNum = new HashMap<>();
        Set<String> cScFilter = new HashSet<>();
        //此处针对cotagDocpcic添加C 和 SC过滤

        if(flag.equals("Doc"))
        {
            cScFilter = getCateAndSubcateFilterForDoc(mixRequestInfo.getUserModel());
        }else{
            cScFilter = getCateAndSubcateFilterForVideo(mixRequestInfo.getUserModel());

        }


        logger.info("check cScFilter uid:{} flag:{} info:{}",mixRequestInfo.getUid(),flag,cScFilter.toString());

        try {

            RecallNumberControlWithNegativeTest2 recallNumberWithNeg = new RecallNumberControlWithNegativeTest2(mixRequestInfo,recordInfos,tagWeights, totalNumber);

            for (String tag : tags) {
                if (cScFilter.contains(tag.split("-")[0]) && Math.random() > 0.3){
                    continue;
                }

                int number = recallNumberWithNeg.getRecallNumberWithNegative(tag); //当前tag应召回的数量

                if (cScFilter.contains(tag.split("-")[0]) && number >= 2) {
                    logger.info("check filterTag flag:{} uid:{} tag:{}_{}",flag,mixRequestInfo.getUid(),tag,number);
                    tagRecallNum.put(tag, 1);
                    continue;
                }
                if (number > 0) {
                    tagRecallNum.put(tag, number);
                }
            }

            logger.info("tag resultTag[Negative] uid:{} tags：{}", mixRequestInfo.getUid(), tagRecallNum.size());


        } catch (Exception e) {
            logger.error("Get recall[Negative] Number uid:{} Error:", mixRequestInfo.getUid(),e);
            e.printStackTrace();
        }
        return tagRecallNum;
    }


    public static Map<String, Integer> getTagRecallNumberWithNegativeTest1(MixRequestInfo mixRequestInfo, int totalNumber, List<RecordInfo> recordInfos, Map<String, Double> tagWeights,String flag) {
        Set<String> tags = tagWeights.keySet();
        Map<String, Integer> tagRecallNum = new HashMap<>();
        Set<String> cScFilter = new HashSet<>();
        //此处针对cotagDocpcic添加C 和 SC过滤

        if(flag.equals("Doc"))
        {
            cScFilter = getCateAndSubcateFilterForDoc(mixRequestInfo.getUserModel());
        }else{
            cScFilter = getCateAndSubcateFilterForVideo(mixRequestInfo.getUserModel());

        }


        logger.info("check cScFilter uid:{} flag:{} info:{}",mixRequestInfo.getUid(),flag,cScFilter.toString());

        try {

            RecallNumberControlWithNegativeTest1 recallNumberWithNeg = new RecallNumberControlWithNegativeTest1(mixRequestInfo,recordInfos,tagWeights, totalNumber);

            for (String tag : tags) {
                if (cScFilter.contains(tag.split("-")[0]) && Math.random() > 0.3){
                    continue;
                }

                int number = recallNumberWithNeg.getRecallNumberWithNegative(tag); //当前tag应召回的数量

                if (cScFilter.contains(tag.split("-")[0]) && number >= 2) {
                    logger.info("check filterTag flag:{} uid:{} tag:{}_{}",flag,mixRequestInfo.getUid(),tag,number);
                    tagRecallNum.put(tag, 1);
                    continue;
                }
                if (number > 0) {
                    tagRecallNum.put(tag, number);
                }
            }

            logger.info("tag resultTag[Negative] uid:{} tags：{}", mixRequestInfo.getUid(), tagRecallNum.size());


        } catch (Exception e) {
            logger.error("Get recall[Negative] Number  Error:", e);
            e.printStackTrace();
        }
        return tagRecallNum;
    }




    public static Map<String, Integer> getTagRecallNumberWithNegativeBase(MixRequestInfo mixRequestInfo, int totalNumber, List<RecordInfo> recordInfos, Map<String, Double> tagWeights) {
        Set<String> tags = tagWeights.keySet();
        Map<String, Integer> tagRecallNum = new HashMap<>();

        try {

            RecallNumberControlWithNegativeBase recallNumberWithNeg = new RecallNumberControlWithNegativeBase(mixRequestInfo,recordInfos,tagWeights, totalNumber);

            for (String tag : tags) {

                int number = recallNumberWithNeg.getRecallNumberWithNegative(tag); //当前tag应召回的数量

                if (number > 0) {
                    tagRecallNum.put(tag, number);
                }
            }
            logger.info("tag resultTag[Negative] uid:{} tags：{}", mixRequestInfo.getUid(), tagRecallNum.size());


        } catch (Exception e) {
            logger.error("Get recall[Negative] Number  Error:", e);
            e.printStackTrace();
        }
        return tagRecallNum;
    }

    public static void getSplitedRecallNumLog(MixRequestInfo mixRequestInfo, String className, TagPeriod
            tagPeriod, Map<String, Integer> tagRecallNum, List<RecordInfo> recordInfoList,String flag) {
        int size = recordInfoList.size();
        List<String> headResult = new ArrayList<>();
        List<String> middleResult = new ArrayList<>();
        List<String> tailResult = new ArrayList<>();
        List<String> result = new ArrayList<>();
        Map<String, Integer> tagCateNum = new HashMap<>();
        Map<String,Map<String,Integer>> recallDetail = new HashMap<>();
        int headSum = 0;
        int middleSum = 0;
        int tailSum = 0;
        int baseSum = 0;

//        new HashMap() {{
//　　put("Name", "Unmi");
//　　put("QQ", "1125535");
//　　}};
        try {
            for (Map.Entry<String, Integer> item : tagRecallNum.entrySet()) {
                String cateName = item.getKey().split("-")[0];
                if (!tagCateNum.keySet().contains(cateName)) {
                    tagCateNum.put(cateName, item.getValue());
                    recallDetail.put(cateName,new HashMap(){{put(item.getKey(),item.getValue());}});
                } else if (tagCateNum.keySet().contains(cateName)) {
                    tagCateNum.put(cateName, tagCateNum.get(cateName) + item.getValue());
                    recallDetail.get(cateName).put(item.getKey(),item.getValue());
                }
            }
            for (Map.Entry<String, Integer> entry : tagRecallNum.entrySet()) {
                result.add(entry.getKey() + ":" + entry.getValue());
                baseSum += entry.getValue();
            }
            logger.info("uid:{} ChannelName:{}_{} NegativeGroup:{} TagSize:{} RecallNumber:{} cateNumber:{} recallDetail:{}", mixRequestInfo.getUid(), className, tagPeriod,flag, result.size(), baseSum, tagCateNum.toString(),
                    recallDetail.toString());
        } catch (Exception e) {
            logger.error("uid: {}, cotagPrint error {}", mixRequestInfo.getUid(), e);
            e.printStackTrace();
        }

    }


    public static Map<String, Integer> getTagRecallNumber4Graph(int number, List<String> recordInfos) {
        Map<String, Integer> tagRecallNum = new HashMap<>();

        try {
            for (String tag : recordInfos) {
                tagRecallNum.put(tag, number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tagRecallNum;
    }
    public static Map<String, Integer> getTagRecallNumberSimWithNegative(int number, List<String> recordInfos,MixRequestInfo mixRequestInfo) {
        Map<String, Integer> tagRecallNum = new HashMap<>();
        Map<String, Double> cotagSimMap = mixRequestInfo.getNegativeMap().get("cotag");

        try {
            for (String tag : recordInfos) {
                tagRecallNum.put(tag, (int)(Math.round(number * cotagSimMap.getOrDefault(tag.split("-")[0],1.0))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tagRecallNum;
    }

    public static Map<String, Integer> getTagRecallNumberTest(int totalNumber, List<
            RecordInfo> recordInfos, Map<String, Double> tagWeights) {
        Map<String, Integer> tagRecallNum = new HashMap<>();

        try {
            RecallNumberTest recallNumberControl = new RecallNumberTest(recordInfos, totalNumber);
            for (RecordInfo recordInfo : recordInfos) {
                String tag = recordInfo.getRecordName();
                int number = recallNumberControl.getRecallNumber(tag); //当前tag应召回的数量
                if (number > 0) {
                    tagRecallNum.put(tag, number);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagRecallNum;
    }



    public static Map<String, Integer> getSourceRecallNumberN(int number, Map<String, Double> tagWeights) {
        Set<String> tags = tagWeights.keySet();
        Map<String, Integer> tagRecallNum = new HashMap<>();

        try {
            for (String tag : tags) {
                tagRecallNum.put(tag, number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tagRecallNum;
    }


    /**
     * 根据document的recall tag把document list转为 map
     *
     * @param documentList List<Document>
     */
    public static Map<String, List<Document>> getRecallTagAndDocListMapFromDocList(List<Document> documentList) {
        return getRecallTagAndDocListMapFromDocList(documentList, null);
    }


    public static Map<String, List<Document>> getRecallTagAndDocListMapFromDocList
            (List<Document> documentList, Map<String, String> idTags) {
        Map<String, List<Document>> tagAndDocListMap = new HashMap<>();
        for (Document document : documentList) {
            if (MapUtils.isNotEmpty(idTags)) {
                document.setRecallTag(idTags.get(document.getDocId()));
            }
            try {
                if (tagAndDocListMap.containsKey(document.getRecallTag())) {
                    List<Document> docList = tagAndDocListMap.get(document.getRecallTag());
                    docList.add(document);
                    tagAndDocListMap.put(document.getRecallTag(), docList);
                } else {
                    List<Document> docList = new ArrayList<>();
                    docList.add(document);
                    tagAndDocListMap.put(document.getRecallTag(), docList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tagAndDocListMap;
    }


    public static void setPositionForDocs(List<Document> docs) {
        Document.HotBoostComparator hotBoostComparator = new Document.HotBoostComparator();
        Map<String, List<Document>> tagDocs = getRecallTagAndDocListMapFromDocList(docs);

        for (Map.Entry<String, List<Document>> entry : tagDocs.entrySet()) {
            String tag = entry.getKey();
            List<Document> docs4tag = entry.getValue();

            docs4tag.sort(hotBoostComparator);

            int i = 1;
            for (Document doc : docs4tag) {
                doc.setPreloadPosition(i);
                i++;
            }
        }
    }

    public static void setPositionForDocs(List<Document> docs, Map<String, Double> tagPosition) {
        Document.HotBoostComparator hotBoostComparator = new Document.HotBoostComparator();
        Map<String, List<Document>> tagDocs = getRecallTagAndDocListMapFromDocList(docs);

        for (Map.Entry<String, List<Document>> entry : tagDocs.entrySet()) {
            String tag = entry.getKey();
            List<Document> docs4tag = entry.getValue();

            docs4tag.sort(hotBoostComparator);

            int i = 1;
            for (Document doc : docs4tag) {
                double profileTagPosition = tagPosition.get(tag);
                doc.setPreloadPosition(i);
                doc.setPositionWeight(profileTagPosition);
                i++;
            }
        }
    }

    /**
     * recall Tag对应的 RecallResult list Map
     *
     * @param documentList
     * @param idTags
     * @return
     */
    public static Map<String, List<RecallResult>> getRecallTagAndRecallResultListMapFromDocList
    (List<Document> documentList, Map<String, String> idTags) {

        Map<String, List<RecallResult>> tagAndDocListMap = new HashMap<>();

        for (Document document : documentList) {
            RecallResult recallResult = new RecallResult();

            try {
                recallResult.setDocument(document);
                String recallTag = idTags.get(document.getDocId());
                recallResult.setRecallTag(recallTag);

                if (tagAndDocListMap.containsKey(recallTag)) {
                    List<RecallResult> recallResults = tagAndDocListMap.get(recallTag);
                    recallResults.add(recallResult);
                    tagAndDocListMap.put(recallTag, recallResults);
                } else {
                    List<RecallResult> recallResults = new ArrayList<>();
                    recallResults.add(recallResult);
                    tagAndDocListMap.put(recallTag, recallResults);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tagAndDocListMap;
    }

    /**
     * 将Document转换为RecallResult
     *
     * @param documentList
     * @return
     */
    public static List<RecallResult> convertDocument2RecallResult(List<Document> documentList) {
        List<RecallResult> recallResults = new ArrayList<>();
        for (Document doc : documentList) {
            RecallResult recallResult = new RecallResult();
            recallResult.setDocument(doc);
            recallResults.add(recallResult);
        }
        return recallResults;
    }

    public static void yxdebugLog(TagPeriod tag, MixRequestInfo info, List<RecordInfo> recordInfoList, Map<String, Integer> tagRecallNum) {
        try {
            InetAddress address = InetAddress.getLocalHost();
            if ((!tag.equals(TagPeriod.LAST) && address.getHostAddress().equals("10.90.25.11"))
                    || ApplicationConfig.getProperty(ApolloConstant.MixRecall_DebugUsers).contains(info.getUid())) {
                getSplitedRecallNumLog(info, CoTagDRecallNew.class.getSimpleName(), tag, tagRecallNum, recordInfoList, "base");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
