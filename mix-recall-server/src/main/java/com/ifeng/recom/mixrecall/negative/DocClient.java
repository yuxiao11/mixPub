package com.ifeng.recom.mixrecall.negative;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.model.NewsPortraitRec;
import com.ifeng.recom.mixrecall.core.cache.DocPreloadCache;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 从HBase中读取新闻画像：只读取必要的字段
 */
@Service
public class DocClient {

    protected static final Logger log = LoggerFactory.getLogger(DocClient.class);

    private static Configuration conf = HBaseConfiguration.create();
    private static Connection con = null;
    private static String familyNameF1 = "f1";
    private static String familyNameF2 = "f2";
    private static final int HASH_CODE = 499;
    public static final String STAT_TableName = "news_statistics_v2";
    private static final String clientPort = "2181";

    // ifeng hbase
    private static final String quorum = "10.80.71.148,10.80.72.148,10.80.73.148,10.80.74.148,10.80.75.148";

    //线下测试使用测试集群，性能很差，不要提交到线上
//    private static final String quorum = "10.80.5.155,10.80.6.155,10.80.7.155,10.80.8.155,10.80.9.155";

	private static String[] HBASE_COLUMNS = {
	  DocConstant.Doc_Title,
	  DocConstant.Doc_Type,
	  DocConstant.Doc_SimId,
	  DocConstant.Doc_Source,
	  DocConstant.Doc_Features,
	  DocConstant.Doc_PublishedTime,
	  DocConstant.Doc_QualityLevel,
      DocConstant.Doc_ValidateCotags,
      DocConstant.Doc_LdaTopic,
      DocConstant.Doc_TitleWords,
      DocConstant.Doc_NewsLenLevel,
      DocConstant.Doc_Performance,
      DocConstant.Doc_Category,
      DocConstant.Doc_QualityLevel,
      DocConstant.Doc_TimeSensitiveLevel,
      DocConstant.Doc_SpecialParam,
      DocConstant.Doc_Distype

	};

    static {
        getConnection();
    }

    private static void getConnection() {
        conf.set("hbase.zookeeper.property.clientPort", clientPort);
        conf.set("hbase.zookeeper.quorum", quorum);

        conf.set("hbase.client.retries.number", "2");
        conf.set("hbase.rpc.timeout", "400");
        conf.set("hbase.client.operation.timeout", "600");

        try {
            con = ConnectionFactory.createConnection(conf);
        } catch (Exception e) {
            log.error("[HBase Init ERROR:{}" + e);
            e.printStackTrace();
        }
    }


    /**
     * 若多线程调用，应当创建自己独立的table并结束时释放
     */
    public Table getDocTable(String tableName) {
        Table reTable = null;
        try {
            if (con == null) {
                getConnection();
            }
            reTable = con.getTable(TableName.valueOf(tableName));
        } catch (Exception e) {
            log.error("init {} table error{}", tableName, e);
        }
        return reTable;
    }

    /**
     * 对传入的rowKey hash化避免热点问题
     *
     * @param rowKey
     * @return
     */
    private String getHashedID(String rowKey) {
        if (rowKey == null || rowKey.isEmpty()) {
            return null;
        }
        try {
            byte[] btInput = rowKey.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(btInput);
            byte[] resultByteArray = messageDigest.digest();
            int i = 0;
            for (int offset = 0; offset < resultByteArray.length; offset++) {
                i += Math.abs(resultByteArray[offset]);
            }

            int prefix = 1000 + i % HASH_CODE;

            StringBuilder sb = new StringBuilder();
            sb.append(prefix).append(DocConstant.Symb_Underline).append(rowKey);
            btInput = null;
            resultByteArray = null;
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("HBase Exception while getting hashed ID!!  {}",
                    e);
//            e.printStackTrace();
            return null;

        }

    }


    /**
     * 查询item库，获取item详细信息
     * @return
     */
    public Map<String, Document> getItemBatch(List<String> keyList) {
        Map<String, Document> reMap = Maps.newHashMap();
        Set<String> docset = new HashSet<>(keyList);
        long start = System.currentTimeMillis();
        try {

            reMap = DocPreloadCache.getBatchDocsNoClone(docset);

            if(reMap.size() > 0){
                convertToItemProfile(reMap);
            }


        } catch (Exception e) {
            e.printStackTrace();
            log.error("get es list error {}", e);
        } finally {
            long cost = System.currentTimeMillis() - start;
            log.debug("size:{},getItemByHbaseCost:{}", keyList.size(), cost);
        }
        return reMap;
    }

