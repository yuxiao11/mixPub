package com.ifeng.recom.mixrecall.common.service.handler.remove;

import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;

public interface IItemRemoveHandler<V> {

    /**
     * true is remove
     * false is save
     *
     * @param info
     * @param item
     * @return
     */
    boolean remove(MixRequestInfo info, V item);

    /**
     * 执行错误的时候如何处理
     * @return
     */
    default boolean errStat() {
        return false;
    }

    /**
     * handler的名称, 需要保证全局唯一
     *
     * @return
     */
    String handlerName();
}
