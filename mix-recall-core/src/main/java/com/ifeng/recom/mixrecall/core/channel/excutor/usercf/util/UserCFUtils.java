package com.ifeng.recom.mixrecall.core.channel.excutor.usercf.util;

import com.google.common.base.Strings;
import com.ifeng.recom.mixrecall.common.model.DocCtrInfo;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.UserCF;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.filter.BeijingFilter;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ifeng.recom.mixrecall.core.cache.DocCtrCache.getBatchDocCtrFromCache;
import static com.ifeng.recom.mixrecall.core.cache.mapping.SimIdDocIdMappingCache.getBatchDocIds;

/**
 * Created by geyl on 2018/1/17.
 */
public class UserCFUtils {

    private static final Logger logger = LoggerFactory.getLogger(UserCFUtils.class);

    public static Map<String, DocCtrInfo> getDocId2Stat(List<String> clickIds) {
        Set<String> clickIdSets = new HashSet<>(clickIds);

        Map<String, Map<String, Double>> map = getBatchDocCtrFromCache(clickIdSets);

        Map<String, DocCtrInfo> doc2StatMap = new HashMap<>();

        for (String id : clickIdSets) {
            Map<String, Double> statMap = map.get(id);

            double pv = statMap.getOrDefault("pv", 0d);
            double ev = statMap.getOrDefault("ev", 0d);
            double ctr = statMap.getOrDefault("ctr", 0d);
            double share = statMap.getOrDefault("share", 0d);
            double store = statMap.getOrDefault("store", 0d);

            DocCtrInfo docCtrInfo = new DocCtrInfo(id, pv, ev, ctr, share, store);
            doc2StatMap.put(id, docCtrInfo);
        }

        return doc2StatMap;
    }

    public static Map<String, Set<Integer>> getDocId2NeighborPos(Map<Integer, List<String>> neighborClick) {
        Map<String, Set<Integer>> idNeighborPos = new HashMap<>();

        for (Map.Entry<Integer, List<String>> entry : neighborClick.entrySet()) {
            int pos = entry.getKey();
            List<String> ids = entry.getValue();

            for (String id : ids) {
                if (idNeighborPos.containsKey(id)) {
                    idNeighborPos.get(id).add(pos);
                } else {
                    Set<Integer> posSet = new HashSet<>();
                    posSet.add(pos);
                    idNeighborPos.put(id, posSet);
                }
            }
        }

        return idNeighborPos;
    }



    /**
     * 按照近邻用户顺序获取特定数量的点击simId
     *
     * @param userCF UserCFCache
     * @return
     */
    public static List<String> getNeighborClickIds(UserCF userCF, int limitNum) {
        Map<Integer, List<String>> neighborClick = new HashMap<>();
        List<String> clickIds = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry : userCF.getNeighborClick().entrySet()) {
            int neighborPos = entry.getKey();
            List<String> clickId = entry.getValue();

            if (clickIds.size() <= limitNum) {
                neighborClick.put(neighborPos, clickId);
                clickIds.addAll(clickId);
            } else {
                break;
            }
        }

        userCF.setNeighborClick(neighborClick);
        return clickIds;
    }


    public  static List<Document> doUserCFFilter(MixRequestInfo mixRequestInfo, List<Document> filteredDocs){
        List<Document> results=new ArrayList<>();
        Set<String> simIds = new HashSet<>();
        TimerEntity timer = TimerEntityUtil.getInstance();
        try{
            Map<String, Double> docScoreMap = new HashMap<>();
            for (Document document : filteredDocs) {
                simIds.add(document.getSimId());
                docScoreMap.put(document.getSimId(),document.getScore());
            }

            timer.addStartTime("ucf_sim");
            Map<String, String> simIdMap = getBatchDocIds(simIds);
            timer.addEndTime("ucf_sim");

            timer.addStartTime("ucf_doc");
            Map<String, Document> documentMap = DocPreloadCache.getBatchDocsWithQueryNoClone(new HashSet<>(simIdMap.values()));
            timer.addEndTime("ucf_doc");

            List<Document> docs=new ArrayList<>();
            for (Map.Entry<String, Document> entry : documentMap.entrySet()) {
                String docId = entry.getKey();
                if (Strings.isNullOrEmpty(docId)) {
                    continue;
                }
                Document document = entry.getValue();
                if(docScoreMap.get(document.getSimId())!=null){
                    document.setScore(docScoreMap.get(document.getSimId()));
                }

                docs.add(document);
            }
            results=BeijingFilter.filterUserCfDocs(mixRequestInfo,docs);
            if(mixRequestInfo.isDebugUser()){
                logger.info("uid:{} filter before size:{} after size:{}",mixRequestInfo.getUid(),filteredDocs.size(),results.size());
            }
        }catch (Exception e){
            logger.error("uid:{} doUserCFFilter error:{}",mixRequestInfo.getUid(),e);
        }

        return results;
    }
}
