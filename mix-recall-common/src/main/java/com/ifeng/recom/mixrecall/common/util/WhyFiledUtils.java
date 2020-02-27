package com.ifeng.recom.mixrecall.common.util;

import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.Why;

import java.util.List;

/**
 * Created by geyl on 2017/11/7.
 */
public class WhyFiledUtils {

    public static void setWhy(WhyReason reason, List<Document> documentList) {
        for (Document d : documentList) {
            Why why = new Why();
            try {
                why.setHotBoost(d.getHotBoost());
                why.setSource(d.getSource());
                why.setReason(reason);
                d.setWhy(why);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setWhyForRecallResult(WhyReason reason, List<RecallResult> recallResultList) {
        for (RecallResult recallResult : recallResultList) {
            try {
                recallResult.setWhyReason(reason);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
