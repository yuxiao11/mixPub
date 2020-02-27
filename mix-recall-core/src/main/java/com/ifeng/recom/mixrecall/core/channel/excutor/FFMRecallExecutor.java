package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.constant.WhyReason;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.ffm.FFMRecallData;
import com.ifeng.recom.mixrecall.common.model.ffm.FFMRecallResult;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.UserProfileService;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.JsonUtil;
import com.ifeng.recom.mixrecall.common.util.http.HttpClientUtil;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.util.RecallUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.ifeng.recom.mixrecall.common.util.http.HttpClientUtil.transMapToPairs;

/**
 * Created by liligeng on 2019/3/14.
 */
public class FFMRecallExecutor implements Callable<List<RecallResult>> {

    private static final Logger logger = LoggerFactory.getLogger(FFMRecallExecutor.class);

    private static final int timeout = 200;

    private MixRequestInfo mixRequestInfo;

    private int number;

    private String FFM_URL;


    public FFMRecallExecutor(MixRequestInfo mixRequestInfo, int number,String url) {
        this.mixRequestInfo = mixRequestInfo;
        this.number = number;
        this.FFM_URL=url;
    }


    @Override
    public List<RecallResult> call() throws Exception {
        List<RecallResult> recallResults=doRecall(FFM_URL);
        return recallResults;
    }


    public List<RecallResult> doRecall(String url) throws Exception {
        Map<String, String> postParam = new HashMap<>();

        postParam.put("size", number + "");
        postParam.put("uid", mixRequestInfo.getUid());
        UserModel userModel=mixRequestInfo.getUserModel();
        if(userModel!=null){
            Map userModelFFM=new HashMap();
            if(GyConstant.FFM_URL_V.equals(url)){
                userModelFFM.put("video_subcate",userModel.getVideo_subcate());
                userModelFFM.put("recent_video_subcate",userModel.getRecentVideoSubCate());
                userModelFFM.put("last_video_subcate",userModel.getLastVideoSubCate());
                userModelFFM.put("last_video_cate",userModel.getLastVideoCate());
            }else{
                userModelFFM.put("docpic_subcate",userModel.getDocpic_subcate());
                userModelFFM.put("recent_docpic_subcate",userModel.getRecent_docpic_subcate());
                userModelFFM.put("last_docpic_subcate",userModel.getLast_docpic_subcate());
                userModelFFM.put("last_docpic_cate",userModel.getLast_docpic_cate());
                userModelFFM.put("docpic_lda_topic",userModel.getDocpicLdaTopic());
                userModelFFM.put("recent_docpic_lda_topic",userModel.getRecentDocpicLdaTopic());
                userModelFFM.put("last_docpic_lda_topic",userModel.getLastLdaDocpicTopicList());
            }
            String jsonV=JsonUtil.object2jsonWithoutException(userModelFFM);
            postParam.put("userProfile", URLEncoder.encode(jsonV));
        }

        long start = System.currentTimeMillis();
        String json = HttpClientUtil.httpPost(url, transMapToPairs(postParam), timeout);

        long end = System.currentTimeMillis();
        logger.info("query ffm cost:{}", end - start);

        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        FFMRecallResult ffmResult = GsonUtil.json2Object(json, FFMRecallResult.class);

        List<FFMRecallData> list = ffmResult.getData();
        Set<String> docIds = list.stream().map(x -> x.getDocId()).collect(Collectors.toSet());

        Map<String, Document> idDocs = DocPreloadCache.getBatchDocsNoClone(docIds);

        List<Document> docs = new ArrayList<>(idDocs.values());

//        List<Document> filteredDocs = filterSimIdByBloomFilter(mixRequestInfo.getUid(), docs);

        //Document对象转换为召回对象
        List<RecallResult> recallResults = RecallUtils.convertDocument2RecallResult(docs);

        String tag = ffmResult.getTag();
        setWhyInfo(recallResults, tag);

        return recallResults;
    }

    private static void setWhyInfo(List<RecallResult> recallResults, String ffmTag) {
        for (RecallResult result : recallResults) {
            if (WhyReason.FFM_A.getValue().equalsIgnoreCase(ffmTag)) {
                result.setWhyReason(WhyReason.FFM_A);
            } else if (WhyReason.FFM_B.getValue().equalsIgnoreCase(ffmTag)) {
                result.setWhyReason(WhyReason.FFM_B);
            } else if (WhyReason.FFM_C.getValue().equalsIgnoreCase(ffmTag)) {
                result.setWhyReason(WhyReason.FFM_C);
            } else if (WhyReason.FFM_D.getValue().equalsIgnoreCase(ffmTag)) {
                result.setWhyReason(WhyReason.FFM_D);
            } else if (WhyReason.ffmv_a.getValue().equalsIgnoreCase(ffmTag)) {
                result.setWhyReason(WhyReason.ffmv_a);
            }else if (WhyReason.ffmv_b.getValue().equalsIgnoreCase(ffmTag)) {
                result.setWhyReason(WhyReason.ffmv_b);
            }else if (WhyReason.ffmv_c.getValue().equalsIgnoreCase(ffmTag)) {
                result.setWhyReason(WhyReason.ffmv_c);
            }else if (WhyReason.ffmv_d.getValue().equalsIgnoreCase(ffmTag)) {
                result.setWhyReason(WhyReason.ffmv_d);
            }else {
                result.setWhyReason(WhyReason.FFM);
            }
        }
    }

}
