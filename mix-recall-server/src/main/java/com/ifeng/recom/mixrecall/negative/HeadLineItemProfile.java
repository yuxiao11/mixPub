package com.ifeng.recom.mixrecall.negative;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ifeng.recom.mixrecall.common.model.NewsPortraitRec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Created by jibin on 2017/5/12.
 */
public class HeadLineItemProfile implements Serializable {

    private static final long serialVersionUID = -1047937785517137135L;
    private static final Log logger = LogFactory.getLog(HeadLineItemProfile.class);

    /**
     * 新闻id（IKV唯一标识）
     */
    private String id;
    /**
     * 新15位cmppid 
     */
    private String newcmppid;
    /**
     * 旧自媒体id
     */
    private String subid;
    /**
     * 自媒体id
     */
    private String zmtid;
    /**
     * 文章simId
     */
    private String simId;
    /**
     * 文章来源url
     */
    private String url;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 分词后的标题
     */
    private String splitTitle;
    /**
     * 分词后的内容
     */
    private String splitContent;
    /**
     * 发布时间
     */
    private String publishedTime;
    /**
     * 文章稿源
     */
    private String source;
    /**
     * 这个item所对应的应用，如newspush、ifengapp等
     */
    private String appId;
    /**
     * item类型，如slide,doc,docpic,video
     */
    private String docType;
    /**
     * 美女分类的结果（目前无结果）
     */
    private boolean beauty;
    /**
     * 前端显示样式，支持从cmpp后端人工控制
     */
    private String showStyle;
    /**
     * 特征最后修改时间
     */
    private long modifyTime;
    /**
     * 抓取来的其他信息（来源，人工标签等内容）
     */
    private String other;
    /**
     * 地域识别结果
     */
    private List<String> loclist;
    /**
     * 人工标签和规则识别分类
     */
    private List<String> tags;
    /**
     * 文章分类提取结果（限定最多2个分类）
     */
    private List<String> category;
    /**
     * 隐含主题结果
     */
    private List<String> topic;
    /**
     * 热点事件结果
     */
    private List<String> hotEvent;
    /**
     * 最终特征词
     */
    private List<String> features;
    /**
     * 测试算法分类结果
     */
    private List<String> features2;
    /*
    媒体id
    */
    private String mediaId;
    /*数据获取来源*/
    private String original;
    /*是否原创*/
    private String isCreation;
    /*动态媒体评级*/
    private String dynamic_qualityEvalLevel;
    /*时效性*/
    private String timeSensitive;
    /*图片数量*/
    private String imgNum;
    /*文章内容汉字数*/
    private String CNwordNum;
    /*文章内容英文单词数*/
    private String ENwordNum;
    /*文章内容标点符号数*/
    private String biaodianNum;
    /*句子数*/
    private String sentenceNum;
    /*段落数*/
    private String paragraphNum;
    /*是否能转为slide类型*/
    private String canbeSlide;
    /*缩略图地址*/
    private String thumbnailpic;
    /*缩略图指纹*/
    private String picFingerprint;
    /*tipic*/
    private String latentTopic;
    /*t1-t2/t3拼接词*/
    private String combineTagList;

    /*文章长度*/
    private int newslenlevel;

    public int getNewslenlevel() {
        return newslenlevel;
    }

    public void setNewslenlevel(int newslenlevel) {
        this.newslenlevel = newslenlevel;
    }


    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    private String performance;
//****************     start ***************************************
    //**************** ikv统计数据，很多没有值， 请使用最下方的从金泽处获取的统计数据*********************
    /**
     * 文章用户数
     */
    private int uv;
    /**
     * 收藏数
     */
    private int storeNum;
    /**
     * 分享数
     */
    private int shareNum;
    /**
     * 文章互动数（点赞+评论）
     */
    private int joinCommentNum;
    /**
     * 推荐头条曝光量
     */
    private int tj_recNum;
    /**
     * 推荐头条点击量
     */
    private int tj_clickNum;
    /**
     * 是否低俗内容
     */
    private boolean porn;
    /**
     * 是否标题党
     */
    private boolean titleParty;
    /**
     * 质量评价得分
     */
    private String qualityEvalLevel;
    /**
     * LDA topic
     */
    private String ldaTopic;

    /**
     * coTags
     */
    private String coTags;

    public String getValidateCotags() {
        return validateCotags;
    }

