package com.ifeng.recom.mixrecall.negative.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class ItemCacheCountUtil {
    private final static Logger logger = LoggerFactory.getLogger(ItemCacheCountUtil.class);
    private ConcurrentSkipListSet<String> cacheItemIds = new ConcurrentSkipListSet();
    public void addItems(String key) {
        cacheItemIds.add(key);
    }

    public void removeItems(Set<String> keys) {
        for(String key: keys){
            cacheItemIds.remove(key);
        }
    }

    public int getSize(){
        return cacheItemIds.size();
    }

    public Set<String> getKeys(){
        return cacheItemIds;
    }

    public static void main(String[] args) {
        ConcurrentSkipListSet<String> cacheItemIds = new ConcurrentSkipListSet();
        cacheItemIds.add("a");
        for(Object k: cacheItemIds) {
            System.out.print(k.toString());
        }
        cacheItemIds.remove("a");
        System.out.print(cacheItemIds);
    }


}


