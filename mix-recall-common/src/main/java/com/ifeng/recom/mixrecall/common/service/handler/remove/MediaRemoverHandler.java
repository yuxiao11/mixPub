package com.ifeng.recom.mixrecall.common.service.handler.remove;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.filter.MediaFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link MediaFilter}
 */
@Component
public class MediaRemoverHandler {

    @Autowired
    private MediaFilter mediaFilter;

    private static class mediaHandler implements IItemRemoveHandler<Document> {
        private MediaFilter mediaFilter;
        private int score;

        public mediaHandler(MediaFilter mediaFilter, int score) {
            this.mediaFilter = mediaFilter;
            this.score = score;
        }

        @Override
        public String handlerName() {
            return "media";
        }


        @Override
        public boolean remove(MixRequestInfo info, Document item) {
            String source = item.getSource();
            if (StringUtils.isBlank(source)) {
                return false;
            }

            Integer mediaScore = mediaFilter.get(source);
            if (mediaScore != null && mediaScore > 0) {
                if (mediaScore >= score) {
                    return true;
                }
            }
            return false;
        }
    }

    public IItemRemoveHandler<Document> buildRemover(final int score) {
        return new MediaRemoverHandler.mediaHandler(this.mediaFilter, score);
    }
}
