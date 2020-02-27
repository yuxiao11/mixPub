package com.ifeng.recom.mixrecall.common.constant;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by geyl on 2017/11/13.
 */
public class RecallConstant {
    public static final double PROFILE_CUT_OFF_WEIGHT = 0.5d;

    public static final int PROFILE_CUT_OFF_NUM_SC = 20;
    public static final int PROFILE_CUT_OFF_Num = 15;

    public enum CHANNEL {
        COTAG, COTAG_VIDEO, COTAG_DOC, COTAG_UC, MIX, SOURCE_SIM, EXPLORE, COTAG_SIM, USER_CF, HOT_TAG, COTAG_S,
        PositiveFeedDocpic, PositiveFeedVideo, USER_SEARCH, USER_SUB, USER_LOCAL, CRAWL_VIDEO, USER_VEC, USER_CF_KTT,
        COTAG_V, COTAG_SMALL_V, LDA_TOPIC, UTOPIC_DOC, UTOPIC_VIDEO, COTAG_V_N, USER_CF_ALS, USER_CF_DSSM,
        EXCELLENT, LOWEXPO_EXPLORE, SOURCE, EXCELLENT_V, EXCELLENT_D, SAFE_D, SAFE_V, PositiveFeedDocpicFromSource,
        PositiveFeedVideoFromSource, FFM, LOWEX_C_D_NEW, LOWEX_SC_D_NEW, LOWEX_C_V_NEW, LOWEX_SC_V_NEW,
        SMALL_V_C, COTAG_D_N, COTAG_D_SIM, COTAG_V_SIM, COTAG_D_GRAPH, COTAG_V_GRAPH, MEDIA_D, MEDIA_V,
        COTAG_L_SIM, LAST_COTAG, DOCPIC_C, DOCPIC_SC, VIDEO_C, VIDEO_SC,ERROR
        ;

        public static Map<String, CHANNEL> name2Channel;

        static {
            name2Channel = Arrays.stream(CHANNEL.values()).collect(Collectors.toMap(s -> s.name(), Function.identity()));
        }

        /**
         * {@link RecallChannelBeanName}
         * 移除 channel_部分进行翻译
         *
         * @param c
         * @return
         */
        public static CHANNEL getChannel(String c) {
            String s = c.substring(8);
            // 历史遗留问题, 导致需要将FFMV映射成FFM
            if ("FFMV".equals(s)) {
                return FFM;
            }
            return name2Channel.getOrDefault(s, ERROR);
        }
    }


    public enum DocType {DOCPIC, SLIDE, VIDEO}

    public static void main(String[] args) {
        System.out.println(CHANNEL.getChannel(RecallChannelBeanName.COTAG_SIM));
    }
}
