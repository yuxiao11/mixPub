package com.ifeng.recom.mixrecall.common.factory;


import com.google.gson.reflect.TypeToken;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserCluster;
import com.ifeng.recom.mixrecall.common.model.item.Index4User;


import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by liligeng on 2019/8/19.
 */

public class JsonTypeFactory {
    /**
     * 添加Json解析类型 用于图谱解析
     */
    public static Type ListMapType = new TypeToken<List<Map<String,String>>>(){}.getType();

    public static Type ListRecordInfo = new TypeToken<List<RecordInfo>>(){}.getType();

    public static Type MapStringString = new TypeToken<Map<String,String>>(){}.getType();

    public static Type ListDocument = new TypeToken<List<Document>>(){}.getType();

    public static Type MapStringListIndex4User = new TypeToken<Map<String, List<Index4User>>>() {}.getType();

    public static Type MapStringMapStringString = new TypeToken<Map<String, Map<String, String>>>() {}.getType();

    public static Type ListMapStringObject = new TypeToken<List<Map<String, Object>>>() {}.getType();

    public static Type ListString = new TypeToken<List<String>>() {}.getType();

    public static Type ListUserCluster = new TypeToken<List<UserCluster>>(){}.getType();


}
