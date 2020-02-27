package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.UserProfileService;
import com.ifeng.recom.mixrecall.common.util.JsonUtil;
import com.ifeng.recom.mixrecall.common.util.http.HttpClientUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ifeng.recom.mixrecall.common.util.http.HttpClientUtil.transMapToPairs;

public class FFMRecallExecutorTest {


    public static void main(String[] args) {
        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        UserProfileService u = new UserProfileService();
        mixRequestInfo.setUid("zhaohonghong");
        Map us = new HashMap();

        List<RecordInfo> vcList = new ArrayList<>();
        RecordInfo re = new RecordInfo("主机&技能", 0.54);
        RecordInfo re1 = new RecordInfo("主机", 0.54);
        re.setRecordName("主机&技能");
        re.setExpose(3);
        re.setReadFrequency(1);
        re.setWeight(0.54);
        re1.setRecordName("主机");
        re1.setExpose(3);
        re1.setReadFrequency(1);
        re1.setWeight(0.54);
        vcList.add(re1);
        vcList.add(re);
        us.put("video_subcate", vcList);
        String json = JsonUtil.object2jsonWithoutException(us);


        Map<String, String> postParam = new HashMap<>();
        postParam.put("size", 50 + "");
        postParam.put("uid", mixRequestInfo.getUid());
        postParam.put("userProfile", URLEncoder.encode(json));
        String json2 = JsonUtil.object2jsonWithoutException(postParam);
        String json1 = HttpClientUtil.httpPost("http://172.30.156.152:8081/recall/ffmv/user", transMapToPairs(postParam), 1000);
        System.out.println(json1);
    }
}
