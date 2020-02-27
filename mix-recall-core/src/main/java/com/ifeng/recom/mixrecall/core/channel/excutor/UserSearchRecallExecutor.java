package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecordTime;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.service.BloomFilter;
import com.ifeng.recom.mixrecall.common.service.UserProfile;
import com.ifeng.recom.mixrecall.common.util.WhyFiledUtils;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;
import com.ifeng.recom.mixrecall.core.cache.UserSearchCache;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by lilg1 on 2018/1/18.
 */
public class UserSearchRecallExecutor implements Callable<List<Document>> {

    private UserModel userModel;
    private static final Logger logger = LoggerFactory.getLogger(UserSearchRecallExecutor.class);
    public UserSearchRecallExecutor(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    public List<Document> call() throws Exception {
        //从画像中获取用户搜过的词
        List<RecordTime> searchList = userModel.getSearch();

        if (CollectionUtils.isEmpty(searchList)) {
            return Collections.emptyList();
        }

        List<Document> result = new ArrayList<>();

        //截取最近五个搜索词
//      searchList.sort(timeComparator);
        searchList = searchList.stream().limit(5).collect(Collectors.toList());

        List<String> searchWords = searchList.stream().map(x -> x.getRecordName()).collect(Collectors.toList());

        Map<String, List<Document>> searchResult = UserSearchCache.getDocuments(searchWords);

        logger.info("before dup uid : {} ,searchResult : {}",userModel.getUserId(),searchResult);
        for (Map.Entry<String, List<Document>> entry : searchResult.entrySet()) {
            result.addAll(entry.getValue());
        }

        if (CollectionUtils.isNotEmpty(result)) {
            result = BloomFilter.filterSimIdByBloomFilter(userModel.getUserId(), result);
        }
        logger.info("after dup uid : {} ,searchResult : {}",userModel.getUserId(),searchResult);
        WhyFiledUtils.setWhy(WhyReason.USER_SEARCH, result);
        return result;
    }



}