    public void setValidateCotags(String validateCotags) {
        this.validateCotags = validateCotags;
    }

    public String getQualityLevel() {
        return qualityLevel;
    }

    public void setQualityLevel(String qualityLevel) {
        this.qualityLevel = qualityLevel;
    }

    /**
     * qualityLevel
     */
    private String qualityLevel;


    /**
     * validateCotags
     */
    private String validateCotags;

    public String getTimeSensitiveLevel() {
        return timeSensitiveLevel;
    }

    public void setTimeSensitiveLevel(String timeSensitiveLevel) {
        this.timeSensitiveLevel = timeSensitiveLevel;
    }

    /**
     *  时效性评级
     */
    private String timeSensitiveLevel;

    public String getDisType() {
        return disType;
    }

    public void setDisType(String disType) {
        this.disType = disType;
    }

    /**
     *  文章是否为精品池
     */
    private String disType;

    public String getSpecialParam() {
        return specialParam;
    }

    public void setSpecialParam(String specialParam) {
        this.specialParam = specialParam;
    }

    /**
     *  文章特殊字段
     */
    private String specialParam;



    //**************** end   请使用最下方的从金泽处获取的统计数据***************************************





    //实时的统计数据
    private String dumptime;
    private String last3h_ev;
    private String last3h_pv;
    private String last3h_share;
    private String last3h_store;
    private String last3h_comment;

    private String last1d_ev;
    private String last1d_pv;
    private String last1d_share;
    private String last1d_store;
    private String last1d_comment;

    private String today_ev;
    private String today_pv;
    private String today_share;
    private String today_store;
    private String today_comment;

    // 龙飞新增统计字段，只有ev，pv，store
    private String last3d_ev;
    private String last3d_pv;
    private String last3d_store;

    private String total_ev;
    private String total_pv;
    private String total_store;

    //最近1天平均阅读完成比
    private double last1d_avg_readrate;
    private double last1d_avg_duration;

    public double getLast1d_avg_readrate() {
        return last1d_avg_readrate;
    }

    public void setLast1d_avg_readrate(double last3d_avg_readrate) {
        this.last1d_avg_readrate = last3d_avg_readrate;
    }

    public double getLast1d_avg_duration() {
        return last1d_avg_duration;
    }

    public void setLast1d_avg_duration(double last3d_avg_duration) {
        this.last1d_avg_duration = last3d_avg_duration;
    }



    /**
     * 解析文章performance
     */
    private String performancevalue = null;

    public String getPerformancevalue() {
        if(performancevalue != null) {
            return performancevalue;
        }
        try{
            performancevalue = JSONObject.parseObject(performance).get("rank").toString();
            return performancevalue;
        }catch (Exception ex) {
            return null;
        }

    }

    public void setPerformancevalue(String performancevalue) {
        this.performancevalue = performancevalue;
    }


    /**
     * 解析文章cotag列表
     */
    public static class CotagElement {
        public String level1;
        public String type1;
        public String level2;
        public String type2;
        public double weight;
        public String tagStr;
    }
    private Set<String> coTagSet = null;
    public synchronized Set<String> getCoTagSet() {
        if (this.coTagSet != null) {
            return this.coTagSet;
        }
        if (this.coTags == null) {
            return null;
        }
        try {
            List<CotagElement> cotagElementList = JsonUtils.json2Object(coTags, new TypeReference<List<CotagElement>>(){});
            if (cotagElementList == null || cotagElementList.size() == 0) {
                return null;
            }
            Set<String> result = new HashSet<>();
            for (CotagElement element : cotagElementList) {
                String cotag = element.tagStr;
                result.add(cotag);
            }
            this.coTagSet = result;
            return result;
        } catch (Exception e) {
            logger.error("Parse cotag list error: " + coTags);
            return null;
        }
    }

    private Set<String> validateCotagSet = null;
    public synchronized Set<String> getvalidateCotagSet() {
        if (this.validateCotagSet != null) {
            return this.validateCotagSet;
        }
        if (this.validateCotags == null) {
            return null;
        }
        try {
            List<CotagElement> cotagElementList = JsonUtils.json2Object(validateCotags, new TypeReference<List<CotagElement>>(){});
            if (cotagElementList == null || cotagElementList.size() == 0) {
                return null;
            }
            Set<String> result = new HashSet<>();
            for (CotagElement element : cotagElementList) {
                String cotag = element.tagStr.split("-")[0];
                if(element.weight > 0.5){
                    result.add(cotag);
                }
            }
            this.validateCotagSet = result;
            return result;
        } catch (Exception e) {
            logger.error("Parse cotag list error: " + coTags);
            return null;
        }
    }

