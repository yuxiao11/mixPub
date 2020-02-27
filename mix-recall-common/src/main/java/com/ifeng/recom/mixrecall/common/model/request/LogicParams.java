package com.ifeng.recom.mixrecall.common.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 存放公用的请求配置，每次请求初始化一个LogicParams，方便做流量试验
 * <p>
 * Created by jibin on 2018/1/19.
 */
@Getter
@Setter
public class LogicParams {
    public Map<String,String>  RatioMap = null;

    /**
     * 新闻正反馈数量
     */
    public int positiveFeedDocpicNum = 30;

    /**
     * 视频正反馈数量
     */
    public int positiveFeedVideoNum = 20;


    /**
     * 总的返回结果的默认值,默认值200
     */
    public int result_size = 200;

    /**
     * 试探的图文召回量
     */
    public int expDocNum = 150;
    public int expDocScNum = 200; 

    public int expDocUcbNum = 300; 
    public int expDocScUcbNum = 300; 

    /**
     * 试探的视频的召回量
     */
    public int expVideoNum = 100; 
    public int expVideoScNum = 200; 

    public int expVideoUcbNum = 300; 
    public int expVideoScUcbNum = 300; 

    /**
     * cotag 召回数量控制
     */
    public int cotagLongNum = 350;
    public int cotagLongNumNew = 300;
    public int cotagRecentNum = 300;
    public int cotagLastNum = 50;
    public int cotagTotalNum = 600;
    public int cotagSTotalNum = 500;
    public int cotagUCNum = 200; //user cluster 试验


    /**
     * topic 召回数量控制
     */
    public int topicLongNum = 50;
    public int topicLastNum = 25;

    /**
     * topic 视频召回数量控制
     */
    public int topicVideoLongNum = 80;
    public int topicVideoLastNum = 50;

    /**
     * 姜明明计算的使用图文标签打给视频 召回数量控制
     */
    public int cotagVideoByDocLongNum = 200;

    /**
     * cotag video 召回数量控制
     */
    public int cotagCrawVideoLongNum = 500;
    public int cotagVideoLongNum = 200;  
    public int cotagVideoRecentNum = 100; 
    public int cotagVideoLastNum = 20;  
    public int cotagVideoTotalNum = 500;

    public int cotagVideoForNewTagLongNum = 250;
    public int cotagVideoForNewTagRecentNum = 250;


    public int cotagDocpicNewTagLongNum = 300;  
    public int cotagDocpicNewTagRecentNum = 200; 

    /**
     * cotagSim video 召回数量控制
     */
    public int cotagVideoSimLastNum = 200;
    public int cotagVideoSimRecentNum = 250;

    /**
     * cotagSim docpic 召回数量控制
     */
    public int cotagDocpicSimLastNum = 250;
    public int cotagDocpicSimRecentNum = 300;

    /**
     * cotagGraph video 召回数量控制
     */
    public int cotagVideoGraphLastNum = 150;
    public int cotagVideoGraphRecentNum = 200;
    public int cotagVideoGraphLongNum = 250;

    /**
     * cotagGraph docpic 召回数量控制
     */
    public int cotagDocpicGraphLastNum = 200;
    public int cotagDocpicGraphRecentNum = 250;
    public int cotagDocpicGraphLongNum = 300;
    /**
     * lda topic召回数量
     */
    public int ldaTopicLongNum = 150; 
    public int ldaTopicRecentNum = 150; 

    /**
     * ffm 通道召回数量
     */

    public int ffmNum = 200;


    /**
     * cotag small video 召回数量控制
     */
    public int cotagSmallVideoLongNum = 50;
    public int cotagSmallVideoRecentNum = 50; 
    public int t1SmallVideoRecentNum = 50; 

    public int CSmallVideoRecentNum = 50;
    /**
     * toutiaoFirst 召回数量控制
     */
    public int toutiaoFirstCotagNum = 400;
    public int toutiaoFirstCotagVideoNum = 250;


    /**
     * 机器push召回数据控制
     */
    public int machinePushCotagNum = 500;


    /**
     * 用户订阅内容控制总量
     */
    public int numToAddUserSub = 120; 

    /**
     * 用户订阅内容控制，每个订阅返回的结果数量
     */
    public int numEachUserSub = 5; 

    /**
     * 热事件召回数量
     */
    public int hotEventNum = 30;
    /**
     * 优质文章召回数量
     */
    public int t1ExcellentNum = 200;

    /**
     * 优质视频召回数量
     */
    public int videoExcellentCNum = 50; //未使用
    public int videoExcellentScNum = 100;

    /**
     * 优质docpic召回数量
     */
    public int docpicExcellentScNum = 100;

    public int docpicExcellentCNum = 50;

    /**
     * 低曝光探索召回数量
     */
    public int t1LowExpoExploerNum = 80;

    /**
     * 渠道召回数量
     */
    public int sourceNum = 70;

    /**
     * 安全文章召回数量
     */
    public int safeDocpicNum =200;
    public int safeVideoNum = 200;

    /**
     * 通过c sc召回文章和视频的数量
     */
    public int docpicCNum = 100;
    public int docpicScNum = 100;
    public int videoCNum = 100;
    public int videoScNum = 100;

//    /**
//     * 添加方法动态设置召回数量 By ZGX YX 20190526
//     */
//    public void changeNum(Map<String,String> numRatio){
//        this.RatioMap = numRatio;
//        Integer totalNum = expDocNum+expDocScNum+expDocUcbNum + expDocScUcbNum
//    }

}
