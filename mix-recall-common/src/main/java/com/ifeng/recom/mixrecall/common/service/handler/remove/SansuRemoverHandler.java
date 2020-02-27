package com.ifeng.recom.mixrecall.common.service.handler.remove;

import com.google.common.base.Strings;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.filter.SansuFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * {@link SansuFilter}
 */
@Component
public class SansuRemoverHandler {

    @Autowired
    private SansuFilter sansuFilter;

    private static class sansuHandler implements IItemRemoveHandler<Document> {
        private SansuFilter sansuFilter;
        private int score;

        public sansuHandler(SansuFilter sansuFilter, int score) {
            this.sansuFilter = sansuFilter;
            this.score = score;
        }

        @Override
        public String handlerName() {
            return "sansu";
        }


        @Override
        public boolean remove(MixRequestInfo info, Document item) {
            if (Strings.isNullOrEmpty(item.getTitle())) {
                return false;
            }
            int titleScore = item.getTitleWordScore();

            if (titleScore == -1) {
                titleScore = sansuFilter.textFilter(item.getTitle(), score);
                item.setTitleWordScore(titleScore);
            }
            if (score >= titleScore) {
                return false;
            }
            return true;
        }
    }

    public IItemRemoveHandler<Document> buildRemover(final int score) {
        return new sansuHandler(this.sansuFilter, score);
    }
}
