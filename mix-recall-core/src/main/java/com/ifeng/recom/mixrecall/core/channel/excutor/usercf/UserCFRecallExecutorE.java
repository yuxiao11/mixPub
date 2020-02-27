package com.ifeng.recom.mixrecall.core.channel.excutor.usercf;

import com.google.common.base.Strings;
import com.ifeng.recom.mixrecall.common.model.*;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.UserProfile;
import com.ifeng.recom.mixrecall.common.service.filter.BeijingFilter;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.channel.excutor.usercf.util.RankBean;
import com.ifeng.recom.mixrecall.core.channel.excutor.usercf.util.UserCFUtils;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;

import static com.ifeng.recom.mixrecall.common.dao.hbase.UserCFClick.getNeighborClick;
import static com.ifeng.recom.mixrecall.common.service.BloomFilter.filterSimIdByBloomFilter;
import static com.ifeng.recom.mixrecall.core.cache.mapping.SimIdDocIdMappingCache.getBatchDocIds;
import static com.ifeng.recom.mixrecall.core.channel.excutor.usercf.util.UserCFUtils.*;

public class UserCFRecallExecutorE implements Callable<List<RecallResult>> {
    private static final Logger logger = LoggerFactory.getLogger(UserCFRecallExecutorE.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    private static final RankBean.scoreComparator scoreComparator = new RankBean.scoreComparator();
    private static final Document.Cotag2VideoScoreComparator docScoreComparator = new Document.Cotag2VideoScoreComparator();
    private static final RecallResult.UserCfScoreComparator RECALLRESULT_SCORE_COMPARATOR = new RecallResult.UserCfScoreComparator();
    private static final Integer NEIGHBOR_CLICK_NUM = 1500;//YX
    private static final Integer USERCF_RECALL_NUM = 1000;


    private static Map<Integer, Double> pos2Weight = new HashMap<>();

    private MixRequestInfo mixRequestInfo;
    private UserModel userModel;
    private UserCF userCF;

    static {
        double sum = 0;
        for (int i = 1; i <= 30; i++) {
            sum += Math.pow(i, 3);
        }

        for (int i = 1; i <= 30; i++) {
            pos2Weight.put(i, Math.pow((31 - i), 3) / sum);
        }
    }

    public UserCFRecallExecutorE(MixRequestInfo mixRequestInfo, UserCF userCF) {
        this.mixRequestInfo = mixRequestInfo;
        this.userModel = mixRequestInfo.getUserModel();
        this.userCF = userCF;
    }

    @Override
    public List<RecallResult> call() throws Exception {
        try {
            return doRecall();
        } catch (Exception e) {
            logger.error("user cf recall", e);
            return Collections.emptyList();
        }
    }

    public List<RecallResult> doRecall() {

        TimerEntity timer = TimerEntityUtil.getInstance();
        timer.addStartTime("ucf_e");

        timer.addStartTime("ucf_cli");
        List<String> clickIds = getNeighborClickIds(userCF, NEIGHBOR_CLICK_NUM);

        Map<Integer, List<String>> neighborClick = userCF.getNeighborClick();
        timer.addEndTime("ucf_cli");


        timer.addStartTime("ucf_st");
        Map<String, DocCtrInfo> docId2Stat = getDocId2Stat(clickIds);
        timer.addEndTime("ucf_st");

        timer.addStartTime("ucf_rank");
        Map<String, Set<Integer>> docId2NeighborPos = getDocId2NeighborPos(neighborClick);

        List<RankBean> rankBeanList = rank(docId2Stat, docId2NeighborPos);
        timer.addEndTime("ucf_rank");

        List<Document> documentList = new ArrayList<>();
        for (RankBean rankBean : rankBeanList) {
            Document document = new Document();
            try {
                document.setSimId(rankBean.getDocId());
                document.setScore(rankBean.getScore());
                documentList.add(document);
            } catch (Exception e) {
                logger.error("", e);
            }
        }

        documentList.sort(docScoreComparator);

        List<Document> recalledDocs;
        List<Document> resultDocs = new ArrayList<>();

        int loopTime = 0;
        int baseDocSize = 0;
        timer.addStartTime("ucf_bl");
        while (documentList.size() > 10 && loopTime < 10 && resultDocs.size() < USERCF_RECALL_NUM) {
            loopTime++;
            int recallSize = (11 - loopTime) * 50;

            recalledDocs = documentList.subList(0, Math.min(recallSize, documentList.size()));
            documentList = documentList.subList(Math.min(recallSize, documentList.size()), documentList.size());

            if (recalledDocs != null) {
                baseDocSize += recalledDocs.size();
            }
            List<Document> filteredDocs = filterSimIdByBloomFilter(userModel.getUserId(), recalledDocs);

            //添加北京和地域为空的过滤  主要过滤几个分类 为了防止召回数量不足 所以将此逻辑前置 到 通道之中
            if(BeijingFilter.isBJOrWXB(mixRequestInfo)) {
                timer.addStartTime("doUserCFFilter");
                filteredDocs=UserCFUtils.doUserCFFilter(mixRequestInfo,filteredDocs);
                timer.addEndTime("doUserCFFilter");
            }
            resultDocs.addAll(filteredDocs);
        }
        timer.addEndTime("ucf_bl");

        List<RecallResult> recallResults = new ArrayList<>();
        if(BeijingFilter.isBJOrWXB(mixRequestInfo)){
            try{
                if(CollectionUtils.isNotEmpty(resultDocs)){
                    for(Document doc:resultDocs){
                        RecallResult recallResult = new RecallResult();
                        recallResult.setDocument(doc);
                        recallResult.setUserCFScore(doc.getScore());

                        recallResults.add(recallResult);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Map<String, Double> docScoreMap = new HashMap<>();
            Set<String> resultSimId = new HashSet<>();

            for (Document document : resultDocs) {
                resultSimId.add(document.getSimId());
                docScoreMap.put(document.getSimId(), document.getScore());
            }

            timer.addStartTime("ucf_sim");
            Map<String, String> simIdDocIdMap = getBatchDocIds(resultSimId);
            timer.addEndTime("ucf_sim");

            timer.addStartTime("ucf_doc");
            Map<String, Document> documentMap = DocPreloadCache.getBatchDocsWithQueryNoClone(new HashSet<>(simIdDocIdMap.values()));
            timer.addEndTime("ucf_doc");


            for (Map.Entry<String, Document> entry : documentMap.entrySet()) {
                String docId = entry.getKey();
                if (Strings.isNullOrEmpty(docId)) {
                    continue;
                }

                Document document = entry.getValue();

                RecallResult recallResult = new RecallResult();
                recallResult.setDocument(document);
                recallResult.setUserCFScore(docScoreMap.getOrDefault(document.getSimId(), 0d));

                recallResults.add(recallResult);
            }
        }


        recallResults.sort(RECALLRESULT_SCORE_COMPARATOR);

        timer.addEndTime("ucf_e");

        TimerEntity.TimeBean ucf=timer.getTimeBean("ucf_e");
        if (ucf != null && ucf.getUsedTime() > 400) {
            timeLogger.info("ChannelLog ucf_e {},loopTime:{},baseDocSize:{}, uid:{}", timer.getStaticsInfo(), loopTime, baseDocSize, mixRequestInfo.getUid());
        }

        return recallResults;
    }



    private List<RankBean> rank(Map<String, DocCtrInfo> docId2Stat, Map<String, Set<Integer>> docId2NeighborPos) {
        List<RankBean> rtl = new ArrayList<>();
        for (String docId : docId2Stat.keySet()) {
            DocCtrInfo bean = docId2Stat.get(docId);
            double ev = bean.getEv();
            double pv = bean.getPv();
            double ctr = bean.getCtr();

            if (pv > 0) {
                ctr = pv / ev;
                if (ev == 0 || (ctr > 0.11 && ev < 100)) ctr = 0.11;

                Set<Integer> posL = docId2NeighborPos.get(docId);
                double weightedCtr = 0;
                double shift = pv;
                for (int pos : posL) {
                    if (shift < 10) shift = 10;
                    weightedCtr += pos2Weight.get(pos) * (1 / Math.log1p(shift));
                }
//                double scoreRt = (double)countOk/20.0;
                double scoreRt;
                /*
                if(scoreRt >= avctr ) {
                    RankBean b = new RankBean(docId, scoreRt, posL);
                    rtl.add(b);
                    System.out.println(docId + " : " + scoreRt + " : " + avctr + " : " + evSum);
                } else if (weightedCtr > 0 && avctr > 0 && avctr < 1) {
                    scoreRt = 0.8 * weightedCtr  + 0.2 * avctr;
                    RankBean b = new RankBean(docId, scoreRt, posL);
                    rtl.add(b);
                }*/

                if (weightedCtr > 0 && ctr > 0 && ctr < 1) {
                    scoreRt = 0.9 * weightedCtr + 0.1 * ctr;
                    RankBean b = new RankBean(docId, scoreRt, posL);
                    rtl.add(b);
                }
            }
        }

        rtl.sort(scoreComparator);
        return rtl;
    }

}
