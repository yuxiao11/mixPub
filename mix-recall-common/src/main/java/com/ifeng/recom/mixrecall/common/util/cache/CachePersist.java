package com.ifeng.recom.mixrecall.common.util.cache;

import com.google.common.cache.Cache;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.ifeng.recom.mixrecall.common.constant.GyConstant;
import com.ifeng.recom.mixrecall.common.model.Document;
import com.ifeng.recom.mixrecall.common.util.JsonUtil;
import org.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Guava Cache 持久化到磁盘
 * Created by geyl on 2018/4/19.
 */
public class CachePersist {
    private static final Logger logger = LoggerFactory.getLogger(CachePersist.class);

    public static final String CACHE_PATH = "/data/prod/service/mix-recall/cache_dump/";

    public static void writeToFile(Cache cache, String cacheName) {
        logger.info("start write cache to file, name:{}", cacheName);

        String path = CACHE_PATH + cacheName;

        try {
            Map map = new HashMap(cache.asMap());
            writeObjToFile(path, map);
            logger.info("write cache to file done, name:{}, size:{}, path:{}", cacheName, map.size(), path);
        } catch (Exception e) {
            logger.error("write cache to file error,name:{}, path:{}", cacheName, path, e.toString(), e);
        }
    }

    public static void loadToCache(Cache cache, String cacheName) {
        logger.info("start load file to cache, name:{}", cacheName);

        try{
            String path = CACHE_PATH + cacheName;

            if (cache == null) {
                logger.error("load file to cache error, cache is null, name:{} path:{}", cacheName, path);
            }

            Map<Object, Object> map = (Map<Object, Object>) readObjFromFile(path);

            if (map != null) {
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    try {
                        cache.put(entry.getKey(), entry.getValue());
                    } catch (Exception e) {
                        logger.error("doc preload", e);
                    }
                }

                logger.info("load file to cache done, name:{}, size:{}, cache size:{}", cacheName, map.size(),cache.size());
            }
        }catch (Exception e){
            logger.error("load file to cache error:{}, name:{}",e, cacheName);
        }

    }

    private static void writeObjToFile(String path, Object fileObj) {
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            File fileParent = file.getParentFile();
            if(!fileParent.exists()){
                fileParent.mkdirs();
            }
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(fileObj);

        } catch (IOException e) {
            logger.error("write obj to file error, path:{}", path, e.toString(), e);
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.flush();
                    objectOutputStream.close();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                logger.error("write obj to file error, path:{}", path, e.toString(), e);
            }
        }
    }

    private static Object readObjFromFile(String path) {
        File file = new File(path);
        Object fileObj = null;
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream = null;

        try {
            if(!file.exists()){
                return null;
            }
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);
            fileObj = objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            logger.error("path:{} error:{}", path, e);
        } finally {
            try {
                if (objectInputStream != null) {
                    logger.info("load file to cache done,path:{}", path);
                    objectInputStream.close();
                }
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        return fileObj;
    }



    public static void writeToFileMore(Cache cache, String cacheName) {
        logger.info("start write cache to file, name:{}", cacheName);

        String path = CACHE_PATH + cacheName+".txt";

        try {
            Map map = new HashMap(cache.asMap());
            writeObjToFileMore(path, map);
            logger.info("write cacheNew to file done, name:{}, size:{}, path:{}", cacheName, map.size(), path);
        } catch (Exception e) {
            logger.error("write cacheNew to file error,name:{}, path:{}", cacheName, path, e.toString(), e);
        }



    }

    public static void writeObjToFileMore(String path, Map<Object ,Object> map){

        try {
            if (map != null) {

                int size = map.size();
                logger.info("into writetolocalfile map size:{}", size);


                Map<Integer, Writer> writerMap = Maps.newHashMap();
                Writer writer = null;
                try {
                    long startwrite = System.currentTimeMillis();


                    for (int i = 0; i < GyConstant.doc_txt_Num; i++) {
                        Writer writerTmp = new FileWriter(path + GyConstant.Symb_Underline + i, false);  // 写入的文本不附加在原来的后面而是直接覆盖
                        writerMap.put(i, writerTmp);
                    }
                    int mod = 0;
                    int i=0;
                    String jsonStr="";
                    for (Map.Entry<Object,Object> docMap:map.entrySet()) {
                        try {
                            Document doc=(Document) docMap.getValue();
                            mod = i % GyConstant.doc_txt_Num;

                            writer = writerMap.get(mod);
                            jsonStr=new Gson().toJson(doc);
                            writer.write(jsonStr + "\n");
                            i++;
                        } catch (Exception e) {
                            logger.error("writetolocalfile error:{}", e);
                        }
                    }
                    long cost = System.currentTimeMillis() - startwrite;
                    logger.info("into writetolocalfile size={},sizewrite={},writecost={}", map.size(), i, cost);
                } catch (Exception e) {
                    logger.error("writetolocalfile write to localfile:{}", e);
                } finally {
                    try {
                        for (Map.Entry<Integer, Writer> entry : writerMap.entrySet()) {
                            Writer tmp = entry.getValue();
                            if (tmp != null) {
                                tmp.close();
                                logger.info("Writer close");
                            }
                        }
                    } catch (Exception e) {
                        logger.error("writetolocalfile close e:{}", e);
                    }
                }


            }
        } catch (Exception e) {
            logger.error("writetolocalfile ERROR:{}", e);
        }
    }

    public static  void  main(String[] args){
        Map<Object,Object> resultMap=Maps.newHashMap();
        Document doc=new Document();
        doc.setDocId("123121231");
        resultMap.put("1",doc);
        resultMap.put("2",doc);
        resultMap.put("3",doc);
        resultMap.put("4",doc);
        resultMap.put("5",doc);
        resultMap.put("6",doc);
        writeObjToFileMore("d:/data/person",resultMap);



    }
}
