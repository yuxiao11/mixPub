package com.ifeng.recom.mixrecall.negative;


import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.ifeng.ikvlite.IkvLiteClient;


/**
 * 新闻内容画像查询
 * Created by jibin on 2017/5/4.
 */
@Service("HeadlineItemProfileUtils")
public class HeadlineItemProfileUtils {
    private static final Logger logger = LoggerFactory.getLogger(HeadlineItemProfileUtils.class);

//    private final String HOST0 = "10.90.9.61";
//    private final String HOST1 = "10.90.9.62";
//    private final String HOST2 = "10.90.9.63";
//    private final String HOST3 = "10.90.9.64";
//    private final String HOST4 = "10.90.9.65";

    private final String HOST0 = "10.90.13.101";
    private final String HOST1 = "10.90.13.102";
    private final String HOST2 = "10.90.13.103";
    private final String HOST3 = "10.90.13.104";
    private final String HOST4 = "10.90.13.105";

    private final String KEYSPACE = "ikv";
    private final String IKV_TABLE = "appitemdb";


    IkvLiteClient client;

    /**
     * IKV的初始化函数
     */
    public void Init() {
        try {
            client = new IkvLiteClient(KEYSPACE, IKV_TABLE, true);
            client.connect(HOST0, HOST1, HOST2, HOST3, HOST4);
            logger.info("IKV client create success. table name is {}", this.IKV_TABLE);
        } catch (Exception e) {
            logger.error("IKV client create failed.{}", e);
            return;
        }

    }



    /**
     * 好神奇的逻辑~~~
     * 因为key不统一，ikv中根据key查到的结果有两种：json 和另外一种key
     * 如果结果以cmpp_或者imcp_开头，则需要再查询一次，相当于第一遍查询只是翻译key的过程
     * @param key
     * @return
     */
    public String queryItem(String key) {
        if (key == null || key.isEmpty())
            return null;
        String json = null;
        json = get(key);
        if (json == null)
            return null;
        if (json.startsWith("cmpp_") || json.startsWith("imcp_"))
            json = get(json);
        if (json == null)
            return null;
        return json;
    }


    /**
     * 单个查询，直接返回查询结果，但是ikv的key比较特殊，可能需要翻译一次，请调用 queryItem方法
     * @param key
     * @return
     */
    private String get(String key) {
        if (key == null)
            return null;
        String value = "";
        try {
            value = client.get(key);
        } catch (Exception e) {
            logger.error("ERROR get:{},ERROR{} ", key, e);
            return null;
        }
        return value;
    }

    /**
     * 批量查询，直接返回查询结果，但是ikv的key比较特殊，可能需要翻译一次，请调用 getItemProfileBatch
     * @param key
     * @return
     */
    private Map<String, String> gets(String... key) {
        if (key == null)
            return null;
        Map<String, String> retmap = null;
        try {
            retmap = client.gets(key);
        } catch (Exception e) {
            logger.error("ERROR get: {},ERROR:{}", key, e);
            return null;
        }
        return retmap;
    }



    /**
     * 批量查询新闻的内容特征
     *
     * @param keysList
     * @return
     */
    public Map<String, String> getItemProfileBatch(List<String> keysList) {
        if (keysList == null || keysList.size() == 0) {
            return null;
        }
        String[] queryKeys = keysList.toArray(new String[keysList.size()]);

        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("ikv");
        Map<String, String> jsonMap = gets(queryKeys);

        if(jsonMap==null){
            return null;
        }


        //ikv的key比较特殊，可能需要翻译一次,对需要翻译的key再次查询并更新jsonMap
        List<String> key2Update=new ArrayList<>();
        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            String str = entry.getValue();
            if (StringUtils.isNotBlank(str) && (str.startsWith("cmpp_") || str.startsWith("imcp_"))) {
                key2Update.add(str);
            }
        }

        if (key2Update.size() > 0) {
            String[] subkeys = key2Update.toArray(new String[key2Update.size()]);
            Map<String, String> subMap = gets(subkeys);

            if (subMap != null && subMap.size() > 0) {
                for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
                    String str = entry.getValue();
                    String realValue=subMap.get(str);
                    if(StringUtils.isNotBlank(realValue)){
                        entry.setValue(realValue);
                    }
                }
            }
        }

        timer.addEndTime("ikv");
        return jsonMap;
    }
}
