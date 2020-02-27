package com.ifeng.recom.mixrecall.controller;

import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.RecallChannelBeanName;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant.CHANNEL;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.channel.impl.CoTagDocGraphImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.CoTagDocSimlImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.CoTagDocpicChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.CoTagDocpicNewTagChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.CoTagVideoForNewTagChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.CoTagVideoGraphImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.CoTagVideoNewChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.CoTagVideoSimlImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.DocpicCChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.DocpicScChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.ExcellentDocpicChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.ExcellentVideoChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.FFMChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.LastCotagChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.LdaDocpicChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.PositiveFeedDocpicNewChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.PositiveFeedDocpicSourceChannelImp;
import com.ifeng.recom.mixrecall.core.channel.impl.PositiveFeedVideoNewChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.PositiveFeedVideoSourceChannelImp;
import com.ifeng.recom.mixrecall.core.channel.impl.UserCFAlsChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.UserCFDssmChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.UserMediaChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.UserSearchChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.UserSourceChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.UserSubChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.VideoCChannelImpl;
import com.ifeng.recom.mixrecall.core.channel.impl.VideoScChannelImpl;
import com.ifeng.recom.mixrecall.model.RecallChannelResult;
import com.ifeng.recom.mixrecall.model.RecallConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by geyl on 2017/11/28.
 */
@Component
public class RecallChannelHandlerFunctionImpl {
    private static final Logger logger = LoggerFactory.getLogger(RecallChannelHandlerFunctionImpl.class);

    @Autowired
    private UserSubChannelImpl subChannel;
    @Autowired
    private UserSearchChannelImpl userSearchChannel;
    @Autowired
    private CoTagDocpicChannelImpl coTagDocpicChannel;
    @Autowired
    private PositiveFeedDocpicNewChannelImpl positiveFeedDocpicNewChannel;
    @Autowired
    private PositiveFeedVideoNewChannelImpl positiveFeedVideoNewChannel;
    @Autowired
    private LdaDocpicChannelImpl ldaDocpicChannel;
    @Autowired
    private PositiveFeedDocpicSourceChannelImp positiveFeedDocpicSourceChannel;
    @Autowired
    private PositiveFeedVideoSourceChannelImp positiveFeedVideoSourceChannel;

    @Autowired
    private CoTagVideoNewChannelImpl coTagVideoNewChannel;

    @Autowired
    private CoTagVideoForNewTagChannelImpl coTagVideoForNewTagChannelChannel;

    @Autowired
    private UserCFAlsChannelImpl userCFAlsChannel;

    @Autowired
    private UserCFDssmChannelImpl userCFDssmChannel;

    @Autowired
    private ExcellentVideoChannelImpl excellentVideoChannelImpl;

    @Autowired
    private ExcellentDocpicChannelImpl excellentDocpicChannelImpl;

    @Autowired
    private CoTagDocpicNewTagChannelImpl coTagDocpicNewTagChannelImpl;

    @Autowired
    private FFMChannelImpl fFMChannelImpl;

    @Autowired
    private CoTagDocSimlImpl coTagDocSimlImpl;

    @Autowired
    private CoTagVideoSimlImpl coTagVideoSimlImpl;

    @Autowired
    private CoTagDocGraphImpl coTagDocGraphImpl;

    @Autowired
    private CoTagVideoGraphImpl coTagVideoGraphlImpl;

    @Autowired
    private UserMediaChannelImpl userMediaChannelImpl;

    @Autowired
    private LastCotagChannelImpl lastCotagChannel;

    @Autowired
    private UserSourceChannelImpl userSourceChannelImpl;

    @Autowired
    private DocpicCChannelImpl docpicCChannel;

    @Autowired
    private DocpicScChannelImpl docpicScChannel;

    @Autowired
    private VideoCChannelImpl videoCChannel;

    @Autowired
    private VideoScChannelImpl videoScChannel;


