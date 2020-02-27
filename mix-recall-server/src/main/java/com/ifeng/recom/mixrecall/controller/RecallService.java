package com.ifeng.recom.mixrecall.controller;

import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant.CHANNEL;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecallThreadResult;
import com.ifeng.recom.mixrecall.common.model.item.LastDocBean;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.tool.ChannelLackLogUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by geyl on 2017/11/28.
 */
@Service
public class RecallService {
    private static final Logger logger = LoggerFactory.getLogger(RecallService.class);


    private final UserSubChannelImpl subChannel;
    private final UserSearchChannelImpl userSearchChannel;
    private final CoTagDocpicChannelImpl coTagDocpicChannel;
    private final PositiveFeedDocpicNewChannelImpl positiveFeedDocpicNewChannel;
    private final PositiveFeedVideoNewChannelImpl positiveFeedVideoNewChannel;
    private final LdaDocpicChannelImpl ldaDocpicChannel;
    private final PositiveFeedDocpicSourceChannelImp positiveFeedDocpicSourceChannel;
    private final PositiveFeedVideoSourceChannelImp positiveFeedVideoSourceChannel;

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
    private FFMChannelImpl FFMChannelImpl;

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
    private final DocpicCChannelImpl docpicCChannel;

    @Autowired
    private final DocpicScChannelImpl docpicScChannel;

    @Autowired
    private final VideoCChannelImpl videoCChannel;

    @Autowired
    private final VideoScChannelImpl videoScChannel;

    @Autowired
    public RecallService(UserSubChannelImpl subChannel, UserSearchChannelImpl userSearchChannel, CoTagDocpicChannelImpl coTagDocpicChannel,
                         PositiveFeedDocpicNewChannelImpl positiveFeedDocpicNewChannel, PositiveFeedVideoNewChannelImpl positiveFeedVideoNewChannel, LdaDocpicChannelImpl ldaDocpicChannel, PositiveFeedDocpicSourceChannelImp positiveFeedDocpicSourceChannel, PositiveFeedVideoSourceChannelImp positiveFeedVideoSourceChannel, DocpicCChannelImpl docpicCChannel, DocpicScChannelImpl docpicScChannel, VideoCChannelImpl videoCChannel, VideoScChannelImpl videoScChannel) {
        this.subChannel = subChannel;
        this.userSearchChannel = userSearchChannel;
        this.coTagDocpicChannel = coTagDocpicChannel;
        this.positiveFeedDocpicNewChannel = positiveFeedDocpicNewChannel;
        this.positiveFeedVideoNewChannel = positiveFeedVideoNewChannel;
        this.ldaDocpicChannel = ldaDocpicChannel;
        this.positiveFeedDocpicSourceChannel = positiveFeedDocpicSourceChannel;
        this.positiveFeedVideoSourceChannel = positiveFeedVideoSourceChannel;
        this.docpicCChannel = docpicCChannel;
        this.docpicScChannel = docpicScChannel;
        this.videoCChannel = videoCChannel;
        this.videoScChannel = videoScChannel;
    }


//    @Async
//    public Future<RecallThreadResult> userCFAls(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = userCFAlsChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.USER_CF_ALS, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.USER_CF_ALS.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

//    @Async
//    public Future<RecallThreadResult> userCFDssm(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = userCFDssmChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.USER_CF_DSSM, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.USER_CF_DSSM.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

//    @Async
//    public Future<RecallThreadResult> ldaTopicRecall(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = ldaDocpicChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.LDA_TOPIC, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.LDA_TOPIC.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

    @Async
    public Future<RecallThreadResult> positiveFeedDocpic(MixRequestInfo mixRequestInfo) {
        List<RecallResult> rt = positiveFeedDocpicNewChannel.doDocpicRecom(mixRequestInfo);
        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.PositiveFeedDocpic, rt);
        return new AsyncResult<>(recallThreadResult);
    }

    @Async
    public Future<RecallThreadResult> positiveFeedDocpicFromSource(MixRequestInfo mixRequestInfo, List<LastDocBean> remainBeanlist) {
        List<RecallResult> rt = positiveFeedDocpicSourceChannel.doDocpicRecom(mixRequestInfo, remainBeanlist);
        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.PositiveFeedDocpicFromSource, rt);
        return new AsyncResult<>(recallThreadResult);
    }

    @Async
    public Future<RecallThreadResult> positiveFeedVideoFromSource(MixRequestInfo mixRequestInfo, List<LastDocBean> remainBeanlist) {
        List<RecallResult> rt = positiveFeedVideoSourceChannel.doVideoRecom(mixRequestInfo, remainBeanlist);
        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.PositiveFeedVideoFromSource, rt);
        return new AsyncResult<>(recallThreadResult);
    }

    @Async
    public Future<RecallThreadResult> positiveFeedVideo(MixRequestInfo mixRequestInfo) {
        List<RecallResult> rt = positiveFeedVideoNewChannel.doVideoRecom(mixRequestInfo);
        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.PositiveFeedVideo, rt);
        return new AsyncResult<>(recallThreadResult);
    }


//    @Async
//    public Future<RecallThreadResult> userSearch(MixRequestInfo mixRequestInfo) {
//        List<Document> rt = userSearchChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.USER_SEARCH, transDocListToRecallResultList(rt));
//        return new AsyncResult<>(recallThreadResult);
//    }

