package com.ifeng.recom.mixrecall.core.channel.excutor.cotag;

import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.preload.CotagDocsNewCache;
import com.ifeng.recom.mixrecall.core.cache.preload.CotagVideoNewCache;
import com.ifeng.recom.mixrecall.core.util.MathUtil;
import com.ifeng.recom.mixrecall.core.util.RecallUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.ifeng.recom.mixrecall.common.service.BloomFilter.filterSimIdByBloomFilter;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getRecallTagAndRecallResultListMapFromDocList;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getTagPositionMap;

/**
 * Created by liligeng on 2019/6/14.
 */
public class LastCoTagExecutor implements Callable<List<RecallResult>> {

    private static Gson gson = new Gson();

    private static final Logger logger = LoggerFactory.getLogger(LastCoTagExecutor.class);
    private static Document.HotBoostComparator hotBoostComparator = new Document.HotBoostComparator();

    private MixRequestInfo mixRequestInfo;
    private List<String> recallTags;
    private UserModel userModel;
    private DocType docType;
    private int number;

    public LastCoTagExecutor(MixRequestInfo mixRequestInfo, List<String> recallTags, DocType docType, UserModel userModel, int number) {
        this.mixRequestInfo = mixRequestInfo;
        this.recallTags = recallTags;
        this.docType = docType;
        this.userModel = userModel;
        this.number = number;
    }

    @Override
    public List<RecallResult> call() throws Exception {
        List<RecallResult> results = new ArrayList<>();
        if (recallTags == null || recallTags.isEmpty()) {
            return results;
        }
        List<RecordInfo> last_uCombinedTag = userModel.getLastCombineTagList();
        Set<String> tagSet = new HashSet<>(recallTags);

        //获取待推荐
        List<RecordInfo> recordInfoList = last_uCombinedTag.stream().filter(x -> tagSet.contains(x.getRecordName())).collect(Collectors.toList());
        if (mixRequestInfo.getUid().equals("99001008030221")) {
            logger.info("uid:{}, last_uComtag:{}, match:{}", mixRequestInfo.getUid(), gson.toJson(last_uCombinedTag), gson.toJson(recordInfoList));
        }


        Map<String, Double> tagPosition = getTagPositionMap(recordInfoList);

        //计算每个标签应召回的文章数量
        Map<String, Integer> tagRecallNum = RecallUtils.getTagRecallNumberTest(number, recordInfoList, null);

        //待召回文章的标签
        Set<String> recallTagSet = tagRecallNum.keySet();

        //tag 和 id map, id List 按hotboost 有序
        Map<String, List<String>> tagIdsImmutable;

        if(DocType.VIDEO.equals(docType)){
            tagIdsImmutable = CotagVideoNewCache.getFromCache(recallTagSet);

        }else{
            tagIdsImmutable = CotagDocsNewCache.getFromCache(recallTagSet);
        }
        if (mixRequestInfo.getUid().equals("99001008030221")) {
            logger.info("uid:{}, result:{}", mixRequestInfo.getUid(), gson.toJson(tagIdsImmutable));
        }

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
            logger.debug("uid:{} num:{}", mixRequestInfo.getUid(), gson.toJson(tagRecallNum, Map.class));
        }

        if (mixRequestInfo.getUid().equals("99001008030221")) {
            logger.info("uid:{}, result:{}", mixRequestInfo.getUid(), gson.toJson(resultDocs));
        }
        setPositionForDocs(resultDocs, tagPosition);
        setWhyInfo(resultDocs, docType);

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

    private static void setWhyInfo(List<RecallResult> recallResults, DocType docType) {
        for (RecallResult recallResult : recallResults) {
            if (DocType.VIDEO.equals(docType)) {
                recallResult.setWhyReason(WhyReason.LAST_COTAG_V);
            } else {
                recallResult.setWhyReason(WhyReason.LAST_COTAG_D);
            }
        }
    }
}
