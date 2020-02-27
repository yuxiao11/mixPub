package com.ifeng.recom.mixrecall.core.channel.excutor.cotag;

import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.config.ApplicationConfig;
import com.ifeng.recom.mixrecall.common.constant.ApolloConstant;
import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum.TagPeriod;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import com.ifeng.recom.mixrecall.common.util.UserUtils;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.preload.CotagDocsNewCache;
import com.ifeng.recom.mixrecall.core.cache.preload.CotagVideoNewCache;
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
 * Created by pd on 2019/03/27.
 */
public class CoTagSimExecutor implements Callable<List<RecallResult>> {

    private static final Logger logger = LoggerFactory.getLogger(CoTagSimExecutor.class);

    public static Document.HotBoostComparator hotBoostComparator = new Document.HotBoostComparator();
    private static Gson gson = new Gson();

    private MixRequestInfo mixRequestInfo;
    private TagPeriod tagPeriod;
    private List<String> recordInfoList;
    private int number;
    private DocType docType;
    private Map<String, Integer> tagRecallNum;
    private Map<String, Integer> tagRecallNum1;

    public CoTagSimExecutor(MixRequestInfo mixRequestInfo, List<String> recordInfoList, DocType docType, TagPeriod tagPeriod, int number){
        this.mixRequestInfo = mixRequestInfo;
        this.recordInfoList = recordInfoList;
        this.tagPeriod = tagPeriod;
        this.docType = docType;
        this.number = number;
    }


    @Override
    public List<RecallResult> call() throws Exception {
        if (CollectionUtils.isEmpty(recordInfoList)) {
            return Collections.EMPTY_LIST;
        }
        //此处添加负反馈
        int originSize = recordInfoList.size();
        Map<String,Double> negativeList = mixRequestInfo.getNegativeMap().get("featureWord");
        if(negativeList != null && negativeList.size() >= 1){
            List<String> finalRecordInfo = recordInfoList.stream().filter(item -> !negativeList.keySet().contains(item.split("-")[1])).collect(Collectors.toList());

            recordInfoList = finalRecordInfo;

            logger.info("{} Channel:{} __________Negative_________ OriginSize:{} FilteredSize:{} Remove:{}",mixRequestInfo.getUid(),CoTagSimExecutor.class.getSimpleName(), originSize,recordInfoList.size(),originSize-recordInfoList.size());
        }

        if(recordInfoList == null || recordInfoList.isEmpty() || number == 0){
            return Collections.emptyList();
        }

        List<RecallResult> result = Collections.emptyList();
        try{
            result = doRecall();
        }catch (Exception e){
            logger.error("cotag home err:{}",e);
        }

        return result;
    }


    private List<RecallResult> doRecall() {
        //画像标签和位置 对于本通道 此不重要
        Map<String, Double> tagPosition = getTagPositionForSim(recordInfoList);

        //计算每个标签应召回的文章数量
        int everyNum=Math.max(number/recordInfoList.size(),3);
        int tagNum=number/everyNum;
        recordInfoList=recordInfoList.subList(0,Math.min(tagNum,recordInfoList.size()));

        tagRecallNum = getTagRecallNumberSimWithNegative(everyNum,recordInfoList,mixRequestInfo);

        logger.info("check sim uid:{} recordInfo:{}",mixRequestInfo.getUid(),recordInfoList.toString());


        //待召回文章的标签
        Set<String> recallTagSet = tagRecallNum.keySet();

        //tag 和 id map, id List 按hotboost 有序
        Map<String, List<String>> tagIdsImmutable;


        if(docType.equals(DocType.VIDEO)) {
            tagIdsImmutable = CotagVideoNewCache.getFromCache(recallTagSet);
        }else{
            tagIdsImmutable = CotagDocsNewCache.getFromCache(recallTagSet);
        }

        Map<String, List<String>> tagIds = new HashMap<>(tagIdsImmutable);

        //id 和 tag map
        Map<String, String> idTags = new HashMap<>();

        //串起整个召回结果
        List<RecallResult> resultDocs = new ArrayList<>();

        List<String> totalId = new ArrayList<>();


        for (Iterator<Map.Entry<String, List<String>>> it = tagIds.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, List<String>> entry = it.next();

            String tag = entry.getKey();
            List<String> docIds = entry.getValue();

            int recallSize = tagRecallNum.get(tag) * 3;

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

        //按召回条数截断
        filteredTagDocs.entrySet().stream().forEach(x->resultDocs.addAll(x.getValue().subList(0,Math.min(x.getValue().size(),tagRecallNum.get(x.getKey())))));


        if (MathUtil.getNum(100) == 1) {
            logger.debug("uid:{} num:{}", mixRequestInfo.getUid(), gson.toJson(tagRecallNum, Map.class));
        }

        setPositionForDocs(resultDocs, tagPosition);
        setWhyInfo(resultDocs, tagPeriod,docType);
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

    private static void setWhyInfo(List<RecallResult> recallResults, TagPeriod tagPeriod,DocType docType) {
        WhyReason whyReasonTrue = null;
        if (tagPeriod.equals(TagPeriod.RECENT)) {
            if(docType.equals(DocType.VIDEO)){
                whyReasonTrue =   WhyReason.COTAG_V_RECENT_SIM;
            }else {
                whyReasonTrue = WhyReason.COTAG_D_RECENT_SIM;
            }
        } else if (tagPeriod.equals(TagPeriod.LAST)) {
            if(docType.equals(DocType.VIDEO)){
                whyReasonTrue = WhyReason.COTAG_V_LAST_SIM;
            }else {
                whyReasonTrue = WhyReason.COTAG_D_LAST_SIM;
            }
        }
        for (RecallResult recallResult : recallResults) {
            recallResult.setWhyReason(whyReasonTrue);
        }
    }

}
