package com.ifeng.recom.mixrecall.common.constant;

/**
 * Created by geyl on 2017/11/07.
 */
public enum WhyReason {

    EXPLORE("MultiAlgMixRank_Explore"),
    MIX_T1("MultiAlgMixRank_MixT1"),
    MIX_T2("MultiAlgMixRank_MixT2"),
    MIX_T3("MultiAlgMixRank_MixT3"),

    COTAG_LONG_A("CotagLong_A"),
    COTAG_RECENT_A("CotagRecent_A"),
    COTAG_LAST_A("CotagLast_A"),

    COTAG_LONG_B("CotagLong_B"),
    COTAG_RECENT_B("CotagRecent_B"),
    COTAG_LAST_B("CotagLast_B"),

    COTAG_LONG_FALSE("cotag_long_false"),
    COTAG_RECENT_FALSE("cotag_recent_false"),
    COTAG_LAST_FALSE("cotag_last_false"),
    COTAG_LONG_TRUE_BEIJING("cotag_long_true_beijing"),

    COTAG_LONG_TRUE_SUB("cotag_long_true_sub"),
    COTAG_RECENT_TRUE_SUB("cotag_recent_true_sub"),

    COTAG_SUPPLY_LONG("cotag_long_supply"),
    COTAG_SUPPLY_RECENT("cotag_recent_supply"),
    COTAG_SUPPLY_LAST("cotag_last_supply"),

    COTAG_LONG_FALSE_SUPPLY("cotag_long_false_supply"),
    COTAG_RECENT_FALSE_SUPPLY("cotag_recent_false_supply"),
    COTAG_LAST_FALSE_SUPPLY("cotag_last_false_supply"),

    COTAG_SIM_LONG("cotag_sim_long"),
    COTAG_SIM_RECENT("cotag_sim_recent"),
    COTAG_SIM_LAST("cotag_sim_last"),

    COTAG_VIDEO_LONG("cotag_video_long"),
    COTAG_VIDEO_RECENT("cotag_video_recent"),
    COTAG_VIDEO_LAST("cotag_video_last"),

    COTAG_CRAWL_VIDEO_LONG("cotag_crawl_video_long"),
    COTAG_CRAWL_VIDEO_RECENT("cotag_crawl_video_recent"),
    COTAG_CRAWL_VIDEO_LAST("cotag_crawl_video_last"),

    T2_SOURCE_SIMS("T2SourceSims"),

    COTAG_FILTER("cotag_filter"),
    COTAG_NO_FILTER("cotag_no_filter"),

    USER_CF_A("user_cf_a"),
    USER_CF_B("user_cf_b"),
    USER_CF_C("user_cf_c"),
    USER_CF_E("user_cf_e"),
    USER_CF_E_d("user_cf_e_d"),
    USER_CF_E_v("user_cf_e_v"),

    USER_CF_E1("user_cf_e1"),
    USER_CF_E_200("user_cf_e_200"),
    USER_CF_F("user_cf_f"),
    USER_CF_G("user_cf_g"),
    USER_CF_H("user_cf_h"),
    USER_CF_K("user_cf_k"),
    USER_CF_M("user_cf_m"),
    USER_CF_N("user_cf_n"),
    USER_CF("user_cf"),
    USER_CF_KTT("user_cf_ktt"),
    USER_CF_ALS("user_cf_ALS"),
    USER_CF_ALS_CACHE("user_cf_als_cache"),
    USER_CF_DSSM("user_cf_DSSM"),

    USER_CF_T1("user_cf_t1"),
    USER_CF_COMM("user_cf_comm"),

    HOT_TAG("hot_tag"),

    ITEMCF("itemcf"),

    POS_FEED_VIDEO_ITEMCF("pos_feedback_video_itemcf"),
    POS_FEED_VIDEO_ITEMCF_CDML("pos_feedback_video_itemcf_cdml"),
    POS_FEED_VIDEO_ITEMCF_LVS("pos_feedback_video_itemcf_lvs"),
    POS_FEED_VIDEO_T3SOLR("pos_feedback_T3solr_video"),
    POS_FEED_VIDEO_ES("pos_feedback_es_video"),
    POS_FEED_VIDEO_ITEM2VEC("pos_feedback_video_item2vec"),
    POS_FEED_VIDEO_ITEMCF_N("pos_feedback_video_itemcf_n"),

