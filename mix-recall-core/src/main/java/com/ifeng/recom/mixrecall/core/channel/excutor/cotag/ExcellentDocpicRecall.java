package com.ifeng.recom.mixrecall.core.channel.excutor.cotag;

import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.*;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;
import com.ifeng.recom.mixrecall.core.cache.preload.ExcellentDocpicCache;
import com.ifeng.recom.mixrecall.core.util.RecallUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.ifeng.recom.mixrecall.common.service.BloomFilter.filterSimIdByBloomFilter;
import static com.ifeng.recom.mixrecall.common.util.UserProfileUtils.profileTagWeightFilter;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.*;


public class ExcellentDocpicRecall implements Callable<List<RecallResult>> {
    private static final Logger logger = LoggerFactory.getLogger(ExcellentDocpicRecall.class);

    private static Document.HotBoostComparator hotBoostComparator = new Document.HotBoostComparator();

    private MixRequestInfo mixRequestInfo;
    private TagPeriod tagPeriod;
    private UserModel userModel;
    private List<RecordInfo> recordInfoList;
    private int number;

    public ExcellentDocpicRecall(MixRequestInfo mixRequestInfo, List<RecordInfo> recordInfoList, TagPeriod tagPeriod, int number) {
        this.mixRequestInfo = mixRequestInfo;
        this.recordInfoList = recordInfoList;
        this.number = number;
        this.userModel = mixRequestInfo.getUserModel();
        this.tagPeriod = tagPeriod;
    }

    @Override
    public List<RecallResult> call() throws Exception {
        if (recordInfoList == null || recordInfoList.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return doRecall();
        } catch (Exception e) {
            logger.error("ExcellentDocpicRecall recall executor", e);
            return Collections.emptyList();
        }
    }

    private List<RecallResult> doRecall() {
        //画像标签和权重
        Map<String, Double> tagWeight = getTagAndWeightMap(recordInfoList);

        //画像标签和位置
        Map<String, Double> tagPosition = getTagPositionMap(recordInfoList);

        //计算每个标签应召回的文章数量
        Map<String, Integer> tagRecallNum = getTagRecallNumber(number, recordInfoList, tagWeight);


        //待召回文章的标签
        Set<String> recallTagSet = tagRecallNum.keySet();

        /**
         * 拼redis key |pin tag
         */
        Set<String> newRecallTagSet= recallTagSet.stream().map(s-> GyConstant.docpic_prefix + s).collect(Collectors.toSet());
        //tag 和 id map, id List 按hotboost 有序
        Map<String, List<String>> tagIdsImmutable = ExcellentDocpicCache.getFromCache(newRecallTagSet);

          /*
           还原tag
         */
        Map<String, List<String>> tagIds = Maps.newHashMap();
        Iterator<Map.Entry<String, List<String>>> iterator = tagIdsImmutable.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<String>> next = iterator.next();
            String key = next.getKey().substring(GyConstant.docpic_prefix.length());
            List<String> value = next.getValue();
            tagIds.put(key,value);
        }

        //id 和 tag map
        Map<String, String> idTags = new HashMap<>();

        //串起整个召回结果
        List<RecallResult> resultDocs = new ArrayList<>();

