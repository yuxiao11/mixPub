package com.ifeng.recom.mixrecall.common.constant;

/**
 * Created by lilg1 on 2017/11/21.
 * 同步调用
 *
 */
public class FlowTypeAsync {

    /**
     * 头条首屏
     */
    public final static String toutiaoFirst = "toutiaoFirst";

    /**
     * 新接口，视频正反馈使用的流量标记，参数为json的list
     * 视频正反馈，引擎直接调用
     */
    public static String positiveFeedVideoNew = "positiveFeedVideoNew";

    /**
     * 新接口，普通新闻正反馈使用的流量标记，参数为json的list
     * docpic正反馈，引擎直接调用
     */
    public static String positiveFeedNew = "positiveFeedNew";

    /**
     * 头条的增量流量标记
     * storm 增量异步调用
     */
    public static final String IncreasedateMerge = "IncreasedateMerge";


    public static final String LastTopic="lastTopic";

    /**
     * 根据用户lastCotag进行召回
     */
    public static final String FlowType_LastCotag = "lastCotag";

}