    POS_FEED_DOCPIC_ITEMCF("pos_feedback_itemcf_docpic"),
    POS_FEED_DOCPIC_ITEMCF_LVS("pos_feedback_itemcf_docpic_lvs"),
    POS_FEED_DOCPIC_T3SOLR("pos_feedback_T3solr_docpic"),
    POS_FEED_DOCPIC_ES("pos_feedback_es_docpic"),
    POS_FEED_DOCPIC_ITEMCF_N("pos_feedback_itemcf_docpic_n"),
    POS_FEED_DOCPIC_SOURCE("pos_feedback_source_d"),
    POS_FEED_VIDEO_SOURCE("pos_feedback_source_v"),


    COTAG_S("cotag_s"),
    USER_SEARCH("UserSearch"),
    USER_SUB("UserSub"),
    USER_LOCAL("Local"),


    /**
     * 增量的正反馈单独区分
     */
    ITEMCF_VIDEO("itemcfvideo"),
    ITEMCF_DOCPIC("itemcfdocpic"),

    /**
     * cotag图文视频拆分后的新通道，标记简短节省空间
     */
    COTAG_V_LONG("cotag_v_long"),
    COTAG_V_LONG_Decay("cotag_v_long_decay"),
    COTAG_V_LONG_Decay_N("cotag_v_long_decay_n"),
    COTAG_V_RECENT("cotag_v_recent"),
    COTAG_V_LAST("cotag_v_last"),

    /**
     * cotag利用新视频标签召回视频
     */
    COTAG_V_LONG_N("cotag_v_long_n"),
    COTAG_V_LONG_N_decay("cotag_v_long_n_decay"),
    COTAG_V_LONG_N_decay_n("cotag_v_long_n_decay_n"),
    COTAG_V_RECENT_N("cotag_v_recent_n"),
    COTAG_V_LAST_N("cotag_v_last_n"),
    COTAG_V_JP("cotag_v_jp"),
    /**
     * cotag小视频
     */
    COTAG_SMALL_V_LONG("cotag_small_v_long"),
    COTAG_SMALL_V_RECENT("cotag_small_v_recent"),
    COTAG_SMALL_V_LAST("cotag_small_v_last"),
    COTAG_SMALL_T1("cotag_small_t1"),

    /**
     * cotagSim图文
     */
    COTAG_D_LAST_SIM("cotag_d_last_sim"),
    COTAG_D_RECENT_SIM("cotag_d_recent_sim"),

    /**
     * cotagSim视频
     */
    COTAG_V_LAST_SIM("cotag_v_last_sim"),
    COTAG_V_RECENT_SIM("cotag_v_recent_sim"),


    /**
     * cotag图谱图文
     */
    COTAG_V_LAST_GRAPH("cotag_v_last_graph"),
    COTAG_V_RECENT_GRAPH("cotag_v_recent_graph"),
    COTAG_V_LONG_GRAPH("cotag_v_Long_graph"),


    /**
     * cotag图谱视频
     */
    COTAG_D_LAST_GRAPH("cotag_d_last_graph"),
    COTAG_D_RECENT_GRAPH("cotag_d_recent_graph"),
    COTAG_D_LONG_GRAPH("cotag_d_Long_graph"),



    /**
     * lastUncombine sim 为0的
     */
    COTAG_D_LAST_SIM0("cotag_d_last_sim0"),
    COTAG_V_LAST_SIM0("cotag_v_last_sim0"),

    /**
     * C精选小视频
     */
    JX_SMALL_V_C("jx_small_v_c"),

    /**
     * 冷启动C小视频
     */
    COLD_SMALL_C("cold_small_c"),
    /**
     * 冷启动UCB小视频
     */
    COLD_SMALL_UCB("cold_small_ucb"),
    /**
     * 拆分后的图文的召回通道
     */
    COTAG_D_LONG("cotag_d_long"),
    COTAG_D_LONG_Decay("cotag_d_long_decay"),
    COTAG_D_LONG_Decay_N("cotag_d_long_decay_n"),
    COTAG_D_LONG_BASE("cotag_d_long_base"),
    COTAG_D_LONG_TEST("cotag_d_long_timely"),
    COTAG_D_RECENT("cotag_d_recent"),
    COTAG_D_LAST("cotag_d_last"),
    COTAG_D_JP("cotag_d_jp"),

