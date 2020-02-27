package com.ifeng.recom.mixrecall.common.service.handler.remove;

import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.PartCategoryEnum;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SimpleFieldRemoveHandlerConfig {

    /**
     * pplive 过滤
     *
     * @return
     */
    @Bean("ppLiveRemover")
    public IItemRemoveHandler<Document> ppLiveRemover() {
        return new IItemRemoveHandler<Document>() {
            @Override
            public boolean remove(MixRequestInfo info, Document item) {
                return PartCategoryEnum.ppLive.getValue().equals(item.getPartCategory());
            }

            @Override
            public String handlerName() {
                return "ppLive";
            }
        };
    }

    /**
     * 趣头条过滤
     *
     * @return
     */
    @Bean("quTouTiaoRemover")
    public IItemRemoveHandler<Document> quTouTiaoRemover() {
        return new IItemRemoveHandler<Document>() {
            @Override
            public boolean remove(MixRequestInfo info, Document item) {
                return GyConstant.qutt.equals(item.getPartCategoryExt());
            }

            @Override
            public String handlerName() {
                return "quTouTiao";
            }
        };
    }

    /**
     * 小视频过滤
     *
     * @return
     */
    @Bean("smallVideosRemover")
    public IItemRemoveHandler<Document> smallVideosRemover() {
        return new IItemRemoveHandler<Document>() {
            @Override
            public boolean remove(MixRequestInfo info, Document item) {
                return PartCategoryEnum.miniVideo.getValue().equals(item.getPartCategory());
            }

            @Override
            public String handlerName() {
                return "smallVideos";
            }
        };
    }

    /**
     * 空指针过滤
     *
     * @return
     */
    @Bean("nullRemover")
    public IItemRemoveHandler<RecallResult> nullRemover() {
        return new IItemRemoveHandler<RecallResult>() {
            @Override
            public boolean remove(MixRequestInfo info, RecallResult item) {
                return item == null || item.getDocument() == null;
            }

            @Override
            public String handlerName() {
                return "null";
            }
        };
    }
}
