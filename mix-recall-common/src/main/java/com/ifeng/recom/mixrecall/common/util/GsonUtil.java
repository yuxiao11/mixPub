package com.ifeng.recom.mixrecall.common.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;

public class GsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(GsonUtil.class);


    private final static Gson gson = new Gson();

    private final static Gson gsonWithoutExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();


    /**
     * 把对象转换为jsonString，并捕获异常
     *
     * @param object
     * @return
     */
    public static String object2json(Object object) {
        try {
            return gson.toJson(object);
        } catch (Exception e) {
            logger.error("jsonstr Exception：{}", e);
            return null;
        }
    }


    /**
     *
     * @param object
     * @param typeOfT
     * @return
     */
    public static String object2json(Object object, Type typeOfT){
        try {
            return gson.toJson(object, typeOfT);
        } catch (Exception e) {
            logger.error("jsonstr Exception：{}", e);
            return null;
        }
    }


    public static String object2jsonWithoutExpose(Object object){
        try {
            return gsonWithoutExpose.toJson(object);
        } catch (Exception e) {
            logger.error("jsonstr Exception：{}", e);
            return null;
        }
    }



    public static String object2jsonWithoutExpose(Object object, Type typeOfT){
        try {
            return gsonWithoutExpose.toJson(object, typeOfT);
        } catch (Exception e) {
            logger.error("jsonstr Exception：{}", e);
            return null;
        }
    }

    /**
     * 将jackson  json转化为list、map等复杂对象
     * 例如： List<Bean> beanList = mapper.readValue(jsonString, new TypeReference<List<Bean>>() {});
     *
     * @param text
     * @param typeReference
     * @param <T>
     * @return
     * @throws IOException
     */
    public static final <T> T json2Object(String text, Type typeReference) {
        try {
            return gson.fromJson(text, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("jsonstr:{},Exception：{}", text, e);
        }
        return null;
    }


    public static final <T> T json2Object(String text, Class<T> classOfT) {
        try {
            return gson.fromJson(text, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("jsonstr:{},Exception：{}", text, e);
        }
        return null;
    }


    public static final <T> T json2ObjectWithoutExpose(String text, Type typeReference) {
        try {
            return gsonWithoutExpose.fromJson(text, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("jsonstr:{},Exception：{}", text, e);
        }
        return null;
    }


    public static final <T> T json2ObjectWithoutExpose(String text, Class<T> classOfT) {
        try {
            return gsonWithoutExpose.fromJson(text, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("jsonstr:{},Exception：{}", text, e);
        }
        return null;
    }

    public static final String o2jWithoutExpose(Object o) {
        try {
            return gsonWithoutExpose.toJson(o);
        } catch (Exception e) {
            logger.error("toJsonError", e);
        }
        return null;
    }


}
