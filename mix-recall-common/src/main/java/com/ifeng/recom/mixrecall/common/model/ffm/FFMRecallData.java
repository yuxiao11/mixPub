package com.ifeng.recom.mixrecall.common.model.ffm;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by liligeng on 2019/6/11.
 */
@Getter
@Setter
public class FFMRecallData {

    // 文章simId
    private String simId;

    // 文章推荐id
    private String docId;

    // 召回模型得分，返回结果按照distance降序排列
    private double distance;

    // URL-Encoding之后的标题
    private String title;

    // 以及分类
    private String category;

}
