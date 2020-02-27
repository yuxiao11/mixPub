package com.ifeng.recom.mixrecall.negative.cache;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author liangky
 * @Date 2017/12/20
 */
public class CacheEntity {

    private Map<String, Integer> missCacheNumMap;

    public CacheEntity() {
        this.missCacheNumMap = Maps.newLinkedHashMap();
    }

    //----------------public 方法---------------------------------------------------------------------

    public void addCacheNum(String cacheName, Integer num) {
        if (StringUtils.isBlank(cacheName)) {
            return;
        }
        this.missCacheNumMap.put(cacheName,num);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,Integer> entry:this.missCacheNumMap.entrySet()){
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
            sb.append(" ");
        }
        return sb.toString();
    }
}