    /**
     * 新加的lda-topic召回通道
     */
    LDA_TOPIC_LONG("lda_topic_long"),
    LDA_TOPIC_RECENT("lda_topic_recent"),

    /**
     * ffm试验通道
     */
    FFM("ffm"),
    FFM_A("ffm_a"),
    FFM_B("ffm_b"),
    FFM_C("ffm_c"),
    FFM_D("ffm_d"),
    ffmv_a("ffmv_a"),
    ffmv_b("ffmv_b"),
    ffmv_c("ffmv_c"),
    ffmv_d("ffmv_d"),

    /**
     * 拆分后的图文的召回通道
     */
    COTAG_D_LONG_n("cotag_d_l_n"),
    COTAG_D_LONG_n_decay("cotag_d_l_n_decay"),
    COTAG_D_LONG_n_decay_n("cotag_d_l_n_decay_n"),
    COTAG_D_LONG_UC("cotag_d_l_uc"),// usercluster试验
    COTAG_D_LONG_UCT("cotag_d_l_uct"), // usercluster_test对照组试验
    COTAG_D_RECENT_n("cotag_d_r_n"),
    COTAG_D_LAST_n("cotag_d_la_n"),


    /**
     * topic 图文通道
     */
    TOPIC_V_LONG("topic_v_long"),
    TOPIC_V_RECENT("topic_v_recent"),
    TOPIC_V_LAST("topic_v_last"),


    /**
     * topic 视频通道
     */
    TOPIC_D_LONG("topic_d_long"),
    TOPIC_D_RECENT("topic_d_recent"),
    TOPIC_D_LAST("topic_d_last"),


    /**
     * Last Cotag试验通道
     */
    LAST_COTAG_D("last_cotag_d"),
    LAST_COTAG_V("last_cotag_v"),


    /**
     * 优质试探
     */
    EXCELLENT_T1("excellent_t1"),
    EXCELLENT_V_C("excellent_v_c"),
    EXCELLENT_V_SC("excellent_v_sc"),
    EXCELLENT_D_C("excellent_d_c"),
    EXCELLENT_D_SC("excellent_d_sc"),


    /**
     * 低曝光试探
     */
    EX_T1("ex_t1"),
    /**
     * 低曝光试探新分类试探
     * D= docpic_cate  docpic_subcate 图文
     * V= video_cate video_subcate 视频
     */
    EX_C_D_NEW("ex_cd"),
    EX_SC_D_NEW("ex_scd"),
    EX_C_V_NEW("ex_cv"),
    EX_SC_V_NEW("ex_scv"),


    /**
     * 兴趣试探
     */
    EXP_D_SC("exp_d_sc"),
    EXP_V_SC("exp_v_sc"),
    EXP_D_UCB("exp_d_ucb"),//ucb
    EXP_V_UCB("exp_v_ucb"),//ucb
    EXP_D_UCB_SC("exp_d_ucb_sc"),//ucb sc
    EXP_V_UCB_SC("exp_v_ucb_sc"),//ucb sc

    /**
     * 安全池子召回通道
     */
    SAFE_D_C("safe_d_c"),
    SAFE_D_SC("safe_d_sc"),
    SAFE_V_C("safe_v_c"),
    SAFE_V_SC("safe_v_sc"),



    SOURCE_EX("source_ex"),

    SOURCE_EX_N("source_ex_n"),
    SOURCE_EX_N_T("source_ex_n_t"),

    USER_INTERREST_C("user_interest_c"),

    DOC_MEDIA_L("media_doc_l"),
    VIDEO_MEDIA_L("media_video_l"),

    COTAG_SUPPLY("cotag_supply"), //增加补足相关标签 by YX
    EXP_D_SUPPLY("explore_supply_d"),
    EXP_V_SUPPLY("explore_supply_v"),

    /**
     * 通过c sc 召回通道
     */
    DOCPIC_C_LONG("docpic_c_l"),
    DOCPIC_C_RECENT("docpic_c_r"),
    DOCPIC_SC_LONG("docpic_sc_l"),
    DOCPIC_SC_RECENT("docpic_sc_r"),

    VIDEO_C_LONG("video_c_l"),
    VIDEO_C_RECENT("video_c_r"),
    VIDEO_SC_LONG("video_sc_l"),
    VIDEO_SC_RECENT("video_sc_r"),

    ;

    private final String value;

    WhyReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
