package com.ifeng.recom.mixrecall.core.channel.excutor;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ifeng.recom.mixrecall.common.api.SearchApi;
import com.ifeng.recom.mixrecall.common.dao.elastic.EsClientFactory;
import com.ifeng.recom.mixrecall.common.dao.hbase.HBaseUtils;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by lilg1 on 2018/1/23.
 */

public class UserSearchQuery {
    private final static Logger logger = LoggerFactory.getLogger(UserSearchQuery.class);

    private static TransportClient client;

    private final static String INDEX = "preload-news";
    private final static String TYPE = "all";

    private final static boolean available = true;
    private final static boolean offline = false;

    private static String[] needFetchSource = new String[]{"docId", "simId"};

    static {
        client = EsClientFactory.getClient();
    }


    /**
     * 根据用户搜索词查询搜索接口获取文章
     *
     * @param searchQuery
     * @return
     */
    public static List<Document> searchQueryEs(String searchQuery) {

        List<String> rowkeys = Lists.newArrayList();
        JsonObject searchResult=null;
        JsonArray items=null;
        try{
            searchResult = SearchApi.doSearch(searchQuery);
            items = searchResult.getAsJsonArray("items");
        }catch (Exception e){
            logger.error("{} searchQueryEs error:{}",searchQuery,e);
        }

        if (items ==null||items.size()<1){
            return Lists.newArrayList();
        }
        logger.info("SearchApi searchSize is :{}",items.size());
        for (JsonElement j: items){
            String id = j.getAsJsonObject().get("id").getAsString();
            String sourceFrom = j.getAsJsonObject().get("sourceFrom").getAsString();
            if("weMedia".equals(sourceFrom)){
                sourceFrom = "sub";
                rowkeys.add(sourceFrom+"_"+id);
            }else if ("video".equals(sourceFrom)) {
                rowkeys.add(id);
            }else {
                rowkeys.add(sourceFrom+"_"+id);
            }
        }
        //搜索id转推荐id
        Map<String, String> news_itemf_index = HBaseUtils.queryCmppIdBySubId(rowkeys, "news_itemf_index");

        Set<String> docids = news_itemf_index.values().stream().map(s -> s.substring("cmpp_".length())).collect(Collectors.toSet());
        logger.info("searchQuery:{} --- id2docIdfromHbase :{} --- docids:{} ",searchQuery,news_itemf_index,docids);
        //根据docid取推荐库取document
        List<Document> rt = new ArrayList<>();
        Map<String, Document> documentMap = DocPreloadCache.getBatchDocsWithQueryNoClone(docids);

        Set<String> includeSimIds = new HashSet<>();
        Collection<Document> values = documentMap.values();
        //过滤docidmiss && simid为空
        Set<Document> collect = values.stream().filter(document -> document!=null && !Strings.isBlank(document.getSimId())).collect(Collectors.toSet());
        for (Document d : collect){
            if (!includeSimIds.contains(d.getSimId())) {
                d.setRecallTag(searchQuery);
                rt.add(d);
            }
            includeSimIds.add(d.getSimId());
        }
        return rt;
    }

}