    /**
     * 解析sc
     */
    private List<String> scList = null;
    public synchronized List<String> getScList() {
        List<String> list = new ArrayList<>();
        if(this.scList != null){
            return this.scList;
        }
        if(this.getAlFeaturesTypeMap() == null || this.getAlFeaturesTypeMap().get("sc") == null) {
            return null;
        }
        List<FeatureWord> featureWords = this.getAlFeaturesTypeMap().get("sc");
        for(FeatureWord featureWord: featureWords) {
            if (featureWord == null || featureWord.word == null || Math.abs(featureWord.weight) <= 0.5) {
                continue;
            }
            list.add(featureWord.word);
        }
        this.scList = list;
        return list;
    }

    public synchronized List<String> getCateList() {
        List<String> list = new ArrayList<>();
        if(this.category != null) {
            return this.category;
        }
        if(this.getAlFeaturesTypeMap() == null || this.getAlFeaturesTypeMap().get("c") == null) {
            return null;
        }
        List<FeatureWord> featureWords = this.getAlFeaturesTypeMap().get("c");
        for(FeatureWord featureWord: featureWords) {
            if(featureWord == null || featureWord.word == null || Math.abs(featureWord.weight) <= 0.5) {
                continue;
            }
            list.add(featureWord.word);
        }
        this.category = list;
        return list;
    }

