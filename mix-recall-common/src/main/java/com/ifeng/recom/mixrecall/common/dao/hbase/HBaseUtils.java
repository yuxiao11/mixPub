package com.ifeng.recom.mixrecall.common.dao.hbase;

import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by geyl on 2017/3/10.
 */
public class HBaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(HBaseUtils.class);

    private static Connection conn;

    private static final int HASH_CODE = 499;

    static {
        conn = HBaseConnection.getConnection();
    }

    public static HashMap<String, String> geAlltData(String rowKey, String tableName) {
        HashMap<String, String> columnValue = new HashMap<String, String>();
        Result result;
        Table table = null;
        try {
            Get get = new Get(Bytes.toBytes(rowKey));
            table = conn.getTable(TableName.valueOf(tableName));
            result = table.get(get);
            if (result.listCells() == null) {
                return null;
            }
            for (Cell cell : result.listCells()) {
                String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                columnValue.put(qualifier, value);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != table) {
                    table.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return columnValue;
    }

    public static void addData(String rowKey, String tableName, String familyName, String column, String value) {
        Table table = null;
        try {
            Put put = new Put(Bytes.toBytes(rowKey));
            table = conn.getTable(TableName.valueOf(tableName));
            put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(column), Bytes.toBytes(value));

            table.put(put);
//            System.out.println("Add Data Success! " + rowKey);
        } catch (IOException e) {
            e.printStackTrace();

        } catch (IllegalArgumentException I) {
            System.out.println(rowKey + "       " + value);
            System.out.println("put error");

        } finally {
            try {
                if (null != table) {
                    table.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addManyColumnData(String rowKey, String tableName, String familyName, String[] columns, String[] values) {
        Table table = null;
        try {
            Put put = new Put(Bytes.toBytes(rowKey));
            table = conn.getTable(TableName.valueOf(tableName));

            for (int i = 0; i < columns.length; i++) {
                put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columns[i]), Bytes.toBytes(values[i]));
            }

            table.put(put);

        } catch (IOException e) {
            e.printStackTrace();

        } catch (IllegalArgumentException I) {
            System.out.println(rowKey + "       " + Arrays.toString(values));
            System.out.println("put error");

        } finally {
            try {
                if (null != table) {
                    table.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addDataWithTTL(String rowKey, String tableName, String familyName, String column, String value, Long ttl) {
        Table table = null;
        try {
            Put put = new Put(Bytes.toBytes(rowKey));
            table = conn.getTable(TableName.valueOf(tableName));
            put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(column), Bytes.toBytes(value));
            put.setTTL(ttl);
            table.put(put);
            System.out.println("Add Data Success! " + rowKey);
        } catch (IOException e) {
            e.printStackTrace();

        } catch (IllegalArgumentException I) {
            System.out.println(rowKey + "       " + value);
            System.out.println("put error");

        } finally {
            try {
                if (null != table) {
                    table.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据rowkey查询数据
     *
     * @param rowKey rowKey
     * @return
     */
    public static ArrayList<String> getData(String rowKey, String tableName) {
        ArrayList<String> valueList = new ArrayList<String>();
        Result result;
        Table table = null;
        try {
            Get get = new Get(Bytes.toBytes(rowKey));
            table = conn.getTable(TableName.valueOf(tableName));
            result = table.get(get);
            if (result.listCells() == null) {
                return null;
            }
            for (Cell cell : result.listCells()) {
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                valueList.add(value);
                String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                System.out.println(qualifier + " : " + value);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != table) {
                    table.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return valueList;
    }


    /**
     * 查询某一列数据
     *
     * @param tableName  表名
     * @param rowKey     rowKey
     * @param familyName 列族
     */
    public static HashMap<String, String> getResultByColumn(String tableName, String rowKey, String familyName, String columnName) {
        HashMap<String, String> columnValue = new HashMap<String, String>();
        Table table;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
            Result result = table.get(get);

            if (result.listCells() == null) {
                return null;
            }

            for (Cell cell : result.listCells()) {
                String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                columnValue.put(qualifier, value);
            }
            return columnValue;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 查询某一列数据
     *
     * @param tableName  表名
     * @param rowKey     rowKey
     * @param familyName 列族
     */
    public static HashMap<String, String> getResultByColumn(String tableName, String rowKey, String familyName, String columnName1, String columnName2, String columnName3) {
        HashMap<String, String> columnValue = new HashMap<String, String>();
        Table table;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName1));
            get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName2));
            get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName3));
            Result result = table.get(get);

            for (Cell cell : result.listCells()) {
                String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                columnValue.put(qualifier, value);
            }
            return columnValue;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return columnValue;
    }


    public static HashMap<String, String> getResultByColumns(String tableName, String rowKey, String familyName, String[] columns) {
        HashMap<String, String> columnValue = new HashMap<>();
        Table table;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));

            for (int i = 0; i < columns.length; i++) {
                get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columns[i]));
            }

            Result result = table.get(get);

            if (result.isEmpty()) {
                return columnValue;
            }

            for (Cell cell : result.listCells()) {
                String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                columnValue.put(qualifier, value);
            }
            return columnValue;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return columnValue;
    }

    /**
     * 查询某一列数据
     *
     * @param tableName  表名
     * @param rowKey     rowKey
     * @param familyName 列族
     */
    public static HashMap<String, String> getResultByCF(String tableName, String rowKey, String familyName) {
        HashMap<String, String> columnValue = new HashMap<String, String>();
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addFamily(Bytes.toBytes(familyName));
            Result result = table.get(get);

            if (result == null) {
                return null;
            }

            if (result.listCells() == null) {
                return null;
            }

            for (Cell cell : result.listCells()) {
                String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                columnValue.put(qualifier, value);
            }
//            return columnValue;

        } catch (IOException e) {
            columnValue = null;
            e.printStackTrace();
        } finally {
            try {
                table.close();
            } catch (IOException e) {
                table = null;
                logger.error("htable:{} close IOException", tableName);
            }
        }
        return columnValue;
    }

    /**
     * HBase数据写入
     *
     * @param tableName
     * @param rowkey
     * @param familyName
     * @param columnName
     * @param json
     */
    public static void putData(String tableName, String rowkey, String familyName, String columnName, String json) {
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowkey));
            put.addColumn((Bytes.toBytes(familyName)), Bytes.toBytes(columnName), Bytes.toBytes(json));
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("htable:{} close IOException", tableName);
            }
        }
    }

    /**
     * 对传入的rowKey hash化避免热点问题
     *
     * @param rowKey
     * @return
     */
    public static String getHashedID(String rowKey) {
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
            return "" + prefix + "_" + rowKey;
        } catch (NoSuchAlgorithmException e) {
            logger.error("[HBase] Exception while getting hashed ID!!  " + e.getMessage());
            return null;
            // e.printStackTrace();
        }

    }

    public static void hbaseClose() {
        try {
            if (null != conn && !conn.isClosed()) {
                conn.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Map<String, String>> queryItemByKeyAndColumns(List<String> keys, Set<String> columns){
        //获取get集
        List<Get> gets = new ArrayList<>();
        for(String k: keys){
            String rowKey = getRowKey(k);
            Get getPerKey = new Get(Bytes.toBytes(rowKey));
            for(String col:columns){
                getPerKey.addColumn(Bytes.toBytes("f1"), Bytes.toBytes(col));
            }
            //不要setMaxVersions() 或者setTimeRange() ，否则可能得不到最新更新的数据
            gets.add(getPerKey);
        }


        Map<String,Map<String, String>> newsInfo = new HashMap<>();

        HTable table = null;
        try {
            table = (HTable) conn.getTable(TableName.valueOf("news_itemf"));
            Result[] results =  table.get(gets);
            for(int i = 0; i < results.length; ++i) {
                Result result = results[i];
                String key = keys.get(i);
                Map<String, String> item = new HashMap<>(64);
                for (KeyValue keyValue : result.raw()) {
                    if (item.size() > 0 && columns.size() <= item.size()) {
                        break;
                    }
                    String column = new String(keyValue.getQualifier());
                    if (columns.contains(column)) {
                        item.put(column, new String(keyValue.getValue()));
                    }
                }
                newsInfo.put(key, item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("htable:{} close IOException", "news_itemf");
            }
        }
        return newsInfo;
    }


    /**
     * 获取rowkey
     */
    private static String getRowKey(String key) {
        if (key == null || key.equals("")) {
            logger.error("aid: " + key + " is null.");
            return null;
        }
        try {
            byte[] btInput = key.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(btInput);
            byte[] resultByteArray = messageDigest.digest();
            int i = 0;
            for (int offset = 0; offset < resultByteArray.length; ++offset) {
                i += Math.abs(resultByteArray[offset]);
            }
            int prefix = 1000 + i % 499;
            return prefix + "_" + key;
        } catch (Exception e) {
            logger.error("Failed to get rowkey of aid: " + key + ".", e);
        }
        return null;
    }

    /**
     * 通过客户端I，simId等获取Cmppid (新Cmppid就是前缀"cmpp_" + 推荐ID)
     */
    public static String queryCmppidByKey(String key,String tableName) {
        String cmppid = null;
        String rowKey = getRowKey(key);
        if (rowKey != null && !rowKey.equals("")) {
            Table table = null;
            try {
                table = conn.getTable(TableName.valueOf(tableName));
                try {
                    Get get = new Get(Bytes.toBytes(rowKey));
                    Result result = table.get(get);
                    if (result != null && !result.isEmpty()) {
                        for (KeyValue keyValue : result.raw()) {
                            String column = new String(keyValue.getQualifier());
                            if (column.equals("jsonItemf")) {
                                cmppid = new String(keyValue.getValue());
                                break;
                            }
                        }
                    }
                } catch(Exception e) {
                    logger.error(e.getMessage(), e);
                }

            } catch (IOException e) {
                 e.printStackTrace();
            } finally {

                try {
                    table.close();
                } catch (IOException e) {
                    table = null;
                    logger.error("htable:{} close IOException", tableName);
                }
             }

        } else {
            logger.info("Failed to get rowKey of key:" + key + "!");
        }

        return cmppid;
    }
    /**
     * 批量查询
     *
     * @param tableName  表名
     * @param keys     rowKey
     */
    public static Map<String, String> queryCmppIdBySubId( List<String> keys,String tableName) {
        Map<String, String> idMap = new HashMap<>();
        Map<String, Get> simIdMap = new HashMap<>();
        Map<Get, String> retMap = new HashMap<>();
        List<Get> gets = new ArrayList<>();
        for(String simId:keys){
            String rowKey = getRowKey(simId);
            if (rowKey != null && !rowKey.equals("")){
                Get get = new Get(Bytes.toBytes(rowKey));
                simIdMap.put(simId, get);
                gets.add(get);
            }
        }

        Table table = null;

        try {
            table = conn.getTable(TableName.valueOf(tableName));

            if(simIdMap.size() > 0){
                try {
                    Result[] results = table.get(gets);
                    for(int i = 0; i < results.length; ++i){
                        if (results[i] != null && !results[i].isEmpty()) {
                            for (KeyValue keyValue : results[i].raw()) {
                                String column = new String(keyValue.getQualifier());
                                if (column.equals("jsonItemf")) {
                                    String cmppid = new String(keyValue.getValue());
                                    if(!cmppid.startsWith("cmpp_"))
                                        break;
                                    if(cmppid.startsWith("cmpp_cmpp"))
                                        cmppid = cmppid.substring(5);
                                    retMap.put(gets.get(i), cmppid);
                                    break;
                                }
                            }
                        }
                    }

                } catch(Exception e) {
                    logger.error(e.getMessage(), e);
                }
                for(String simId: simIdMap.keySet()){
                    if(retMap.containsKey(simIdMap.get(simId))){
                        idMap.put(simId, retMap.get(simIdMap.get(simId)));
                    }
                }
                return idMap;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                table.close();
            } catch (IOException e) {
                table = null;
                logger.error("htable:{} close IOException", tableName);
            }
        }
        return idMap;
    }


    public static void main(String args[]) {

        List<String> keys = Lists.newArrayList();

        keys.add("sub_93555365");
        keys.add("sub_93555369");
        keys.add("sub_93553782");
        keys.add("sub_93525987");
//        String rowkey = getHashedID("sub_65654651");



        Map<String, String> itemMap = queryCmppIdBySubId(keys, "news_itemf_index");

        System.out.println(itemMap);

        String s = queryCmppidByKey("sub_93525987", "news_itemf_index");
        System.out.println("s="+s);

//        List<String> rowkeys = Lists.newArrayList();
//        rowkeys.add("video-d02f7419-33d7-40f1-833e-8b0e256c8063");
//        rowkeys.add("7ihwx1wGO36");
//        Set<String> columns = new HashSet<>();
//        columns.add("url");//id 和 guid
//        columns.add("publishedTime");
//        columns.add("thumbnailpic");
//        columns.add("simId");
//        columns.add("title");
//        columns.add("source");
//        Map<String, Map<String,String>> retMap = queryItemByKeyAndColumns(rowkeys, columns);
//        System.out.println(retMap);

    }
}
