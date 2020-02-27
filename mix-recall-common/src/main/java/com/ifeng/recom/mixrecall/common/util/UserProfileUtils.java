package com.ifeng.recom.mixrecall.common.util;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.model.*;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory.*;

/**
 * Created by geyl on 2017/11/1.
 */
public class UserProfileUtils {
    private static final Logger logger = LoggerFactory.getLogger(UserModel.class);
    private static String regEx = "[^\\u4e00-\\u9fa5A-Za-z0-9]|lt_";// 匹配中英文及数字之外的字符
    private static Pattern pattern = Pattern.compile(regEx);
    private static int userSearchTimeAgo = 10800000;

    /**
     * 根据用户画像标签权重做截断
     *
     * @param recordInfoList
     * @param minWeight      截取最低权重
     * @return
     */
    public static List<RecordInfo> profileTagWeightFilter(List<RecordInfo> recordInfoList, double minWeight) {
        if (recordInfoList == null) {
            return Collections.emptyList();
        }

        List<RecordInfo> list = new ArrayList<>();
        list.addAll(recordInfoList);

        list.sort(new RecordInfo.RecordInfoWeightComparator());

        int listEndPosition = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            RecordInfo recordInfo = list.get(i);

            if (recordInfo.getWeight() > minWeight) {
                listEndPosition = i + 1;
                break;
            }
        }

