package com.ifeng.recom.mixrecall.common.util.itemUtil.index4User;

import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import org.apache.commons.lang3.StringUtils;

import com.ifeng.recom.mixrecall.common.model.item.Index4User;
import com.ifeng.recom.tools.recallInfo.model.RecallInfo;
import com.ifeng.recom.tools.recallInfo.model.protobuf.RecallInfoProbuf;
import com.ifeng.recom.tools.recallInfo.util.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jibin on 2018/5/3.
 */
public class MixReasonUtils {

    /**
     * 字符串到枚举名称的翻译
     */
    private static Map<String, WhyReason> whyReasonMap = new HashMap<String, WhyReason>() {
        {
            put(WhyReason.POS_FEED_VIDEO_ITEMCF.getValue(), WhyReason.POS_FEED_VIDEO_ITEMCF);
            put(WhyReason.POS_FEED_VIDEO_ITEMCF_LVS.getValue(), WhyReason.POS_FEED_VIDEO_ITEMCF_LVS);
            put(WhyReason.POS_FEED_VIDEO_ES.getValue(), WhyReason.POS_FEED_VIDEO_ES);
            put(WhyReason.POS_FEED_VIDEO_ITEM2VEC.getValue(), WhyReason.POS_FEED_VIDEO_ITEM2VEC);
            put(WhyReason.POS_FEED_DOCPIC_ITEMCF.getValue(), WhyReason.POS_FEED_DOCPIC_ITEMCF);
            put(WhyReason.POS_FEED_DOCPIC_ITEMCF_LVS.getValue(), WhyReason.POS_FEED_DOCPIC_ITEMCF_LVS);
            put(WhyReason.POS_FEED_DOCPIC_ES.getValue(), WhyReason.POS_FEED_DOCPIC_ES);
            put(WhyReason.POS_FEED_VIDEO_ITEMCF_N.getValue(), WhyReason.POS_FEED_VIDEO_ITEMCF_N);
            put(WhyReason.POS_FEED_DOCPIC_ITEMCF_N.getValue(), WhyReason.POS_FEED_DOCPIC_ITEMCF_N);
            put(WhyReason.POS_FEED_DOCPIC_SOURCE.getValue(), WhyReason.POS_FEED_DOCPIC_SOURCE);
            put(WhyReason.POS_FEED_VIDEO_SOURCE.getValue(), WhyReason.POS_FEED_VIDEO_SOURCE);
        }
    };


    /**
     * 正反馈的枚举名称->到枚举的翻译
     * 后面想想办法省去这一步的翻译
     */
    @Deprecated
    public static WhyReason getWhyReasonPosi(String reasonName) {
        WhyReason whyReason = whyReasonMap.get(reasonName);
        if (whyReason == null) {
            whyReason = WhyReason.ITEMCF;
        }
        return whyReason;
    }


    /**
     * 把字符串转换为protobuf对象,兼容老接口
     *
     * @param bufStr
     * @return
     * @throws Exception
     */
    public static RecallInfo gerRecallInfo(Index4User index4User, String bufStr) throws Exception {
        RecallInfo recallInfo = new RecallInfo();
        if (StringUtils.isNotBlank(bufStr)) {
            byte[] value = org.apache.commons.codec.binary.Base64.decodeBase64(bufStr);
            RecallInfoProbuf.RecallInfoProto proto = RecallInfoProbuf.RecallInfoProto.parseFrom(value);

            recallInfo.setStrategy(proto.getStrategy());
            recallInfo.setReason(proto.getReason());
            recallInfo.setHotBoost(proto.getHotBoost());
            recallInfo.setRecallTag(proto.getRecallTag());
            recallInfo.setDebugInfo(proto.getDebugInfo());
        } else {
            recallInfo.setStrategy(index4User.getS());
            recallInfo.setReason(index4User.getR());
            recallInfo.setHotBoost(index4User.getH());
            recallInfo.setRecallTag(index4User.getRT());
            recallInfo.setDebugInfo(index4User.getD());

        }
        return recallInfo;
    }


    /**
     * 把字符串转换为protobuf对象
     *
     * @param bufStr
     * @return
     * @throws Exception
     */
    public static RecallInfoProbuf.RecallInfoProto gerReasonProto(Index4User index4User, String bufStr) throws Exception {
        RecallInfoProbuf.RecallInfoProto proto = null;
        if (StringUtils.isNotBlank(bufStr)) {
            byte[] value = org.apache.commons.codec.binary.Base64.decodeBase64(bufStr);
            proto = RecallInfoProbuf.RecallInfoProto.parseFrom(value);
        } else {
            RecallInfoProbuf.RecallInfoProto.Builder builder = RecallInfoProbuf.RecallInfoProto.newBuilder();

            builder.setReason(index4User.getR());

            if (index4User.getS() != null) {
                builder.setStrategy(index4User.getS());
            }

            if (index4User.getH() != null) {
                builder.setHotBoost(MathUtils.getNewScaleNum(index4User.getH()));
            }
            if (index4User.getRT() != null) {
                builder.setRecallTag(index4User.getRT());
            }
            if (index4User.getD() != null) {
                builder.setDebugInfo(index4User.getD());
            }
            proto = builder.build();
        }
        return proto;
    }

}
