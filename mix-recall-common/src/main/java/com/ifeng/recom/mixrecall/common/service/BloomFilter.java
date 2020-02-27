package com.ifeng.recom.mixrecall.common.service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.util.bloomfilter.LocalBloomFilter;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.collect.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 远端的集中过滤
 * Created by geyl on 2017/10/30.
 */
@Service
public class BloomFilter {
    private static final Logger logger = LoggerFactory.getLogger(BloomFilter.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    public static Set<String> queryByLocalBloom(String uid, Set<String> simIds) {
        if (simIds != null) {
            //romoveIf 替代removeAll  采取迭代删除 防止抛出ConcurrentModificationException
            simIds.removeIf(s -> LocalBloomFilter.onlyCheck(uid, s));
            simIds.removeIf(s -> StringUtils.isBlank(s));
        }
        return simIds;
    }

    /**
     * 通过http获取bloom数据
     *
     * @param uid
     * @param simIds
     * @return
     */
    private static Set<String> requestBloomFilter(String uid, Set<String> simIds) {
        simIds = queryByLocalBloom(uid, simIds);
        if (CollectionUtils.isEmpty(simIds)) {
            return Collections.EMPTY_SET;
        }
        try {
            Tuple<BloomFilterClient.Status, Set<String>> bloomResult = BloomFilterClient.requestBloomFilter(uid, simIds);
            if (BloomFilterClient.Status.OK == bloomResult.v1()) {
                //simIds 当中存储的成为看过的id;
                simIds.removeAll(bloomResult.v2());
                //将用户看过的结果入本机布隆
                simIds.stream().forEach(tmpSimId -> LocalBloomFilter.onlyPut(uid, tmpSimId));
                return bloomResult.v2();
            }
        } catch (Exception e) {
            logger.error("request bloom error, uid:{}, {}", uid, e);
        }
        return simIds;
    }

    /**
     * 分段请求bloom filter
     *
     * @param uid
     * @param simIds
     * @return
     */
    private static Set<String> requestBloomFilterByPartitions(String uid, Set<String> simIds) {
        Set<String> resultSimIds = new HashSet<>();
        for (List<String> partitionSimIds : Iterables.partition(simIds, 800)) {
            resultSimIds.addAll(requestBloomFilter(uid, new HashSet<>(partitionSimIds)));
        }
        return resultSimIds;
    }

    public static Set<String> filterSimIdByBloomFilter(String uid, Set<String> simIdSet) {
        return requestBloomFilter(uid, simIdSet);
    }

    /**
     * 根据Document对象获取simId后才可进行布隆过滤
     * @param uid
     * @param documentList
     * @return
     */
    public static List<Document> filterSimIdByBloomFilter(String uid, List<Document> documentList) {
        if (StringUtils.isBlank(uid) || documentList == null || documentList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> simIdSet = new HashSet<>();
        for (Document document : documentList) {
            simIdSet.add(document.getSimId());
        }

        Set<String> resultSimIds = requestBloomFilterByPartitions(uid, simIdSet);

        List<Document> filteredList = new ArrayList<>();
        for (Document document : documentList) {
            if (resultSimIds.contains(document.getSimId())) {
                filteredList.add(document);
            }
        }

        return filteredList;
    }

    /**
     * 保证原顺序，分段请求bloom
     *
     * @param uid
     * @param recallResults
     * @return
     */
    public static List<RecallResult> bloomFilterForRecallResult(String uid, List<RecallResult> recallResults) {
        if (StringUtils.isBlank(uid) || recallResults == null || recallResults.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> simIdSet = new HashSet<>();
        for (RecallResult recallResult : recallResults) {
            simIdSet.add(recallResult.getDocument().getSimId());
        }

        Set<String> resultSimIds = requestBloomFilterByPartitions(uid, simIdSet);

        List<RecallResult> filteredList = new ArrayList<>();
        for (RecallResult recallResult : recallResults) {
            if (resultSimIds.contains(recallResult.getDocument().getSimId())) {
                filteredList.add(recallResult);
            }
        }

        return filteredList;
    }


    public static void main(String[] args) {
//        Document document = new Document();
//        document.setSimId("clusterId_48453344");
//
//        Document document1 = new Document();
//        document1.setSimId("clusterId_24855408");
//
//
//        Document document2 = new Document();
//        document2.setSimId("clusterId_1111");
//
//        List<Document> documentList = new ArrayList<>();
//        documentList.add(document);
//        documentList.add(document1);
//        documentList.add(document2);
//
//        filterSimIdByBloomFilter("867305035296545", documentList).forEach(x -> System.out.println(x.getSimId()));
//        filterSimIdByBloomFilter("867305035296545", documentList).forEach(x -> System.out.println(x.getSimId()));


//        Set<String> simIds=new HashSet<>();
//        simIds.add("1");
//        simIds.add("b");
//        simIds.add("");
//        simIds.add(null);
//
//        System.out.println(new Gson().toJson(simIds));
//        simIds.removeIf(s->StringUtils.isBlank(s));
//        System.out.println(new Gson().toJson(simIds));

    }

    private static final ThreadPoolExecutor bloomPool = new ThreadPoolExecutor(10,20,1, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(100), new ThreadFactoryBuilder().setNameFormat("bloom-pool-%d").build());

    public static List<Document> concurrentBySimid(String uid, List<Document> documentList) {
        if (StringUtils.isBlank(uid) || documentList == null || documentList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> simIdSet = new HashSet<>();
        for (Document document : documentList) {
            simIdSet.add(document.getSimId());
        }
        List<List<String>> list = Lists.newArrayList();

        for (List<String> partitionSimIds : Iterables.partition(simIdSet, 800)) {
            list.add(partitionSimIds);
        }

        CountDownLatch countDownLatch = new CountDownLatch(list.size());
        List<Future<Set<String>>> futures = Lists.newArrayList();
        for (List<String> sids : list) {
            Set<String> s = new HashSet<>(sids);
            Future<Set<String>> f = bloomPool.submit(new Callable<Set<String>>() {
                @Override
                public Set<String> call() throws Exception {
                    Set<String> resultSimIds = requestBloomFilter(uid, s);
                    countDownLatch.countDown();
                    return resultSimIds;
                }
            });
            futures.add(f);
        }

        Set<String> resultSimIds = new HashSet<>();
        try {
            countDownLatch.await(50,TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("concurrentBySimid error,", e);
        }

        for (Future<Set<String>> f : futures) {
            try {
                Set<String> s = f.get(0, TimeUnit.NANOSECONDS);
                resultSimIds.addAll(s);
            } catch (Exception e) {
                f.cancel(true);
                logger.error("concurrentBySimid futureGet error,", e);
            }
        }


        List<Document> filteredList = new ArrayList<>();
        for (Document document : documentList) {
            if (resultSimIds.contains(document.getSimId())) {
                filteredList.add(document);
            }
        }

        return filteredList;
    }
}
