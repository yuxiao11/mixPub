package com.ifeng.recom.mixrecall.negative;


import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.common.model.item.EvItem;
import com.ifeng.recom.mixrecall.common.model.request.MixRequestInfo;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import net.sf.ehcache.CacheManager;

import java.util.*;

@Service("SessionServiceImpl")
public class SessionServiceImpl implements SessionService {
    private static final Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);

    @Autowired
    @Qualifier("HeadlineItemProfileService")
    HeadlineItemProfileService headlineItemProfileService;

    public List<EvItem> getSessionByCache(String key) {

        Cache document = CacheManager.getInstance().getCache("documentEntity");
        if (document == null) {
            logger.error("没有 sessionCache 缓存");
            return null;
        }
        Element elem = document.get(key);
        if (elem != null) {
            return (List<EvItem>) elem.getObjectValue();
        }
        return null;
    }

    public void addSessionCache(String uid, List<EvItem> session) {
        Cache sessionCache = CacheManager.getInstance().getCache("sessionCache");
        if (sessionCache == null) {
            logger.error("没有 sessionCache 缓存");
            return;
        }
        Element elem = new Element(uid, session);
        sessionCache.put(elem);
    }


    public List<EvItem> merge_add(List<EvItem> session, MixRequestInfo requestInfo) {

        List<EvItem> oldSessions = getSessionByCache(requestInfo.getUid()); //从缓存中获取cache
        if(session == null || session.size() == 0) {
            return oldSessions;
        }

        if (oldSessions != null ) {
            Long t = 0L;
            Long FiveHourAgo = System.currentTimeMillis() - 3600 * 5000;
            int index = 0;
            int begin = 0;
            while(begin < oldSessions.size() && oldSessions.get(begin).getT() < FiveHourAgo) {
                begin++;
            }

            //找到上次sessionList尾部在本次sessionList中的位置
            while (t < oldSessions.get(oldSessions.size() - 1).getT() && index < session.size()) {
                t = session.get(index).getT();
                index += 1;
            }
            if (index < session.size()) {
                List<EvItem> slist = oldSessions.subList(begin, oldSessions.size());
                List<EvItem> sList = session.subList(index, session.size());
                slist.addAll(sList);
                oldSessions = slist;
                addSessionCache(requestInfo.getUid(), slist);
            }else {
                addSessionCache(requestInfo.getUid(), oldSessions);
            }
        } else {
            oldSessions = new ArrayList<>();
            oldSessions.addAll(session);
            addSessionCache(requestInfo.getUid(), oldSessions);
        }
        return oldSessions;
    }

    public List<EvItem.EvObj> getevListFromSessionList(List<EvItem> sessionInfos) {
        List<EvItem.EvObj> exposeInfos = new ArrayList<>();
        if(sessionInfos == null || sessionInfos.size() == 0) {
            return exposeInfos;
        }
        for (EvItem item : sessionInfos) {
            exposeInfos.addAll(item.getEv());
        }
        return exposeInfos;
    }

    public Map<String, ItemProfile> getClick(MixRequestInfo requestInfo, List<EvItem> evInfos) {

        List<EvItem.EvObj> evList = new ArrayList<>();
        if(evInfos == null || evInfos.size() == 0) {
            return null;
        }
        for (EvItem item : evInfos) {
            try {
                evList.addAll(item.getEv());
            }catch (Exception e) {
                logger.error("getClick error e:{}", e);
            }
        }
        List<String> docIds = new ArrayList<>();
        for(EvItem.EvObj item: evList){
            docIds.add(item.getI());
        }

        Map<String, ItemProfile> EvitemProfilMap = genItemProfiles(requestInfo, docIds);


        for (EvItem item : evInfos) {

            for (EvItem.EvObj ev : item.getEv()) {

                Document document = EvitemProfilMap.get(ev.getI()).getDocument();

                ev.setT(item.getT());

                ev.setCategories(document.getCateList());

                ev.setLdatopics(document.getLdaTopicList());

                ev.setSubcates(document.getScList());

                ev.setCotags(document.getcoTagSet());

            }
        }

        return EvitemProfilMap;
    }

    public Map<String, ItemProfile> genItemProfiles(MixRequestInfo requestInfo, List<String> docIds) {
        List<Item> itemList = new ArrayList<>();

        for (String docId : docIds) {
            Item item = new Item();
            item.setId(docId);
            itemList.add(item);
        }
        /**
         * 此处获取内容画像（从ES中获取）
         */
        Map<String, ItemProfile> itemProfileMap = headlineItemProfileService.getItemProfileModel(itemList, requestInfo);

        return itemProfileMap;
    }





    public static void main(String[] args){
        List<RecordInfo> combineTagList = null;
        System.out.println(combineTagList);
        Set<String> a = new HashSet<>();
        a.add("a");
        a.add("b");
        a.add("c");
        Set<String> b = new HashSet<>();
//        b.add("b");
        b.add("d");
//        b.add("c");
        ;

        System.out.println(a.retainAll(b));

//        String ev = "[{\"t\":1555978401053,\"ev\":[{\"i\":\"7m6CnNAckhE\",\"y\":\"jpPool#focus0\"},{\"i\":\"7m6ElC3e1qq\",\"y\":\"jpPool#focus1\"},{\"i\":\"7m6AhlYAKOm\",\"y\":\"jpPool#focus2\"},{\"i\":\"7m2j3S7TNQm\",\"y\":\"pre\"},{\"i\":\"7m4mLLRhiDo\",\"y\":\"recomEs\"},{\"i\":\"7m4hIlUfbRw\",\"y\":\"cotag_v_long_n\"},{\"i\":\"7m24dx3Tdyi\",\"y\":\"cotag_d_recent\"},{\"i\":\"130461532\",\"y\":\"cotag_d_recent\"},{\"i\":\"7m1okDh3s7G\",\"y\":\"cotag_d_l_n\"},{\"i\":\"7lyP1xnHdXV\",\"y\":\"cotag_v_long_n\"}]},{\"t\":1555978478015,\"ev\":[{\"i\":\"7m5KuoDiLAG\",\"y\":\"HotTagInsert\"},{\"i\":\"7lt6P9CMafo\",\"y\":\"user_cf_DSSM\"},{\"i\":\"7m5DKQOCS92\",\"y\":\"recomEs\"},{\"i\":\"7m5r0wVsp5U\",\"y\":\"cotag_d_r_n\"},{\"i\":\"7lZVDQWf47M\",\"y\":\"user_cf_DSSM\"},{\"i\":\"7lIciPg6j0f\",\"y\":\"user_cf_DSSM\"},{\"i\":\"7m5NYIVlCAi\",\"y\":\"cotag_d_r_n\"},{\"i\":\"47248264\",\"y\":\"user_cf_DSSM\"},{\"i\":\"7m4ziUPo3Oa\",\"y\":\"exp_d\"},{\"i\":\"7lvLBmzBr9h\",\"y\":\"user_cf_als_cache\"}]},{\"t\":1555979333076,\"ev\":[{\"i\":\"7m4xWMtHvhA\",\"y\":\"HotTagInsert\"},{\"i\":\"564233\",\"y\":\"user_cf_DSSM\"},{\"i\":\"7m4dZT39w2K\",\"y\":\"UserSub\"},{\"i\":\"129633878\",\"y\":\"user_cf_DSSM\"},{\"i\":\"132032324\",\"y\":\"user_cf_DSSM\"},{\"i\":\"7m5LTamIEwR\",\"y\":\"lda_topic_recent\"},{\"i\":\"130205459\",\"y\":\"user_cf_als_cache\"},{\"i\":\"7loOl4ACa3A\",\"y\":\"user_cf_DSSM\"},{\"i\":\"7m5TR4lmTgm\",\"y\":\"UserSub\"},{\"i\":\"7m4hoivshrU\",\"y\":\"recomEs\"}]},{\"t\":1555979351110,\"ev\":[{\"i\":\"7m5Hv03luqG\",\"y\":\"HotTagInsert\"},{\"i\":\"125508349\",\"y\":\"user_cf_als_cache\"},{\"i\":\"7lceeuBHqUx\",\"y\":\"user_cf_DSSM\"},{\"i\":\"7m1qxfLcBG0\",\"y\":\"excellent_d_sc\"},{\"i\":\"43898156\",\"y\":\"user_cf_DSSM\"},{\"i\":\"7lQGandhkt9\",\"y\":\"lda_topic_recent\"},{\"i\":\"7m4oSbfaWuK\",\"y\":\"excellent_d_sc\"},{\"i\":\"121893215\",\"y\":\"exp_v\"},{\"i\":\"7m2zyFJ1cE5\",\"y\":\"excellent_d_sc\"},{\"i\":\"7m5LAnFcbSF\",\"y\":\"recomEs\"}]}]";
//        RequestInfo requestInfo = new RequestInfo();
//        requestInfo.setLastDoc(new ArrayList<LastDocBean>());
//        LastDocBean lastDoc = new LastDocBean();
//        lastDoc.setSimId("7m6CnNAckhE");
//        requestInfo.getLastDoc().add(lastDoc);
//        SessionServiceImpl sessionService = new SessionServiceImpl();
//        List<cotagSession> cotagSessionList = sessionService.genCotagSession(sessionInfos, requestInfo, ev);
    }
}