//    @Async
//    public Future<RecallThreadResult> userSub(MixRequestInfo mixRequestInfo) {
//        List<Document> rt = subChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.USER_SUB, transDocListToRecallResultList(rt));
//        return new AsyncResult<>(recallThreadResult);
//    }


//    @Async
//    public Future<RecallThreadResult> callSourceNewsN(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = userSourceChannelImpl.doRecallN(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.SOURCE, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.SOURCE.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }


//    @Async
//    public Future<RecallThreadResult> callDocpicMedia(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = userMediaChannelImpl.doRecall(mixRequestInfo, DocType.DOCPIC.getValue());
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.MEDIA_D, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.MEDIA_D.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }


//    @Async
//    public Future<RecallThreadResult> callVideoMedia(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = userMediaChannelImpl.doRecall(mixRequestInfo, DocType.VIDEO.getValue());
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.MEDIA_V, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.MEDIA_V.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }


    /**
     * cotag新视频标签召回通道
     *
     * @param mixRequestInfo
     * @return
     */
//    @Async
//    public Future<RecallThreadResult> cotagVideoForNewTag(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = coTagVideoForNewTagChannelChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.COTAG_V_N, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.COTAG_V_N.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }


//    @Async
//    public Future<RecallThreadResult> cotagVideoForSim(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = coTagVideoSimlImpl.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.COTAG_V_SIM, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.COTAG_V_SIM.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

//    @Async
//    public Future<RecallThreadResult> cotagDocForSim(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = coTagDocSimlImpl.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.COTAG_D_SIM, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.COTAG_D_SIM.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

    /**
     * 此处添加图谱召回通道 docpic和video图谱 by yx20191128
     *
     * @param mixRequestInfo
     * @return
     */
//    @Async
//    public Future<RecallThreadResult> cotagDocForGraph(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = coTagDocGraphImpl.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.COTAG_D_GRAPH, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.COTAG_D_GRAPH.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

//    @Async
//    public Future<RecallThreadResult> cotagVideoForGraph(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = coTagVideoGraphlImpl.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.COTAG_V_GRAPH, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.COTAG_V_GRAPH.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }


//    @Async
//    public Future<RecallThreadResult> cotagDocpicForNewTag(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = coTagDocpicNewTagChannelImpl.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.COTAG_D_N, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.COTAG_D_N.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//
//    }


    /**
     * cotag拆分后的视频cotag召回通道
     *
     * @param mixRequestInfo
     * @return
     */
//    @Async
//    public Future<RecallThreadResult> cotagVideoNew(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = coTagVideoNewChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.COTAG_V, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.COTAG_V.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }


//    @Async
//    public Future<RecallThreadResult> ffmRecall(MixRequestInfo mixRequestInfo, boolean isNeedVideo) {
//        List<RecallResult> rt = FFMChannelImpl.doRecall(mixRequestInfo, isNeedVideo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.FFM, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.FFM.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }


//    @Async
//    public Future<RecallThreadResult> cotagDoc(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = coTagDocpicChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.COTAG_DOC, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.COTAG_DOC.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

//    @Async
//    public Future<RecallThreadResult> cotagDocLast(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = coTagDocpicChannel.doRecallLast(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.COTAG_L_SIM, rt);
//        return new AsyncResult<>(recallThreadResult);
//    }


    @Async
    public Future<RecallThreadResult> lastCotag(MixRequestInfo mixRequestInfo) {
        List<RecallResult> rt = lastCotagChannel.doRecall(mixRequestInfo);
        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.LAST_COTAG, rt);
        return new AsyncResult<>(recallThreadResult);
    }


//    @Async
//    public Future<RecallThreadResult> callHighQualityVideo(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = excellentVideoChannelImpl.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.EXCELLENT_V, rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

//    @Async
//    public Future<RecallThreadResult> callHighQualityDocpic(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = excellentDocpicChannelImpl.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.EXCELLENT_D, rt);
//        return new AsyncResult<>(recallThreadResult);
//    }


    /**
     * 单独召回c sc 视频+图文
     *
     * @param mixRequestInfo
     * @return
     */
//    @Async
//    public Future<RecallThreadResult> callDocpicC(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = docpicCChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.DOCPIC_C, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.DOCPIC_C.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

//    @Async
//    public Future<RecallThreadResult> callDocpicSc(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = docpicScChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.DOCPIC_SC, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.DOCPIC_SC.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

//    @Async
//    public Future<RecallThreadResult> callVideoC(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = videoCChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.VIDEO_C, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.VIDEO_C.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }

//    @Async
//    public Future<RecallThreadResult> callVideoSc(MixRequestInfo mixRequestInfo) {
//        List<RecallResult> rt = videoScChannel.doRecall(mixRequestInfo);
//        RecallThreadResult recallThreadResult = new RecallThreadResult(CHANNEL.VIDEO_SC, rt);
//        ChannelLackLogUtils.recordChannelLack(CHANNEL.VIDEO_SC.toString(), rt);
//        return new AsyncResult<>(recallThreadResult);
//    }


    /**
     * 转换老的document list 到新的 recallResult list，只对旧通道生效
     *
     * @param documents
     * @return
     */
//    private static List<RecallResult> transDocListToRecallResultList(List<Document> documents) {
//        List<RecallResult> recallResults = new ArrayList<>();
//        for (Document document : documents) {
//            try {
//                RecallResult recallResult = new RecallResult(document, document.getRecallTag(), document.getWhy().getReason());
//                recallResults.add(recallResult);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return recallResults;
//    }
}