        List<String> totalId = new ArrayList<>();
        int loopTime = 0;
        while (tagIds.keySet().size() >= 1 && loopTime < 8) {
            loopTime++;

            for (Iterator<Map.Entry<String, List<String>>> it = tagIds.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, List<String>> entry = it.next();

                String tag = entry.getKey();
                List<String> docIds = entry.getValue();

                int recallSize = 50;

                List<String> recalledIds = docIds.subList(0, Math.min(recallSize, docIds.size()));
                List<String> alternativeIds = docIds.subList(Math.min(recallSize, docIds.size()), docIds.size());

                totalId.addAll(recalledIds);

                for (String id : recalledIds) {
                    idTags.put(id, tag);
                }

                if (alternativeIds.size() == 0) {
                    it.remove();
                } else {
                    tagIds.put(tag, alternativeIds);
                }
            }

            Set<String> totalIdSet = new HashSet<>(totalId);
            totalId.clear();

            Map<String, Document> idDocs = DocPreloadCache.getBatchDocsNoClone(totalIdSet);


            List<Document> docs = new ArrayList<>(idDocs.values());

            //需要根据Document对象获取simId后才可进行布隆过滤;
            List<Document> filteredDocs = filterSimIdByBloomFilter(mixRequestInfo.getUid(), docs);
            filteredDocs.sort(RecallUtils.hotBoostComparator);
            Map<String, List<RecallResult>> filteredTagDocs = getRecallTagAndRecallResultListMapFromDocList(filteredDocs, idTags);

            for (Map.Entry<String, List<RecallResult>> entry : filteredTagDocs.entrySet()) {
                String tag = entry.getKey();
                List<RecallResult> docs4tag = entry.getValue();

                int number = tagRecallNum.get(tag);
                int subSize = Math.min(number, docs4tag.size());

                resultDocs.addAll(docs4tag.subList(0, subSize));

                int needNum = number - subSize;

                if (needNum <= 0) {
                    tagIds.remove(tag);
                    tagRecallNum.remove(tag);
                } else {
                    tagRecallNum.put(tag, needNum);
                }
            }
        }

        setPositionForDocs(resultDocs, tagPosition);
        setWhyInfo(resultDocs, tagPeriod);
        DocUtils.setRealPositionForDocs(resultDocs,tagIdsImmutable);

        return resultDocs;
    }


    public static void setPositionForDocs(List<RecallResult> recallResultList, Map<String, Double> tagPosition) {
        Map<String, List<RecallResult>> tagRecallResults = getRecallTagAndResultListMap(recallResultList);

        for (Map.Entry<String, List<RecallResult>> entry : tagRecallResults.entrySet()) {
            String tag = entry.getKey();
            List<RecallResult> recallResults = entry.getValue();

            Map<String, RecallResult> idResultMap = new HashMap<>();
            List<Document> documents = new ArrayList<>();
            for (RecallResult recallResult : recallResults) {
                documents.add(recallResult.getDocument());
                idResultMap.put(recallResult.getDocument().getDocId(), recallResult);
            }

            documents.sort(hotBoostComparator);

            int i = 1;
            for (Document doc : documents) {
                double profileTagPosition = tagPosition.get(tag);
                idResultMap.get(doc.getDocId()).setPreloadPosition(i);
                idResultMap.get(doc.getDocId()).setPositionWeight(profileTagPosition);
                i++;
            }
        }
    }

    private static Map<String, List<RecallResult>> getRecallTagAndResultListMap(List<RecallResult> recallResultList) {
        Map<String, List<RecallResult>> tagAndResultListMap = new HashMap<>();
        for (RecallResult recallResult : recallResultList) {
            try {
                if (tagAndResultListMap.containsKey(recallResult.getRecallTag())) {
                    List<RecallResult> docList = tagAndResultListMap.get(recallResult.getRecallTag());
                    docList.add(recallResult);
                    tagAndResultListMap.put(recallResult.getRecallTag(), docList);
                } else {
                    List<RecallResult> docList = new ArrayList<>();
                    docList.add(recallResult);
                    tagAndResultListMap.put(recallResult.getRecallTag(), docList);
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return tagAndResultListMap;
    }

    private static void setWhyInfo(List<RecallResult> recallResults, TagPeriod tagPeriod) {
        WhyReason whyReasonTrue = null;
        if (tagPeriod.equals(TagPeriod.LONG)) {
            whyReasonTrue = WhyReason.EXCELLENT_D_C;
        }else if(tagPeriod.equals(TagPeriod.RECENT)){
            whyReasonTrue = WhyReason.EXCELLENT_D_SC;
        }

        for (RecallResult recallResult : recallResults) {
            recallResult.setWhyReason(whyReasonTrue);
        }
    }
}
