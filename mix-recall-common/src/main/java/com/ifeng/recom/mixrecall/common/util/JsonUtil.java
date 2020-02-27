package com.ifeng.recom.mixrecall.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by lilg1 on 2017/11/21.
 */
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    /** app客户端使用的date格式，这里保持统一 */
    public final static String TIME_FORMAT ="MMM d, yyyy h:mm:ss a";

    private static ObjectMapper om = new ObjectMapper();

    static {
        //提供其它默认设置
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        om.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        om.setDateFormat(new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH));
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    /**
     * 把对象转换为jsonString
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String object2json(Object object) throws JsonProcessingException {
        return om.writeValueAsString(object);
    }

    /**
     * 把对象转换为jsonString，并捕获异常
     * @param object
     * @return
     */
    public static String object2jsonWithoutException(Object object) {
        try {
            return object2json(object);
        } catch (JsonProcessingException e) {
            logger.error("jsonstr Exception：{}", e);
            return null;
        }
    }

    /**
     * 将jackson  json转化为对象
     * @param text
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static final <T> T json2ObjectWithoutException(String text, Class<T> clazz) {
        try {
            return om.readValue(text, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("jsonstr:{},Exception：{}", text, e);
            return null;
        }
    }

    /**
     * 将jackson  json转化为对象
     * @param text
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static final <T> T json2Object(String text, Class<T> clazz) throws IOException {
        return om.readValue(text, clazz);
    }


    /**
     * 将jackson  json转化为list、map等复杂对象
     * 例如： List<Bean> beanList = mapper.readValue(jsonString, new TypeReference<List<Bean>>() {});
     * @param text
     * @param typeReference
     * @param <T>
     * @return
     * @throws IOException
     */
    public static final <T> T json2Object(String text, TypeReference typeReference) {
        try {
            return om.readValue(text, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("jsonstr:{},Exception：{}", text, e);
            return null;
        }
    }
}
