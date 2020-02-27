package com.ifeng.recom.mixrecall.common.service.filter;

import com.ifeng.recom.mixrecall.common.cache.LowTagInfoCache;
import com.ifeng.recom.mixrecall.common.cache.ScoreKeywordCache;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ifeng.recom.mixrecall.common.constant.ProidTypeEnum.ifengnews;
import static com.ifeng.recom.mixrecall.common.constant.ProidTypeEnum.ifengnewsdiscovery;
import static com.ifeng.recom.mixrecall.common.constant.ProidTypeEnum.ifengnewslite;
import static com.ifeng.recom.mixrecall.common.constant.ProidTypeEnum.ifengnewssdk;
import static com.ifeng.recom.mixrecall.common.util.StringUtil.removeStringMark;

/**
 * Created by geyl on 2017/12/14.
 */
@Service
public class BeijingFilter {
    private static final Logger logger = LoggerFactory.getLogger(BeijingFilter.class);

    private static final Integer THRESHOLD = 6;
    private static Map<String, Integer> wordScores = new HashMap<>();

    @PostConstruct
    private void init() {
        wordScores = ScoreKeywordCache.cache.asMap();
    }

    public static void updateWordScores() {
        wordScores = ScoreKeywordCache.cache.asMap();
        logger.info("update word scores,size:" + wordScores.size() + " map:" + wordScores.toString());
    }

    private boolean textFilter(String text) {
        text = removeStringMark(text);
        int score = 0;

        for (String word : wordScores.keySet()) {
            if (text.contains(word)) {
                score += wordScores.get(word);
                if (score >= THRESHOLD) {
                    return true;
                }
            }
        }

        return false;
    }



    public static List<Document> filterUserCfDocs(MixRequestInfo mixRequestInfo, List<Document> docs) {
        List<Document> filteredDocs = new ArrayList<>();

        for (Document doc : docs) {
            try {
                //视频先暂时全部过滤 等后续topic2 积累一段时间后 可以放开 为了安全
                String topic1 = doc.getTopic1();
                if(StringUtils.isBlank(topic1)){
                    continue;
                }

                if(DocUtils.isVideo(doc)){
                    if (topic1.contains("社会") || topic1.contains("美女")|| topic1.contains("电视剧")|| topic1.contains("电影")
                            || topic1.contains("情感")|| topic1.contains("健康")||topic1.contains("搞笑")||topic1.contains("猎奇")) {
                        continue;
                    }
                }else{
                    if (topic1.contains("娱乐") || topic1.contains("美女")|| topic1.contains("两性")|| topic1.contains("搞笑")|| topic1.contains("健康")) {
                        continue;
                    }
                }
                filteredDocs.add(doc);
            } catch (Exception e) {
                logger.error("uid:{} filterUserCfDocs error:{}",mixRequestInfo.getUid(), e);
            }
        }
        return filteredDocs;
    }


    public static boolean isBJOrWXB(MixRequestInfo mixRequestInfo) {
        try{
            Map<String, Boolean> userTypeMap = mixRequestInfo.getUserTypeMap();
            String proid=mixRequestInfo.getProid();
            //针对资讯版 正式 lite 探索 以及 proid为空的判断
            if (StringUtils.isBlank(proid)||proid.equals(ifengnews.getValue()) || proid.equals(ifengnewsdiscovery.getValue()) ||
                    proid.equals(ifengnewssdk.getValue()) || proid.equals(ifengnewslite.getValue())) {
                UserModel userModel=mixRequestInfo.getUserModel();
                String loc="";
                if (userModel != null) {
                    loc =  userModel.getGeneralLoc()+userModel.getLoc();
                }
                if (StringUtils.isNotBlank(loc)) {
                    //北京用户 || wxb 都为true
                    if (loc.contains("北京") || userTypeMap.getOrDefault("isBeiJingUserNotWxb", true)||userTypeMap.getOrDefault("isWxb", true)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }catch (Exception e){
            logger.error("uid:{},isBJOrWXB error:{}",mixRequestInfo.getUid(),e);
            return true;
        }
        return true;
    }

    public static boolean isWXB(MixRequestInfo mixRequestInfo) {
        try{
            Map<String, Boolean> userTypeMap = mixRequestInfo.getUserTypeMap();
            String proid=mixRequestInfo.getProid();
            //针对资讯版 正式 lite 探索 以及 proid为空的判断
            if (StringUtils.isBlank(proid)||proid.equals(ifengnews.getValue()) || proid.equals(ifengnewsdiscovery.getValue()) ||
                    proid.equals(ifengnewssdk.getValue()) || proid.equals(ifengnewslite.getValue())) {
                UserModel userModel=mixRequestInfo.getUserModel();
                String loc="";
                if (userModel != null) {
                    loc =  userModel.getGeneralLoc()+userModel.getLoc();
                }
                if (StringUtils.isNotBlank(loc)) {
                    // wxb 都为true
                    if (userTypeMap.getOrDefault("isWxb", true)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }catch (Exception e){
            logger.error("uid:{},isWXB error:{}",mixRequestInfo.getUid(),e);
            return true;
        }
        return true;
    }
}
