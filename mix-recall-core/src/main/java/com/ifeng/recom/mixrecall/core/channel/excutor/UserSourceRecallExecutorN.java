package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.ifeng.recom.mixrecall.common.config.ApplicationConfig;
import com.ifeng.recom.mixrecall.common.constant.ApolloConstant;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.preload.TrueDocsCache;
import com.ifeng.recom.mixrecall.core.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;

import static com.ifeng.recom.mixrecall.common.service.BloomFilter.filterSimIdByBloomFilter;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.*;


public class UserSourceRecallExecutorN implements Callable<List<RecallResult>> {
    private static final Logger logger = LoggerFactory.getLogger(UserSourceRecallExecutorN.class);

    public static Document.HotBoostComparator hotBoostComparator = new Document.HotBoostComparator();


    private MixRequestInfo mixRequestInfo;
    private List<RecordInfo> recordInfoList;
    private int number = ApplicationConfig.getIntProperty(ApolloConstant.SOURCE_N);//暂定五条

    public UserSourceRecallExecutorN(MixRequestInfo mixRequestInfo, List<RecordInfo> recordInfoList) {
        this.mixRequestInfo = mixRequestInfo;
        this.recordInfoList = recordInfoList;
    }

    @Override
    public List<RecallResult> call() throws Exception {
        if (recordInfoList == null || recordInfoList.isEmpty() || number == 0) {
            return Collections.emptyList();
        }

        try {
            return doRecall();
        } catch (Exception e) {
            logger.error("UserSourceRecallExecutorN recall executor ", e);
            return Collections.emptyList();
        }
    }

    private List<RecallResult> doRecall() {
        long checkSum = MathUtil.getNumByUid(mixRequestInfo.getUid(),"UserSourceRecallTest");

        //画像标签和权重
        Map<String, Double> tagWeight = getTagAndWeightMapTop(recordInfoList,15,checkSum);

        //画像标签和位置
        Map<String, Double> tagPosition = getTagPositionMap(recordInfoList);

        //计算每个标签应召回的文章数量
        Map<String, Integer> tagRecallNum = getSourceRecallNumberN(number, tagWeight);

        //待召回文章的标签
        Set<String> recallTagSet = tagRecallNum.keySet();

        //tag 和 id map, id List 按hotboost 有序
        Map<String, List<String>> tagIdsImmutable = TrueDocsCache.getFromCache(recallTagSet);

        Map<String, List<String>> tagIds = new HashMap<>(tagIdsImmutable);


        //id 和 tag map
        Map<String, String> idTags = new HashMap<>();

        //串起整个召回结果
        List<RecallResult> resultDocs = new ArrayList<>();

        List<String> totalId = new ArrayList<>();
        int loopTime = 0;
        while (tagIds.keySet().size() >= 2 && loopTime < 10) {
            loopTime++;

            for (Iterator<Map.Entry<String, List<String>>> it = tagIds.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, List<String>> entry = it.next();

                String tag = entry.getKey();
                List<String> docIds = entry.getValue();

                int recallSize = loopTime * tagRecallNum.get(tag);

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
            filteredDocs.sort(hotBoostComparator);
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

        if (MathUtil.getNum(100) == 1) {
            logger.debug("UserSourceRecallExecutorN uid:{} num:{}", mixRequestInfo.getUid(), GsonUtil.object2jsonWithoutExpose(tagRecallNum, Map.class));
        }

        setPositionForDocs(resultDocs, tagPosition);
        setWhyForCotag(resultDocs,checkSum);

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


    private static void setWhyForCotag(List<RecallResult> recallResults,long checkSum) {
        if(recallResults!=null){
            for (RecallResult recallResult : recallResults) {
                if(checkSum<0){
                    recallResult.setWhyReason(WhyReason.SOURCE_EX_N);
                }else {
                    recallResult.setWhyReason(WhyReason.SOURCE_EX_N_T);
                }
            }
        }
    }
}
