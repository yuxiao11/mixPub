package com.ifeng.recom.mixrecall.common.service.handler.remove;


import com.google.common.collect.Lists;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.RecallConstant;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.filter.BeijingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RemoverHandlerService {

    private static final Set<String> needLevelForSafeUser = new HashSet<>(Arrays.asList("A", "B", "S", "C"));

    private static final Set<String> needLevelForAllUser = new HashSet<>(Arrays.asList("A", "B", "S", "C", "D"));

    private static final Logger logger = LoggerFactory.getLogger(RemoverHandlerService.class);

    @Resource(name = "quTouTiaoRemover")
    private IItemRemoveHandler<Document> quTouTiaoRemover;

    @Resource(name = "smallVideosRemover")
    private IItemRemoveHandler<Document> smallVidesRemover;

    @Resource(name = "ppLiveRemover")
    private IItemRemoveHandler<Document> ppLiveRemover;

    @Resource(name = "nullRemover")
    private IItemRemoveHandler<RecallResult> nullRemover;

    @Resource(name = "idRemover")
    private IItemRemoveHandler<Document> idRemover;

    @Resource(name = "lowTagRemover")
    private IItemRemoveHandler<Document> lowTagRemover;

    @Resource(name = "sourceNameRemover")
    private IItemRemoveHandler<Document> sourceNameRemover;

    @Resource(name = "timeSensitiveRemover")
    private IItemRemoveHandler<Document> timeSensitiveRemover;

    @Resource(name = "timeSensitiveWithCategoryRemover")
    private IItemRemoveHandler<Document> timeSensitiveWithCategoryRemover;

    @Autowired
    private LevelRemoverHandler levelRemoverHandler;
    @Autowired
    private SansuRemoverHandler sansuRemoverHandler;
    @Autowired
    private MediaRemoverHandler mediaRemoverHandler;

    public List<RecallResult> doCommonFilter(MixRequestInfo mixRequestInfo,
                                             List<RecallResult> recallResults, RecallConstant.CHANNEL recallChannel) {
        if (empty(recallResults)) {
            return Lists.newArrayList();
        }
        return remove(mixRequestInfo, recallResults, recallChannel,  buildCommonHandlers(mixRequestInfo));
    }

    public List<IItemRemoveHandler<Document>> buildCommonHandlers(MixRequestInfo mixRequestInfo) {
        String uid = mixRequestInfo.getUid();
        Map<String, Boolean> userTypeMap = mixRequestInfo.getUserTypeMap();
        Map<String, String> devMap = mixRequestInfo.getDevMap();
        List<IItemRemoveHandler<Document>> removeHandlers = Lists.newArrayList();
        //id过滤：来自张阳的低品质id过滤 和 协同出的低俗id过滤
        if (userTypeMap.getOrDefault("needLowSimidFilter", true)
                || userTypeMap.getOrDefault("isWxb", true)) {
//            docList = idFilter.filterDocsId(docList);
            removeHandlers.add(idRemover);
        }
        //针对北京及地域为空过滤 审核标签（血腥、三俗、色情、政治敏感）内容
        if (BeijingFilter.isBJOrWXB(mixRequestInfo)) {
//            docList = beijingFilter.filterlowTagDocsId(docList);
            removeHandlers.add(lowTagRemover);
        }
        if (userTypeMap.getOrDefault(GyConstant.needMinVideoFilter, true)
                && !uid.equals(GyConstant.uid_Jibin) && !uid.equals(GyConstant.uid_pd)) {
//            smallVidesFilter(docList);
            removeHandlers.add(smallVidesRemover);
        }
        //进行泡泡直播的过滤
//        ppLiveFilter(docList);
        removeHandlers.add(ppLiveRemover);
        if (userTypeMap.getOrDefault(GyConstant.needQttFilter, true)) {
//            quTouTiaoFilter(docList);
            removeHandlers.add(quTouTiaoRemover);
        }
        //快头条用户，不进行过滤
        if (!userTypeMap.getOrDefault("isTitleFilterWhiteNotWxb", false)) {
            //三俗词记分过滤:12.15日放开全国过滤，01.09面向全国过滤切换到预加载做
            int filterScore = Integer.valueOf(devMap.getOrDefault("threadHold_title_filter", "6"));
//            docList = sansuFilter.titleFilter(docList, filterScore);
            removeHandlers.add(sansuRemoverHandler.buildRemover(filterScore));
            //特定媒体级别过滤
            if (!userTypeMap.getOrDefault("isLvsWhite", true) || userTypeMap.getOrDefault("isWxb", false)) {
//                docList = levelFilter.filterDocByCache(docList, needLevelForSafeUser);
                removeHandlers.add(levelRemoverHandler.buildRemover(needLevelForSafeUser));
            }
//            docList = levelFilter.filterDocByCache(docList, needLevelForAllUser);
            removeHandlers.add(levelRemoverHandler.buildRemover(needLevelForAllUser));
            //禁推媒体过滤
//            docList = mediaFilter.filterByMedia(docList, filterScore);
            removeHandlers.add(mediaRemoverHandler.buildRemover(filterScore));
            //特定媒体名称过滤
            if (!userTypeMap.getOrDefault("isJiGouWhite", true)) {
//                docList = sourceNameFilter.filterSourceName(docList);
                removeHandlers.add(sourceNameRemover);
            }
        }
        //过滤时效性过期的内容
//        docList = timeSensitiveFilter.filterDocsByTimeSensitive(mixRequestInfo, docList);
        removeHandlers.add(timeSensitiveRemover);
        //全国用户过滤时政、国际类时效性
//        docList = timeSensitiveFilter.filterDocsByTimeSensitiveWithCategory(mixRequestInfo, docList);
        removeHandlers.add(timeSensitiveWithCategoryRemover);
        return removeHandlers;
    }

    private boolean empty(List<RecallResult> recallResults) {
        if (recallResults == null || recallResults.isEmpty()) {
            return true;
        }
        return false;
    }

    public List<RecallResult> remove(MixRequestInfo mixRequestInfo,
                                      List<RecallResult> recallResults, RecallConstant.CHANNEL recallChannel,
                                      List<IItemRemoveHandler<Document>> removeHandlers) {
        if (recallResults == null || removeHandlers == null) {
            return Lists.newArrayList();
        }
        String uid = mixRequestInfo.getUid();
        List<RecallResult> result = new ArrayList<>();
        boolean debugUser = mixRequestInfo.isDebugUser();
        int[] debugCount = null;
        if (debugUser) {
            debugCount = new int[removeHandlers.size()];
        }
        DupRemoveHandler dupRemoveHandler = new DupRemoveHandler();
        Outer:
        for (RecallResult recallResult : recallResults) {
            // 内置过滤, 空指针
            if (nullRemover.remove(mixRequestInfo, recallResult)) {
                continue;
            }
            // 内置过滤, 针对重复内容
            if (dupRemoveHandler.remove(mixRequestInfo, recallResult.getDocument())) {
                continue;
            }
            int i = 1;
            for (IItemRemoveHandler<Document> handler : removeHandlers) {
                try {
                    if (handler.remove(mixRequestInfo, recallResult.getDocument())) {
                        if (debugUser) {
                            debugCount[i]++;
                        }
                        continue Outer;
                    }
                } catch (Exception e) {
                    logger.error("remove error, handler:{} uid:{}, doc:{} e:{}", handler.handlerName(), uid, recallResult.getDocument(), e);
                    if (handler.errStat()) {
                        if (debugUser) {
                            debugCount[i]++;
                        }
                        continue Outer;
                    }
                }
                i++;
            }
            result.add(recallResult);
        }
        if (debugUser) {
            int docNum = recallResults.size();
            for (int i = 0; i < debugCount.length; i++) {
                logger.info("channel:{} before:{} after:{} uid:{} handler:{}, filter", recallChannel, docNum, docNum - debugCount[i], uid, removeHandlers.get(i).handlerName());
                docNum = docNum - debugCount[i];
            }
        }
        logger.info("remover, channel:{}, before:{}, after:{}, uid:{}", recallChannel, recallResults.size(), result.size(), uid);

        return result;
    }


    public List<RecallResult> doSubFilter(MixRequestInfo mixRequestInfo, List<RecallResult> recallResults, RecallConstant.CHANNEL recallChannel) {
        if (empty(recallResults)) {
            return Lists.newArrayList();
        }
        return remove(mixRequestInfo, recallResults, recallChannel, buildSubHandlers(mixRequestInfo));
    }

    public List<IItemRemoveHandler<Document>> buildSubHandlers(MixRequestInfo mixRequestInfo) {
        List<IItemRemoveHandler<Document>> removeHandlers = Lists.newArrayList();
        Map<String, Boolean> userTypeMap = mixRequestInfo.getUserTypeMap();

        //id过滤：来自张阳的低品质id过滤 和 协同出的低俗id过滤
        if (userTypeMap.getOrDefault("needLowSimidFilter", true) || userTypeMap.getOrDefault("isWxb", true)) {
//            docList = idFilter.filterDocsId(docList);
            removeHandlers.add(idRemover);
        }
        //针对北京及地域为空过滤 审核标签（血腥、三俗、色情、政治敏感）内容
        if (BeijingFilter.isBJOrWXB(mixRequestInfo)) {
//            docList = beijingFilter.filterlowTagDocsId(docList);
            removeHandlers.add(lowTagRemover);
        }

        //快头条用户，不进行过滤
        if (!userTypeMap.getOrDefault("isTitleFilterWhiteNotWxb", false)) {
            //三俗词记分过滤:12.15日放开全国过滤，01.09面向全国过滤切换到预加载做
//            docList = sansuFilter.titleFilter(docList, 6);
//            docList = mediaFilter.filterByMedia(docList, 6);
            removeHandlers.add(sansuRemoverHandler.buildRemover(6));
            removeHandlers.add(mediaRemoverHandler.buildRemover(6));
            //北京用户时效性过滤，只出时效性true的内容
            if (mixRequestInfo.isDebugUser() || userTypeMap.getOrDefault("isBeiJingUserNotWxb", false) || userTypeMap.getOrDefault("isWxb", false)) {
//                docList = timeSensitiveFilter.filterDocsByTimeSensitive(mixRequestInfo, docList);
                removeHandlers.add(timeSensitiveRemover);
            }
        }
        //全国用户过滤时政、国际类时效性
//        docList = timeSensitiveFilter.filterDocsByTimeSensitiveWithCategory(mixRequestInfo, docList);
        removeHandlers.add(timeSensitiveWithCategoryRemover);
        if (userTypeMap.getOrDefault(GyConstant.needQttFilter, true)) {
//            quTouTiaoFilter(docList);
            removeHandlers.add(quTouTiaoRemover);
        }
        return removeHandlers;
    }


    public List<RecallResult> doPosFeedFilter(MixRequestInfo mixRequestInfo, List<RecallResult> recallResults, RecallConstant.CHANNEL recallChannel) {
        if (empty(recallResults)) {
            return Lists.newArrayList();
        }
        Map<String, Boolean> userTypeMap = mixRequestInfo.getUserTypeMap();
        UserModel userModel = mixRequestInfo.getUserModel();
        List<IItemRemoveHandler<Document>> removeHandlers = Lists.newArrayList();
        removeHandlers.add(new DupRemoveHandler());
        String uid = mixRequestInfo.getUid();

        //id过滤：来自张阳的低品质id过滤 和 协同出的低俗id过滤
        if (userTypeMap.getOrDefault("needLowSimidFilter", true) || userTypeMap.getOrDefault("isWxb", true)) {
//            docList = idFilter.filterDocsId(docList);
            removeHandlers.add(idRemover);
        }
        //三俗词记分过滤:12.15日放开全国过滤，01.09面向全国过滤切换到预加载做
//        docList = sansuFilter.titleFilter(docList, 6);
//        docList = mediaFilter.filterByMedia(docList, 6);
        removeHandlers.add(sansuRemoverHandler.buildRemover(6));
        removeHandlers.add(mediaRemoverHandler.buildRemover(6));
        //北京用户时效性过滤，只出时效性true的内容
        if (mixRequestInfo.isDebugUser() || (userModel.getLoc() != null && userModel.getLoc().contains("北京"))) {
//            docList = timeSensitiveFilter.filterDocsByTimeSensitive(mixRequestInfo, docList);
            removeHandlers.add(timeSensitiveRemover);
        }
        if (userTypeMap.getOrDefault(GyConstant.needQttFilter, true)) {
//            quTouTiaoFilter(docList);
            removeHandlers.add(quTouTiaoRemover);
        }
        //全国用户过滤时政、国际类时效性
//        docList = timeSensitiveFilter.filterDocsByTimeSensitiveWithCategory(mixRequestInfo, docList);
        removeHandlers.add(timeSensitiveWithCategoryRemover);

        return remove(mixRequestInfo, recallResults, recallChannel, removeHandlers);
    }

    public List<RecallResult> dupFilter(MixRequestInfo mixRequestInfo, List<RecallResult> recallResults, RecallConstant.CHANNEL recallChannel) {
        if (empty(recallResults)) {
            return Lists.newArrayList();
        }
        List<IItemRemoveHandler<Document>> removeHandlers = Lists.newArrayList();
        return remove(mixRequestInfo, recallResults, recallChannel, removeHandlers);
    }
}
