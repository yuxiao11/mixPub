package com.ifeng.recom.mixrecall.template;

import com.ifeng.recom.mixrecall.common.constant.MonitorKey;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant.CHANNEL;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.item.Index4User;
import com.ifeng.recom.mixrecall.common.model.item.MixResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.handler.remove.RemoverHandlerService;
import com.ifeng.recom.mixrecall.common.tool.RecallResultLogUtils;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.controller.RealtimeService;
import com.ifeng.recom.mixrecall.controller.RecallService;
<<<<<<< HEAD
import com.ifeng.recom.mixrecall.core.util.MathUtil;
import com.ifeng.recom.mixrecall.prerank.constant.CTRConstant;
import com.ifeng.recom.mixrecall.prerank.executor.FeatureVectorExtractor;
import com.ifeng.recom.mixrecall.prerank.modelconfig.ModelConfigParser;
import com.ifeng.recom.mixrecall.prerank.tools.CtrSmoothParamsManager;
import com.ifeng.recom.mixrecall.prerank.tools.MediaEvalLevelCacheManager;
import com.ifeng.recom.tools.common.logtools.model.TimerEntity;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
=======
import com.ifeng.recom.mixrecall.model.RecallChannelResult;
>>>>>>> add7216e9597a942f2c2c4e74105441dd134a281
import com.ifeng.recom.tools.log.MonitorLogEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

<<<<<<< HEAD
import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
=======
>>>>>>> add7216e9597a942f2c2c4e74105441dd134a281
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by jibin on 2017/12/25.
 */
public abstract class BaseTemplate<T> {
    private final static Logger logger = LoggerFactory.getLogger(BaseTemplate.class);

    @Autowired
    protected RecallService recallService;

    @Autowired
    protected RealtimeService realtimeService;

    @Autowired
    protected DocUtils docUtils;

    @Autowired
    private RemoverHandlerService removerHandlerService;

    /**
     * 获取mix的召回结果，添加abTestMap，对结果进行压缩，回传给调用方，方便后续实验统计
     *
     * @param mixRequestInfo   请求参数
     * @param recallResultList 召回结果
     * @return 压缩后的String
     */
    protected MixResult buildMixResult(MixRequestInfo mixRequestInfo, List<RecallResult> recallResultList, boolean isNewResult) {
        List<Index4User> index4Users = docUtils.getIndex4UserList(mixRequestInfo, recallResultList, isNewResult);
        Map<String, String> abTestMap = mixRequestInfo.getAbTestMap();
        MixResult mixResult = new MixResult(index4Users, abtest(abTestMap), mixRequestInfo);
        return mixResult;
    }

    protected MixResult buildMixResultForLast(MixRequestInfo mixRequestInfo, List<RecallResult> recallResultList, boolean isNewResult) {

        List<Index4User> index4Users = docUtils.getIndex4UserForLastTopic(mixRequestInfo, recallResultList, isNewResult);
        Map<String, String> abTestMap = mixRequestInfo.getAbTestMap();
        MixResult mixResult = new MixResult(index4Users, abtest(abTestMap), mixRequestInfo);
        return mixResult;
    }

    private static Map<String,String> abtest(Map<String,String> ab) {
        if (ab == null) {
            ab = new HashMap<>();
        }
        return ab;
    }

    /**
     * 后续将都迁移到新方法中
     *
     * @param mixRequestInfo
     * @return
     */
    public abstract T doRecom(MixRequestInfo mixRequestInfo);



    /**
     * 通用过滤方法：网信办、三俗、时效、媒体
     *
     * @param mixRequestInfo
     * @param recallResults
     * @param recallChannel
     */
    protected List<RecallResult> doCommonFilter(MixRequestInfo mixRequestInfo, List<RecallResult> recallResults, CHANNEL recallChannel) {
        List<RecallResult> newResult = removerHandlerService.doCommonFilter(mixRequestInfo, recallResults, recallChannel);
        return newResult;
   }

    /**
     * 正反馈强插过滤
     */
    protected List<RecallResult> doPosFeedFilter(MixRequestInfo mixRequestInfo, List<RecallResult> recallResults, CHANNEL recallChannel) {
        List<RecallResult> newResult = removerHandlerService.doPosFeedFilter(mixRequestInfo, recallResults, recallChannel);
        return newResult;
   }


    /**
     * Sub订阅过滤
     *
     * @param mixRequestInfo
     * @param recallResults
     * @param recallChannel
     */
    protected List<RecallResult> doSubFilter(MixRequestInfo mixRequestInfo, List<RecallResult> recallResults, CHANNEL recallChannel) {
        List<RecallResult> newResult = removerHandlerService.doSubFilter(mixRequestInfo, recallResults, recallChannel);
        return newResult;
    }

    protected List<RecallResult> removeDup(MixRequestInfo info, List<RecallResult> recallResults) {
        return removerHandlerService.dupFilter(info, recallResults, CHANNEL.LAST_COTAG);
    }

