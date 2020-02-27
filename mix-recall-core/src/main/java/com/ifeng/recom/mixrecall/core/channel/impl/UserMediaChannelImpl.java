package com.ifeng.recom.mixrecall.core.channel.impl;

import com.ifeng.recom.mixrecall.common.constant.DocType;
import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.core.channel.excutor.UserMediaRecall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilg1 on 2018/1/18.
 */
@Service
public class UserMediaChannelImpl {

    private final static Logger logger = LoggerFactory.getLogger(UserMediaChannelImpl.class);

    public List<RecallResult> doRecall(MixRequestInfo mixRequestInfo,String docType) {

        UserModel userModel = mixRequestInfo.getUserModel();
        List<RecallResult> result = new ArrayList<>();
        try {
            if(docType.equals(DocType.DOCPIC.getValue())){
                UserMediaRecall userMediaRecall = new UserMediaRecall(mixRequestInfo,userModel.getDocpic_media(), UserProfileEnum.TagPeriod.LONG,200,DocType.DOCPIC.getValue());
                result = userMediaRecall.call();
            }else{
                UserMediaRecall userMediaRecall = new UserMediaRecall(mixRequestInfo,userModel.getVideo_media(), UserProfileEnum.TagPeriod.LONG,200,DocType.VIDEO.getValue());
                result = userMediaRecall.call();
            }
        } catch (Exception e) {
            logger.error("uid:{},UserMediaChannelImpl thread error {}", mixRequestInfo.getUid(), e);
            e.printStackTrace();
        }
        return result;
    }


}
