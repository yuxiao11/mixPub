package com.ifeng.recom.mixrecall.common.service.handler.remove;

import com.google.common.collect.Sets;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TimeSensitiveRemoveHandler {

    private static Set<String> specialCategory = Sets.newHashSet("时事", "国际", "足球", "篮球", "军事", "时政", "财经");


    /**
     * 时效性过滤
     *
     * @return
     */
    @Bean("timeSensitiveRemover")
    public IItemRemoveHandler<Document> timeSensitiveRemover() {
        return new IItemRemoveHandler<Document>() {
            @Override
            public boolean remove(MixRequestInfo info, Document item) {
                long now = System.currentTimeMillis();
                if (item.isAvailable()) {
                    if (item.getTimeSensitive() == null || now > Double.valueOf(DocUtils.convertTime(item.getTimeSensitive()))) {
                        return true;
                    }
                    return false;
                }
                return true;
            }

            @Override
            public boolean errStat() {
                return true;
            }

            @Override
            public String handlerName() {
                return "timeSensitive";
            }
        };
    }

    /**
     * 时政、国际类 时效性过滤
     *
     * @return
     */
    @Bean("timeSensitiveWithCategoryRemover")
    public IItemRemoveHandler<Document> timeSensitiveWithCategoryRemover() {
        return new IItemRemoveHandler<Document>() {
            @Override
            public boolean remove(MixRequestInfo info, Document item) {
                long now = System.currentTimeMillis();
                String topic1 = item.getTopic1();
                if (StringUtils.isNotBlank(topic1)) {
                    if (topic1.contains("时事") || topic1.contains("国际") || topic1.contains("足球")
                            || topic1.contains("篮球") || topic1.contains("军事") || topic1.contains("时政")
                            || topic1.contains("财经")) {
                        if (!item.isAvailable()) {
                            return true;
                        } else if (item.getTimeSensitive() == null ||
                                now > Double.valueOf(DocUtils.convertTime(item.getTimeSensitive()))) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean errStat() {
                return true;
            }

            @Override
            public String handlerName() {
                return "timeSensitiveCategory";
            }
        };
    }
}
