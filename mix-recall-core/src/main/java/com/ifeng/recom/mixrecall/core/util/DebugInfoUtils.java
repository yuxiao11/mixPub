package com.ifeng.recom.mixrecall.core.util;


import com.ifeng.recom.mixrecall.common.constant.UserProfileEnum;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.RecordInfo;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import com.ifeng.recom.mixrecall.core.cache.mapping.SimIdDocIdMappingCache;

import java.util.*;

/**
 * Created by geyl on 2017/11/16.
 */
public class DebugInfoUtils {

    public static List<String> getBatchDocForSimId(Set<String> set) {
        List<String> resultList=new ArrayList<>();
        try{
            Map<String, String> simIdDocIdMap = SimIdDocIdMappingCache.getBatchDocIds(set);
            Map<String, Document> documentMap = DocPreloadCache.getBatchDocsWithQueryNoClone(new HashSet<>(simIdDocIdMap.values()));

            for(Map.Entry<String, Document> entry : documentMap.entrySet()) {
                Document doc = entry.getValue();
                String result="docId:"+doc.getDocId()+",title:"+doc.getTitle()+",topic1:"+doc.getTopic1()+",hotBoost:"+doc.getHotBoost();
                resultList.add(result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return resultList;
    }
}
