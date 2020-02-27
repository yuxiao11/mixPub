package com.ifeng.recom.mixrecall.threadpool;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.handler.remove.RemoverHandlerService;
import com.ifeng.recom.mixrecall.common.tool.ChannelLackLogUtils;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.MathTools;
import com.ifeng.recom.mixrecall.model.RecallChannelResult;
import com.ifeng.recom.mixrecall.model.RecallConfig;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class RecallExecutor {
    private static final Logger logger = LoggerFactory.getLogger(RecallExecutor.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RemoverHandlerService removerHandlerService;

    /**
     * 进行召回
     *
     * @param info         请求参数
     * @param channels     需要召回的通道配置
     * @param timeout      总超时
     * @param poolExecutor 异步使用到的线程池
     * @return
     */
    public List<RecallChannelResult> recall(MixRequestInfo info, List<RecallConfig> channels, long timeout,
                                            ThreadPoolExecutor poolExecutor) {
        if (CollectionUtils.isEmpty(channels)) {
            throw new IllegalArgumentException("channel is empty");
        }
        List<Callable<RecallChannelResult>> task = Lists.newArrayList();
        List<String> channelName = new ArrayList<>(channels.size());
        for (RecallConfig b : channels) {
            Function<RecallConfig, RecallChannelResult> mapper = getChannelFunction(b);
            if (mapper == null) {
                logger.info("not found channel function, channel:{}", b.getBeanName());
                continue;
            }
            b.setInfo(info);
            channelName.add(b.getBeanName());
            task.add(buildCallable(b, mapper));
        }
        List<RecallChannelResult> result;
        try {
            result = execute(timeout, task, channelName, info, poolExecutor);
        } catch (InterruptedException e) {
            logger.error("invoke error, uid:{}", info.getUid(), e);
            return Lists.newArrayList();
        }
        return result;
    }

    private List<RecallChannelResult> execute(long timeout,
                                              List<Callable<RecallChannelResult>> list, List<String> channelNames,
                                              MixRequestInfo info, ThreadPoolExecutor threadPool) throws InterruptedException {
        long runStartTime = System.currentTimeMillis();
        List<RecallChannelResult> result = Lists.newArrayList();
        List<Future<RecallChannelResult>> r = threadPool.invokeAll(list, MathTools.closedInterval(0, 5000, timeout), TimeUnit.MILLISECONDS);
        int i = 0;
        long runEndTime = System.currentTimeMillis();
        for (Future<RecallChannelResult> f : r) {
            // 强依赖invoke中根据遍历顺序生成的list, 保证channelName可以对应上.
            String channelName = channelNames.get(i);
            i++;
            RecallChannelResult s = null;
            try {
                s = f.get(0, TimeUnit.NANOSECONDS);
            } catch (Exception e) {
                logger.error("channel execute error, channel:{}, uid:{}, e:{}", channelName, info.getUid(), e);
            }
            if (s == null) {
                logger.info("recall channel result null, channelName:{}, uid:{}", channelName, info.getUid());
                s = new RecallChannelResult().
                        setChannel(RecallConstant.CHANNEL.getChannel(channelName)).
                        setChannelResult(Lists.newArrayList()).
                        setCost(runEndTime - runStartTime);
            }

            result.add(s);
        }
        return result;
    }

    private Function<RecallConfig, RecallChannelResult> getChannelFunction(RecallConfig b) {
        Function<RecallConfig, RecallChannelResult> mapper;
        try {
            mapper = (Function<RecallConfig, RecallChannelResult>) applicationContext.getBean(b.getBeanName());  //这里的mapper的意思是 通过 getBean的形式将RecallConfig 和RecallChannelResult进行联系
        } catch (Exception e) {
            logger.error("not found channel function, channel:{},{}", b.getBeanName(), e);
            return null;
        }
        return mapper;
    }

    private Callable<RecallChannelResult> buildCallable(RecallConfig c, Function<RecallConfig, RecallChannelResult> mapper) {
        return () -> {
            long start = System.currentTimeMillis();
            RecallChannelResult channelResult = mapper.apply(c);
            if (channelResult != null) {
                List<RecallResult> recallResults = removerHandlerService.remove(
                        c.getInfo(),
                        channelResult.getChannelResult(),
                        channelResult.getChannel(),
                        c.getRemoverList()
                );
                int size = recallResults.size();
                channelResult.setRemoveSize(channelResult.getSize() - size).
                        setChannelResult(recallResults).
                        setCost(System.currentTimeMillis() - start);
                // 打印一个监控日志
                ChannelLackLogUtils.recordChannelLack(channelResult.getChannel().toString(), channelResult.getChannelResult());
                return channelResult;
            }
            ChannelLackLogUtils.recordChannelLack(RecallConstant.CHANNEL.getChannel(c.getBeanName()).toString(), Collections.EMPTY_LIST);
            return null;
        };
    }

    /**
     * 进行结果转换
     *
     * @param list
     * @return
     */
    public Map<RecallConstant.CHANNEL, List<RecallResult>> change(List<RecallChannelResult> list) {
        Map<RecallConstant.CHANNEL, List<RecallResult>> r = Maps.newHashMap();
        for (RecallChannelResult result : list) {
            r.put(result.getChannel(), result.getChannelResult());
        }
        return r;
    }

    public String storeLoggerInfo(List<RecallChannelResult> recallChannelResults) {
        return GsonUtil.o2jWithoutExpose(recallChannelResults);
    }
}
