package com.ifeng.recom.mixrecall.common.util;

import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 用户级别的工具类
 */

@Service
public class UserUtils {
    private static final Logger logger = LoggerFactory.getLogger(UserUtils.class);

    public static boolean isColder(MixRequestInfo mixRequestInfo) {
        try{
            if(mixRequestInfo==null||mixRequestInfo.getDevMap().get("isColder")==null){
                return false;
            }
            if(mixRequestInfo.getDevMap().get("isColder").equals("Yes")) {
                logger.info("{} isColder is:{}", mixRequestInfo.getUid(),"Yes");
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }


    public static boolean isColderMiniUcb(MixRequestInfo mixRequestInfo) {
        try{
            if(mixRequestInfo==null||mixRequestInfo.getDevMap().get("isMiniUcb")==null){
                return false;
            }
            if(mixRequestInfo.getDevMap().get("isMiniUcb").equals("Yes")) {
                logger.info("{} isMiniUcb is:{}", mixRequestInfo.getUid(),"Yes");
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isColderMiniModel(MixRequestInfo mixRequestInfo) {
        try{
            if(mixRequestInfo==null||mixRequestInfo.getDevMap().get("isMiniModel")==null){
                return false;
            }
            if(mixRequestInfo.getDevMap().get("isMiniModel").equals("Yes")) {
                logger.info("{} isMiniModel is:{}", mixRequestInfo.getUid(),"Yes");
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }


    public static boolean isLastSim0Model(MixRequestInfo mixRequestInfo) {
        try{
            if(mixRequestInfo==null||mixRequestInfo.getDevMap().get("LastSim0")==null){
                return false;
            }
            if(mixRequestInfo.getDevMap().get("LastSim0").equals("true")) {
                logger.info("{} LastSim0 is:{}", mixRequestInfo.getUid(),"true");
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isLastSimVideoModel(MixRequestInfo mixRequestInfo) {
        try{
            if(mixRequestInfo==null||mixRequestInfo.getDevMap().get("LastSimVideo")==null){
                return false;
            }
            if(mixRequestInfo.getDevMap().get("LastSimVideo").equals("true")) {
                logger.info("{} LastSimVideo is:{}", mixRequestInfo.getUid(),"true");
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isSubTestModel(MixRequestInfo mixRequestInfo) {
        try{
            if(mixRequestInfo==null||mixRequestInfo.getDevMap().get("subSolo")==null){
                return false;
            }
            if(mixRequestInfo.getDevMap().get("subSolo").equals("true")) {
                logger.info("{} subSolo is:{}", mixRequestInfo.getUid(),"true");
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }


    public static boolean isFFMTest(MixRequestInfo mixRequestInfo) {
        try{
            if(mixRequestInfo==null||StringUtils.isBlank(mixRequestInfo.getDevMap().get("userFFM"))){
                return false;
            }
            if(mixRequestInfo.getDevMap().get("userFFM").equals("true")) {
                logger.info("{} userFFM is:{}", mixRequestInfo.getUid(),"true");
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isFFMVTest(MixRequestInfo mixRequestInfo) {
        try{
            if(mixRequestInfo==null||StringUtils.isBlank(mixRequestInfo.getDevMap().get("userFFMV"))){
                return false;
            }
            if(mixRequestInfo.getDevMap().get("userFFMV").equals("true")) {
                logger.info("{} userFFMV is:{}", mixRequestInfo.getUid(),"true");
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isUserInterestDecayNew(MixRequestInfo mixRequestInfo) {
        try{
            if(mixRequestInfo==null||mixRequestInfo.getDevMap().get("UserInterestDecay")==null){
                return false;
            }
            if(mixRequestInfo.getDevMap().get("UserInterestDecay").equals("trueNew")) {
                logger.info("{} isUserInterestDecay is:{}", mixRequestInfo.getUid(),"trueNew");
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 此处添加ABtest调用接口
     * @param mixRequestInfo
     * @param abtestName  实验名
     * @param value  实验分组名 格式为Filter_test_20
     * @return
     */

    public static boolean getABtestTag(MixRequestInfo mixRequestInfo,String abtestName,String value){
        try{
            if(mixRequestInfo == null || mixRequestInfo.getAbTestMap().get(abtestName) == null){
                return false;
            }
            if(mixRequestInfo.getAbTestMap().get(abtestName).equals(value)){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;

    }
}


