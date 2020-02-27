package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.ifeng.recom.mixrecall.common.config.ApplicationConfig;
import com.ifeng.recom.mixrecall.common.constant.ApolloConstant;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.BloomFilter;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;
import com.ifeng.recom.mixrecall.core.cache.preload.LdaTopicCache;
import com.ifeng.recom.mixrecall.core.util.RecallUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.ifeng.recom.mixrecall.common.service.BloomFilter.filterSimIdByBloomFilter;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.*;

/**
 * Created by liligeng on 2018/8/22.
 */
public class LdaDocpicExecutor implements Callable<List<RecallResult>> {

    private static final Logger logger = LoggerFactory.getLogger(LdaDocpicExecutor.class);

    private MixRequestInfo mixRequestInfo;

    private List<RecordInfo> recordInfoList;

    private UserProfileEnum.TagPeriod tagPeriod;
    private static Document.HotBoostComparator hotBoostComparator = new Document.HotBoostComparator();

    private int number;

    public LdaDocpicExecutor(MixRequestInfo mixRequestInfo, List<RecordInfo> recordInfoList, UserProfileEnum.TagPeriod tagPeriod, int number) {
        this.mixRequestInfo = mixRequestInfo;
        this.recordInfoList = recordInfoList;
        this.tagPeriod = tagPeriod;
        this.number = number;
    }

    @Override
    public List<RecallResult> call() throws Exception {
        if (CollectionUtils.isEmpty(recordInfoList)) {
            return Collections.EMPTY_LIST;
        }
        //此处添加负反馈

        Map<String,Double> negativeList = mixRequestInfo.getNegativeMap().get("lda");
        int originSize = recordInfoList.size();

        if(negativeList != null && negativeList.size() >= 1){
            List<RecordInfo> finalRecordInfo = recordInfoList.stream().filter(item -> !negativeList.keySet().contains(item.getRecordName())).collect(Collectors.toList());

            recordInfoList = finalRecordInfo;

            logger.info("{} Channel:{} __________Negative_________ OriginSize:{} FilteredSize:{} RemoveSize:{} CotagWeightMap:{}",mixRequestInfo.getUid(),"LDA",originSize,recordInfoList.size(),originSize-recordInfoList.size(),mixRequestInfo.getNegativeMap().get("lda").toString());

        }
        if (recordInfoList == null || recordInfoList.isEmpty() || number == 0) {
            return Collections.emptyList();
        }

        Map<String, Double> tagWeight = getTagAndWeightMap(recordInfoList);

        Map<String, Double> tagPosition = getTagPositionMap(recordInfoList);

        Map<String, Integer> tagRecallNum = getTagRecallNumber(number, recordInfoList, tagWeight);

        Set<String> recallTagSet = tagRecallNum.keySet();

        //id 和 tag map
        Map<String, String> idTags = new HashMap<>();

        //tag 和 id map, id List 按hotboost 有序
        Map<String, List<String>> tagIdsImmutable = LdaTopicCache.getFromCache(recallTagSet);
        Map<String, List<String>> tagIds = new HashMap<>(tagIdsImmutable);


        //串起整个召回结果
        List<RecallResult> resultDocs = new ArrayList<>();

        List<String> totalId = new ArrayList<>();
        int loopTime = 0;
        while (tagIds.keySet().size() >= 2 && loopTime < 8) {
            loopTime++;

            for (Iterator<Map.Entry<String, List<String>>> it = tagIds.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, List<String>> entry = it.next();

                String tag = entry.getKey();
                List<String> docIds = entry.getValue();

                int recallSize = loopTime * tagRecallNum.get(tag) * 3;

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
            List<Document> filteredDocs = BloomFilter.concurrentBySimid(mixRequestInfo.getUid(), docs);
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

    private static void setWhyInfo(List<RecallResult> recallResults, UserProfileEnum.TagPeriod tagPeriod) {
        WhyReason whyReasonTrue = null;
        if (tagPeriod.equals(UserProfileEnum.TagPeriod.LONG)) {
            whyReasonTrue = WhyReason.LDA_TOPIC_LONG;
        }else if(tagPeriod.equals(UserProfileEnum.TagPeriod.RECENT)){
            whyReasonTrue = WhyReason.LDA_TOPIC_RECENT;
        }

        for (RecallResult recallResult : recallResults) {
            recallResult.setWhyReason(whyReasonTrue);
        }
    }


}
