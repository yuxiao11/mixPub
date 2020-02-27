package com.ifeng.recom.mixrecall.common.model.item;

import java.util.List;
import java.util.Map;

import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jibin on 2018/1/23.
 */
@Getter
@Setter
public class MixResult {
    /**
     * 召回结果
     */
    List<Index4User> index4UserList;

    /**
     * 预留，供召回实验分组，回传引擎使用
     */
    Map<String, String> abtestMap;

    /**
     * 召回批次id
     */
    String recallid;


    public MixResult() {

    }


    public MixResult(List<Index4User> index4UserList, Map<String, String> abtestMap, MixRequestInfo mixRequestInfo) {
        this.index4UserList = index4UserList;
        this.recallid = mixRequestInfo.getRecallid();
        this.abtestMap = abtestMap;
    }
}
