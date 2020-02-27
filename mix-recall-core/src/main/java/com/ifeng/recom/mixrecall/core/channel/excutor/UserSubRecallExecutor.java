package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.RecordTime;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.JsonUtil;
import com.ifeng.recom.mixrecall.common.util.UserProfileUtils;
import com.ifeng.recom.mixrecall.common.util.WhyFiledUtils;
import com.ifeng.recom.mixrecall.core.cache.CacheManager;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.ifeng.recom.mixrecall.common.service.BloomFilter.filterSimIdByBloomFilter;
import static com.ifeng.recom.mixrecall.core.util.RecallUtils.getRecallTagAndDocListMapFromDocList;

/**
 * Created by lilg1 on 2018/1/18.
 * 用户订阅召回
 */
public class UserSubRecallExecutor implements Callable<List<Document>> {
    private static final Logger logger = LoggerFactory.getLogger(UserSubRecallExecutor.class);

    private MixRequestInfo mixRequestInfo;

    private final int numSub_Need_recent = 20;
    private final int numSub_Need_long = 180;

    private static final String key_Source = "source=";

    private static RecordTime.TimeComparator timeComparator = new RecordTime.TimeComparator();


    /**
     * 总的召回结果数量
     */
    private int numToAdd = 100;

    /**
     * 每个媒体的召回结果数量
     */
    private int numEach = 3;