    /**
     * {@link RecallService#userCFAls(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.USER_CF_ALS)
    public Function<RecallConfig, RecallChannelResult> userCFAls() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.USER_CF_ALS).
                setChannelResult(userCFAlsChannel.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#userCFDssm(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.USER_CF_DSSM)
    public Function<RecallConfig, RecallChannelResult> userCFDssm() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.USER_CF_DSSM).
                setChannelResult(userCFDssmChannel.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#ldaTopicRecall(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.LDA_TOPIC)
    public Function<RecallConfig, RecallChannelResult> ldaTopicRecall() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.LDA_TOPIC).
                setChannelResult(ldaDocpicChannel.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#positiveFeedDocpic(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.PositiveFeedDocpic)
    public Function<RecallConfig, RecallChannelResult> positiveFeedDocpic() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.PositiveFeedDocpic).
                setChannelResult(positiveFeedDocpicNewChannel.doDocpicRecom(config.getInfo()));
    }


//    /**
//     * {@link RecallService#positiveFeedDocpicFromSource(MixRequestInfo)}
//     *
//     * 
//     * @return
//     */
//    @Bean(RecallChannelBeanName.PositiveFeedDocpicFromSource)
//    public Function<RecallConfig, RecallChannelResult> positiveFeedDocpicFromSource(MixRequestInfo mixRequestInfo,
//                                                                                    List<LastDocBean> remainBeanlist) {
//        List<RecallResult> rt = positiveFeedDocpicSourceChannel.doDocpicRecom(mixRequestInfo, remainBeanlist);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.PositiveFeedDocpicFromSource, rt);
//        return config -> new RecallChannelResult().setChannel(CHANNEL.PositiveFeedDocpicFromSource).
//                setChannelResult(positiveFeedDocpicSourceChannel.doDocpicRecom(config.getInfo()));
//    }


//    /**
//     * {@link RecallService#positiveFeedVideoFromSource}
//     *
//     * 
//     * @return
//     */
//    @Bean(RecallChannelBeanName.MEDIA_D)
//    public Function<RecallConfig, RecallChannelResult> positiveFeedVideoFromSource(MixRequestInfo mixRequestInfo, List<LastDocBean> remainBeanlist) {
//        List<RecallResult> rt = positiveFeedVideoSourceChannel.doVideoRecom(mixRequestInfo, remainBeanlist);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.PositiveFeedVideoFromSource, rt);
//        return config -> new RecallChannelResult().setChannel(CHANNEL.LDA_TOPIC).
//                setChannelResult(ldaDocpicChannel.doRecall(config.getInfo()));
//    }


    /**
     * {@link RecallService#positiveFeedVideo(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.PositiveFeedVideo)
    public Function<RecallConfig, RecallChannelResult> positiveFeedVideo() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.PositiveFeedVideo).
                setChannelResult(positiveFeedVideoNewChannel.doVideoRecom(config.getInfo()));
    }


    /**
     * {@link RecallService#userSearch(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.USER_SEARCH)
    public Function<RecallConfig, RecallChannelResult> userSearch() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.USER_SEARCH).
                setChannelResult(transDocListToRecallResultList(userSearchChannel.doRecall(config.getInfo())));
    }


    /**
     * {@link RecallService#userSub(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.USER_SUB)
    public Function<RecallConfig, RecallChannelResult> userSub() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.USER_SUB).
                setChannelResult(transDocListToRecallResultList(subChannel.doRecall(config.getInfo())));
    }

    /**
     * {@link RecallService#callSourceNewsN(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.SOURCE)
    public Function<RecallConfig, RecallChannelResult> channelSource() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.SOURCE).
                setChannelResult(userSourceChannelImpl.doRecallN(config.getInfo()));
    }


    /**
     * {@link RecallService#callDocpicMedia(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.MEDIA_D)
    public Function<RecallConfig, RecallChannelResult> callDocpicMedia() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.MEDIA_D).
                setChannelResult(userMediaChannelImpl.doRecall(config.getInfo(), DocType.DOCPIC.getValue()));
    }


    @Bean(RecallChannelBeanName.MEDIA_V)
    public Function<RecallConfig, RecallChannelResult> callVideoMedia() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.MEDIA_V).
                setChannelResult(userMediaChannelImpl.doRecall(config.getInfo(), DocType.VIDEO.getValue()));
    }



    @Bean(RecallChannelBeanName.COTAG_V_N)
    public Function<RecallConfig, RecallChannelResult> cotagVideoForNewTag() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.COTAG_V_N).
                setChannelResult(coTagVideoForNewTagChannelChannel.doRecall(config.getInfo()));
    }



    @Bean(RecallChannelBeanName.COTAG_V_SIM)
    public Function<RecallConfig, RecallChannelResult> cotagVideoForSim() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.COTAG_V_SIM).
                setChannelResult(coTagVideoSimlImpl.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#cotagDocForSim(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.COTAG_D_SIM)
    public Function<RecallConfig, RecallChannelResult> cotagDocForSim() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.COTAG_D_SIM).
                setChannelResult(coTagDocSimlImpl.doRecall(config.getInfo()));
    }

    /**
     * 此处添加图谱召回通道 docpic和video图谱 by yx20191128
     *
     *
     * @return
     */

