package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geyl on 2017/10/30.
 * 根据画像中的Combine Tag 字段进行召回
 */
@Service
public class SingleTagChannelImpl {
    public static List<RecordInfo> defaultExploreList;

    static {
        String[] exploreTags = {"育儿","奇闻轶事","萌宠萌娃","自然","综艺","时政","社会","娱乐","体育","电视剧","艺术","教育","科技"
                ,"军事", "美女", "民生", "旅游", "情感", "汽车", "历史", "时尚", "美食", "家居", "电影", "互联网", "摄影", "房产", "亲子", "搞笑", "科学探索",  "星座",  "大陆时事",  "萌宠", "电视娱乐",  "收藏", "it", "动漫", "生活", "音乐", "职场", "网球", "游戏",  "高科技产", "公益", "风水", "健身", "台湾",
                "移民", "港澳", "财经", "社会八卦", "数码", "国际", "足球", "健康", "明星", "文化"};

        defaultExploreList = new ArrayList<>();
        for (String tag : exploreTags) {
            RecordInfo recordInfo = new RecordInfo(tag, 0.6);
            defaultExploreList.add(recordInfo);
        }
    }


}