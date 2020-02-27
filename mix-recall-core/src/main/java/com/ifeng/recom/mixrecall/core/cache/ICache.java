package com.ifeng.recom.mixrecall.core.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ICache<V> {
    /**
     * cache 的状态, 打印日志使用. 统计信息.
     * @return
     */
    String status();

    /**
     * 批量查询cache中的数据
     * @param keys
     * @return
     */
    Map<String, V> batchByCache(Collection<String> keys);

    /**
     * 单个查询cache中的数据
     * @param key
     * @return
     */
    V getByCache(String key);

    /**
     * 单个查询接口
     *
     * @param key
     * @return
     */
    V query(String key);

    /**
     * 批量查询接口
     *
     * @param keys
     * @return
     */
    Map<String, V> batchQuery(Set<String> keys);

}
