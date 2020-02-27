package com.ifeng.recom.mixrecall.common.dao.elastic;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.util.DocUtils;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by geyl on 2017/12/4.
 */
public class Query {
    private static final Logger logger = LoggerFactory.getLogger(Query.class);

    private static TransportClient client;
    private static final String INDEX = "preload-news";
    private static final String TYPE = "_doc";

    private static String[] noFetchSource = new String[]{};
    private static String[] needFetchSource = new String[]{"docId"};

    private static LoadingCache<String, String> guid2DocidCache;

    static {
        client = EsClientFactory.getClient();
        initGuid2DocidCache();
    }

    private static void initGuid2DocidCache() {
        guid2DocidCache = CacheBuilder
                .newBuilder()
                .concurrencyLevel(15)
                .expireAfterWrite(180, TimeUnit.MINUTES)
                .initialCapacity(1000000)
                .maximumSize(1500000)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String guId) {
                        String docId = Query.queryDocIdByGuid(guId);
                        if (StringUtils.isNotBlank(docId)) {
                            return docId;
                        } else {
                            return "";
                        }
                    }
                });
    }

    /**
     * 根据docId查询ES获取Document对象
     *
     * @param docId
     * @return Document
     */
    public static Document queryDocument(String docId) {
        //兼容guid
        if (docId.length() >= GyConstant.GUID_Length) {
            //正常是没有这部分流量的，观察无流量后续可以下掉
            try {
                String docIdNew = guid2DocidCache.get(docId);
                logger.info("guid2DocidCache:{} to docid: {}", docId, docIdNew);
                docId = docIdNew;
            } catch (ExecutionException e) {
                logger.error("guid2Docid ERROR:{}", docId);
                e.printStackTrace();
            }
        }
        //兼容字符串开头的其他id，
        if (!StringUtil.startWithNum(docId)) {
            docId = docId.substring(docId.indexOf(GyConstant.Symb_Underline) + 1);
        }
        Document doc =null;
        try{
            GetResponse response = client.prepareGet(INDEX, TYPE, docId).get(new TimeValue(500));
            String rt = response.getSourceAsString();

//            GetResponse response = client.prepareGet(INDEX, TYPE, "7nQ2frp3YM6").get(new TimeValue(500));
//            String rt = response.getSourceAsString();
//
//
//            Document doc = GsonUtil.json2ObjectWithoutExpose(rt, Document.class);


            doc = GsonUtil.json2ObjectWithoutExpose(rt, Document.class);
            DocUtils.initDocument(doc);
        }catch (Exception e){
            logger.error("guid2Docid query doc ERROR:{},docId:{}",e,docId);
            e.printStackTrace();
        }
        return doc;
    }

    public static String queryDocId(String simId) {
        QueryBuilder qb = QueryBuilders.matchQuery("simId", simId);

        SearchResponse response = client.prepareSearch(INDEX)
//                .setScroll(new TimeValue(6000))
                .setFetchSource(needFetchSource, noFetchSource)
                .setQuery(qb)
                .setTimeout(new TimeValue(500))
                .setSize(1).get();

        return Arrays.stream(response.getHits().getHits()).map(hit -> hit.getSourceAsMap().get("docId").toString()).findFirst().orElse("");

    }

    /**
     * 根据docid查询es转换
     *
     * @param guid
     * @return Document
     */
    public static Document queryDocumentByGuid(String guid) {
        String docId = queryDocIdByGuid(guid);
        if (StringUtils.isBlank(docId)) {
            return null;
        }

        GetResponse response = client.prepareGet(INDEX, TYPE, docId).get(new TimeValue(500));
        String rt = response.getSourceAsString();

        Document doc = GsonUtil.json2ObjectWithoutExpose(rt, Document.class);
        return doc;
    }

    /**
     * 根据guid查询docid
     *
     * @param guid
     * @return docId
     */
    public static String queryDocIdByGuid(String guid) {
        QueryBuilder qb = QueryBuilders.matchQuery("url", guid);

        SearchResponse response = client.prepareSearch(INDEX)
                .setScroll(new TimeValue(60000))
                .setTimeout(new TimeValue(500))
                .setFetchSource(needFetchSource, noFetchSource)
                .setQuery(qb)
                .setSize(1).get();

        return Arrays.stream(response.getHits().getHits()).map(hit -> hit.getSourceAsMap().get("docId").toString()).findFirst().orElse("");

    }

    public static Map<String, Document> batchQueryByDocId(Set<String> ids) {
        Map<String, Document> docs = Maps.newHashMap();
        MultiGetResponse response = null;
        try {
            response = client.prepareMultiGet().add(INDEX, TYPE, ids).get(new TimeValue(500));
        } catch (Exception e) {
            logger.error("request doc error,", e);
            return docs;
        }
        MultiGetItemResponse[] rs = response.getResponses();
        for (MultiGetItemResponse rt : rs) {
            Document doc = null;
            try {
                doc = GsonUtil.json2ObjectWithoutExpose(rt.getResponse().getSourceAsString(), Document.class);
            } catch (Exception e) {
            }
            Document d = DocUtils.initDocument(doc);
            if (doc == null) {
                continue;
            }
            docs.put(d.getDocId(), d);
        }
        return docs;
    }

    public static void main(String[] args) {


        Map<String,String> s= Maps.newHashMap();
        System.out.println(s);
        s.put("asd","asd");
        System.out.println(s.get("xx"));


//        List<String> s = new ArrayList();
//        s.add("asd");
//        s.add("asd");
//        String b = s.get(10);
//        System.out.println(b);


        try{
            for(int i = 0; i<50; i++) {
                long start = System.currentTimeMillis();
                GetResponse response = client.prepareGet(INDEX, TYPE, "7nQ2frp3YM6").get(new TimeValue(500));
                String rt = response.getSourceAsString();


                Document doc = GsonUtil.json2ObjectWithoutExpose(rt, Document.class);
                List<String> category = new ArrayList<>();


                DocUtils.initDocument(doc);
                String docCotag = doc.getCotag();
                Set<String> cotags = new HashSet<>(Arrays.asList(docCotag.split(" ")));
                doc.setcoTagSet(cotags);
                System.out.println(doc.getcoTagSet());


//                doc.getCoTagSet()
//                public void SetcoTagSet(Set<String> coTagSet) {
//                    this.coTagSet = coTagSet;
//                }


            }
        }catch (Exception e){
            logger.error("guid2Docid query doc ERROR:{},docId:{}",e,"124612821");
            e.printStackTrace();
        }
    }
}