    /**
     * 将map转换为Itemf
     *
     * @return
     */
    private void convertToItemProfile(Map<String,Document> documentMap) {
        //初始化cotag
        for(Map.Entry<String,Document> item : documentMap.entrySet()){
            Document doc = item.getValue();
            String docCotag = doc.getCotag();
            if(docCotag != null){
                Set<String> cotags = new HashSet<>(Arrays.asList(docCotag.split(" ")));
                doc.setcoTagSet(cotags);
            }


            //初始化cate
            List<String> category = new ArrayList<>();
            String c = doc.getC();
            if( c != null) {
                String[] cate = c.split(" ");
                for(int i =0 ;i <cate.length;i++  ){
                    String cateName = cate[i].split("\\^")[0];
                    category.add(cateName);
                }
                doc.setCategory(category);
            }


            //初始化subcate

            List<String> subCategory = new ArrayList<>();
            String sc = doc.getSubcate();
            if(sc != null) {
                String[] subcate = sc.split(" ");
                for(int i =0 ;i <subcate.length;i++  ){
                    String subcateName = subcate[i].split("\\^")[0];
                    subCategory.add(subcateName);
                }
                doc.setSubCateList(subCategory);
            }

            //初始化lda
            List<String> ldaList = new ArrayList<>();
            String lda = doc.getLdaTopic();
            if(lda != null ) {
                String[] ldalist = lda.split(" ");
                for(int i =0 ;i <ldalist.length;i++  ){
                    String ldaName = ldalist[i].split("\\^")[0];
                    ldaList.add(ldaName);
                }
                doc.setldaTopicList(ldaList);

            }

        }


    }