        List<RecordInfo> filteredList = list.subList(0, listEndPosition);
        return filteredList;
    }


    /**
     * 根据用户画像标签顺序做截断
     *
     * @param recordInfoList
     * @param num
     * @return
     */
    public static List<RecordInfo> profileTagNumFilter(List<RecordInfo> recordInfoList, int num) {
        if (recordInfoList == null) {
            return Collections.emptyList();
        }

        List<RecordInfo> list = new ArrayList<>();
        list.addAll(recordInfoList);

        List<RecordInfo> filteredList = list.subList(0, Math.min(num, recordInfoList.size()));
        return filteredList;
    }


    /**
     * 解析画像中#号分隔符(hashtag就是#号的意思)
     *
     * @param t
     * @return
     */
    public static List<String> extractBySim(String t) {
        if (t == null || t.isEmpty()) {
            return null;
        }
        List<String> recordList = new ArrayList<String>();
        try {
            return GsonUtil.json2ObjectWithoutExpose(t, ListString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recordList;
    }



    public static List<String> extractByGraph(String t) {
        if (t == null || t.isEmpty()) {
            return null;
        }
        List<String> recordList = new ArrayList<String>();


        try {
            if (t.startsWith("[{")) {

                List<Map<String, String>> tmpList = GsonUtil.json2ObjectWithoutExpose(t, ListMapType);

                for (Map<String, String> item : tmpList) {
                    Iterator<Map.Entry<String, String>> it = item.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, String> entry = it.next();
                        recordList.add(entry.getValue());
                    }
                }
            } else {

                return GsonUtil.json2ObjectWithoutExpose(t, ListString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordList;
    }

    /**
     * 解析画像中符号分隔符tag字段
     *
     * @param t mac_3_0.3840000152587891#数码_2_0.30000000000000004
     * @return List<RecordInfo>
     */
    public static List<RecordInfo> extractRecordListFromStr(String t) {
        if (t == null || t.isEmpty()) {
            return null;
        }

        List<RecordInfo> recordList = new ArrayList<RecordInfo>();

        try {
            int ind = t.indexOf("$");
            if (ind >= 0)
                t = t.substring(0, ind);
            String[] itemArr = t.split("#");

            for (int i = 0; i < itemArr.length; i++) {
                String item = itemArr[i];
                if (item.startsWith("lt_")) {
                    item = item.replace("lt_", "lt!");
                }

                if (item.startsWith("tp_")) {
                    item = item.replace("tp_", "tp!");
                }

                String[] recordObj = item.split("_");

                if (recordObj.length == 3) {
                    try {
                        String recordName = recordObj[0];
                        if (recordName.startsWith("lt!")) {
                            recordName = recordName.replace("lt!", "lt_");
                        }
                        int frequecy = Integer.parseInt(recordObj[1]);
                        double weight = Double.parseDouble(recordObj[2]);

                        // recordName非空且仅限于中英文及数字
                        if (!recordName.isEmpty()) {
                            if (recordName.startsWith("lt_")) {
                                RecordInfo recordInfo = new RecordInfo(recordName, frequecy, weight);
                                recordList.add(recordInfo);
                            } else if (!pattern.matcher(recordName).find()) {
                                RecordInfo recordInfo = new RecordInfo(recordName, frequecy, weight);
                                recordList.add(recordInfo);
                            }

                        }
                    } catch (Exception e) {
                        logger.error("extractRecordListFromStr error:" + t + " " + e);
                        e.printStackTrace();
                    }
                } else if (recordObj.length == 4) {
                    try {
                        String recordName = recordObj[0];
                        if (recordName.startsWith("lt!")) {
                            recordName = recordName.replace("lt!", "lt_");
                        }

                        if (recordName.startsWith("tp!")) {
                            recordName = recordName.replace("tp!", "tp_");
                        }

                        int frequecy = Integer.parseInt(recordObj[1]);
                        int expose = Integer.parseInt(recordObj[2]);
                        double weight = Double.parseDouble(recordObj[3]);

                        // recordName非空且仅限于中英文及数字
                        if (!recordName.isEmpty()) {
                            if (recordName.startsWith("lt_")) {
                                RecordInfo recordInfo = new RecordInfo(recordName, frequecy, weight, expose);
                                recordList.add(recordInfo);
                            } else if (recordName.startsWith("tp_")) {
                                RecordInfo recordInfo = new RecordInfo(recordName, frequecy, weight, expose);
                                recordList.add(recordInfo);
                            } else if (!pattern.matcher(recordName).find()) {
                                RecordInfo recordInfo = new RecordInfo(recordName, frequecy, weight, expose);
                                recordList.add(recordInfo);
                            }

                        }
                    } catch (Exception e) {
                        logger.error("extractRecordListFromStr error:" + t + " " + e);
                        e.printStackTrace();
                    }
                } else if (recordObj.length == 5) {
                    try {
                        String recordName = recordObj[0];
                        if (recordName.startsWith("lt!")) {
                            recordName = recordName.replace("lt!", "lt_");
                        }

                        int frequecy = 0;
                        if (recordObj[1] != null && recordObj[1].length() >= 1 && Character.isDigit(recordObj[1].charAt(0))) {
//                            frequecy = Integer.parseInt(recordObj[1]);
                        } else {
                            recordName = recordName + "_" + recordObj[1];
                        }

//                        int expose = Integer.parseInt(recordObj[2]);
                        int expose = 0;
                        double weight = Double.parseDouble(recordObj[recordObj.length - 1]);

                        // recordName非空且仅限于中英文及数字
                        if (!recordName.isEmpty()) {
                            if (recordName.startsWith("lt_")) {
                                RecordInfo recordInfo = new RecordInfo(recordName, frequecy, weight, expose);
                                recordList.add(recordInfo);
                            } else {
                                RecordInfo recordInfo = new RecordInfo(recordName, frequecy, weight, expose);
                                recordList.add(recordInfo);
                            }

                        }
                    } catch (Exception e) {
                        logger.error("extractRecordListFromStr error:" + t + " " + e);
                        e.printStackTrace();
                    }
                }
            }
        } catch (NumberFormatException e) {
            logger.error("extractRecordListFromStr error:" + t + " " + e);
            e.printStackTrace();
        }

        return recordList;
    }


    public static List<RecordInfo> extractRecordListFromStrOnlyWeight(String t) {
        if (t == null || t.isEmpty()) {
            return null;
        }

        List<RecordInfo> recordList = new ArrayList<>();

        try {
            int ind = t.indexOf("$");
            if (ind >= 0) {
                t = t.substring(0, ind);
                if (t.equalsIgnoreCase( "null")||t.equalsIgnoreCase( "#")) {
                    return null;
                }
            }

            String[] itemArr = t.split("#");
            if(itemArr!=null&&!"null".equals(itemArr[0])){
                for (String item : itemArr) {
                    if (StringUtils.isBlank(item)||item.startsWith("lt_") || item.startsWith("tp_")) {
                        continue;
                    }

                    String recordName="";
                    String[] recordObj = item.split("_");
                    RecordInfo recordInfo=null;
                    if(recordObj!=null){
                        recordName= recordObj[0];
                        double weight = Double.parseDouble(recordObj[recordObj.length - 1]);
                        recordInfo = new RecordInfo(recordName, weight);
                    }

                    if(recordInfo!=null){
                        recordList.add(recordInfo);
                    }

                }
            }
        } catch (NumberFormatException e) {
            logger.error("extractRecordListFromStr error:" + t + " " + e);
        }

        return recordList;
    }


    /**
     * 解析：娱乐_2#音乐_1
     * @param t
     * @return
     */
    public static List<RecordInfo> extractAppUserList(String t) {
        if (t == null || t.isEmpty()) {
            return null;
        }

        List<RecordInfo> recordList = new ArrayList<>();

        try {
            if(StringUtils.isBlank(t)){
                return recordList;
            }

            String[] itemArr = t.split("#");
            if(itemArr!=null&&!"null".equals(itemArr[0])){
                for (String item : itemArr) {
                    if (StringUtils.isBlank(item)) {
                        continue;
                    }

                    String recordName="";
                    String[] recordObj = item.split("_");
                    RecordInfo recordInfo=null;
                    if(recordObj!=null){
                        recordName= recordObj[0];
                        int appNum = Integer.parseInt(recordObj[recordObj.length - 1]);
                        recordInfo = new RecordInfo(recordName, appNum);
                    }

                    if(recordInfo!=null){
                        recordList.add(recordInfo);
                    }

                }
            }
        } catch (Exception e) {
            logger.error("extractRecordListFromStr error:" + t + " " + e);
        }

        return recordList;
    }

    /**
     * 解析画像中uTopic字段
     *
     * @param combineTagJsonStr
     * @return
     */
    public static List<RecordInfo> extractUTopic(String uid,String combineTagJsonStr) {
        try {
            return GsonUtil.json2ObjectWithoutExpose(combineTagJsonStr, ListRecordInfo);
        } catch (Exception e) {
            logger.error("uid{} ",uid, e);
        }
        return null;
    }

    /**
     * 解析画像中的user_cluster字段
     * @param uid
     * @param userClusterStr
     * @return
     */
    public static List<UserCluster> extractUserCluster(String uid, String userClusterStr){
        try{
            return GsonUtil.json2ObjectWithoutExpose(userClusterStr, ListUserCluster);
        }catch (Exception e){
            logger.error("uid:{}",uid,e);
        }
        return null;
    }


    /**
     * 解析画像中combineTag字段
     *
     * @param combineTagJsonStr
     * @return
     */
    public static List<RecordInfo> extractCombineTag(String combineTagJsonStr) {
        try {
            return GsonUtil.json2ObjectWithoutExpose(combineTagJsonStr, ListRecordInfo);
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }


    /**
     * @param search_str
     * @return
     */
    public static List<RecordTime> extractSearch(String search_str) {
        if (StringUtils.isBlank(search_str)) {
            return null;
        }

        List<RecordTime> result = new ArrayList<>();
        if (StringUtils.isNotBlank(search_str)) {
            String[] searchArr = search_str.split("#");
            for (String search : searchArr) {
                String[] item = search.split("\\|");
                if (item.length > 1) {
                    RecordTime recordTime = new RecordTime(item[0], item[1].replace("+", " "));
                    //只获取三小时内的搜索词
                    if (recordTime.getTimestamp().before(new Timestamp(System.currentTimeMillis() - userSearchTimeAgo))) {
                        continue;
                    }
                    result.add(recordTime);
                }
            }
        }

        return result;
    }



    /**
     * @param ub_str
     * @param group_ub
     * @return
     */
    public static List<RecordTime> extractUserSub(String ub_str, String group_ub, String uid) {

        if (Strings.isBlank(ub_str) && Strings.isBlank(group_ub)) {
            return null;
        }

        List<RecordTime> resultUniqu = Lists.newArrayList();
        try {
            String ubStr = ub_str == null ? "" : ub_str;
            ub_str = ubStr;
            List<RecordTime> result = new ArrayList<>();
            if (StringUtils.isNotBlank(ub_str)) {
                String[] ubArr = ub_str.split("#");
                for (String ub : ubArr) {
                    String[] item = ub.split("\\|");
                    if (item.length > 1) {
                        if(StringUtils.isNotBlank(item[0])&&item[0].contains("user")){
                            if((int)Math.random() * 100==1){
                                logger.info("{} subName:{}",uid,item[0]);
                            }
                            continue;
                        }
                        List<String> subName = result.stream().map(x -> x.getRecordName()).collect(Collectors.toList());
                        if (!subName.contains(item[0]) && StringUtils.isNotEmpty(item[0])) {
                            RecordTime recordTime = new RecordTime(item[0], item[1]);
                            if(StringUtils.isNotBlank(item[1])&&item[1].contains("+")){
                                logger.warn("uid:{} usersub time:{} format error",uid,item[1]);
                            }
                            result.add(recordTime);
                        }
                    }
                }
            }

            //解析group_ub字段
            List<String> ubList = new ArrayList<>();
            if (Strings.isNotBlank(group_ub)) {
                try {
                    if (group_ub.startsWith(GyConstant.Symb_openBrace)) {
                        Map<String, String> groupUbMap = GsonUtil.json2Object(group_ub, Map.class);
                        groupUbMap.entrySet().stream().forEach(x -> ubList.add(x.getValue()));
                    }
                } catch (Exception e) {
                    logger.error("{} parse group_ub err, Ex:{}", uid, e);
                }
            }


            long start = System.currentTimeMillis();
            for (String ub_Str : ubList) {
                try {
                    if (StringUtils.isNotBlank(ub_Str)) {
                        String[] generalUbArr = ub_Str.split("#");
                        for (String gub : generalUbArr) {
                            String[] item = gub.split("\\|");
                            if (item.length > 1) {
                                if(StringUtils.isNotBlank(item[0])&&item[0].contains("user")){
                                     if((int)Math.random() * 100==1){
                                         logger.info("{} subName:{}",uid,item[0]);
                                     }
                                     continue;
                                }
                                if (StringUtils.isNotEmpty(item[1])) {
                                    RecordTime recordTime = new RecordTime(item[1], item[2]);
                                    result.add(recordTime);
                                    if (System.currentTimeMillis() - start > 400) {
                                        ServiceLogUtil.debug("parseub_Str {} cost:{}", uid, System.currentTimeMillis() - start);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("{} parse ubList err,{}, Ex:{}", uid, ub_Str, e);
                }
            }

            Set<String> ubSet = new HashSet<>(result.size());
            for (RecordTime recordTime : result) {
                if (ubSet.add(recordTime.getRecordName())) {
                    resultUniqu.add(recordTime);
                }
            }

        } catch (Exception e) {
            logger.error("{} parse group_ub err,Ex:{}", uid, e);
        }
        return resultUniqu;
    }


    /**
     * 解析画像中SourceSim字段
     *
     * @param sourceSimJsonStr
     */
    public static List<SourceSims> extractSourceSimsList(String sourceSimJsonStr) {
        if (sourceSimJsonStr == null || sourceSimJsonStr.isEmpty()) {
            return null;
        }

        List<SourceSims> sourceSimsList = new ArrayList<>();

        try {
            List sourceSimMapList = GsonUtil.json2Object(sourceSimJsonStr, ListMapStringObject);

            for (Object o : sourceSimMapList) {
                Map m = (Map) o;
                SourceSims sourceSims = new SourceSims();
                Source source = new Source();
                source.setName(String.valueOf(m.get("b")));
                sourceSims.setOriginalSource(source);
                Map<String, String> exMap = (Map) m.get("ex");

                List<Source> exSourceList = new ArrayList<>();
                for (Map.Entry<String, String> entry : exMap.entrySet()) {
                    Source source1 = new Source(entry.getKey(), Double.valueOf(entry.getValue()));
                    exSourceList.add(source1);
                }

                sourceSims.setExtendSourceList(exSourceList);
                sourceSimsList.add(sourceSims);
            }

            return sourceSimsList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解析画像中CotagSim字段
     *
     * @param cotagSimJsonStr
     */
    public static List<CotagSims> extractCotagSimsList(String cotagSimJsonStr) {
        if (cotagSimJsonStr == null || cotagSimJsonStr.isEmpty()) {
            return null;
        }

        List<CotagSims> sourceSimsList = new ArrayList<>();


        try {
            List sourceSimMapList = GsonUtil.json2Object(cotagSimJsonStr, ListMapStringObject);

            for (Object o : sourceSimMapList) {
                Map m = (Map) o;
                CotagSims sourceSims = new CotagSims();
                Source source = new Source();
                source.setName(String.valueOf(m.get("b")));
                sourceSims.setOriginalCotag(source);
                Map<String, String> exMap = (Map) m.get("ex");

                List<Source> exSourceList = new ArrayList<>();
                for (Map.Entry<String, String> entry : exMap.entrySet()) {
                    Source source1 = new Source(entry.getKey(), Double.valueOf(entry.getValue()));
                    exSourceList.add(source1);
                }

                sourceSims.setExtendCotagList(exSourceList);
                sourceSimsList.add(sourceSims);
            }

            return sourceSimsList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> extractUserLocation(String locDetail, String generalLocDetail) {
        try {
            Map<String, String> map;
            if (StringUtils.isBlank(locDetail) && StringUtils.isBlank(generalLocDetail)) {
                return null;
            }

            if (StringUtils.isNotBlank(locDetail)) {
                map = GsonUtil.json2Object(locDetail, Map.class);
                return map;
            }

            List<Map> list = GsonUtil.json2Object(generalLocDetail, List.class);
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.info("", e);
        }

        return Collections.emptyMap();
    }
}