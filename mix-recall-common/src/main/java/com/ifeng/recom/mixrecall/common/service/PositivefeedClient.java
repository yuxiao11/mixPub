package com.ifeng.recom.mixrecall.common.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.FlowTypeAsync;
import com.ifeng.recom.mixrecall.common.model.item.Index4User;
import com.ifeng.recom.mixrecall.common.model.item.LastDocBean;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.StringZipUtil;
import com.ifeng.recom.mixrecall.common.util.http.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory.MapStringListIndex4User;
import static com.ifeng.recom.mixrecall.common.util.http.HttpClientUtil.transMapToPairs;

/**
 * ctr服务api，可以分别 调用线上
 * Created by jibin on 2017/6/28.
 */
public class PositivefeedClient {
    protected static Logger logger = LoggerFactory.getLogger(PositivefeedClient.class);
    private static final String positivefeedUrl = "http://local.recom.ifeng.com/positivefeed/list?uid=";

    /**
     * 查询正反馈服务
     *
     * @param mixRequestInfo
     * @param timeout
     * @return 获取正反馈结果，结果为一个map，key为simId
     */
    public static Map<String, List<Index4User>> getPositivefeedResult(MixRequestInfo mixRequestInfo, List<LastDocBean> lastDocBeanList, DocType docType, int timeout) {
        String uid = mixRequestInfo.getUid();
        Map<String, List<Index4User>> indexResult = null;
        try {
            MixRequestInfo tmp = new MixRequestInfo();
            tmp.setUid(mixRequestInfo.getUid());
            tmp.setDebugUser(mixRequestInfo.isDebugUser());
            tmp.setAbTestMap(mixRequestInfo.getAbTestMap());
            tmp.setUserTypeMap(mixRequestInfo.getUserTypeMap());
            tmp.setRecomChannel(mixRequestInfo.getRecomChannel());
            tmp.setProid(mixRequestInfo.getProid());
            tmp.setLastDocBeans(lastDocBeanList);
            String flowType = FlowTypeAsync.positiveFeedNew;
            if (DocType.VIDEO.equals(docType)) {
                flowType = FlowTypeAsync.positiveFeedVideoNew;
            }
            tmp.setFlowType(flowType);


            Map<String, String> postParam = new HashMap<>();
            postParam.put("requestInfo", new Gson().toJson(tmp));

            String url = positivefeedUrl + uid;
            String result = HttpClientUtil.httpPost(url, transMapToPairs(postParam), timeout);
            if (StringUtils.isNotBlank(result)) {
                result = StringZipUtil.decompress(result);
                indexResult = GsonUtil.json2Object(result, MapStringListIndex4User);
            }

        } catch (Exception e) {
            logger.error("{} getPositivefeed ERROR:{}", mixRequestInfo.getUid(), e);
        }

        return indexResult;
    }


    public static void main(String[] args) {
//        String uid = "geyalu";
        String uid = "867392030450848";

        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        Map<String, Boolean> userTypeMap = new HashMap<>();
        mixRequestInfo.setUserTypeMap(userTypeMap);
        mixRequestInfo.setUid(uid);

        //last doc
        List<LastDocBean> lastDocBeanList = Lists.newArrayList();
        LastDocBean lastDocBean1 = new LastDocBean("66559695", "clusterId_50244589","");
        LastDocBean lastDocBean2 = new LastDocBean("44732264", "clusterId_26802883","");
        lastDocBeanList.add(lastDocBean1);
        lastDocBeanList.add(lastDocBean2);
        mixRequestInfo.setLastDocBeans(lastDocBeanList);

        //user info
        //userTypeMap.put("isWhiteLv2NotWxbNotBSG", true);
        userTypeMap.put("isWxb", false);
        userTypeMap.put("isLvsWhite", true);
        userTypeMap.put("isBeiJingUserNotWxb", true);
        mixRequestInfo.setDebugUser(true);

//        mixRequestInfo.setFlowType(FlowTypeAsync.positiveFeedNew);
        mixRequestInfo.setFlowType(FlowTypeAsync.IncreasedateMerge);

        for (int i = 0; i < 10; i++) {
            Map<String, List<Index4User>> result = getPositivefeedResult(mixRequestInfo, lastDocBeanList, DocType.DOCPIC, 5000);
            System.out.println(new Gson().toJson(result));
        }
    }
}
