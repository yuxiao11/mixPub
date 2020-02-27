package com.ifeng.recom.mixrecall.prerank;


import com.ifeng.recom.mixrecall.common.model.RecallResult;

import com.ifeng.recom.mixrecall.prerank.constant.CTRConstant;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import com.ifeng.recom.mixrecall.prerank.threadUtil.CTRThreadPool;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * Created by jibin on 2017/5/23.
 */
public class CalcCtrUtils {

    private static final Logger logger = LoggerFactory.getLogger(CalcCtrUtils.class);

    /**
     * 计算ctr
     * @throws InterruptedException
     */
    public static void calcCTR(ConcurrentHashMap<String, String> ctrMap,
                               List<RecallResult> itemList, Model model, List<FeatureContext> featureEntitys,
                               CacheManager cacheManager, double [] uidVec) throws InterruptedException {
        long start = System.currentTimeMillis();  //ctrMap 为空CurrentHashMap
        if (featureEntitys == null) {
            //默认处理
            calcCTRDefProcess(ctrMap, itemList);
//            logger.warn("sid: featureEntities is null:{} ,bsMapList: {}" ,sId , itemList.size());
            return;
        }

        if (featureEntitys.size() < itemList.size()) {
            //默认处理
            calcCTRDefProcess(ctrMap, itemList);
//            logger.warn("sid:{} failed featureEntitys:{} ,bsMapList: {}",sId , featureEntitys.size() , itemList.size());
            return;
        }


        // 分线程计算ctr
        int size = itemList.size();
        int numOfEachThread = size / CTRThreadPool.NUMBER_OF_CORES;
        int mod = size % CTRThreadPool.NUMBER_OF_CORES;
        int numOfSomeThread = numOfEachThread + 1;
        Collection<CalcCtrTask> tasks = new ArrayList<CalcCtrTask>();

        //每个线程计算的Item个数尽可能平均，对于不能整除的id们分别分配到在前mod个线程中，即 前mod个线程平均每个多消费一个id
        for (int i = 0; i < mod; i++) {
            tasks.add(new CalcCtrTask(i * numOfSomeThread, (i + 1) * numOfSomeThread, model,
              featureEntitys,cacheManager, uidVec));
        }

        if (numOfEachThread > 0) {
            for (int i = mod; i < CTRThreadPool.NUMBER_OF_CORES; i++) {
                tasks.add(new CalcCtrTask(i * numOfEachThread + mod, (i + 1) * numOfEachThread + mod, model,
                  featureEntitys,cacheManager, uidVec));
            }
        }

        List<Future<Map<String, String>>> result =
          CTRThreadPool.THREAD_POOL_Calc.invokeAll(tasks, CTRConstant.MaxTimeOut_CalcCtr, TimeUnit.MILLISECONDS);
        int cancelledNum = 0;
        for (Future<Map<String, String>> future : result) {
            try {
                Map<String, String> childResult = future.get();
                if (childResult != null && childResult.size() > 0) {
                    ctrMap.putAll(childResult);
                }
            } catch (CancellationException e) {
                cancelledNum++;
            } catch (Exception e) {
                logger.error("calcCTR ERROR:{}", e);
            }finally {
                future.cancel(true);
            }
        }

        long cost = System.currentTimeMillis() - start;

        //cancelledNum:线程丢弃的数目 ctrMap 赋默认值
        if (cancelledNum > 0) {
            calcCTRDefProcess(ctrMap, itemList);
            logger.error("calcCTR  cancelledNum: {}  of  {}   tasks are cancelled! cost:{}", cancelledNum, tasks.size(), cost);
        }

    }


    public static void calcCTRDefProcess(ConcurrentHashMap<String, String> ctrMap, List<RecallResult> itemList) {
        for (RecallResult item : itemList) {
            if(StringUtils.isBlank(ctrMap.get(item.getDocument().getDocId()))){
                item.setCtr(Double.parseDouble(CTRConstant.DEFAULT_CTR));
                ctrMap.put(item.getDocument().getDocId(), CTRConstant.DEFAULT_CTR);
            }
        }
    }
}