    /**
     * 根据docid批量查询Document
     *
     * @param docIds
     * @return
     */
    public Map<String, Document> getDocBatch(List<String> docIds) {
        Map<String, Document> result = Maps.newHashMap();

        try {
            Map<String, String> ikvIdMap = Maps.newHashMap();
            List<String> ikvIdList = Lists.newArrayList();
            for (String docid : docIds) {
                if(StringUtils.isBlank(docid)){
                    log.error("docId is null");
                    continue;
                }

                ikvIdMap.put(docid, docid);
                ikvIdList.add(docid);
            }

            //拆分成小段来查询
            List<List<String>> indexMapList = getIndexListBatch(ikvIdList, 100);

            Map<String, Document> itemResult = Maps.newHashMap();
            long start = System.currentTimeMillis();
            for (List<String> tmp : indexMapList) {

                Map<String, Document> itemMap = getItemBatch(tmp);

                if (itemMap != null && itemMap.size() > 0) {
                    itemResult.putAll(itemMap);
                }

                long cost = System.currentTimeMillis() - start;
                if (cost > DocConstant.MaxTimeOut_QueryHbaseDocOnline) {
                    log.info("getItemBatch break,size:{},cost:{}", docIds.size(), cost);
                    break;
                }
            }

            for (String docid : docIds) {
                Document item = itemResult.get(docid);
                if (item == null) {
                    log.info("{}  ikv doc is null!!!", docid);
                    continue;
                }
                item.setId(docid);
                result.put(docid, item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getDocBatch ERROR:{}", e);
        }
        return result;
    }


    public Map<String,NewsPortraitRec> getNewsPortraitRecFromHbase(List<String> simIdList) {
        Table table = getDocTable(STAT_TableName);
        SimpleDateFormat daySdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        String today_column = daySdf.format(c.getTime());
        String[] columns = {"LAST_P1D", "LAST_PT3H"};
        Map<String, NewsPortraitRec> reMap = Maps.newHashMap();
        List<Get> listGets = new ArrayList<Get>();
        long start = System.currentTimeMillis();
        try {
            for (String simid : simIdList) {
                if (StringUtils.isBlank(simid)) {
                    log.error("simId is null");
                    continue;
                }

            String rowKey = getHashedID(simid);
            if (rowKey == null) {
                log.error("key get rowKey error: {} " + simid);
                continue;
            }
            Get get = new Get(Bytes.toBytes(rowKey));
            for (String column : columns) {
                get.addColumn(Bytes.toBytes(familyNameF2), Bytes.toBytes(column));
            }
            get.addColumn(Bytes.toBytes(familyNameF1), Bytes.toBytes(today_column));
            listGets.add(get);
            }
            Result[] statlist = table.get(listGets);
            if (statlist == null || statlist.length < 1) {
                log.error("batch get statistics list error.");
            } else {
                for (Result result : statlist) {
                    try {
                        Map<String, String> map = new HashMap<>();
                        String simid = Bytes.toString(result.getRow());
                        for (String column : columns) {
                            String value = Bytes.toString(result.getValue(Bytes.toBytes(familyNameF2), Bytes.toBytes(column)));
                            if (StringUtils.isBlank(value)) {
                                continue;
                            }
                            map.put(column, value);
                        }
                        String value = Bytes.toString(result.getValue(Bytes.toBytes(familyNameF1), Bytes.toBytes(today_column)));
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }
                        map.put(today_column, value);
                        if (map.size() > 0) {
                            String key = Bytes.toString(result.getRow());
                            if (key != null && key.contains("_")) {
                                key = key.substring(key.indexOf("_") + 1);
                                NewsPortraitRec newsPortraitRec = parseMap(map, today_column);
                                newsPortraitRec.setId(simid);
                                reMap.put(key, newsPortraitRec);
                            }
                        }
                    } catch (Exception e) {
                        log.error("batch getItem error {}", e);
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("batch getItemBatch list error {}", e);
        } finally {
            long cost = System.currentTimeMillis() - start;
            log.debug("size:{},getItemByHbaseCost:{}", reMap.size(), cost);
        }
        return reMap;
    }



    private NewsPortraitRec parseMap(Map<String, String> remap, String todayColumn) {
        NewsPortraitRec newsPortraitRec = new NewsPortraitRec();
        if (remap.containsKey("LAST_P1D")) {
            JSONObject object = JSONObject.parseObject(remap.get("LAST_P1D"));
            if(object.containsKey("ev")) {
                newsPortraitRec.setLast1d_ev(object.getString("ev"));
            }
            if(object.containsKey("pv")) {
                newsPortraitRec.setLast1d_pv(object.getString("pv"));
            }
            if(object.containsKey("share")) {
                newsPortraitRec.setLast1d_share(object.getString("share"));
            }
            if(object.containsKey("store")) {
                newsPortraitRec.setLast1d_store(object.getString("store"));
            }
            if(object.containsKey("comment")) {
                newsPortraitRec.setLast1d_comment(object.getString("comment"));
            }
            double duration = 0.0;
            double duration_c = 0.0;
            double v_duration = 0.0;
            double v_duration_c = 0.0;
            double readrate = 0.0;
            double readrate_c = 0.0;
            double v_readrate = 0.0;
            double v_readrate_c = 0.0;
            if(object.containsKey("duration")) {
                duration = object.getDouble("duration");
            }
            if(object.containsKey("duration_count")) {
                duration_c = object.getDouble("duration_count");
            }
            if(object.containsKey("v_duration")){
                v_duration = object.getDouble("v_duration");
            }
            if(object.containsKey("v_duration_count")){
                v_duration_c = object.getDouble("v_duration_count");
            }
            if(object.containsKey("readrate")) {
                readrate = object.getDouble("readrate");
            }
            if(object.containsKey("readrate_count")) {
                readrate_c = object.getDouble("readrate_count");
            }
            if(object.containsKey("v_rate")) {
                v_readrate = object.getDouble("v_rate");
            }
            if(object.containsKey("v_rate_count")) {
                v_readrate_c = object.getDouble("v_rate_count");
            }
            if(v_duration_c != 0.0) {
                newsPortraitRec.setLast1d_avgduration(v_duration/v_duration_c);
                if(v_readrate_c != 0.0) {
                    newsPortraitRec.setLast1d_avgreadrate(v_readrate / v_readrate_c);
                }
            }else {
                if(duration_c != 0.0) {
                    newsPortraitRec.setLast1d_avgduration(duration / duration_c);
                }
                if(readrate_c != 0.0) {
                    newsPortraitRec.setLast1d_avgreadrate(readrate/readrate_c);
                }
            }
        }
        if (remap.containsKey("LAST_PT3H")) {
            JSONObject object = JSONObject.parseObject(remap.get("LAST_PT3H"));
            if(object.containsKey("ev")) {
                newsPortraitRec.setLast3h_ev(object.getString("ev"));
            }
            if(object.containsKey("pv")) {
                newsPortraitRec.setLast3h_pv(object.getString("pv"));
            }
            if(object.containsKey("share")) {
                newsPortraitRec.setLast3h_share(object.getString("share"));
            }
            if(object.containsKey("store")) {
                newsPortraitRec.setLast3h_store(object.getString("store"));
            }
            if(object.containsKey("comment")) {
                newsPortraitRec.setLast3h_comment(object.getString("comment"));
            }
        }

        if (remap.containsKey(todayColumn)) {
            JSONObject object = JSONObject.parseObject(remap.get(todayColumn));
            if(object.containsKey("ev")) {
                newsPortraitRec.setToday_ev(object.getString("ev"));
            }
            if(object.containsKey("pv")) {
                newsPortraitRec.setToday_pv(object.getString("pv"));
            }
            if(object.containsKey("share")) {
                newsPortraitRec.setToday_share(object.getString("share"));
            }
            if(object.containsKey("store")) {
                newsPortraitRec.setToday_store(object.getString("store"));
            }
            if(object.containsKey("comment")) {
                newsPortraitRec.setToday_comment(object.getString("comment"));
            }
        }

        return newsPortraitRec;
    }

	/**
	 * 判断字符串 以数字开头
	 * @param str
	 * @return
	 */
	private boolean startWithNum(String str) {
		return DocConstant.NUMBER_PATTERN.matcher(str).matches();
	}


    /**
     * 拆分成小段来查询,限定一批40个
     *
     * @param maxsize
     * @return
     */
    private List<List<String>> getIndexListBatch(List<String> indexList, int maxsize) {
        List<List<String>> indexListBatch = Lists.newArrayList();

        List<String> tmp = new ArrayList<>();
        for (String ikvid : indexList) {
            if (tmp.size() > maxsize) {
                indexListBatch.add(tmp);
                tmp = new ArrayList<>();
            }
            tmp.add(ikvid);
        }
        if (tmp.size() > 0) {
            indexListBatch.add(tmp);
        }
        return indexListBatch;
    }

}