package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.preload.TrueDocsCache;
import com.ifeng.recom.mixrecall.core.util.MathUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.ifeng.recom.mixrecall.common.service.BloomFilter.filterSimIdByBloomFilter;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.*;

/**
 * Created by pd on 2019/4/13.
 */
public class UserMediaRecall implements Callable<List<RecallResult>> {
    private static final Logger logger = LoggerFactory.getLogger(UserMediaRecall.class);

    public static Document.HotBoostComparator hotBoostComparator = new Document.HotBoostComparator();
    private MixRequestInfo mixRequestInfo;
    private TagPeriod tagPeriod;
    private List<RecordInfo> recordInfoList;
    private int number;
    private String docType;



    public UserMediaRecall(MixRequestInfo mixRequestInfo, List<RecordInfo> recordInfoList, TagPeriod tagPeriod, int number,String docType) {
        this.mixRequestInfo = mixRequestInfo;
        this.recordInfoList = recordInfoList;
        this.number = number;
        this.tagPeriod = tagPeriod;
        this.docType=docType;
    }

    @Override
    public List<RecallResult> call() throws Exception {
        /**
         * 由于粗排部分需要统计所有mediaTag的分数所以在召回部分添加得分过滤
         */
        List<RecordInfo> finalRecordInfo = recordInfoList.stream().filter(item -> item.getWeight()>0.5).collect(Collectors.toList());
        recordInfoList = finalRecordInfo;

        if (recordInfoList == null || recordInfoList.isEmpty() || number == 0) {
            return Collections.emptyList();
        }

        try {
            return doRecall();
        } catch (Exception e) {
            logger.error("cotag recall executor ", e);
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
        Set<String> newRecallTagSet= recallTagSet.stream().map(s-> GyConstant.key_Source + s).collect(Collectors.toSet());

        //tag 和 id map, id List 按hotboost 有序
        Map<String, List<String>> tagIdsImmutable= TrueDocsCache.getFromCache(newRecallTagSet);
        /*
           还原tag
         */
        Map<String, List<String>> tagIds = Maps.newHashMap();
        Iterator<Map.Entry<String, List<String>>> iterator = tagIdsImmutable.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<String>> next = iterator.next();
            String key = next.getKey().substring(GyConstant.key_Source.length());
            List<String> value = next.getValue();
            tagIds.put(key,value);
        }

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
            //根据类型过滤
            filterByDocType(docs,docType);

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
            logger.debug("uid:{} num:{}", mixRequestInfo.getUid(), GsonUtil.object2json(tagRecallNum, Map.class));
        }

        setPositionForDocs(resultDocs, tagPosition);
        setWhyInfo(resultDocs, tagPeriod, docType);
        //给每个文章加上预加载redis中真实的位置信息
        DocUtils.setRealPositionForDocs(resultDocs, tagIdsImmutable);
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

    private static void setWhyInfo(List<RecallResult> recallResults, TagPeriod tagPeriod, String docType) {
        WhyReason whyReasonTrue = null;
        if (tagPeriod.equals(TagPeriod.LONG)) {
            if(docType.equals(DocType.DOCPIC.getValue())){
                whyReasonTrue=WhyReason.DOC_MEDIA_L;
            }else{
                whyReasonTrue=WhyReason.VIDEO_MEDIA_L;
            }
        }
        for (RecallResult recallResult : recallResults) {
            recallResult.setWhyReason(whyReasonTrue);
        }
    }

    public static void filterByDocType(List<Document> docs,String docType){
        if(CollectionUtils.isEmpty(docs)){
            return;
        }
        List<Document> removeDocs=new ArrayList<>();
        for(Document doc:docs){
            if(doc!=null&&StringUtils.isNotBlank(doc.getDocType())&&!docType.equalsIgnoreCase(doc.getDocType())){
                removeDocs.add(doc);
            }
        }
        docs.removeAll(removeDocs);
    }

}
