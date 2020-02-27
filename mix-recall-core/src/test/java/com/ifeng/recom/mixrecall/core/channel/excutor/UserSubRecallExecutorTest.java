package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.UserModel;
import com.ifeng.recom.mixrecall.common.model.request.LogicParams;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import com.ifeng.recom.mixrecall.common.util.UserProfileUtils;
import com.ifeng.recom.mixrecall.core.cache.UserProfileCache;

import java.util.List;

public class UserSubRecallExecutorTest {
    public static void main(String[] args) throws Exception {
//        CacheManager.init();
        String uid = "4447273aeee5e88d";
        UserModel u = UserProfileCache.getUserModel(uid);
        String group_ub="{\"83721629\":\"weMedia_588577|三国演义|1542360303#weMedia_742957|军武大观|1542162815#weMedia_310821|澎湃新闻|1542162794#weMedia_500770|娱闻小姐|1537234269#weMedia_849721|搜候体育|1537234266#weMedia_298982|克利斯艾伦|1536314518#weMedia_536366|每日人物|1536314504#weMedia_1001532|说西道东|1536314498#weMedia_1003957|秘闻历史|1536314496#weMedia_534498|上饶网|1536314490#weMedia_536073|书房记1|1536314484#weMedia_1051695|武侠天地|1536314483#source_谈资|谈资|1507769202#user_82506885|KissyZhou|1504276813#user_78101220|G-葛亚鲁|1503928085#user_79466580|原杨杰|1503912733#user_85528980|❶❾❾❾|1503848146#user_76546016|阿步|1503848093#user_86393248|青栀|1503847521#user_85642291|H.|1503847240#user_84075730|名字放长放长在放长哈哈领稀有字|1503847065#user_85178129|蘑菇文|1503846987#user_87065422|遗忘|1503846851#user_75568492|佳|1503846755#user_83144616|好雪片片|1503830052#user_86809866|小木木|1503044482#user_86781634|手机用户9227|1503024329#user_86295439|鼻子插着葱的大象|1503016843#user_85938710|曾|1502957526#user_63836172|五月天|1502696242#user_82287234|我在凤凰玩推荐|1502622889#user_86441304|洋仔|1502598614#user_86472714|Min|1502453821#user_66363810|一尘|1502453360#user_79184798|唐唐|1502416308#user_86440609|你好|1502415086#user_78149756|java|1502378232#user_86478324|心有林夕|1502378136#user_85547449|手机用户0542|1502378032#user_86442132|周末夜惊魂|1502377949#user_83798057|刑者九合|1502365755#user_86456564|小仙儿|1502365706#user_85616959|朝着日落大道奔去|1502353630#user_86552574|葡萄萄～喵\uD83D\uDC31|1502346757#user_86727249|风车|1502346203#user_55038817|半人马阿尔法星|1502333418#user_85029913|雨巷|1502325358#user_87144736|倾国倾城只为你嫣然一笑！|1502262563#user_86396204|李惠|1502262497#user_86440841|mumi|1502259084#user_86905047|清风|1502258848#user_84142642|yyt|1502256706#user_85244667|日紫气东来照|1502243928#user_75303928|智慧老人的大魔棒|1502193518#user_77611689|ゞ偉︶§|1502187889#user_86332567|仙子|1502187846#user_86614334|风轻云淡|1502187822#user_76511547|Xu\uD83C\uDF80雪寒|1502153866#user_64848801|Palpably|1502111101#user_82903229|2580369.|1502111099#user_83382425|零渡宇|1502111097#user_83652392|猪蹄红烧|1502111096#user_85955099|等待~|1502111094#user_72851809|Andy|1502111093#user_78874278|云岫成诗|1502111092#user_70675739|CaptainTeemO_OnDuty|1502111091#user_85721636|Li尉\uF8FF|1502111064#weMedia_298621|每日评说|1502105742#weMedia_507956|揭秘UFO|1502090545#weMedia_588675|世界奥秘|1502088153#weMedia_371858|非常历史|1502087867#user_71951498|无名之无名|1501995964#user_86506105|手机用户8506|1501927400#user_85252605|\uD83D\uDC14|1501828031#user_86447678||1501818113#user_85854415|神^_^|1501803741#source_环球网|环球网|1501232870#weMedia_590933|军事新资讯|1499931069#\",\"4447273aeee5e88d\":\"weMedia_285807|宝宝树|1540278325#weMedia_6266|育儿技巧大全|1540278245#\"}";
        u.setUb(UserProfileUtils.extractUserSub("", group_ub, uid));

        MixRequestInfo mixRequestInfo = new MixRequestInfo();
        mixRequestInfo.setUserModel(u);
        mixRequestInfo.setUid(uid);

        UserSubRecallExecutor userSubRecallExecutor = new UserSubRecallExecutor(mixRequestInfo, new LogicParams());
        List<Document> docs = userSubRecallExecutor.call();
        userSubRecallExecutor.call();
//        userSubRecallExecutor.call();
//        userSubRecallExecutor.call();

        for (Document document : docs) {
            System.out.println(document.getTitle());
        }

//        List<RecordTime> userSub = u.getUb();
//
//        Set<String> subMedias = userSub.stream().map(x -> key_Source + x.getRecordName()).collect(Collectors.toSet());
//
//        Set<String> userMedia=null;
//
//
//        List<RecordInfo> mediaList=new ArrayList<>();
//        mediaList.addAll(null);
//        mediaList.addAll(null);
//
//        userMedia = mediaList.stream().map(x -> key_Source + x.getRecordName()).collect(Collectors.toSet());
//        userMedia.retainAll(subMedias);
//        System.out.println(userMedia.contains("菜鸟理财"));


    }
}
