package com.ifeng.recom.mixrecall.common.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * Created by wangxl6 on 2018/12/17.
 */
public class SearchApi {
    private final static Logger logger = LoggerFactory.getLogger(SearchApi.class);

    private final static String url = "http://local.so.v.ifeng.com/websearch/ifeng-search-server/all/webSearch";

    /**
        调用搜索接口
     *
     * @param text
     * @return
     */
    public static JsonObject doSearch(String text) {

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("waigua", "MIX");
        requestMap.put("uid", "mix");
        requestMap.put("hl", "0");
        requestMap.put("page", "1");
        requestMap.put("n", "5");
        requestMap.put("k", text);


        try {
            String json = HttpUtils.doGet(url, 50, 500, requestMap);

            JsonObject jsonObject = GsonUtil.json2Object(json, JsonObject.class);
            return jsonObject;
        } catch (Exception e) {
            logger.error("do doSearch : {},error :{}",text, e.getMessage());
        }

        return  null;
    }

    public static void main(String[] args) {

        CRC32 crc32 = new CRC32();
        crc32.update(("04e7966889e04effbe5c2efa74fae090"+"cotagCeshi").getBytes());
        System.out.println(crc32.getValue() % 100);
        String uid  = "334791b3ebfd4e1ca97e28a4b16cf784";

        System.out.println(DigestUtils.md5DigestAsHex(uid.getBytes()));
        int s = 5;
        if(s >3){
            System.out.println(5);
        }else if(s>2){
            System.out.println(6);
        }
        JsonObject jsonObject  = doSearch("spark测试教程");
        JsonObject jsonObject1  = doSearch("李小璐出轨pgone");

//        jsonObject.getAsJsonArray().get


        JsonArray items = jsonObject.getAsJsonArray("items");
        for (JsonElement j: items){
            String id = j.getAsJsonObject().get("id").getAsString();
            String sourceFrom = j.getAsJsonObject().get("sourceFrom").getAsString();
            if("weMedia".equals(sourceFrom)){
                sourceFrom = "sub";
            }
            if ("video".equals(sourceFrom)) {
                sourceFrom="";
            }
            System.out.println("rowkey="+sourceFrom+"_"+id
            );
        }


        System.out.println(jsonObject);
        System.out.println(jsonObject1);
    }
}
