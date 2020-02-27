package com.ifeng.recom.mixrecall.common.service.filter;

import static com.ifeng.recom.mixrecall.common.util.StringUtil.removeStringMark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.ifeng.recom.mixrecall.common.cache.ScoreKeywordCache;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.util.TrieTree;

/**
 * Created by geyl on 2017/12/14.
 */
@Service
public class SansuFilter {
    private static final Logger logger = LoggerFactory.getLogger(SansuFilter.class);

    private static final Integer THRESHOLD = 6;
    private static Map<String, Integer> wordScores = new HashMap<>();

    /**
     * 字典树过滤
     */
    private static TrieTree trieTree = null;

    @PostConstruct
    private void init() {
        wordScores = ScoreKeywordCache.cache.asMap();
        trieTree = new TrieTree(wordScores.keySet(), wordScores);
    }

    public static void updateWordScores() {
        wordScores = ScoreKeywordCache.cache.asMap();
        logger.info("update word scores,size:" + wordScores.size() + " map:" + wordScores.toString());

        //这部分new完成后直接赋值，避免有问题
        TrieTree trieTreeNew = new TrieTree(wordScores.keySet(), wordScores);
        trieTree = trieTreeNew;
    }

    private boolean textFilter(String text) {
        text = removeStringMark(text);

        int point = trieTree.matchesPoints(text, THRESHOLD);
        return point >= THRESHOLD;
    }

    public Integer textFilter(String text, int score) {
        text = removeStringMark(text);

        int point = trieTree.matchesPoints(text, score);
        return point;
    }


    public List<Document> titleFilter(List<Document> documents, int score) {
        List<Document> filteredDocs = new ArrayList<>();

        for (Document document : documents) {
            if (Strings.isNullOrEmpty(document.getTitle())) {
//                logger.error("{} title is null", document.getDocId());
                continue;
            }
            try {
                int titleScore = document.getTitleWordScore();

                if (titleScore == -1) {
                    titleScore = textFilter(document.getTitle(), score);
                    document.setTitleWordScore(titleScore);
                }

                if (score >= titleScore) {
                    filteredDocs.add(document);
                }
            } catch (Exception e) {
                filteredDocs.add(document);
                logger.error("score title filter error:{}", e);
            }
        }
        return filteredDocs;
    }

}
