package com.ifeng.recom.mixrecall.biz;

import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;

/**
 * mix引擎出总结果的入口中枢
 * Created by jibin on 2017/12/25.
 */
public interface IAssembleBiz {

    /**
     * 新接口调用，可能需求不同，所以结果用string，方便兼容多种数据格式
     *
     * @param mixRequestInfo
     * @return
     */
    String doRecom(MixRequestInfo mixRequestInfo);
}
