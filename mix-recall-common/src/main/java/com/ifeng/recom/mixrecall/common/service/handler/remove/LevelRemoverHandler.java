package com.ifeng.recom.mixrecall.common.service.handler.remove;

import com.ifeng.recom.mixrecall.common.cache.SourceInfoCache;
import com.ifeng.recom.mixrecall.common.cache.VideoSourceInfoCache;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * 根据媒体评级，进行过滤，保留指定level的
 *
 * @return 过滤后 document list
 */
@Component
public class LevelRemoverHandler {
    @Autowired
    private VideoSourceInfoCache videoSourceInfoCache;

    @Autowired
    private SourceInfoCache sourceInfoCache;

    private static final class sourceHandler implements IItemRemoveHandler<Document> {
        private VideoSourceInfoCache videoSourceInfoCache;
        private SourceInfoCache sourceInfoCache;
        private Set<String> needLevel;


        public sourceHandler(VideoSourceInfoCache videoSourceInfoCache, SourceInfoCache sourceInfoCache, Set<String> needLevel) {
            this.videoSourceInfoCache = videoSourceInfoCache;
            this.sourceInfoCache = sourceInfoCache;
            this.needLevel = needLevel;
        }

        @Override
        public String handlerName() {
            return "level";
        }

        @Override
        public boolean remove(MixRequestInfo info, Document item) {
            String source = item.getSource();
            if (StringUtils.isBlank(source)) {
                return false;
            }

            String sourceLevel;
            if ("video".equalsIgnoreCase(item.getDocType())) {
                sourceLevel = videoSourceInfoCache.get(source);
                if (StringUtils.isBlank(sourceLevel)) {
                    sourceLevel = sourceInfoCache.get(source);
                }
            } else {
                sourceLevel = sourceInfoCache.get(source);
            }

            if (StringUtils.isBlank(sourceLevel) || needLevel.contains(sourceLevel)) {
                return false;
            }
            return true;
        }
    }

    public IItemRemoveHandler<Document> buildRemover(Set<String> needLevel) {
        return new LevelRemoverHandler.sourceHandler(this.videoSourceInfoCache, this.sourceInfoCache, needLevel);
    }
}