    @Override
    public List<Document> call() throws Exception {

        List<Document> result = new ArrayList<>();
        UserModel userModel = mixRequestInfo.getUserModel();
        if (userModel == null) {
            return result;
        }

        List<RecordTime> userSub = userModel.getUb();
        if (CollectionUtils.isEmpty(userSub)) {
            return result;
        }

        List<RecordTime> userSubCall = Lists.newArrayList();
        if (numSub_Need_long >= userSub.size()) {
            userSubCall.addAll(userSub);
        } else {
            userSubCall = randomPickSub(userSub);
        }


        Set<String> subMedias = userSubCall.stream().map(x -> key_Source + x.getRecordName()).collect(Collectors.toSet());
        Set<String> mediaSet=null;

        //求交集
        mediaSet=getIntersection(subMedias,userModel);

        Map<String, List<String>> tagIdsImmutable = CacheManager.getPreloadDocId(subMedias, CacheManager.PreloadDocType.DOC);
        Map<String, List<String>> tagIds = new HashMap<>(tagIdsImmutable);


        if (mixRequestInfo.isDebugUser()) {
            logger.info("{} tagIdsSize:{}, subMediasSize: {}", mixRequestInfo.getUid(), tagIds.size(), subMedias.size());
            if (tagIds.isEmpty()) {
                logger.info("{} userSubCall isNull :{} ", mixRequestInfo.getUid(), JsonUtil.object2jsonWithoutException(subMedias));
            }
        }


        //id 和 tag map
        Map<String, String> idTags = new HashMap<>();

        List<String> totalId = new ArrayList<>();
        int loopTime = 0;
        while (tagIds.keySet().size() >= 1 && loopTime < 5) {
            loopTime++;

            for (Iterator<Map.Entry<String, List<String>>> it = tagIds.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, List<String>> entry = it.next();

                String tag = entry.getKey();
                List<String> docIds = entry.getValue();

                int recallSize = loopTime * loopTime * 10;

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

            Map<String, Document> idDocs =  DocPreloadCache.getBatchDocsWithQueryNoClone(totalIdSet);

            List<Document> docs = new ArrayList<>(idDocs.values());

            List<Document> filteredDocs = filterSimIdByBloomFilter(userModel.getUserId(), docs);

            Map<String, List<Document>> filteredTagDocs = getRecallTagAndDocListMapFromDocList(filteredDocs, idTags);
            Map<String, List<Document>> supplyTagDocs = Maps.newHashMap();

            //先取有交叉的
            for (Map.Entry<String, List<Document>> entry : filteredTagDocs.entrySet()) {
                if (result.size() > numToAdd) {
                    continue;
                }

                String tag = entry.getKey();
                List<Document> docs4tag = entry.getValue();
                if(mediaSet!=null&&!mediaSet.contains(tag)){
                    supplyTagDocs.put(tag,docs4tag);
                    continue;
                }
                int number = this.numEach;

                int subSize = Math.min(number, docs4tag.size());

                docs4tag.forEach(x -> x.setRecallTag(tag));
                result.addAll(docs4tag.subList(0, subSize));

                int needNum = number - subSize;

                if (needNum <= 0) {
                    tagIds.remove(tag);
                }
            }


            if(result.size()<numToAdd){
                doSupply(tagIds,result,supplyTagDocs,numToAdd);
            }
        }

        WhyFiledUtils.setWhy(WhyReason.USER_SUB, result);
        return result;
    }

    private void doSupply(Map<String, List<String>> tagIds,List<Document> result, Map<String, List<Document>> supplyTagDocs,int num){
        try{
            if(MapUtils.isEmpty(supplyTagDocs)){
                return;
            }
            for (Map.Entry<String, List<Document>> entry : supplyTagDocs.entrySet()) {
                if (result.size() > numToAdd) {
                    continue;
                }

                String tag = entry.getKey();
                List<Document> docs4tag = entry.getValue();

                int number = this.numEach;

                int subSize = Math.min(number, docs4tag.size());

                docs4tag.forEach(x -> x.setRecallTag(tag));
                result.addAll(docs4tag.subList(0, subSize));

                int needNum = number - subSize;

                if (needNum <= 0) {
                    tagIds.remove(tag);
                }
            }
        }catch (Exception e){
            logger.error("{} userSubCall doSupply :{} ", mixRequestInfo.getUid(), e);
            e.printStackTrace();
        }
    }
    private Set<String> getIntersection(Set<String> subMedias, UserModel userModel) {
        Set<String> userMedia=null;
        try {
            if (userModel == null||CollectionUtils.isEmpty(subMedias)) {
                return userMedia;
            }

            List<RecordInfo> mediaList = new ArrayList<>();
            List<RecordInfo> docpicMedia = userModel.getDocpic_media() == null ? new ArrayList<>() : userModel.getDocpic_media();
            List<RecordInfo> videoMedia = userModel.getVideo_media() == null ? new ArrayList<>() : userModel.getVideo_media();

            mediaList.addAll(docpicMedia);
            mediaList.addAll(videoMedia);

            if (CollectionUtils.isEmpty(mediaList)) {
                return userMedia;
            }

            userMedia = mediaList.stream().map(x -> key_Source + x.getRecordName()).collect(Collectors.toSet());
            userMedia.retainAll(subMedias);
            if(mixRequestInfo.isDebugUser()){
                logger.info("uid:{} userSubCall userMedias:{}",mixRequestInfo.getUid(),JsonUtil.object2jsonWithoutException(userMedia));
            }
        }catch (Exception e){
            logger.error("{} userSubCall getIntersection :{} ", mixRequestInfo.getUid(), e);
            e.printStackTrace();
        }

        return userMedia;
    }



    /**
     * @param recordList 按时间排序，用户订阅时间最近被选中的概率是以前订阅的三倍，并按时间概率衰减
     *                   采用轮盘赌的方式计算概率
     * @return
     */
    public List<RecordTime> randomPickSub(List<RecordTime> recordList) {
        List<RecordTime> list = new ArrayList<>();

        List<RecordTime> tmp = Lists.newArrayList();
        tmp.addAll(recordList);

        tmp.sort(timeComparator);
        int num = tmp.size();
        List<Double> weightArr = new ArrayList<>();

        //随机数计算的边界
        Double bound = 0d;
        for (int i = 0; i < tmp.size(); i++) {
            Double weight = num + (i / (num - 1.00)) * 2.00 * num;
            weightArr.add(weight);
            bound = bound + weight;
        }

        Random random = new Random();
        for (int i = 0; i < numSub_Need_recent; i++) {
            Double randomNum = num + (bound - num) * random.nextDouble();
            int sum = 0;
            int j = 0;
            while (sum < randomNum && j < weightArr.size() - 1) {
                sum += weightArr.get(j);
                j++;
            }
            bound = bound - weightArr.get(j);
            list.add(tmp.get(j));
            tmp.remove(j);
            weightArr.remove(j);
        }


        Collections.shuffle(tmp);
        for (RecordTime recordTime : tmp) {
            list.add(recordTime);
            if (list.size() > numSub_Need_long) {
                break;
            }
        }
        return list;
    }


    /**
     * 更新用户订阅的召回数量
     *
     * @param mixRequestInfo
     * @param logicParams
     */
    public UserSubRecallExecutor(MixRequestInfo mixRequestInfo, LogicParams logicParams) {
        this.mixRequestInfo = mixRequestInfo;
        this.numToAdd = logicParams.getNumToAddUserSub();
        this.numEach = logicParams.getNumEachUserSub();
    }



}
