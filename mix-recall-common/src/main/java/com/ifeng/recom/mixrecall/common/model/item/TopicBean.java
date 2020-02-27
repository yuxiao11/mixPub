package com.ifeng.recom.mixrecall.common.model.item;

import lombok.Getter;
import lombok.Setter;

/**
 * 内容画像中的
 * <p>
 * topic 结构体
 * Created by jibin on 2017/10/23.
 */
@Getter
@Setter
public class TopicBean {
    private String name;
    private String score;

    public TopicBean() {

    }


    public TopicBean(String name, String score) {
        this.name = name;
        this.score = score;
    }
}
