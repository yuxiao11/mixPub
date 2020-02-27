package com.ifeng.recom.mixrecall.common.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.annotations.Expose;
import com.ifeng.recom.mixrecall.common.model.document.Topic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.annotation.Cacheable;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNumeric;


@Cacheable(cacheNames = "documentEntity")
public class Document implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private static final Log logger = LogFactory.getLog(Document.class);


    @Expose
    private String docId;

    @Expose
    private String title;

    @Expose
    private String splitTitle;

    //    @Expose
    private String readableFeatures;

    @Expose
    private String simId;

    @Expose
    private String date; // 文章时间

    @Expose
    private String c;

    @Expose
    private Double hotBoost;

    //    @Expose
    private boolean hasThumnail;

    @Expose
    private String docType; // 文章类型 (slide/video/doc/hdSlide)

    @Expose
    private String source;

    @Expose
    private String coTag;

    @Expose
    private String sc;

//    @Expose
//    private Set<String> coTags;

    //    @Expose
    private boolean isJp;

    @Expose
    private Double score; //视频距离得分：明明算出的cotag2video数据

    @Expose
    private String level;

    @Expose
    private String topic1;

    @Expose
    private String topic2;

    @Expose
    private String topic3;

    @Expose
    private String performance;

    @Expose
    private String lda_topic;

    @Expose
    private Set<String> coTagSet = null;

    private List<String> category = null;

    @Expose

    private List<String> scList = null;

    @Expose
    private List<String> ldaTopicList = null;



    /*文章长度*/
    @Expose
    private int newslenlevel;

    /*图像特征*/
    @Expose
    private String lowImgFeature;

    /*发布时间*/
    @Expose
    private String publishedTime;



    /**
     * 解析图像特征
     * @return
     */
    private Map<String, String> lowFeatureMap = null;



    private List<Topic> topic1List;
    private List<Topic> topic3List;

    private Why why;
    private String id;
    private List<String> topic;
    private List<String> features;
    private List<Tag> topicList;
    private List<Tag> featureList;
    private double dynamicQualityEvalLevel;
    private boolean isTitleParty = false;
    private boolean isPron = false;





    public String getTopic2() {
        return topic2;
    }

    public void setTopic2(String topic2) {
        this.topic2 = topic2;
    }

    public String getlda_topic() {
        return lda_topic;
    }

    public void setlda_topic(String lda_topic) {
        this.lda_topic = lda_topic;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    @Expose
    private String location;

    public String getLocliststr() {
        return locliststr;
    }

    public void setLocliststr(String locliststr) {
        this.locliststr = locliststr;
    }

    @Expose
    private String locliststr;

    public String getSingleTag() {
        return singleTag;
    }

    public void setSingleTag(String singleTag) {
        this.singleTag = singleTag;
    }

    @Expose
    private String singleTag;
    @Expose
    private String timeSensitive;

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    @Expose
    private String expireTime;

    private Double tagCtr; //召回标签的转化率
    private String recallTag; //召回标签

    private Map<String, Object> debugInfo = new HashMap<>();

    private Map<String, Tag> word2Tag = new HashMap<>();

    private int PreloadPosition;

    private Double positionWeight;

    @Expose
    private boolean available;

    @Expose
    private String distype;

    public String getPartCategory() {
        return partCategory;
    }

    public void setPartCategory(String partCategory) {
        this.partCategory = partCategory;
    }

    public void setSubCateList(List<String> scList) {
        this.scList = scList;
    }


//    public List<String> getSubCateList() {
//        return sclist;
//    }


    public void setldaTopicList(List<String> ldaTopicList) {
        this.ldaTopicList = ldaTopicList;
    }


    public List<String> getldaTopicList() {
        return ldaTopicList;
    }

    public String getPartCategoryExt() {
        return partCategoryExt;
    }

    public void setPartCategoryExt(String partCategoryExt) {
        this.partCategoryExt = partCategoryExt;
    }

    public String getRecommendLevel() {
        return recommendLevel;
    }

    public void setRecommendLevel(String recommendLevel) {
        this.recommendLevel = recommendLevel;
    }

    public String getSubcate() {
        return sc;
    }

    public void setSubcate(String sc) {
        this.sc = sc;
    }

    @Expose
    private String partCategory;

    @Expose
    private String partCategoryExt;

    @Expose
    private String recommendLevel;

    private int titleWordScore = -1;

    public boolean isLongLowQuality() {
        return isLongLowQuality;
    }

    public void setLongLowQuality(boolean longLowQuality) {
        isLongLowQuality = longLowQuality;
    }

    private boolean isLongLowQuality = false;


    public int getTitleWordScore() {
        return titleWordScore;
    }

    public void setTitleWordScore(int titleWordScore) {
        this.titleWordScore = titleWordScore;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getDistype() {
        return distype;
    }

    public void setDistype(String distype) {
        this.distype = distype;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public List<Topic> getTopic1List() {
        return topic1List;
    }



    public void setcoTagSet(Set<String> coTagSet) {
        this.coTagSet = coTagSet;
    }

    public Set<String> getcoTagSet() {
        if(this.coTagSet != null){
            return this.coTagSet;
        }else{
            return new HashSet<String>();
        }
    }



    public void setTopic1List(List<Topic> topic1List) {
        this.topic1List = topic1List;
    }

    public List<Topic> getTopic3List() {
        return topic3List;
    }

    public void setTopic3List(List<Topic> topic3List) {
        this.topic3List = topic3List;
    }

    public Double getPositionWeight() {
        return positionWeight;
    }

    public void setPositionWeight(Double positionWeight) {
        this.positionWeight = positionWeight;
    }

    public int getPreloadPosition() {
        return PreloadPosition;
    }

    public void setPreloadPosition(int preloadPosition) {
        PreloadPosition = preloadPosition;
    }

    public Double getTagCtr() {
        return tagCtr;
    }

    public void setTagCtr(Double tagCtr) {
        this.tagCtr = tagCtr;
    }

    public String getRecallTag() {
        return recallTag;
    }

    public void setRecallTag(String recallTag) {
        this.recallTag = recallTag;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }


    public String getReadableFeatures() {
        return readableFeatures;
    }

    public void setReadableFeatures(String readableFeatures) {
        this.readableFeatures = readableFeatures;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getHotBoost() {
        return hotBoost;
    }

    public void setHotBoost(Double hotBoost) {
        this.hotBoost = hotBoost;
    }

    public boolean isHasThumnail() {
        return hasThumnail;
    }

    public void setHasThumnail(boolean hasThumnail) {
        this.hasThumnail = hasThumnail;
    }


    public String getCotag() {
        return coTag;
    }

    public void setCotag(String coTag) {
        this.coTag = coTag;
    }

    public boolean isJp() {
        return isJp;
    }

    public void setJp(boolean jp) {
        isJp = jp;
    }

    public Why getWhy() {
        return why;
    }

    public void setWhy(Why why) {
        this.why = why;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTopic() {
        return topic;
    }

    public void setTopic(List<String> topic) {
        this.topic = topic;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public List<Tag> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<Tag> topicList) {
        this.topicList = topicList;
    }

    public List<Tag> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<Tag> featureList) {
        this.featureList = featureList;
    }

    public double getDynamicQualityEvalLevel() {
        return dynamicQualityEvalLevel;
    }

    public void setDynamicQualityEvalLevel(double dynamicQualityEvalLevel) {
        this.dynamicQualityEvalLevel = dynamicQualityEvalLevel;
    }

    public boolean isTitleParty() {
        return isTitleParty;
    }

    public void setTitleParty(boolean titleParty) {
        isTitleParty = titleParty;
    }

    public boolean isPron() {
        return isPron;
    }

    public void setPron(boolean pron) {
        isPron = pron;
    }

    public String getTimeSensitive() {
        return timeSensitive;
    }

    public void setTimeSensitive(String timeSensitive) {
        this.timeSensitive = timeSensitive;
    }

    public Map<String, Object> getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(Map<String, Object> debugInfo) {
        this.debugInfo = debugInfo;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTopic1() {
        return topic1;
    }

    public void setTopic1(String topic1) {
        this.topic1 = topic1;
    }

    public String getTopic3() {
        return topic3;
    }

    public void setTopic3(String topic3) {
        this.topic3 = topic3;
    }

    private List<Tag> getTagList(List<String> list) {
        List<Tag> tagList = new ArrayList<>();
        int start = 0;
        while (start != list.size()) {
            List<String> threeList = list.subList(start, start + 3);

            String name = threeList.get(0);
            String type = threeList.get(1);
            Float score = Float.valueOf(threeList.get(2));

            if (score < 0 && (type.equalsIgnoreCase("c"))) {
                type = "c0";
                score = Math.abs(score);
            }
            if (score > 0 && (type.equalsIgnoreCase("c"))) {
                type = "c1";
                score = Math.abs(score);
            }

            if (isNumeric(score.toString().replace(".", "").replace("-", ""))) {
                if (score > 0) {
                    Tag tag = new Tag();
                    tag.setDscore(score);
                    tag.setName(name);
                    tag.setDtype(type);
                    tagList.add(tag);
                    this.word2Tag.put(name, tag);
                }
            }
            start += 3;
        }
        return tagList;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    /**
     * 根据features、topic 来设置对应的featureList 、topicList
     */
    public void fillTagList() {
        if (this.features != null) {
            this.featureList = getTagList(this.features);
        }

        if (this.topic != null) {
            this.topicList = getTagList(this.topic);
        }
    }




    @Override
    public Document clone() throws CloneNotSupportedException {
        Document clone;
        clone = (Document) super.clone();

//        clone.features = Lists.newArrayList();
//        if (CollectionUtils.isNotEmpty(features)) {
//            for (Iterator iterator = features.iterator(); iterator.hasNext(); ) {
//                String feature = (String) iterator.next();
//                clone.features.add(feature);
//            }
//        }
//
//        clone.topic = Lists.newArrayList();
//        if (CollectionUtils.isNotEmpty(topic)) {
//            for (Iterator iterator = topic.iterator(); iterator.hasNext(); ) {
//                String topic = (String) iterator.next();
//                clone.topic.add(topic);
//            }
//        }
//
//        clone.featureList = Lists.newArrayList();
//        if (CollectionUtils.isNotEmpty(featureList)) {
//            for (Iterator iterator = featureList.iterator(); iterator.hasNext(); ) {
//                Tag tag = (Tag) iterator.next();
//                clone.featureList.add(tag.clone());
//            }
//        }

//        clone.topicList = Lists.newArrayList();
//        if (CollectionUtils.isNotEmpty(topicList)) {
//            for (Iterator iterator = topicList.iterator(); iterator.hasNext(); ) {
//                Tag tag = (Tag) iterator.next();
//                clone.topicList.add(tag.clone());
//            }
//        }
        return clone;
    }

    /**
     * position Comparator
     */
    public static class PreloadPositionComparator implements Comparator<Document> {
        @Override
        public int compare(Document o1, Document o2) {
            return Integer.compare(o1.getPreloadPosition(), o2.getPreloadPosition());
        }
    }

    /**
     * position weight Comparator
     */
    public static class PositionWeightComparator implements Comparator<Document> {
        @Override
        public int compare(Document o1, Document o2) {
            return Double.compare(o1.getPositionWeight(), o2.getPositionWeight());
        }
    }

    public static class WeightAndPreloadPositionComparator implements Comparator<Document> {
        @Override
        public int compare(Document o1, Document o2) {
            int rt = Double.compare(o1.getPositionWeight(), o2.getPositionWeight());
            if (rt == 0) {
                return Integer.compare(o1.getPreloadPosition(), o2.getPreloadPosition());
            } else {
                return rt;
            }
        }
    }

    /**
     * HotBoost Comparator
     */
    public static class HotBoostComparator implements Comparator<Document> {
        @Override
        public int compare(Document o1, Document o2) {
            if (o1.getHotBoost() == null || o2.getHotBoost() == null) {
                return -1;
            }
            return Double.compare(o2.getHotBoost(), o1.getHotBoost());
        }
    }

    /**
     * Cotag2Video Score Comparator
     */
    public static class Cotag2VideoScoreComparator implements Comparator<Document> {
        @Override
        public int compare(Document o1, Document o2) {
            if (o1.getScore() == null && o2.getScore() == null) {
                return 0;
            }
            if (o1.getScore() == null) {
                return 1;
            }
            if (o2.getScore() == null) {
                return -1;
            }
            return Double.compare(o2.getScore(), o1.getScore());
        }
    }

    public static class TagCtrComparatorWithHotBoost implements Comparator<Document> {
        @Override
        public int compare(Document o1, Document o2) {
            Double ctrWithHotO2 = o2.getTagCtr() * o2.getHotBoost();
            Double ctrWithHotO1 = o1.getTagCtr() * o1.getHotBoost();

            return ctrWithHotO2.compareTo(ctrWithHotO1);
        }
    }

    public Map<String, Tag> getWord2Tag() {
        return word2Tag;
    }

    public void setWord2Tag(Map<String, Tag> word2Tag) {
        this.word2Tag = word2Tag;
    }

    @Override
    public int hashCode() {
        return this.docId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Document) {
            Document d = (Document) obj;
            if (d.docId.equals(this.docId)) {
                return true;
            }
        }
        return false;
    }

    public String getDateAsStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(this.date);
        return dateStr;
    }


    @Override
    public String toString() {
        return "Document{" +
                "docId='" + docId + '\'' +
                ", title='" + title + '\'' +
                ", hotBoost=" + hotBoost +
                ", docType='" + docType + '\'' +
                ", available=" + available +
                '}';
    }


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

    public synchronized List<String> getScList() {
        List<String> list = new ArrayList<>();
        if(this.scList != null){
            return this.scList;
        }
        if(this.getAlFeaturesTypeMap() == null || this.getAlFeaturesTypeMap().get("sc") == null) {
            return list;
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
            return list;
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
    public static class LdaTopicElement {
        public String topic;
        public double weight;
    }
    public synchronized List<String> getLdaTopicList() {
        if (this.ldaTopicList != null) {
            return this.ldaTopicList;
        }else{
            return new ArrayList<>();
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
    public Document() {
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
//    //latentTopic解析为Map<tp,weight>
//    private Map<String, Double> latentTopicMap = null;
//    public synchronized Map<String, Double> getlatentTopicMap() {
//        if (this.latentTopicMap != null) {
//            return this.latentTopicMap;
//        }
//        if(latentTopic == null || latentTopic.length()<5){
//            return null;
//        }
//        this.latentTopicMap = new LinkedHashMap<>();
//        JSONObject jsobj = JSONObject.parseObject(latentTopic);
//        if(jsobj!= null && jsobj.containsKey("topics")){
//            String topicStr = jsobj.get("topics").toString();
//            JSONArray jsarr = JSON.parseArray(topicStr);
//            if(jsarr.size()>0){
//                for(int i=0;i<jsarr.size();i++){
//                    JSONObject jsobj1 = jsarr.getJSONObject(i);
//                    String id = jsobj1.get("id").toString();
//                    Double weight = Double.parseDouble(jsobj1.get("weight").toString());
//                    latentTopicMap.put(id,weight);
//                }
//            }
//        }
//        return latentTopicMap;
//    }

//
//    //combineTagList解析为Map<tagStr,FeatureWord>形式
//    private Map<String,FeatureWord> combineTagMap = null;
//    public synchronized Map<String,FeatureWord> getcombineTagMap(){
//        if (this.combineTagMap != null) {
//            return this.combineTagMap;
//        }
//        if(combineTagList == null || combineTagList.length()<5){
//            return null;
//        }
//        this.combineTagMap = new LinkedHashMap<>();
//        JSONArray jsarr = JSONArray.parseArray(combineTagList);
//        if(null!= jsarr && jsarr.size()>0){
//            FeatureWord featureWordTemp = null;
//            for(int i=0;i<jsarr.size();i++){
//                JSONObject jsobj = JSON.parseObject(jsarr.get(i).toString());
//                String level1=jsobj.get("level1").toString();
//                String type1=jsobj.get("type1").toString();
//                String level2=jsobj.get("level2").toString();
//                String type2=jsobj.get("type2").toString();
//                Double cotagweight = Double.parseDouble(jsobj.get("weight").toString());
//                String tagStr=jsobj.get("tagStr").toString();
//                if(tagStr.contains("-")){
//                    featureWordTemp = new FeatureWord(level1,type1,level2,type2,cotagweight,tagStr);
//                    combineTagMap.put(tagStr,featureWordTemp);
//                }
//            }
//        }
//        return combineTagMap;
//    }


//    public String getNewcmppid() {
//        return newcmppid;
//    }
//
//    public void setNewcmppid(String newcmppid) {
//        this.newcmppid = newcmppid;
//    }
//
//    public String getSubid() {
//        return subid;
//    }
//

//
//    public String getZmtid() {
//        return zmtid;
//    }
//
//    public void setZmtid(String zmtid) {
//        this.zmtid = zmtid;
//    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }

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

//    public String getSplitContent() {
//        return splitContent;
//    }
//
//    public void setSplitContent(String splitContent) {
//        this.splitContent = splitContent;
//    }
//
//    public String getPublishedTime() {
//        return publishedTime;
//    }
//
//    public void setPublishedTime(String publishedTime) {
//        this.publishedTime = publishedTime;
//    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

//    public String getAppId() {
//        return appId;
//    }
//
//    public void setAppId(String appId) {
//        this.appId = appId;
//    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

//    public boolean isBeauty() {
//        return beauty;
//    }
//
//    public void setBeauty(boolean beauty) {
//        this.beauty = beauty;
//    }
//
//    public String getShowStyle() {
//        return showStyle;
//    }
//
//    public void setShowStyle(String showStyle) {
//        this.showStyle = showStyle;
//    }
//
//    public long getModifyTime() {
//        return modifyTime;
//    }
//
//    public void setModifyTime(long modifyTime) {
//        this.modifyTime = modifyTime;
//    }
//
//    public String getOther() {
//        return other;
//    }
//
//    public void setOther(String other) {
//        this.other = other;
//    }
//
//    public List<String> getLoclist() {
//        return loclist;
//    }
//
//    public void setLoclist(List<String> loclist) {
//        this.loclist = loclist;
//    }
//
//    public List<String> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<String> tags) {
//        this.tags = tags;
//    }
//
//
//    public List<String> getHotEvent() {
//        return hotEvent;
//    }
//
//    public void setHotEvent(List<String> hotEvent) {
//        this.hotEvent = hotEvent;
//    }
//
//
//    public List<String> getFeatures2() {
//        return features2;
//    }
//
//    public void setFeatures2(List<String> features2) {
//        this.features2 = features2;
//    }

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

    public int getNewslenlevel() {
        return newslenlevel;
    }

    public void setNewslenlevel(int newslenlevel) {
        this.newslenlevel = newslenlevel;
    }




    public String getLowImgFeature() {
        return lowImgFeature;
    }

    public void setLowImgFeature(String lowImgFeature) {
        this.lowImgFeature = lowImgFeature;
    }


    public synchronized Map<String, String> getLowFeatureMap(){
        if(lowImgFeature == null) {
            return null;
        }
        if(lowFeatureMap != null) {
            return lowFeatureMap;
        }
        try{
            lowFeatureMap = JSONObject.parseObject(lowImgFeature, Map.class);
        }catch (Exception e){
            return null;
        }
        return lowFeatureMap;
    }





//    public String getMediaId() {
//        return mediaId;
//    }
//
//    public void setMediaId(String mediaId) {
//        this.mediaId = mediaId;
//    }
//
//    public String getOriginal() {
//        return original;
//    }
//
//    public void setOriginal(String original) {
//        this.original = original;
//    }
//
//    public String getIsCreation() {
//        return isCreation;
//    }
//
//    public void setIsCreation(String isCreation) {
//        this.isCreation = isCreation;
//    }
//
//    public String getDynamic_qualityEvalLevel() {
//        return dynamic_qualityEvalLevel;
//    }
//
//    public void setDynamic_qualityEvalLevel(String dynamic_qualityEvalLevel) {
//        this.dynamic_qualityEvalLevel = dynamic_qualityEvalLevel;
//    }
//
//    public String getImgNum() {
//        return imgNum;
//    }
//
//    public void setImgNum(String imgNum) {
//        this.imgNum = imgNum;
//    }
//
//    public String getCNwordNum() {
//        return CNwordNum;
//    }
//
//    public void setCNwordNum(String CNwordNum) {
//        this.CNwordNum = CNwordNum;
//    }
//
//    public String getENwordNum() {
//        return ENwordNum;
//    }
//
//    public void setENwordNum(String ENwordNum) {
//        this.ENwordNum = ENwordNum;
//    }
//
//    public String getBiaodianNum() {
//        return biaodianNum;
//    }
//
//    public void setBiaodianNum(String biaodianNum) {
//        this.biaodianNum = biaodianNum;
//    }
//
//    public String getSentenceNum() {
//        return sentenceNum;
//    }
//
//    public void setSentenceNum(String sentenceNum) {
//        this.sentenceNum = sentenceNum;
//    }
//
//    public String getParagraphNum() {
//        return paragraphNum;
//    }
//
//    public void setParagraphNum(String paragraphNum) {
//        this.paragraphNum = paragraphNum;
//    }
//
//    public String getCanbeSlide() {
//        return canbeSlide;
//    }
//
//    public void setCanbeSlide(String canbeSlide) {
//        this.canbeSlide = canbeSlide;
//    }
//
//    public String getThumbnailpic() {
//        return thumbnailpic;
//    }
//
//    public void setThumbnailpic(String thumbnailpic) {
//        this.thumbnailpic = thumbnailpic;
//    }
//
//    public String getPicFingerprint() {
//        return picFingerprint;
//    }
//
//    public void setPicFingerprint(String picFingerprint) {
//        this.picFingerprint = picFingerprint;
//    }
//
//    public String getLatentTopic() {
//        return latentTopic;
//    }
//
//    public void setLatentTopic(String latentTopic) {
//        this.latentTopic = latentTopic;
//    }
//
//    public String getCombineTagList() {
//        return combineTagList;
//    }
//
//    public void setCombineTagList(String combineTagList) {
//        this.combineTagList = combineTagList;
//    }

    public String getLdaTopic() {
        return lda_topic;
    }

    public void setLdaTopic(String ldaTopic) {
        this.lda_topic = ldaTopic;
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

    public String getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(String publishedTime) {
        this.publishedTime = publishedTime;
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
        Document itemProfile = new Document();
        itemProfile.setFeatures(features);
        Map<String, List<FeatureWord>> typeMap = itemProfile.getAlFeaturesTypeMap();
        System.out.println("Done");
    }
}
