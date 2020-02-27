package com.ifeng.recom.mixrecall.test;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecallResult;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.service.UserProfile;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.prerank.CalcCtrUtils;
import com.ifeng.recom.mixrecall.prerank.entity.BasicContext;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import com.ifeng.recom.mixrecall.prerank.tools.CtrSmoothParams;
import com.ifeng.recom.mixrecall.prerank.tools.CtrSmoothParamsManager;
import com.ifeng.recom.mixrecall.prerank.tools.MediaEvalLevelCacheManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory.ListDocument;

public class testCost {


    public static UserModel setUserModel(UserModel userModel) {
        userModel.setGeneral_ub_fm(null);
        userModel.setGeneralLoc("中国_北京市_北京市_朝阳区");
        userModel.setLikevideo("false");
        userModel.setUmos("iphone_13.3");
        userModel.setUa_v(7);
        userModel.setGeneral_vid_timeSensitive("0.2");
        userModel.setGeneral_doc_timeSensitive("0.68");
        userModel.setFullness("0.7475");
        userModel.setDaily_pullNum("9.3");
        userModel.setUmt("iphone11_6,iphone");
        userModel.setNet("4g");
        userModel.setLoc("中国_北京市_北京市_朝阳区");
        return userModel;


    }

    public static BasicContext fillBasicContext(UserModel userModel) {
        BasicContext basiccontext = new BasicContext();

        basiccontext.setUid("e9de1d39e0a54c0c96b7072f75f778c1");
        basiccontext.setUa(userModel.getUmt());
        basiccontext.setMos(userModel.getUmos());
        String loc = userModel.getLoc();
        String[] retarray = {"-", "-", "-"};
        if (StringUtils.isNotBlank(loc)) {
            String[] locInfo = loc.split("_");

            int size = locInfo.length;
            if (size < 4) {
                //处理没有国籍的情况,size 为1到3
                //上海市_上海市_奉贤区
                //上海市_上海市
                if (size > 0) {
                    retarray[0] = locInfo[0].split("\\$")[0];
                }
                if (size > 1) {
                    retarray[1] = locInfo[1].split("\\$")[0];
                }
                if (size > 2) {
                    retarray[2] = locInfo[2].split("\\$")[0];
                }
            } else {
                //处理带有国籍的情况：
                //中国_北京市_北京市_朝阳区
                retarray[0] = locInfo[1].split("\\$")[0];
                retarray[1] = locInfo[2].split("\\$")[0];
                retarray[2] = locInfo[3].split("\\$")[0];
            }
        }
        basiccontext.setLoc1(retarray[0]);
        basiccontext.setLoc2(retarray[1]);
        basiccontext.setLoc3(retarray[2]);

        return basiccontext;

    }

    public static List<FeatureContext> createTestData(File file, UserModel userModel, BasicContext basicContext) {
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        List<FeatureContext> featureEntitys = new ArrayList<FeatureContext>();

        try {


            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        System.out.println(sbf.toString());
        String[] s = sbf.toString().split("\t");
        List<Document> docList = new ArrayList<>();

        int i = 1;

        for (String item : s) {
            for (int j = 0; j < 100; j++) {
                FeatureContext featureContext = new FeatureContext();

                Document doc = GsonUtil.json2ObjectWithoutExpose(item, Document.class);
                DocUtils.initDocument(doc);
                RecallResult recallResult = new RecallResult();
                recallResult.setDocument(doc);

                // 设置文章媒体评级

                int mod = i % 10;
                i += 1;
                String why = new String();
                if (mod == 1) {
                    why = "cotag_v_long";
                } else if (mod == 2) {
                    why = "docpic_c_l";
                } else if (mod == 3) {
                    why = "cotag_d_long";
                } else if (mod == 4) {
                    why = "user_cf_als_cache";
                } else if (mod == 5) {
                    why = "cotag_d_recent_sim";
                } else if (mod == 6) {
                    why = "cotag_v_Long_graph";
                } else if (mod == 7) {
                    why = "ffm";
                } else {
                    why = "UserSub";
                }


                featureContext.setUserProfile(userModel);
                featureContext.setBasicContext(basicContext);
                featureContext.setItemDocument(doc);
                featureContext.setItemMedianDuration(doc.getLast1d_avg_duration());
                featureContext.setItemMediaEvalLevel("3");
                featureEntitys.add(featureContext);
            }
        }
        Collections.shuffle(featureEntitys);
        return featureEntitys;
    }

    public static Map<String ,double[]> createModelVec(){
        Map<String,double[]> modelVec = new HashMap<>();
        for(int i =0; i<10; i++){

        }
    }


    public static void main(String args[]) {
        File file = new File("C:\\Users\\yuxiao1\\Desktop\\testDoc.txt");

        UserModel userModel = new UserModel();
        BasicContext basicContext = new BasicContext();
        List<RecallResult> itemResults = new ArrayList<>();
        Map<String, Map<String, double[]>> smoothParams;

        String uid = "e9de1d39e0a54c0c96b7072f75f778c1";

        double[] userVec = new double[]{0.069011800,0.034813810,0.009361136,0.056097258,0.096367680,0.066587880,0.059169770,0.008095480,0.008616579,0.058521820,0.058450844};

        userModel = setUserModel(userModel);
        basicContext = fillBasicContext(userModel);

        List<FeatureContext> featureEntities = createTestData(file,userModel,basicContext);

        ConcurrentHashMap<String, String> ctrMap = new ConcurrentHashMap<String, String>();

        try {
            CalcCtrUtils.calcCTR(mixRequestInfo,ctrMap, results, model, featureEntities, cacheManager, uidVec);
        } catch (Exception e) {
            logger.error("calcCTR ERROR:{} ", e.getMessage());
            e.printStackTrace();
        }









//        setFeatureEntities(docList,basicContext);

        System.out.println("s");


    }
}