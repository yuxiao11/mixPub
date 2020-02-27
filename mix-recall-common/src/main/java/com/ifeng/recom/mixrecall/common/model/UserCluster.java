package com.ifeng.recom.mixrecall.common.model;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by liligeng on 2019/1/28.
 * 用户兴趣偏好
 *
 */
@Getter
@Setter
public class UserCluster {

    @Expose
    private List<String> bad;   //用户不感兴趣分类，前面更不感兴趣

    @Expose
    private String cate;   //文章分类

    @Expose
    private List<String> good;  //用户感兴趣分类

    @Expose
    private String isdeep;   //是否为深度用户

    @Expose
    private String sens;    //是否为标题党敏感用户

}
