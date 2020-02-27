package com.ifeng.recom.mixrecall.common.service.handler.remove;

import com.ifeng.recom.mixrecall.common.cache.FilterDocsCache;
import com.ifeng.recom.mixrecall.common.cache.LowTagInfoCache;
import com.ifeng.recom.mixrecall.common.cache.WeMediaSourceNameCache;
import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SimpleCacheContaintsRemoveHandlerConfig {

    @Autowired
    private FilterDocsCache filterDocsCache;

    @Autowired
    private LowTagInfoCache lowTagInfoCache;

    @Autowired
    private WeMediaSourceNameCache weMediaSourceNameCache;

    /**
     * 过滤低品质文章，数据来源：张阳
     */
    @Bean("idRemover")
    public IItemRemoveHandler<Document> idRemover() {
        return new IItemRemoveHandler<Document>() {
            @Override
            public boolean remove(MixRequestInfo info, Document item) {
                return filterDocsCache.containsKey(item.getSimId());
            }

            @Override
            public String handlerName() {
                return "id";
            }

        };
    }

    /**
     * 过滤审核low标签
     *
     * @return
     */
    @Bean("lowTagRemover")
    public IItemRemoveHandler<Document> lowTagRemover() {
        return new IItemRemoveHandler<Document>() {
            @Override
            public boolean remove(MixRequestInfo info, Document item) {
                return lowTagInfoCache.containsKey(item.getSimId());
            }

            @Override
            public String handlerName() {
                return "lowTag";
            }

        };
    }

    /**
     * 过滤特定媒体名称
     * 视频不用进行机构媒体过滤
     *
     * @return
     */
    @Bean("sourceNameRemover")
    public IItemRemoveHandler<Document> sourceNameRemover() {
        return new IItemRemoveHandler<Document>() {
            @Override
            public boolean remove(MixRequestInfo info, Document item) {
                return (
                        !DocType.VIDEO.getValue().equals(item.getDocType()) &&
                                !weMediaSourceNameCache.containsKey(item.getSource())
                );
            }

            @Override
            public String handlerName() {
                return "sourceName";
            }

        };
    }
}
