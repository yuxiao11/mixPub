package com.ifeng.recom.mixrecall.common.model.request;

import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.item.EvItem;
import com.ifeng.recom.mixrecall.common.model.item.LastDocBean;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户请求信息的封装对象
 */
@Accessors(chain = true)
@Setter
@Getter
public class MixRequestInfo implements Cloneable {
    /**
     * 用户id
     **/
    protected String uid;

    /**
     * 增量调用召回接口时指定 召回的size大小
     */
    protected int size;

    /**
     * 请求来源
     */
    protected String flowType;

    /**
     * 判断是否是debug用户
     */
    private boolean debugUser = false;

    /**
     * 通道召回参数的控制
     */
    private LogicParams logicParams;

    /**
     * abtest分组map，key为实验类型、value为具体标记
     */
    protected Map<String, String> abTestMap = Maps.newHashMap();

    /**
     * 用户的点击记录
     */
    private List<LastDocBean> lastDocBeans;

    /**
     * 用户曝光队列
     */
    private List<EvItem> evItems;


    /**
     * 指定召回时使用的coTag, 用于实时响应的 last CoTag召回
     */
    private String lastCotag;


    /**
     * 用户画像信息
     */
    private UserModel userModel;

    /**
     * 区分头条频道流量和推荐频道流量
     * 头条值为： headline
     * 推荐值为： recom
     */
    private String recomChannel;


    /**
     * 不压缩，压缩消耗CPU很内存，而且只是在storm调召回的时候压缩，后面写kafka又不压缩了  ^_^!!!
     */
    private boolean compress = true;

    /**
     * 渠道标识
     */
    protected String proid;


    protected String recallid;

    /**
     * 标记当前用户的用户类型
     */
    protected Map<String, Boolean> userTypeMap = Maps.newHashMap();

    /**
     * 标记相关业务信息：
     * threadHold_title_filter：组合词过滤分
     */
    protected Map<String, String> devMap = Maps.newHashMap();

    /**
     * 负反馈map，用来传递参数使用
     */
    protected Map<String, List<String>> negMaps = Maps.newHashMap();
    /**
     * 3刷以内限制机构，以外不过滤
     */
    private int pullCount = 4;

    /**
     *  添加用户负反馈信息
     *  格式为Map<String,List<String>
     */
    private Map<String,Map<String,Double>> negativeMap = Maps.newHashMap();


    /**
     * 添加标识 是否为动态策略用户
     */
    private Boolean pullNumTag = false;

    /**
     * 渠道标识
     */
    protected String publishid;
    /**
     * 添加abtest信息
     *
     * @param abtestGroup
     * @param expFlag
     */
    public void addAbtestInfo(String abtestGroup, String expFlag) {
        this.abTestMap.put(abtestGroup, expFlag);
    }

    /**
     * 检查abtestGroup 下的实验名称是否满足待判断的expFlag 情况
     *
     * @param abtestGroup
     * @param expFlag2Check
     * @return
     */
    public boolean checkAbtestInfo(String abtestGroup, String expFlag2Check) {
        if (StringUtils.isBlank(expFlag2Check)) {
            return false;
        }
        String flagNow = this.abTestMap.get(abtestGroup);
        return expFlag2Check.equals(flagNow);
    }

    @Override
    public String toString() {
        return "MixRequestInfo{" +
                "uid='" + uid + '\'' +
                ", size=" + size +
                ", flowType='" + flowType + '\'' +
                ", debugUser=" + debugUser +
                ", logicParams=" + logicParams +
                ", abTestMap=" + abTestMap +
                ", lastDocBeans=" + lastDocBeans +
                ", userModel=" + userModel +
                '}';
    }

    @Override
    public MixRequestInfo clone() throws CloneNotSupportedException {
        MixRequestInfo clone;
        clone = (MixRequestInfo) super.clone();

        return clone;
    }
}
