package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.HomeLogUtils;
import com.ifeng.recom.mixrecall.core.channel.excutor.cotag.CoTagHomeExecutor;
import com.ifeng.recom.mixrecall.core.threadpool.HomeThreadPool;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by geyl on 2017/10/30.
 * 根据画像中的Combine Tag 字段进行召回
 */
@Service
public class CoTagChannelFirstImpl {
    private static final Logger logger = LoggerFactory.getLogger(CoTagChannelFirstImpl.class);

    private static int timeout = 100;

    private RecallResult.WeightAndPreloadPositionComparator weightAndPreloadPositionComparatorNew = new RecallResult.WeightAndPreloadPositionComparator();

    /**
     * 新版头条首屏
     *
     * @param mixRequestInfo
     * @return
     */
    public List<RecallResult> doToutiaoFirstRecall(MixRequestInfo mixRequestInfo) {
        TimerEntity timer = TimerEntityUtil.getInstance();
        UserModel userModel = mixRequestInfo.getUserModel();
        String uid = mixRequestInfo.getUid();

        List<RecallResult> result = null;
        int resultSize = 0;
        int docSize = 0;
        int videoSize = 0;

        if(userModel==null) {
            return new ArrayList<>();
        }
        timer.addStartTime("recall");
        try {

            UserProfileEnum.TagPeriod vPeriod = UserProfileEnum.TagPeriod.LONG;
            UserProfileEnum.TagPeriod dPeriod = UserProfileEnum.TagPeriod.LONG;

            List<RecordInfo> dCotagList = null;
            List<RecordInfo> vCotagList = null;
            if(CollectionUtils.isNotEmpty(userModel.getDocpic_cotag())) {
                dCotagList = userModel.getDocpic_cotag();
            }else{
                //长期兴趣标签为空时，可能是新用户，使用短期last兴趣进行打底
                dCotagList = userModel.getLastCombineTagList();
                dPeriod = UserProfileEnum.TagPeriod.LAST;
            }

            if(CollectionUtils.isNotEmpty(userModel.getVideo_cotag())) {
                vCotagList = userModel.getVideo_cotag();
            }else{
                //长期兴趣标签为空时，可能是新用户，使用短期last兴趣进行打底
                vCotagList = userModel.getLastCombineTagList();
                vPeriod = UserProfileEnum.TagPeriod.LAST;
            }

            Future<List<RecallResult>> cotagD = HomeThreadPool.submitExecutorTask(new CoTagHomeExecutor(mixRequestInfo, dCotagList, DocType.DOCPIC, dPeriod, 300));
            Future<List<RecallResult>> cotagV = HomeThreadPool.submitExecutorTask(new CoTagHomeExecutor(mixRequestInfo, vCotagList, DocType.VIDEO, vPeriod, 300));

            Map<String, Future<List<RecallResult>>> threadMap = new HashMap<>();
            threadMap.put(DocType.DOCPIC.getValue(), cotagD);
            threadMap.put(DocType.SVIDEO.getValue(), cotagV);

            Map<String, List<RecallResult>> recallResult = HomeThreadPool.getExecutorResult(threadMap, timeout);
            result = recallResult.values().stream().collect(ArrayList::new, List::addAll, List::addAll);

            result.sort(weightAndPreloadPositionComparatorNew);
        }catch (Exception e){
            logger.error("uid:{} get result err:{}", mixRequestInfo.getUid(), e);
        }finally {
            timer.addEndTime("recall");
            HomeLogUtils.info("uid:{} home recall: {}",uid,timer.getStaticsInfo());
            HomeLogUtils.info("uid:{}, type:{} ,recall num:total:{},docpicLong:{},vLong:{}", uid, "touTiaoHome", resultSize, docSize, videoSize);
        }
        return result;
    }


}