    /**
     * 此处 将不同文章的Cotag(cotags) 和 召回原因(channels) 进行融合
     *
     * @param recallResults
     * @return
     */
    protected static List<RecallResult> mergeMultiChannelDocs(MixRequestInfo mixRequestInfo, List<RecallResult> recallResults,Map<String,Double> channelRatio) {
        if (recallResults == null || recallResults.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, RecallResult> recallResultMap = new HashMap<>();
        Map<Object, Integer> recallCateNumMap = new HashMap<>();

        for (RecallResult result : recallResults) {
            try {
                String docId = result.getDocument().getDocId();
                try {
                    String cate = result.getDocument().getC().split("\\^")[0];
                    if (!recallCateNumMap.keySet().contains(cate)) {
                        recallCateNumMap.put(cate, 1);
                    } else {
                        recallCateNumMap.put(cate, recallCateNumMap.get(cate) + 1);

                    }
                } catch (Exception e) {
                    continue;
                }


                if (recallResultMap.containsKey(docId)) {
                    RecallResult oldResult = recallResultMap.get(docId);

                    List<WhyReason> channels = oldResult.getChannels();
                    if (channels == null) {
                        channels = new ArrayList<>();
                        channels.add(oldResult.getWhyReason());
                        oldResult.setChannels(channels);
                    }
                    channels.add(result.getWhyReason());

                    if (StringUtils.isNotBlank(result.getRecallTag())) {
                        //多通道recallTag合并
                        Set<String> tags = oldResult.getTags();
                        if (tags == null) {
                            tags = new HashSet<>();
                        }
                        tags.add(result.getRecallTag());
                    }

                } else {
                    //召回文章如果有recallTag,放到tags内,后验数据使用
                    if (StringUtils.isNotBlank(result.getRecallTag())) {
                        Set<String> tags = new HashSet<>();
                        tags.add(result.getRecallTag());
                        result.setTags(tags);
                    }
                    recallResultMap.put(result.getDocument().getDocId(), result);
                }

            } catch (Exception e) {
                logger.error("merge multi channels err:", e);
            }
        }


        // change map to json
        Map<Object, String> resultMap4Json = new HashMap<>();
        try {
            recallCateNumMap.forEach((k, v) -> resultMap4Json.put(k, v.toString()));
            channelRatio.forEach((k,v) -> resultMap4Json.put(k,v.toString()));
            resultMap4Json.put("uid", mixRequestInfo.getUid());
            resultMap4Json.put("recallid", mixRequestInfo.getRecallid());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            resultMap4Json.put("recallTime", String.valueOf(df.format(new Date())));
        } catch (Exception e) {
            logger.error("result4Json is error:", e);
        }

        //增加日志打印项 20190730 by YX

        RecallResultLogUtils.debug("uid:{} FinalResult:{}", mixRequestInfo.getUid(), GsonUtil.object2json(resultMap4Json));

        List<RecallResult> mergedDocs = recallResultMap.values().stream().collect(Collectors.toList());
        return mergedDocs;
    }

    public static MonitorLogEntity[] monitorChannel(List<RecallResult> recallResults,String uid) {
        try {
            Map<WhyReason, Integer> map = new EnumMap<WhyReason, Integer>(WhyReason.class);
            for (RecallResult r : recallResults) {
                if (r.getWhyReason() != null) {
                    int num = map.getOrDefault(r.getWhyReason(), 0);
                    map.put(r.getWhyReason(), num + 1);
                }
                if (r.getChannels() != null) {
                    for (WhyReason w : r.getChannels()) {
                        int num = map.getOrDefault(r.getWhyReason(), 0);
                        map.put(r.getWhyReason(), num + 1);
                    }
                }
            }
            if (map.isEmpty()) {
                return null;
            }

            MonitorLogEntity[] result = new MonitorLogEntity[map.size()+1];

            int i = 0;
            for (Map.Entry<WhyReason, Integer> num : map.entrySet()) {
                result[i++] = MonitorLogEntity.intEntity(
                        MonitorKey.CHANNEL_SIZE.strMapper(num.getKey().getValue()), num.getValue());
            }
            result[i] = MonitorLogEntity.stringEntity(MonitorKey.UID, uid);
            return result;
        } catch (Exception e) {
        }
        return null;
    }

    public static MonitorLogEntity[] channelCost(List<RecallChannelResult> channelResults, String uid) {
        try {
            MonitorLogEntity[] result = new MonitorLogEntity[channelResults.size() + 1];
            int i = 0;
            for (RecallChannelResult r : channelResults) {
                result[i++] = MonitorLogEntity.longEntity(
                        MonitorKey.CHANNEL_COST.strMapper(r.getChannel().name()), r.getCost());
            }
            result[i] = MonitorLogEntity.stringEntity(MonitorKey.UID, uid);
            return result;
        } catch (Exception e) {
        }
        return null;
    }
}
