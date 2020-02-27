package com.ifeng.recom.mixrecall.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by jibin on 2017/5/23.
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper jsonObjectMapper = new ObjectMapper();

    static {
        jsonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jsonObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        jsonObjectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        jsonObjectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        jsonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    /**
     * 将实体写成json格式字符串
     *
     * @param value
     * @return
     */
    public static String writeToJSON(Object value) {
        String result = null;
        try {
            result = jsonObjectMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.error("writeToJSON ERROR:{}", e);
        }
        return result;
    }


    public static final <T> T json2Object(String text, Class<T> clazz) throws IOException {

        return jsonObjectMapper.readValue(text, clazz);
    }

    /**
     * 将text转换为对象
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static final <T> T json2ObjectWithoutException(String text, Class<T> clazz) {
        try {
            return jsonObjectMapper.readValue(text, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("jsonstr:{},Exception：{}", text, e);
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
    public static final <T> T json2Object(String text, TypeReference typeReference) {
        try {
            return jsonObjectMapper.readValue(text, typeReference);
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error("jsonstr:{},Exception：{}", text, e);
            return null;
        }
    }
}