    /**
     * {@link RecallService#cotagDocForGraph(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.COTAG_D_GRAPH)
    public Function<RecallConfig, RecallChannelResult> cotagDocForGraph() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.COTAG_D_GRAPH).
                setChannelResult(coTagDocGraphImpl.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#cotagVideoForGraph(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.COTAG_V_GRAPH)
    public Function<RecallConfig, RecallChannelResult> cotagVideoForGraph() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.COTAG_V_GRAPH).
                setChannelResult(coTagVideoGraphlImpl.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#cotagDocpicForNewTag(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.COTAG_D_N)
    public Function<RecallConfig, RecallChannelResult> cotagDocpicForNewTag() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.COTAG_D_N).
                setChannelResult(coTagDocpicNewTagChannelImpl.doRecall(config.getInfo()));

    }


    /**
     * cotag拆分后的视频cotag召回通道
     *
     *
     * @return
     */

    /**
     * {@link RecallService#cotagVideoNew(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.COTAG_V)
    public Function<RecallConfig, RecallChannelResult> cotagVideoNew() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.COTAG_V).
                setChannelResult(coTagVideoNewChannel.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#ffmRecall(MixRequestInfo, boolean), false}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.FFM)
    public Function<RecallConfig, RecallChannelResult> ffmRecallDoc() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.FFM).
                setChannelResult(fFMChannelImpl.doRecall(config.getInfo(), false));
    }


    /**
     * {@link RecallService#ffmRecall(MixRequestInfo, boolean) ture}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.FFMV)
    public Function<RecallConfig, RecallChannelResult> ffmRecallVideo() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.FFM).
                setChannelResult(fFMChannelImpl.doRecall(config.getInfo(), true));
    }


    /**
     * {@link RecallService#cotagDoc(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.COTAG_DOC)
    public Function<RecallConfig, RecallChannelResult> cotagDoc() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.COTAG_DOC).
                setChannelResult(coTagDocpicChannel.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#cotagDocLast(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.COTAG_L_SIM)
    public Function<RecallConfig, RecallChannelResult> cotagDocLast() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.COTAG_L_SIM).
                setChannelResult(coTagDocpicChannel.doRecallLast(config.getInfo()));
    }


    /**
     * {@link RecallService#lastCotag(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.LAST_COTAG)
    public Function<RecallConfig, RecallChannelResult> lastCotag() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.LAST_COTAG).
                setChannelResult(lastCotagChannel.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#callHighQualityVideo(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.EXCELLENT_V)
    public Function<RecallConfig, RecallChannelResult> callHighQualityVideo() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.EXCELLENT_V).
                setChannelResult(excellentVideoChannelImpl.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#callHighQualityDocpic(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.EXCELLENT_D)
    public Function<RecallConfig, RecallChannelResult> callHighQualityDocpic() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.EXCELLENT_D).
                setChannelResult(excellentDocpicChannelImpl.doRecall(config.getInfo()));
    }


    /**
     * 单独召回c sc 视频+图文
     *
     *
     * @return
     */

    /**
     * {@link RecallService#callDocpicC(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.DOCPIC_C)
    public Function<RecallConfig, RecallChannelResult> callDocpicC() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.DOCPIC_C).
                setChannelResult(docpicCChannel.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#callDocpicSc(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.DOCPIC_SC)
    public Function<RecallConfig, RecallChannelResult> callDocpicSc() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.DOCPIC_SC).
                setChannelResult(docpicScChannel.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#callVideoC(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.VIDEO_C)
    public Function<RecallConfig, RecallChannelResult> callVideoC() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.VIDEO_C).
                setChannelResult(videoCChannel.doRecall(config.getInfo()));
    }


    /**
     * {@link RecallService#callVideoSc(MixRequestInfo)}
     *
     * @return
     */
    @Bean(RecallChannelBeanName.VIDEO_SC)
    public Function<RecallConfig, RecallChannelResult> callVideoSc() {
        return config -> new RecallChannelResult().setChannel(CHANNEL.VIDEO_SC).
                setChannelResult(videoScChannel.doRecall(config.getInfo()));
    }


    /**
     * 转换老的document list 到新的 recallResult list，只对旧通道生效
     *
     * @param documents
     * @return
     */
    private static List<RecallResult> transDocListToRecallResultList(List<Document> documents) {
        List<RecallResult> recallResults = new ArrayList<>();
        for (Document document : documents) {
            try {
                RecallResult recallResult = new RecallResult(document, document.getRecallTag(), document.getWhy().getReason());
                recallResults.add(recallResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return recallResults;
    }
}