    /**
     * 解析lda topic
     */
    private List<String> ldaTopicList = null;
    public static class LdaTopicElement {
        public String topic;
        public double weight;
    }
    public synchronized List<String> getLdaTopicList() {
        if (this.ldaTopicList != null) {
            return this.ldaTopicList;
        }
        if (this.ldaTopic == null || "#ERROR#".equals(ldaTopic)) {
            return null;
        }
        try {
            List<LdaTopicElement> ldaElementList = JsonUtils.json2Object(ldaTopic, new TypeReference<List<LdaTopicElement>>(){});
            if (ldaElementList == null || ldaElementList.size() == 0) {
                return null;
            }
            List<String> result = new ArrayList<>();
            LdaTopicElement firstTopicObject = ldaElementList.get(0);
            result.add(firstTopicObject.topic);
            if (ldaElementList.size() > 1) {
                LdaTopicElement secondTopicObject = ldaElementList.get(1);
                double secondWeight = secondTopicObject.weight;
                if (secondWeight >= 0.05 && secondWeight / firstTopicObject.weight >= 0.5) {
                    result.add(secondTopicObject.topic);
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Parse lda topic error: " + ldaTopic);
            return null;
        }
    }

    /**
     * 解析统计信息，得到时长和完成比的平均值
     * @param statInfo
     */
    public synchronized void parseStatInfo(String statInfo) {
        if(statInfo == null)
            return;
        JSONObject object = JSONObject.parseObject(statInfo);
        double duration = 0.0;
        double duration_c = 0.0;
        double v_duration = 0.0;
        double v_duration_c = 0.0;
        double readrate = 0.0;
        double readrate_c = 0.0;
        double v_readrate = 0.0;
        double v_readrate_c = 0.0;
        if(object.containsKey("duration")) {
            duration = object.getDouble("duration");
        }
        if(object.containsKey("duration_count")) {
            duration_c = object.getDouble("duration_count");
        }
        if(object.containsKey("v_duration")){
            v_duration = object.getDouble("v_duration");
        }
        if(object.containsKey("v_duration_count")){
            v_duration_c = object.getDouble("v_duration_count");
        }
        if(object.containsKey("readrate")) {
            readrate = object.getDouble("readrate");
        }
        if(object.containsKey("readrate_count")) {
            readrate_c = object.getDouble("readrate_count");
        }
        if(object.containsKey("v_rate")) {
            v_readrate = object.getDouble("v_rate");
        }
        if(object.containsKey("v_rate_count")) {
            v_readrate_c = object.getDouble("v_rate_count");
        }
        if(v_duration_c != 0.0) {
            this.setLast1d_avg_duration(v_duration/v_duration_c);
            if(v_readrate_c != 0.0) {
                this.setLast1d_avg_readrate(v_readrate / v_readrate_c);
            }
        }else {
            if(duration_c != 0.0) {
                this.setLast1d_avg_duration(duration / duration_c);
            }
            if(readrate_c != 0.0) {
                this.setLast1d_avg_readrate(readrate/readrate_c);
            }
        }
    }

    /**
     * 解析并返回al_features为一个map<word, featureWord>的形式，只解析一次
     * added by zhaohh@20170726
      */
    private Map<String, FeatureWord> alFeaturesMap = null;
    public HeadLineItemProfile() {
    }

    public synchronized Map<String, FeatureWord> getAlFeaturesMap() {
        if (this.alFeaturesMap != null) {
            return this.alFeaturesMap;
        }
        if(features == null){
            return null;
        }

        this.alFeaturesMap = new HashMap<>();
        int maxIndex = features.size() / 3;
        FeatureWord featureWordTemp = null;
        for (int index = 0; index < maxIndex; index++) {
            String word = features.get(3*index);
            String type = features.get(3*index + 1);
            double weight = Double.parseDouble(features.get(3*index + 2));
            featureWordTemp = new FeatureWord(word, type, weight);
            this.alFeaturesMap.put(word, featureWordTemp);
        }
        return this.alFeaturesMap;
    }

    /**
     * 解析al_features为一个map<type, List<FeatureWord>>的形式，只解析一次
     * 需加synchronized同步，因为有可能不同用户会有同一篇的文章，而文章画像是存在ehcache中全局共享的，线程不安全
     * added by zhaohh@20170731
     */
    private Map<String, List<FeatureWord>> alFeaturesTypeMap = null;
    public synchronized Map<String, List<FeatureWord>> getAlFeaturesTypeMap() {
        if (this.alFeaturesTypeMap != null) {
            return this.alFeaturesTypeMap;
        }
        if(features == null){
            return null;
        }

        this.alFeaturesTypeMap = new HashMap<>();
        int maxIndex = features.size() / 3;
        FeatureWord featureWordTemp;
        for (int index = 0; index < maxIndex; index++) {
            String word = features.get(3*index);
            String type = features.get(3*index + 1);
            double weight = Double.parseDouble(features.get(3*index + 2));
            featureWordTemp = new FeatureWord(word, type, weight);
            List<FeatureWord> featureList = this.alFeaturesTypeMap.get(type);
            if (featureList == null) {
                featureList = new ArrayList<>();
                this.alFeaturesTypeMap.put(type, featureList);
            }
            featureList.add(featureWordTemp);
        }
        return this.alFeaturesTypeMap;
    }
    //latentTopic解析为Map<tp,weight>
    private Map<String, Double> latentTopicMap = null;
    public synchronized Map<String, Double> getlatentTopicMap() {
        if (this.latentTopicMap != null) {
            return this.latentTopicMap;
        }
        if(latentTopic == null || latentTopic.length()<5){
            return null;
        }
        this.latentTopicMap = new LinkedHashMap<>();
        JSONObject jsobj = JSONObject.parseObject(latentTopic);
        if(jsobj!= null && jsobj.containsKey("topics")){
            String topicStr = jsobj.get("topics").toString();
            JSONArray jsarr = JSON.parseArray(topicStr);
            if(jsarr.size()>0){
                for(int i=0;i<jsarr.size();i++){
                    JSONObject jsobj1 = jsarr.getJSONObject(i);
                    String id = jsobj1.get("id").toString();
                    Double weight = Double.parseDouble(jsobj1.get("weight").toString());
                    latentTopicMap.put(id,weight);
                }
            }
        }
        return latentTopicMap;
    }
    //combineTagList解析为Map<tagStr,FeatureWord>形式
    private Map<String,FeatureWord> combineTagMap = null;
    public synchronized Map<String,FeatureWord> getcombineTagMap(){
        if (this.combineTagMap != null) {
            return this.combineTagMap;
        }
        if(combineTagList == null || combineTagList.length()<5){
            return null;
        }
        this.combineTagMap = new LinkedHashMap<>();
        JSONArray jsarr = JSONArray.parseArray(combineTagList);
        if(null!= jsarr && jsarr.size()>0){
            FeatureWord featureWordTemp = null;
            for(int i=0;i<jsarr.size();i++){
                JSONObject jsobj = JSON.parseObject(jsarr.get(i).toString());
                String level1=jsobj.get("level1").toString();
                String type1=jsobj.get("type1").toString();
                String level2=jsobj.get("level2").toString();
                String type2=jsobj.get("type2").toString();
                Double cotagweight = Double.parseDouble(jsobj.get("weight").toString());
                String tagStr=jsobj.get("tagStr").toString();
                if(tagStr.contains("-")){
                    featureWordTemp = new FeatureWord(level1,type1,level2,type2,cotagweight,tagStr);
                    combineTagMap.put(tagStr,featureWordTemp);
                }
            }
        }
            return combineTagMap;
    }





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewcmppid() {
        return newcmppid;
    }

    public void setNewcmppid(String newcmppid) {
        this.newcmppid = newcmppid;
    }

    public String getSubid() {
        return subid;
    }

    public void setSubid(String subid) {
        this.subid = subid;
    }

    public String getZmtid() {
        return zmtid;
    }

    public void setZmtid(String zmtid) {
        this.zmtid = zmtid;
    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSplitTitle() {
        return splitTitle;
    }

    public void setSplitTitle(String splitTitle) {
        this.splitTitle = splitTitle;
    }

    public String getSplitContent() {
        return splitContent;
    }

    public void setSplitContent(String splitContent) {
        this.splitContent = splitContent;
    }

    public String getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(String publishedTime) {
        this.publishedTime = publishedTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public boolean isBeauty() {
        return beauty;
    }

    public void setBeauty(boolean beauty) {
        this.beauty = beauty;
    }

    public String getShowStyle() {
        return showStyle;
    }

    public void setShowStyle(String showStyle) {
        this.showStyle = showStyle;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public List<String> getLoclist() {
        return loclist;
    }

    public void setLoclist(List<String> loclist) {
        this.loclist = loclist;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public List<String> getTopic() {
        return topic;
    }

    public void setTopic(List<String> topic) {
        this.topic = topic;
    }

    public List<String> getHotEvent() {
        return hotEvent;
    }

    public void setHotEvent(List<String> hotEvent) {
        this.hotEvent = hotEvent;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public List<String> getFeatures2() {
        return features2;
    }

    public void setFeatures2(List<String> features2) {
        this.features2 = features2;
    }

    public int getUv() {
        return uv;
    }

    public void setUv(int uv) {
        this.uv = uv;
    }

    public int getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(int storeNum) {
        this.storeNum = storeNum;
    }

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public int getJoinCommentNum() {
        return joinCommentNum;
    }

    public void setJoinCommentNum(int joinCommentNum) {
        this.joinCommentNum = joinCommentNum;
    }

    public int getTj_recNum() {
        return tj_recNum;
    }

    public void setTj_recNum(int tj_recNum) {
        this.tj_recNum = tj_recNum;
    }

    public int getTj_clickNum() {
        return tj_clickNum;
    }

    public void setTj_clickNum(int tj_clickNum) {
        this.tj_clickNum = tj_clickNum;
    }

    public boolean isPorn() {
        return porn;
    }

    public void setPorn(boolean porn) {
        this.porn = porn;
    }

    public boolean isTitleParty() {
        return titleParty;
    }

    public void setTitleParty(boolean titleParty) {
        this.titleParty = titleParty;
    }

    public String getQualityEvalLevel() {
        return qualityEvalLevel;
    }

    public void setQualityEvalLevel(String qualityEvalLevel) {
        this.qualityEvalLevel = qualityEvalLevel;
    }

    public String getDumptime() {
        return dumptime;
    }

    public void setDumptime(String dumptime) {
        this.dumptime = dumptime;
    }

    public String getLast3h_ev() {
        return last3h_ev;
    }

    public void setLast3h_ev(String last3h_ev) {
        this.last3h_ev = last3h_ev;
    }

    public String getLast3h_pv() {
        return last3h_pv;
    }

    public void setLast3h_pv(String last3h_pv) {
        this.last3h_pv = last3h_pv;
    }

    public String getLast3h_share() {
        return last3h_share;
    }

    public void setLast3h_share(String last3h_share) {
        this.last3h_share = last3h_share;
    }

    public String getLast3h_store() {
        return last3h_store;
    }

    public void setLast3h_store(String last3h_store) {
        this.last3h_store = last3h_store;
    }

    public String getLast3h_comment() {
        return last3h_comment;
    }

    public void setLast3h_comment(String last3h_comment) {
        this.last3h_comment = last3h_comment;
    }

    public String getLast1d_ev() {
        return last1d_ev;
    }

    public void setLast1d_ev(String last1d_ev) {
        this.last1d_ev = last1d_ev;
    }

    public String getLast1d_pv() {
        return last1d_pv;
    }

    public void setLast1d_pv(String last1d_pv) {
        this.last1d_pv = last1d_pv;
    }

    public String getLast1d_share() {
        return last1d_share;
    }

    public void setLast1d_share(String last1d_share) {
        this.last1d_share = last1d_share;
    }

    public String getLast1d_store() {
        return last1d_store;
    }

    public void setLast1d_store(String last1d_store) {
        this.last1d_store = last1d_store;
    }

    public String getLast1d_comment() {
        return last1d_comment;
    }

    public void setLast1d_comment(String last1d_comment) {
        this.last1d_comment = last1d_comment;
    }

    public String getToday_ev() {
        return today_ev;
    }

    public void setToday_ev(String today_ev) {
        this.today_ev = today_ev;
    }

    public String getToday_pv() {
        return today_pv;
    }

    public void setToday_pv(String today_pv) {
        this.today_pv = today_pv;
    }

    public String getToday_share() {
        return today_share;
    }

    public void setToday_share(String today_share) {
        this.today_share = today_share;
    }

    public String getToday_store() {
        return today_store;
    }

    public void setToday_store(String today_store) {
        this.today_store = today_store;
    }

    public String getToday_comment() {
        return today_comment;
    }

    public void setToday_comment(String today_comment) {
        this.today_comment = today_comment;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getIsCreation() {
        return isCreation;
    }

    public void setIsCreation(String isCreation) {
        this.isCreation = isCreation;
    }

    public String getDynamic_qualityEvalLevel() {
        return dynamic_qualityEvalLevel;
    }

    public void setDynamic_qualityEvalLevel(String dynamic_qualityEvalLevel) {
        this.dynamic_qualityEvalLevel = dynamic_qualityEvalLevel;
    }

    public String getTimeSensitive() {
        return timeSensitive;
    }

    public void setTimeSensitive(String timeSensitive) {
        this.timeSensitive = timeSensitive;
    }

    public String getImgNum() {
        return imgNum;
    }

    public void setImgNum(String imgNum) {
        this.imgNum = imgNum;
    }

    public String getCNwordNum() {
        return CNwordNum;
    }

    public void setCNwordNum(String CNwordNum) {
        this.CNwordNum = CNwordNum;
    }

    public String getENwordNum() {
        return ENwordNum;
    }

    public void setENwordNum(String ENwordNum) {
        this.ENwordNum = ENwordNum;
    }

    public String getBiaodianNum() {
        return biaodianNum;
    }

    public void setBiaodianNum(String biaodianNum) {
        this.biaodianNum = biaodianNum;
    }

    public String getSentenceNum() {
        return sentenceNum;
    }

    public void setSentenceNum(String sentenceNum) {
        this.sentenceNum = sentenceNum;
    }

    public String getParagraphNum() {
        return paragraphNum;
    }

    public void setParagraphNum(String paragraphNum) {
        this.paragraphNum = paragraphNum;
    }

    public String getCanbeSlide() {
        return canbeSlide;
    }

    public void setCanbeSlide(String canbeSlide) {
        this.canbeSlide = canbeSlide;
    }

    public String getThumbnailpic() {
        return thumbnailpic;
    }

    public void setThumbnailpic(String thumbnailpic) {
        this.thumbnailpic = thumbnailpic;
    }

    public String getPicFingerprint() {
        return picFingerprint;
    }

    public void setPicFingerprint(String picFingerprint) {
        this.picFingerprint = picFingerprint;
    }

    public String getLatentTopic() {
        return latentTopic;
    }

    public void setLatentTopic(String latentTopic) {
        this.latentTopic = latentTopic;
    }

    public String getCombineTagList() {
        return combineTagList;
    }

    public void setCombineTagList(String combineTagList) {
        this.combineTagList = combineTagList;
    }

    public String getLdaTopic() {
        return ldaTopic;
    }

    public void setLdaTopic(String ldaTopic) {
        this.ldaTopic = ldaTopic;
    }

    public String getCoTags() {
        return coTags;
    }

    public void setCoTags(String coTags) {
        this.coTags = coTags;
    }

    public String getLast3d_ev() {
        return last3d_ev;
    }

    public void setLast3d_ev(String last3d_ev) {
        this.last3d_ev = last3d_ev;
    }

    public String getLast3d_pv() {
        return last3d_pv;
    }

    public void setLast3d_pv(String last3d_pv) {
        this.last3d_pv = last3d_pv;
    }

    public String getLast3d_store() {
        return last3d_store;
    }

    public void setLast3d_store(String last3d_store) {
        this.last3d_store = last3d_store;
    }

    public String getTotal_ev() {
        return total_ev;
    }

    public void setTotal_ev(String total_ev) {
        this.total_ev = total_ev;
    }

    public String getTotal_pv() {
        return total_pv;
    }

    public void setTotal_pv(String total_pv) {
        this.total_pv = total_pv;
    }

    public String getTotal_store() {
        return total_store;
    }

    public void setTotal_store(String total_store) {
        this.total_store = total_store;
    }

    //金泽给出的数据中的统计数据的key
    private final String key_dumptime = "dumptime";
    private final String key_last3h_ev = "last3h_ev";
    private final String key_last3h_pv = "last3h_pv";
    private final String key_last3h_share = "last3h_share";
    private final String key_last3h_store = "last3h_store";
    private final String key_last3h_comment = "last3h_comment";
    private final String key_last1d_ev = "last1d_ev";
    private final String key_last1d_pv = "last1d_pv";
    private final String key_last1d_share = "last1d_share";
    private final String key_last1d_store = "last1d_store";
    private final String key_last1d_comment = "last1d_comment";
    private final String key_today_ev = "today_ev";
    private final String key_today_pv = "today_pv";
    private final String key_today_share = "today_share";
    private final String key_today_store = "today_store";
    private final String key_today_comment = "today_comment";


    /**
     * 根据金泽给出的map对象，补足统计数据
     *
     * @param statisticInfo
     */
    public void fillItemProfile(NewsPortraitRec statisticInfo) {
        if (statisticInfo != null) {
            this.setLast3h_ev(statisticInfo.getLast3h_ev());
            this.setLast3h_pv(statisticInfo.getLast3h_pv());
            this.setLast3h_share(statisticInfo.getLast3h_share());
            this.setLast3h_store(statisticInfo.getLast3h_store());
            this.setLast3h_comment(statisticInfo.getLast3h_comment());

            this.setLast1d_ev(statisticInfo.getLast1d_ev());
            this.setLast1d_pv(statisticInfo.getLast1d_pv());
            this.setLast1d_share(statisticInfo.getLast1d_share());
            this.setLast1d_store(statisticInfo.getLast1d_store());
            this.setLast1d_comment(statisticInfo.getLast1d_comment());

            this.setLast3d_ev(statisticInfo.getLast3d_ev());
            this.setLast3d_pv(statisticInfo.getLast3d_pv());
            this.setLast3d_store(statisticInfo.getLast3d_store());

            this.setTotal_ev(statisticInfo.getTotal_ev());
            this.setTotal_pv(statisticInfo.getTotal_pv());
            this.setTotal_store(statisticInfo.getTotal_store());

            this.setToday_ev(statisticInfo.getToday_ev());
            this.setToday_pv(statisticInfo.getToday_pv());
            this.setToday_share(statisticInfo.getToday_share());
            this.setToday_store(statisticInfo.getToday_store());
            this.setToday_comment(statisticInfo.getToday_comment());

            this.setLast1d_avg_duration(statisticInfo.getLast1d_avgduration());
            this.setLast1d_avg_readrate(statisticInfo.getLast1d_avgreadrate());
        }
    }


    public static void main(String[] args) {
        String str = "娱乐, c, -1.0, 时尚, c, 0.5, tp_886, t, 1.0, tp_2058, t, 0.97, tp_14236, t, 1.0, tp_12281, t, 0.86, 国产电影视频叔, s1, 1.0, 李小璐, et, 1.0, 电影, et, 1.0, 颜值, x, 0.82, TOP, x, -1, 其它转载, x, -1";
        List<String> features = Arrays.asList(str.split(", "));
        HeadLineItemProfile itemProfile = new HeadLineItemProfile();
        itemProfile.setFeatures(features);
        Map<String, List<FeatureWord>> typeMap = itemProfile.getAlFeaturesTypeMap();
        System.out.println("Done");
    }

}
