package com.ifeng.recom.mixrecall.common.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserModel {

    /**
     * 热点事件
     */
    private String e;
    /**
     * 优质稿源
     */
    private String s;
    /**
     * 以下字段为FM模型使用字段 解析方式不用于Mix  所以以String类型存储
     */
    private String ub_fm;
    private String general_ub_fm;
    private String ui_fm;
    private String uk_fm;
    private String search_fm;
    private String general_search_fm;

    private String group_ub;
    /**
     * 周末对视频喜好程度
     */
    private String general_likeVidR_weekends;
    private String general_likeVidR_dayInWork;
    private String general_likeVidR_nightInWork;

    /**
     * 用户对文章时效敏感度
     */
    private String general_doc_timeSensitive;

    /**
     * 用户对视频时效敏感度
     */
    private String general_vid_timeSensitive;




    /**
     * 用户不同时间段图文sc
     */
    private String docpic_sc_period;

    /**
     *用户不同时间段视频sc
     */
    private String video_sc_period;

    /**
     * 用户group
     */
    private String user_group;






    private String userType;
    private String userId;
    private String umt;
    private String umos;
    private String gender;
    private String ua;
    private String upoi;
    private String userChannel;
    private String net;
    private String loc;
    private String first_in;
    private int ua_v;

    private Boolean likevideo;

    private List<String> ui;
    private List<String> uk;
    private List<String> eList;
    private List<String> swopen;
    private List<String> lastitems;
    private String lastIn;

    private List<RecordTime> ub;
    private List<RecordTime> search;
    private String lng;
    private String lat;
    private String subLocality;
    private String city;
    private String state;
    private String street;
    private String user_cluster;

    /**
     * add daily pullNum By YX
     */
    private String daily_pullnum;

    private List<RecordInfo> loginInterest;
    private List<RecordInfo> searchRecordList;

    private List<RecordInfo> t1RecordList;
    private List<RecordInfo> t2RecordList;
    private List<RecordInfo> t3RecordList;

    private List<RecordInfo> Lastt1RecordList;
    private List<RecordInfo> Lastt2RecordList;
    private List<RecordInfo> Lastt3RecordList;

    private List<RecordInfo> recentt1RecordList;
    private List<RecordInfo> recentt2RecordList;
    private List<RecordInfo> recentt3RecordList;

    private List<RecordInfo> vidt1RecordList;
    private List<RecordInfo> vidt2RecordList;
    private List<RecordInfo> vidt3RecordList;

    /**
     * 图文的兴趣
     */
    private List<RecordInfo> docpic_cotag;

    /**
     * 小视频的兴趣
     */
    private List<RecordInfo> svideo_cotagList;

    /**
     * 用户分群字段
     */
    private List<UserCluster> userClusterList;


    private List<RecordInfo> docpicLdaTopic;
    private List<RecordInfo> recentDocpicLdaTopic;

    private List<RecordInfo> combineTagList;
    private List<RecordInfo> recentCombineTagList;
    private List<RecordInfo> lastCombineTagList;

    private List<CotagSims> combineTagSimsList;
    private List<CotagSims> recentCombineTagSimsList;
    private List<CotagSims> lastCombineTagSimsList;

    private List<RecordInfo> slideT3RecordList;
    private List<RecordInfo> slideT1RecordList;
    private List<RecordInfo> slideT2RecordList;

    private List<SourceSims> t2SourceSims;
    private List<SourceSims> lastT2SourceSims;

    private List<SourceSims> ubSourceSims;
    private List<SourceSims> lastUbSourceSims;

    private List<RecordInfo> topic1Explore;

    private List<RecordInfo> long_uTopic;

    /**
     * 视频一级分类
     */
    private List<RecordInfo> video_cate;
    /**
     * 图文一级分类
     */
    private List<RecordInfo> docpic_cate;

    /**
     * 图文及视频一级，二级试探分类
     */
    private List<RecordInfo> docpic_explore;
    private List<RecordInfo> video_explore;

    private List<RecordInfo> docpic_sc_explore;
    private List<RecordInfo> video_sc_explore;
    /**
     图文二级分类
     */
    private List<RecordInfo> docpic_subcate;
    /**
     * 视频二级分类
     */
    private List<RecordInfo> video_subcate;

    /**
     * 小视频一级分类
     */
    private List<RecordInfo> svideo_cate;


    /**
     * 图文媒体
     */
    private List<RecordInfo> docpic_media;
    /**
     * 视频媒体
     */
    private List<RecordInfo> video_media;

    private List<String> lastCotagSim;

    private List<String> recentCotagDSim;

    private List<String> recentCotagVSim;


    /**
     * 中期图文媒体 add by yx 20191228
     */
    private List<RecordInfo> recent_docpic_media;
    /**
     * 中期视频媒体 add by yx 20191228
     */
    private List<RecordInfo> recent_video_media;

    /**
     * 不感兴趣的ucombinetag add by yx 20191228
     */
    private List<RecordInfo> last_dis_ucombine_tag;

    /**
     * 添加图谱字段 by yx 20191128
     */
    private List<String> recentCotagDGraph;
    private List<String> recentCotagVGraph;

    private List<String> longCotagDGraph;
    private List<String> longCotagVGraph;


    /**
     * 常驻地址
     */
    private String generalLoc;

    /**
     * 视频媒体
     */
    private List<RecordInfo> appUserInterest;

    private List<RecordInfo> recent_video_cotag;

    private List<RecordInfo> recent_docpic_cotag;

    private List<RecordInfo> lastLdaTopicList;

    /**
     * 画像丰满度
     */
    private String fullness;

    private List<RecordInfo> recentVideoSubCate;
    private List<RecordInfo> lastVideoSubCate;
    private List<RecordInfo> lastVideoCate;

    /**
     * 图文分类 所有字段 给ffm使用
     */
    private List<RecordInfo> recent_docpic_subcate;
    private List<RecordInfo> last_docpic_cate;
    private List<RecordInfo> last_docpic_subcate;


    /**
     * 粗排相关参数 add by yx 20191228
     * @return
     */

    private UserFeatureTn docpicSubcateTn = null;
    private UserFeatureTn recentDocpicSubcateTn = null;
    private UserFeatureTn videoSubcateTn = null;
    private UserFeatureTn recentVideoSubcateTn = null;

    //combineTag
    private UserFeatureTn last_ucombineTagTn = null;
    private UserFeatureTn last_dis_ucombineTagTn = null;
    private UserFeatureTn recent_ucombineTagTn = null;
    private UserFeatureTn recent_dis_ucombineTagTn = null;
    private UserFeatureTn ucombineTagTn = null;
    private UserFeatureTn dis_ucombineTagTn = null;

    // LDA topic偏好字段
    private UserFeatureTn docpicLDATopicFeature = null;
    private UserFeatureTn recentDocpicLDATopicFeature = null;

    //medias
    private UserFeatureTn docpicMediaFeature = null;
    private UserFeatureTn recentDocpicMediaFeature = null;
    private UserFeatureTn videoMediaFeature = null;
    private UserFeatureTn recentVideoMediaFeature = null;

    //图文sc
    private UserFeatureTn docpicSubcate = null;
    private UserFeatureTn recentDocpicSubcate = null;

    //视频sc
    private UserFeatureTn videoSubcate = null;
    private UserFeatureTn recentVideoSubcate = null;

    private String[] eArray = null; //热点
    private String[] sArray = null; //优质稿源
    private String[] ubArray = null; //
    private String[] general_ubArray = null; //
    private String[] uiArray = null; //
    private String[] ukArray = null; //
    private String[] searchArray = null; //
    private String[] general_searchArray = null; //


    private Set<String> groupUbSet = null;//


    public String getE(){return e;}
    public void setE(String e){
        this.e = e;
    }
    public String getS(){return s;}
    public void setS(String s){
        this.s = s;
    }

    public String getUb_fm(){return ub_fm;}
    public void setUb_fm(String ub_fm){
        this.ub_fm = ub_fm;
    }

    public String getGeneral_ub_fm(){return general_ub_fm;}
    public void setGeneral_ub_fm(String general_ub_fm){
        this.general_ub_fm = general_ub_fm;
    }

    public String getUi_fm(){return ui_fm;}
    public void setUi_fm(String ui_fm){
        this.ui_fm = ui_fm;
    }

    public String getUk_fm(){return uk_fm;}
    public void setUk_fm(String uk_fm){
        this.uk_fm = uk_fm;
    }

    public String getSearch_fm(){return search_fm;}
    public void setSearch_fm(String search_fm){
        this.search_fm = search_fm;
    }

    public String getGeneral_search_fm(){return general_search_fm;}
    public void setGeneral_Search_fm(String general_search_fm){
        this.general_search_fm = general_search_fm;
    }


    public List<RecordInfo> getRecent_docpic_subcate() {
        return recent_docpic_subcate;
    }

    public void setRecent_docpic_subcate(List<RecordInfo> recent_docpic_subcate) {
        this.recent_docpic_subcate = recent_docpic_subcate;
    }

    public List<RecordInfo> getLast_docpic_cate() {
        return last_docpic_cate;
    }

    public void setLast_docpic_cate(List<RecordInfo> last_docpic_cate) {
        this.last_docpic_cate = last_docpic_cate;
    }

    public List<RecordInfo> getLast_docpic_subcate() {
        return last_docpic_subcate;
    }

    public void setLast_docpic_subcate(List<RecordInfo> last_docpic_subcate) {
        this.last_docpic_subcate = last_docpic_subcate;
    }

    public List<RecordInfo> getRecentVideoSubCate() {
        return recentVideoSubCate;
    }

    public void setRecentVideoSubCate(List<RecordInfo> recentVideoSubCate) {
        this.recentVideoSubCate = recentVideoSubCate;
    }

//    public List<RecordInfo> getLastVideoSubCate() {
//        return lastVideoSubCate;
//    }

    public void setLastVideoSubCate(List<RecordInfo> lastVideoSubCate) {
        this.lastVideoSubCate = lastVideoSubCate;
    }



    public void setLastVideoCate(List<RecordInfo> lastVideoCate) {
        this.lastVideoCate = lastVideoCate;
    }

    public String getFullness() {
        return fullness;
    }

    public void setFullness(String fullness) {
        this.fullness = fullness;
    }

    public List<RecordInfo> getLastLdaDocpicTopicList() {
        return lastLdaDocpicTopicList;
    }

    public void setLastLdaDocpicTopicList(List<RecordInfo> lastLdaDocpicTopicList) {
        this.lastLdaDocpicTopicList = lastLdaDocpicTopicList;
    }

    private List<RecordInfo> lastLdaDocpicTopicList;

    public List<RecordInfo> getLastVideoCotag() {
        return lastVideoCotag;
    }

    public void setLastVideoCotag(List<RecordInfo> lastVideoCotag) {
        this.lastVideoCotag = lastVideoCotag;
    }

    private List<RecordInfo> lastVideoCotag;

    public List<RecordInfo> getRecent_docpic_cotag() {
        return recent_docpic_cotag;
    }

    public void setRecent_docpic_cotag(List<RecordInfo> recent_docpic_cotag) {
        this.recent_docpic_cotag = recent_docpic_cotag;
    }

    public List<RecordInfo> getLastLdaTopicList() {
        return lastLdaTopicList;
    }

    public void setLastLdaTopicList(List<RecordInfo> lastLdaTopicList) {
        this.lastLdaTopicList = lastLdaTopicList;
    }

    public List<String> getLastCotagSim() { //D
        return lastCotagSim;
    }

    public void setLastCotagSim(List<String> lastCotagSim) {
        this.lastCotagSim = lastCotagSim;
    }

    public List<String> getRecentCotagDSim() { return recentCotagDSim; }

    public void setRecentCotagDSim(List<String> recentCotagDSim) {
        this.recentCotagDSim = recentCotagDSim;
    }

    public List<String> getRecentCotagVSim() {
        return recentCotagVSim;
    }

    public void setRecentCotagVSim(List<String> recentCotagVSim) {
        this.recentCotagVSim = recentCotagVSim;
    }




    public List<String> getRecentCotagDGraph() { return recentCotagDGraph; }

    public void setRecentCotagDGraph(List<String> recentCotagDGraph) {
        this.recentCotagDGraph = recentCotagDGraph;
    }

    public List<String> getRecentCotagVGraph() { return recentCotagVGraph; }

    public void setRecentCotagVGraph(List<String> recentCotagVGraph) {
        this.recentCotagVGraph = recentCotagVGraph;
    }





    public List<String> getLongCotagDGraph() { return longCotagDGraph; }

    public void setLongCotagDGraph(List<String> longCotagDGraph) {
        this.longCotagDGraph = longCotagDGraph;
    }

    public List<String> getLongCotagVGraph() { return longCotagVGraph; }

    public void setLongCotagVGraph(List<String> longCotagVGraph) {
        this.longCotagVGraph = longCotagVGraph;
    }



    public List<RecordInfo> getAppUserInterest() {
        return appUserInterest;
    }

    public void setAppUserInterest(List<RecordInfo> appUserInterest) {
        this.appUserInterest = appUserInterest;
    }

    public List<RecordInfo> getDocpic_media() {
        return docpic_media;
    }

    public void setDocpic_media(List<RecordInfo> docpic_media) {
        this.docpic_media = docpic_media;
    }

    public List<RecordInfo> getRecentDocpic_media() {
        return recent_docpic_media;
    }

    /**
     * 添加粗排需要的画像信息 add by yx 20191228
     * @param recent_docpic_media
     */
    public void setRecentDocpic_media(List<RecordInfo> recent_docpic_media) {
        this.recent_docpic_media = recent_docpic_media;
    }

    public List<RecordInfo> getRecentVideo_media() {
        return recent_video_media;
    }

    public void setRecentVideo_media(List<RecordInfo> recent_video_media) {
        this.recent_video_media = recent_video_media;
    }

    public List<RecordInfo> getLastDisUcombineTag() { return last_dis_ucombine_tag;}

    public void setLastDisUcombineTag(List<RecordInfo> last_dis_ucombine_tag){
        this.last_dis_ucombine_tag = last_dis_ucombine_tag;
    }


    public List<RecordInfo> getVideo_media() {
        return video_media;
    }

    public void setVideo_media(List<RecordInfo> video_media) {
        this.video_media = video_media;
    }

    public List<RecordInfo> getSvideo_cate() {
        return svideo_cate;
    }

    public void setSvideo_cate(List<RecordInfo> svideo_cate) {
        this.svideo_cate = svideo_cate;
    }

    public String getGeneralLoc() {
        return generalLoc;
    }

    public void setGeneralLoc(String generalLoc) {
        this.generalLoc = generalLoc;
    }

    public List<RecordInfo> getDocpic_sc_explore() {
        return docpic_sc_explore;
    }

    public void setDocpic_sc_explore(List<RecordInfo> docpic_sc_explore) {
        this.docpic_sc_explore = docpic_sc_explore;
    }

    public List<RecordInfo> getVideo_sc_explore() {
        return video_sc_explore;
    }

    public void setVideo_sc_explore(List<RecordInfo> video_sc_explore) {
        this.video_sc_explore = video_sc_explore;
    }
    public List<RecordInfo> getVideo_cate() {
        return video_cate;
    }

    public void setVideo_cate(List<RecordInfo> video_cate) {
        this.video_cate = video_cate;
    }

    public List<RecordInfo> getDocpic_cate() {
        return docpic_cate;
    }

    public void setDocpic_cate(List<RecordInfo> docpic_cate) {
        this.docpic_cate = docpic_cate;
    }

    public List<RecordInfo> getLast_uTopic() {
        return last_uTopic;
    }

    public void setLast_uTopic(List<RecordInfo> last_uTopic) {
        this.last_uTopic = last_uTopic;
    }

    private List<RecordInfo> last_uTopic;
    private Boolean isFromRedis;

    public List<RecordInfo> getVideo_cotag() {
        return video_cotag;
    }

    public void setVideo_cotag(List<RecordInfo> video_cotag) {
        this.video_cotag = video_cotag;
    }

    private List<RecordInfo> video_cotag;

    public List<RecordInfo> getRecent_video_cotag() {
        return recent_video_cotag;
    }

    public void setRecent_video_cotag(List<RecordInfo> recent_video_cotag) {
        this.recent_video_cotag = recent_video_cotag;
    }


    public UserModel() {
    }

    public UserModel(String userId) {
        this.userId = userId;
    }

    public String getLastIn() {
        return lastIn;
    }

    public void setLastIn(String lastIn) {
        this.lastIn = lastIn;
    }

    public String getFirst_in() {
        return first_in;
    }

    public void setFirst_in(String first_in) {
        this.first_in = first_in;
    }

    public Boolean getFromRedis() {
        return isFromRedis;
    }

    public void setFromRedis(Boolean fromRedis) {
        isFromRedis = fromRedis;
    }

    public List<RecordInfo> getTopic1Explore() {
        return topic1Explore;
    }

    public void setTopic1Explore(List<RecordInfo> topic1Explore) {
        this.topic1Explore = topic1Explore;
    }

    public List<RecordInfo> getLong_uTopic() {
        return long_uTopic;
    }

    public void setLong_uTopic(List<RecordInfo> long_uTopic) {
        this.long_uTopic = long_uTopic;
    }

    public List<RecordInfo> getRecentt3RecordList() {
        return recentt3RecordList;
    }

    public void setRecentt3RecordList(List<RecordInfo> recentt3RecordList) {
        this.recentt3RecordList = recentt3RecordList;
    }

    public List<RecordInfo> getVidt1RecordList() {
        return vidt1RecordList;
    }

    public void setVidt1RecordList(List<RecordInfo> vidt1RecordList) {
        this.vidt1RecordList = vidt1RecordList;
    }

    public List<RecordInfo> getVidt2RecordList() {
        return vidt2RecordList;
    }

    public void setVidt2RecordList(List<RecordInfo> vidt2RecordList) {
        this.vidt2RecordList = vidt2RecordList;
    }

    public List<RecordInfo> getVidt3RecordList() {
        return vidt3RecordList;
    }

    public void setVidt3RecordList(List<RecordInfo> vidt3RecordList) {
        this.vidt3RecordList = vidt3RecordList;
    }

    public List<RecordInfo> getDocpicLdaTopic() {
        return docpicLdaTopic;
    }

    public void setDocpicLdaTopic(List<RecordInfo> docpicLdaTopic) {
        this.docpicLdaTopic = docpicLdaTopic;
    }

    public List<RecordInfo> getRecentDocpicLdaTopic() {
        return recentDocpicLdaTopic;
    }

    public void setRecentDocpicLdaTopic(List<RecordInfo> recentDocpicLdaTopic) {
        this.recentDocpicLdaTopic = recentDocpicLdaTopic;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public List<UserCluster> getUserClusterList() {
        return userClusterList;
    }

    public void setUserClusterList(List<UserCluster> userClusterList) {
        this.userClusterList = userClusterList;
    }

    public Boolean getLikevideo() {
        return likevideo;
    }

    public void setLikevideo(Boolean likevideo) {
        this.likevideo = likevideo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getUpoi() {
        return upoi;
    }

    public void setUpoi(String upoi) {
        this.upoi = upoi;
    }

    public List<RecordTime> getUb() {
        return ub;
    }

    public void setUb(List<RecordTime> ub) {
        this.ub = ub;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getSubLocality() {
        return subLocality;
    }

    public void setSubLocality(String subLocality) {
        this.subLocality = subLocality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Add Daily_pullNum By YX
     * @return
     */
    public String getDaily_pullNum() {
        return daily_pullnum;
    }

    public void setDaily_pullNum(String daily_pullnum) {
        this.daily_pullnum = daily_pullnum;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getUserChannel() {
        return userChannel;
    }

    public void setUserChannel(String userChannel) {
        this.userChannel = userChannel;
    }

    public List<RecordInfo> getRecentCombineTagList() {
        return recentCombineTagList;
    }

    public void setRecentCombineTagList(List<RecordInfo> recentCombineTagList) {
        this.recentCombineTagList = recentCombineTagList;
    }

    public List<RecordInfo> getCombineTagList() {
        return combineTagList;
    }

    public void setCombineTagList(List<RecordInfo> combineTagList) {
        this.combineTagList = combineTagList;
    }

    public List<RecordInfo> getLastCombineTagList() {
        return lastCombineTagList;
    }

    public void setLastCombineTagList(List<RecordInfo> lastCombineTagList) {
        this.lastCombineTagList = lastCombineTagList;
    }

    public List<CotagSims> getCombineTagSimsList() {
        return combineTagSimsList;
    }

    public void setCombineTagSimsList(List<CotagSims> combineTagSimsList) {
        this.combineTagSimsList = combineTagSimsList;
    }

    public List<CotagSims> getRecentCombineTagSimsList() {
        return recentCombineTagSimsList;
    }

    public void setRecentCombineTagSimsList(List<CotagSims> recentCombineTagSimsList) {
        this.recentCombineTagSimsList = recentCombineTagSimsList;
    }

    public List<CotagSims> getLastCombineTagSimsList() {
        return lastCombineTagSimsList;
    }

    public void setLastCombineTagSimsList(List<CotagSims> lastCombineTagSimsList) {
        this.lastCombineTagSimsList = lastCombineTagSimsList;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public List<RecordInfo> getSlideT3RecordList() {
        return slideT3RecordList;
    }

    public void setSlideT3RecordList(List<RecordInfo> slideT3RecordList) {
        this.slideT3RecordList = slideT3RecordList;
    }

    public List<RecordInfo> getSlideT1RecordList() {
        return slideT1RecordList;
    }

    public void setSlideT1RecordList(List<RecordInfo> slideT1RecordList) {
        this.slideT1RecordList = slideT1RecordList;
    }

    public List<RecordInfo> getSlideT2RecordList() {
        return slideT2RecordList;
    }

    public void setSlideT2RecordList(List<RecordInfo> slideT2RecordList) {
        this.slideT2RecordList = slideT2RecordList;
    }

    public List<SourceSims> getUbSourceSims() {
        return ubSourceSims;
    }

    public void setUbSourceSims(List<SourceSims> ubSourceSims) {
        this.ubSourceSims = ubSourceSims;
    }

    public List<SourceSims> getLastT2SourceSims() {
        return lastT2SourceSims;
    }

    public void setLastT2SourceSims(List<SourceSims> lastT2SourceSims) {
        this.lastT2SourceSims = lastT2SourceSims;
    }

    public List<SourceSims> getLastUbSourceSims() {
        return lastUbSourceSims;
    }

    public void setLastUbSourceSims(List<SourceSims> lastUbSourceSims) {
        this.lastUbSourceSims = lastUbSourceSims;
    }

    public List<SourceSims> getT2SourceSims() {
        return t2SourceSims;
    }

    public void setT2SourceSims(List<SourceSims> t2SourceSims) {
        this.t2SourceSims = t2SourceSims;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUmt() {
        return umt;
    }

    public void setUmt(String umt) {
        this.umt = umt;
    }

    public void setUmos(String umos){
        this.umos = umos;
    }

    public String getUmos(){return this.umos;}


    public List<String> getUi() {
        return ui;
    }

    public void setUi(List<String> ui) {
        this.ui = ui;
    }

    public List<RecordTime> getSearch() {
        return search;
    }

    public void setSearch(List<RecordTime> search) {
        this.search = search;
    }

    public List<String> getUk() {
        return uk;
    }

    public void setUk(List<String> uk) {
        this.uk = uk;
    }

    public List<String> geteList() {
        return eList;
    }

    public void seteList(List<String> eList) {
        this.eList = eList;
    }

    public List<String> getSwopen() {
        return swopen;
    }

    public void setSwopen(List<String> swopen) {
        this.swopen = swopen;
    }

    public List<String> getLastitems() {
        return lastitems;
    }

    public void setLastitems(List<String> lastitems) {
        this.lastitems = lastitems;
    }

    public List<RecordInfo> getLoginInterest() {
        return loginInterest;
    }

    public void setLoginInterest(List<RecordInfo> loginInterest) {
        this.loginInterest = loginInterest;
    }

    public List<RecordInfo> getT1RecordList() {
        return t1RecordList;
    }

    public void setT1RecordList(List<RecordInfo> t1RecordList) {
        this.t1RecordList = t1RecordList;
    }

    public List<RecordInfo> getT2RecordList() {
        return t2RecordList;
    }

    public void setT2RecordList(List<RecordInfo> t2RecordList) {
        this.t2RecordList = t2RecordList;
    }

    public List<RecordInfo> getT3RecordList() {
        return t3RecordList;
    }

    public void setT3RecordList(List<RecordInfo> t3RecordList) {
        this.t3RecordList = t3RecordList;
    }

    public List<RecordInfo> getLastt1RecordList() {
        return Lastt1RecordList;
    }

    public void setLastt1RecordList(List<RecordInfo> lastt1RecordList) {
        Lastt1RecordList = lastt1RecordList;
    }

    public List<RecordInfo> getLastt2RecordList() {
        return Lastt2RecordList;
    }

    public void setLastt2RecordList(List<RecordInfo> lastt2RecordList) {
        Lastt2RecordList = lastt2RecordList;
    }

    public List<RecordInfo> getLastt3RecordList() {
        return Lastt3RecordList;
    }

    public void setLastt3RecordList(List<RecordInfo> lastt3RecordList) {
        Lastt3RecordList = lastt3RecordList;
    }

    public List<RecordInfo> getRecentt1RecordList() {
        return recentt1RecordList;
    }

    public void setRecentt1RecordList(List<RecordInfo> recentt1RecordList) {
        this.recentt1RecordList = recentt1RecordList;
    }

    public List<RecordInfo> getSearchRecordList() {
        return searchRecordList;
    }

    public void setSearchRecordList(List<RecordInfo> searchRecordList) {
        this.searchRecordList = searchRecordList;
    }

    public List<RecordInfo> getRecentt2RecordList() {
        return recentt2RecordList;
    }

    public void setRecentt2RecordList(List<RecordInfo> recentt2RecordList) {
        this.recentt2RecordList = recentt2RecordList;
    }

    public int getUa_v() {
        return ua_v;
    }

    public void setUa_v(int ua_v) {
        this.ua_v = ua_v;
    }

    public List<RecordInfo> getDocpic_cotag() {
        return docpic_cotag;
    }

    public void setDocpic_cotag(List<RecordInfo> docpic_cotag) {
        this.docpic_cotag = docpic_cotag;
    }

    public List<RecordInfo> getSvideo_cotagList() {
        return svideo_cotagList;
    }

    public void setSvideo_cotagList(List<RecordInfo> svideo_cotagList) {
        this.svideo_cotagList = svideo_cotagList;
    }

    public List<RecordInfo> getDocpic_explore() {
        return docpic_explore;
    }

    public void setDocpic_explore(List<RecordInfo> docpic_explore) {
        this.docpic_explore = docpic_explore;
    }

    public List<RecordInfo> getVideo_explore() {
        return video_explore;
    }

    public void setVideo_explore(List<RecordInfo> video_explore) {
        this.video_explore = video_explore;
    }

    public List<RecordInfo> getDocpic_subcate() {
        return docpic_subcate;
    }

    public void setDocpic_subcate(List<RecordInfo> docpic_subcate) {
        this.docpic_subcate = docpic_subcate;
    }

    public List<RecordInfo> getVideo_subcate() {
        return video_subcate;
    }

    public void setVideo_subcate(List<RecordInfo> video_subcate) {
        this.video_subcate = video_subcate;
    }

    /**
     * 用户阅读习惯的分组特征
     * added by zhaohh @ 2018-10-17 16:42
     */
    public String getUser_cluster() {
        return user_cluster;
    }
    public void setUser_cluster(String user_cluster) {
        this.user_cluster = user_cluster;
    }
    public static class UserClusterElement {
        public String cate;
        public String isdeep;
        public List<String> good;
        public List<String> bad;
        public List<String> vgood;
        public List<String> vbad;

        public List<String> getGood() {
            return good;
        }

        public void setGood(List<String> good) {
            this.good = good;
        }

        public List<String> getBad() {
            return bad;
        }

        public void setBad(List<String> bad) {
            this.bad = bad;
        }

        public List<String> getVgood() {
            return vgood;
        }

        public void setVgood(List<String> vgood) {
            this.vgood = vgood;
        }

        public List<String> getVbad() {
            return vbad;
        }

        public void setVbad(List<String> vbad) {
            this.vbad = vbad;
        }
    }
    private List<UserClusterElement> userClusterFeature = null;
    public synchronized List<UserClusterElement> getUserClusterFeature() {
        if (userClusterFeature != null) {
            return userClusterFeature;
        }
        if (user_cluster == null || user_cluster.equals("-")) {
            return null;
        }

        this.userClusterFeature = JsonUtils.json2Object(user_cluster, new TypeReference<List<UserClusterElement>>() {});

        return this.userClusterFeature;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userId='" + userId + '\'' +
                ", combineTagList=" + combineTagList +
                '}';
    }

    /**
     * 封装用户画像Tn特征的内部类
     */
    public static class UserFeatureTn implements Serializable {
        private static final long serialVersionUID = -3006200800927096231L;

        private String featureName;
        private List<UserFeatureWord> featureWordList;
        private Map<String,List<UserFeatureWord>> featureWordMap;
        private List<UserFeatureWord> tailWordList;
        private boolean isNewPortrait;

        public Map<String, List<UserFeatureWord>> getFeatureWordMap() {
            return featureWordMap;
        }
        public void setFeatureWordMap(Map<String, List<UserFeatureWord>> featureWordMap) {
            this.featureWordMap = featureWordMap;
        }
        public UserFeatureTn(String featureName, boolean isNewPortrait) {
            this.featureName = featureName;
            this.isNewPortrait = isNewPortrait;
            this.featureWordList = new ArrayList<>();
            this.tailWordList = new ArrayList<>();
            this.featureWordMap = new HashMap<>();
        }

        public void addUserFeatureWord(UserFeatureWord userFeatureWord) {
            this.featureWordList.add(userFeatureWord);
        }

        public void addDislikeFeatureWord(UserFeatureWord userFeatureWord) {
            this.tailWordList.add(userFeatureWord);
        }
        //按类别添加UserFeatureWord
        public void addUserFeatureWord(UserFeatureWord userFeatureWord,String type){
            if(this.featureWordMap ==null){
                this.featureWordMap = new HashMap<>();
            }
            if(this.featureWordMap.containsKey(type)){
                this.featureWordMap.get(type).add(userFeatureWord);
            }else{
                List<UserFeatureWord> featureWordListByType = new ArrayList<>();
                featureWordListByType.add(userFeatureWord);
                this.featureWordMap.put(type,featureWordListByType);
            }
        }
        public List<UserFeatureWord> getFeatureWordList() {
            return featureWordList;
        }
        public boolean isNewPortrait() {
            return isNewPortrait;
        }
        public String getFeatureName() {
            return featureName;
        }

        public List<UserFeatureWord> getTailWordList() {
            return tailWordList;
        }

        public void setFeatureWordList(List<UserFeatureWord> featureWordList) {
            this.featureWordList = featureWordList;
        }
        public void setNewPortrait(boolean newPortrait) {
            isNewPortrait = newPortrait;
        }
        public void setFeatureName(String featureName) {
            this.featureName = featureName;
        }

        // 排序后的点击特征词
        public synchronized List<UserFeatureWord> getPositiveSorted() {
            List<UserFeatureWord> positiveSorted = new ArrayList<>();
            for (UserFeatureWord userFeatureWord : featureWordList) {
                if (userFeatureWord.click > 0) {
                    positiveSorted.add(userFeatureWord);
                }
            }
            // 按照点击降序，曝光升序排列
            positiveSorted.sort(new Comparator<UserFeatureWord>() {
                @Override
                public int compare(UserFeatureWord o1, UserFeatureWord o2) {
                    if (o1.click != o2.click) {
                        return Integer.compare(o2.click, o1.click);
                    } else {
                        return Integer.compare(o1.impression, o2.impression);
                    }
                }
            });
            return positiveSorted;
        }

        // 排序后的未点击特征词
        public synchronized List<UserFeatureWord> getNegativeSorted() {
            List<UserFeatureWord> negativeSorted = new ArrayList<>();
            for (UserFeatureWord userFeatureWord : featureWordList) {
                if (userFeatureWord.click == 0) {
                    negativeSorted.add(userFeatureWord);
                }
            }
            // 按照曝光降序排列
            negativeSorted.sort(new Comparator<UserFeatureWord>() {
                @Override
                public int compare(UserFeatureWord o1, UserFeatureWord o2) {
                    return Integer.compare(o2.impression, o1.impression);
                }
            });
            return negativeSorted;
        }
    }






    public static class UProfileElement {
        public String n;
        public int c;
        public int e;
        public double s;
    }



    public static class UserFeatureWord implements Serializable {
        private static final long serialVersionUID = -1113683863995213489L;
        private String word;
        private double weight;
        private int impression;
        private int click;

        //combinetag中词元素
        public String level1;
        public String type1;
        public String level2;
        public String type2;
        public double cotagweight;
        public String tagStr;

        public UserFeatureWord(String word, double weight) {
            this.word = word;
            this.weight = weight;
        }

        public UserFeatureWord(String word, double weight, int impression, int click) {
            this.word = word;
            this.weight = weight;
            this.impression = impression;
            this.click = click;
        }

        public UserFeatureWord(String level1,String type1,String level2,String type2,Double cotagweight,String tagStr) {
            this.level1 = level1;
            this.type1 = type1;
            this.level2 = level2;
            this.type2 = type2;
            this.cotagweight = cotagweight;
            this.tagStr = tagStr;
        }
        public String getWord() {
            return word;
        }
        public double getWeight() {
            return weight;
        }

        public void setWord(String word) {
            this.word = word;
        }
        public void setWeight(double weight) {
            this.weight = weight;
        }

        public int getImpression() {
            return impression;
        }
        public void setImpression(int impression) {
            this.impression = impression;
        }

        public int getClick() {
            return click;
        }
        public void setClick(int click) {
            this.click = click;
        }

        public String getLevel1() {
            return level1;
        }

        public void setLevel1(String level1) {
            this.level1 = level1;
        }

        public String getType1() {
            return type1;
        }

        public void setType1(String type1) {
            this.type1 = type1;
        }

        public String getLevel2() {
            return level2;
        }

        public void setLevel2(String level2) {
            this.level2 = level2;
        }

        public String getType2() {
            return type2;
        }

        public void setType2(String type2) {
            this.type2 = type2;
        }

        public double getCotagweight() {
            return cotagweight;
        }

        public void setCotagweight(double cotagweight) {
            this.cotagweight = cotagweight;
        }

        public String getTagStr() {
            return tagStr;
        }

        public void setTagStr(String tagStr) {
            this.tagStr = tagStr;
        }
    }

    public void fillUserModel(){

        this.docpicMediaFeature = parseUserFeatureMedia("docpic_media", true, this.docpic_media);
        this.recentDocpicMediaFeature = parseUserFeatureMedia("recent_docpic_media", true, this.recent_docpic_media);
        this.videoMediaFeature = parseUserFeatureMedia("video_media", true, video_media);
        this.recentVideoMediaFeature = parseUserFeatureMedia("recent_video_media", true, recent_video_media);

        this.last_ucombineTagTn = parseUserFeatureCotag("last_ucombineTag",true,this.lastCombineTagList);
        this.last_dis_ucombineTagTn = parseUserFeatureCotag("last_dis_ucombineTag",true,this.last_dis_ucombine_tag);
        this.recent_ucombineTagTn = parseUserFeatureCotag("recent_ucombineTag",true,this.recentCombineTagList);
        this.ucombineTagTn = parseUserFeatureCotag("ucombineTag",true, this.combineTagList);

        this.docpicLDATopicFeature = parseLdaTopicFeature("docpic_lda_topic", this.docpicLdaTopic);
        this.recentDocpicLDATopicFeature = parseLdaTopicFeature("recent_docpic_lda_topic", this.recentDocpicLdaTopic);

        this.docpicSubcateTn = parseUserFeatureMedia("docpic_subcate", true, docpic_subcate);;
        this.recentDocpicSubcateTn =  parseUserFeatureMedia("recent_docpic_subcate", true, recent_docpic_subcate);
        this.videoSubcateTn = parseUserFeatureMedia("video_subcate", true, video_subcate);
        this.recentVideoSubcateTn = parseUserFeatureMedia("recent_video_subcate", true, recentVideoSubCate);



        //字段解析
        this.eArray = getEStr2Array(e);
        this.sArray = getEStr2Array(s);
        this.ubArray = getUBStr2Array(ub_fm);
        this.general_ubArray = getUBStr2Array(general_ub_fm);
        this.uiArray = getEStr2Array(ui_fm);
        this.ukArray = getEStr2Array(uk_fm);
        this.searchArray = getEStr2Array(search_fm);
        this.general_searchArray = getEStr2Array(general_search_fm);

        //用户分群
        this.userClusterFeature = getUserClusterFeature();


    }
    //热点事件&search#分隔字符串转Array,取第一个竖线前字符串，北京雷电黄色预警|2017-08-20+10:24:58#
    private String[] getEStr2Array(String str){
        if(str==null || str.isEmpty() || str.equals("-") || str.equals("\\N") || str.equals("NULL")){
            return new String[]{};
        }else{
            String arr[] = str.replace(",","").split("#");
            for(int i=0;i<arr.length;i++){
                int index = arr[i].indexOf("|");
                if (index>-1){
                    arr[i]=arr[i].substring(0,index);
                }
            }
            return arr;
        }
    }

    //订阅#分隔字符串转Array，取第一个和第二个竖线间字符串，weMedia_722881|乐游科技知识|1503041362#
    private String[] getUBStr2Array(String str){
        if(str==null || str.isEmpty() || str.equals("-") || str.equals("\\N") || str.equals("NULL")){
            return new String[]{};
        }else{
            String arr[] = str.replace(",","").split("#");
            for(int i=0;i<arr.length;i++){
                int index1 = arr[i].indexOf("|");
                int index2 = arr[i].indexOf("|",index1+1);
                if (index1>-1 && index2>-1){
                    arr[i]=arr[i].substring(index1+1,index2);
                }
            }
            return arr;
        }
    }




    private UserFeatureTn parseUserFeatureCotag(String featureName, boolean isNewPortrait, List<RecordInfo> cotagSet) {
        if (cotagSet == null || cotagSet.size() == 0) {
            return null;
        }

        UserFeatureTn userFeatureTn = new UserFeatureTn(featureName, isNewPortrait);


        for(RecordInfo element : cotagSet) {
            int expo = element.getExpose();
            int click = element.getReadFrequency();
            String tagStr = element.getRecordName();
            double weight = element.getWeight();

            UserFeatureWord userFeatureWord = new UserFeatureWord(tagStr, weight, expo, click);
            if(expo >= 30 && click <= 1){
                userFeatureTn.addDislikeFeatureWord(userFeatureWord);
            }
            userFeatureTn.addUserFeatureWord(userFeatureWord);
        }
        return userFeatureTn;
    }

    private UserFeatureTn parseUserFeatureMedia(String featureName, boolean isNewPortrait, List<RecordInfo> mediaList) {
        if (mediaList == null || mediaList.size() == 0) {
            return null;
        }

        UserFeatureTn userFeatureTn = new UserFeatureTn(featureName, isNewPortrait);

        for(RecordInfo element : mediaList) {
            int expo = element.getExpose();
            int click = element.getReadFrequency();
            String tagStr = element.getRecordName();
            double weight = element.getWeight();

            UserFeatureWord userFeatureWord = new UserFeatureWord(tagStr, weight, expo, click);
            if(weight < 0.4){
                userFeatureTn.addDislikeFeatureWord(userFeatureWord);
            }
            userFeatureTn.addUserFeatureWord(userFeatureWord);

        }


        return userFeatureTn;
    }
    /**
     * 解析用户lda_topic信息
     * added by zhaohh @ 2018-08-29 15:40
     */
    private UserFeatureTn parseLdaTopicFeature(String featureName, List<RecordInfo> ldaList) {
        if (ldaList == null || ldaList.size() == 0) {
            return null;
        }
        UserFeatureTn userFeatureTn = new UserFeatureTn(featureName, true);
        for (RecordInfo element : ldaList) {
            String ldaTopic = element.getRecordName();
            int clicks = element.getReadFrequency();
            int impressions = element.getExpose();
            double weight = element.getWeight();

            UserFeatureWord topicFeatureWord = new UserFeatureWord(ldaTopic, weight, impressions, clicks);
            if(impressions >= 30 && clicks <= 1){
                userFeatureTn.addDislikeFeatureWord(topicFeatureWord);
            }
            userFeatureTn.addUserFeatureWord(topicFeatureWord);
        }
        return userFeatureTn;
    }

    public UserFeatureTn getDocpicSubcate() {
        if(docpicSubcate != null) {
            return docpicSubcate;
        }
        this.docpicSubcate = parseUserFeatureMedia("docpic_subcate", true, docpic_subcate);
        return this.docpicSubcate;
    }

    public UserFeatureTn getRecentDocpicSubcate() {
        if(recentDocpicSubcate != null) {
            return recentDocpicSubcate;
        }
        this.recentDocpicSubcate = parseUserFeatureMedia("recent_docpic_subcate", true, recent_docpic_subcate);
        return recentDocpicSubcate;
    }

    public UserFeatureTn getLastDocpicSubcate() {
        return parseUserFeatureMedia("last_docpic_subcate", true, last_docpic_subcate);
    }

    public UserFeatureTn getVideoSubcate() {
        if(videoSubcate != null) {
            return videoSubcate;
        }
        this.videoSubcate = parseUserFeatureMedia("video_subcate", true, video_subcate);
        return this.videoSubcate;
    }

    public UserFeatureTn getRecentVideoSubcate() {
        if(recentVideoSubcate != null) {
            return recentVideoSubcate;
        }
        this.recentVideoSubcate = parseUserFeatureMedia("recent_video_subcate", true, recentVideoSubCate);
        return this.recentVideoSubcate;
    }

    public UserFeatureTn getDocpicLDATopicFeature() {
        if (docpicLDATopicFeature != null) {
            return docpicLDATopicFeature;
        }
        this.docpicLDATopicFeature = parseLdaTopicFeature("docpic_lda_topic", docpicLdaTopic);
        return this.docpicLDATopicFeature;
    }
    public UserFeatureTn getRecentDocpicLDATopicFeature() {
        if (recentDocpicLDATopicFeature != null) {
            return recentDocpicLDATopicFeature;
        }
        this.recentDocpicLDATopicFeature = parseLdaTopicFeature("recent_docpic_lda_topic", recentDocpicLdaTopic);
        return this.recentDocpicLDATopicFeature;
    }

    public UserFeatureTn getLastLDATopicFeature() {
        return parseLdaTopicFeature("last_lda_topic", lastLdaDocpicTopicList );
    }


    public UserFeatureTn getLastDocpicCate() {
        return parseUserFeatureMedia("last_docpic_cate", true, last_docpic_cate);
    }


    public UserFeatureTn getLastVideoCateTN() {
        return parseUserFeatureMedia("last_video_cate", true, lastVideoCate);
    }

    public UserFeatureTn getLastVideoSubcateTN() {
        return parseUserFeatureMedia("last_video_subcate", true, lastVideoSubCate);
    }

    public static class UserScPeriod{
        public List<UProfileElement> weekday_morning;
        public List<UProfileElement> weekday_noon;
        public List<UProfileElement> weekday_afternoon;
        public List<UProfileElement> weekday_night;
        public List<UProfileElement> weekend_morning;
        public List<UProfileElement> weekend_noon;
        public List<UProfileElement> weekend_afternoon;
        public List<UProfileElement> weekend_night;
    }

    public UserScPeriod docpicScPeriod = null;
    public synchronized UserScPeriod getDocpicScPeriod(){
        if(docpicScPeriod != null) {
            return docpicScPeriod;
        }
        if(docpic_sc_period == null || docpic_sc_period.equals("-")) {
            return null;
        }
        try{
            docpicScPeriod = JsonUtils.json2Object(docpic_sc_period, new TypeReference<UserScPeriod>(){});
            return docpicScPeriod;
        }catch (Exception e){
            return null;
        }
    }
    public UserScPeriod videoScPeriod = null;
    public synchronized UserScPeriod getVideoScPeriod(){
        if(videoScPeriod != null) {
            return videoScPeriod;
        }
        if(video_sc_period == null || video_sc_period.equals("-")) {
            return null;
        }
        try{
            videoScPeriod = JsonUtils.json2Object(video_sc_period, new TypeReference<UserScPeriod>() {});
            return videoScPeriod;
        }catch (Exception e) {
            return null;
        }
    }


    public synchronized Set<String> getGroupUbSet() {
        if (groupUbSet != null) {
            return groupUbSet;
        }
        if (group_ub == null) {
            return null;
        }
        groupUbSet = new HashSet<>();
        try {
            if(group_ub.equals("-")){
                return groupUbSet;
            }
            JSONObject ubObject = JSON.parseObject(group_ub);
            for (String key : ubObject.keySet()) {
                String ubStr = ubObject.getString(key);
                if (!StringUtils.isEmpty(ubStr)) {
                    for (String oneUbStr : ubStr.split("#")) {
                        String [] arr = oneUbStr.split("\\|");
                        // 排除用户对用户的关注，参见http://know.mnews.ifeng.com/pages/viewpage.action?pageId=2425532
                        if (arr.length == 3 && !arr[0].startsWith("user_") && StringUtils.isNotBlank(arr[1])) {
                            groupUbSet.add(arr[1]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupUbSet;
    }

    public String getUserGroup() {
        return user_group;
    }

    private Set<String> ubSet = null;
    public synchronized Set<String> getUbSet() {
        if (ubSet != null) {
            return ubSet;
        }
        if (ub_fm == null) {
            return null;
        }
        ubSet = new HashSet<>();
        try {
            if (!StringUtils.isEmpty(ub_fm)) {
                for (String oneUbStr : ub_fm.split("#")) {
                    String [] arr = oneUbStr.split("\\|");
                    // 排除用户对用户的关注，参见http://know.mnews.ifeng.com/pages/viewpage.action?pageId=2425532
                    if (arr.length == 2 && StringUtils.isNotBlank(arr[0])) {
                        ubSet.add(arr[0]);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ubSet;
    }

}