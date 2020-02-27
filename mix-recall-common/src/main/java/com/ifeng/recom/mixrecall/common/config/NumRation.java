package com.ifeng.recom.mixrecall.common.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.google.common.collect.Sets;
import com.ifeng.recom.mixrecall.common.config.constant.ApolloConstant;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ifeng.recom.mixrecall.common.factory.JsonTypeFactory.MapStringMapStringString;

@EnableApolloConfig({"application","DebugUsers"})
@Configuration
public class NumRation {
    private static Logger logger = LoggerFactory.getLogger(NumRation.class);

    @PostConstruct
    public Map<String,Map<String,String>> getValue(){
        Map<String,Map<String,String>> ratioMap=null;

        Config config = ConfigService.getAppConfig();
        String key = "pullnumRatio";
        String defaultValue = "apollo_client";
        String value = config.getProperty(key,defaultValue);
        ratioMap=GsonUtil.json2Object(value, MapStringMapStringString);

//        Collections.shuffle(exploreList);
//        loadPullNumRatio(value);
        System.out.println(String.format("value is %s",value));
        return ratioMap;
    }

    public void loadPullNumRatio(String yml) {
        Yaml yaml = new Yaml();
        try {
            if (StringUtils.isBlank(yml)) {
                logger.warn("parse debug user configuration empty! ");
            }
            Object obj = yaml.load(yml);
            Map<String, Object> userMap = (Map) obj;
            for (String key : userMap.keySet()) {
                List<Object> list = (List<Object>) userMap.get(key);
                Set<String> userSet = Sets.newHashSet();
                list.forEach(x -> userSet.add(String.valueOf(x)));
//                this.putUserSet(key, userSet);
            }
        } catch (Exception e) {
            logger.error("parse debug user configuration error: {}", e);
        }
    }


    public static void main(String[] args){
        Set<String> xx = BossUserConfig.getBossUser(ApolloConstant.boss_users_key);
        NumRatioConfig.getRatioNum(ApolloConstant.Pull_Num_Ratio);
//        NumRation s = new NumRation();
        Map<String,Map<String,String>> ratioMap=new NumRation().getValue();
        Map<String,String> as = ratioMap.get("userType1-pullGroup1");
        int pullCount = 1;
        Map<String,String> b = ratioMap.get("userType1-pullGroup"+String.valueOf(pullCount));
    }

}
