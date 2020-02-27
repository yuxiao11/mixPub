package com.ifeng.recom.mixrecall.negative.cache;

/**
 * @author liangky
 * @Date 2017/12/20
 */
public class CacheEntityUtil {
    private static ThreadLocal<CacheEntity> instance = new ThreadLocal<CacheEntity>(){
        @Override
        protected CacheEntity initialValue() {
            return new CacheEntity();
        }
    };

    //----------------public 方法---------------------------------------------------------------------

    public static CacheEntity getInstance(){
        return instance.get();
    }

}
