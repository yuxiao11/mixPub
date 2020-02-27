package com.ifeng.recom.mixrecall.common.dao.hbase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ifeng.recom.mixrecall.common.dao.redis.RedisUtils;
import com.ifeng.recom.mixrecall.common.model.UserCF;
import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import com.ifeng.recom.mixrecall.common.util.JsonUtil;
import com.ifeng.recom.tools.common.logtools.utils.timer.TimerEntityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ifeng.recom.mixrecall.common.dao.hbase.HBaseUtils.getResultByColumn;
import static com.ifeng.recom.mixrecall.common.dao.hbase.HBaseUtils.getResultByColumns;

/**
 * @author geyl
 */
public class UserCFClick {
    private static final Logger logger = LoggerFactory.getLogger(UserCFClick.class);
    private static final Logger timeLogger = LoggerFactory.getLogger(TimerEntityUtil.class);

    private static final String tableName = "recom_usercf";
    private static final String familyName = "cf";
    private static final String[] HBASE_COLUMN = {"n1", "n2", "n3", "n4", "n5", "n6", "n7", "n8", "n9", "n10", "n11", "n12", "n13", "n14", "n15", "n16", "n17", "n18", "n19", "n20", "n21", "n22", "n23", "n24", "n25", "n26", "n27", "n28", "n29", "n30"};

    private static String getRowKey(String uid) {
        if (StringUtils.isBlank(uid))
            return null;
        try {
            byte[] btInput = uid.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(btInput);
            byte[] resultByteArray = messageDigest.digest();
            int i = 0;
            for (byte aResultByteArray : resultByteArray) {
                i += Math.abs(aResultByteArray);
            }
            int prefix = 1000 + Math.abs(i) % 499;
            return "" + prefix + "_" + uid;
        } catch (Exception e) {
            logger.error("get rowkey error", e);
        }
        return null;
    }

    public static UserCF getNeighborClick(String uid) {
        long start = System.currentTimeMillis();
        String rowKey = getRowKey(uid);
        HashMap<String, String> clickMap = getResultByColumns(tableName, rowKey, familyName, HBASE_COLUMN);

        long cost = System.currentTimeMillis() - start;
        if (cost > 50) {
            ServiceLogUtil.debug("ucf_cli {} cost:{}", uid, cost);
        }

        Map<Integer, List<String>> neighborClick = transformHbaseToMap(clickMap);
        UserCF userCF = new UserCF(uid, neighborClick);
        return userCF;
    }


    private static Map<Integer, List<String>> transformHbaseToMap(HashMap<String, String> clickMap) {
        Map<Integer, List<String>> neighborClick = new HashMap<>();
        for (Map.Entry<String, String> entry : clickMap.entrySet()) {
            if (StringUtils.isBlank(entry.getValue())) {
                continue;
            }
            try {
                String[] clickArr = entry.getValue().split("#");
                int position = Integer.parseInt(entry.getKey().replace("n", ""));

                List<String> clickId = new ArrayList<>();
                if (!entry.getValue().contains("clusterId_")) {
                    for (String id : clickArr) {
                        clickId.add("clusterId_" + id);
                    }
                } else {
                    clickId = Arrays.asList(clickArr);
                }

                neighborClick.put(position, clickId);
            } catch (Exception e) {
                logger.error("", e);
            }
        }

        return neighborClick;
    }

    private static Map<Integer, List<String>> transformRedisToMap(Map<String, String> clickMap) {
        Map<Integer, List<String>> neighborClick = new HashMap<>();

        for (Map.Entry<String, String> entry : clickMap.entrySet()) {
            if (StringUtils.isBlank(entry.getValue())) {
                continue;
            }
            String key = entry.getKey();
            String[] keyArr = key.split(":");
            if (keyArr == null || keyArr.length < 2) {
                continue;
            }
            Integer position = Integer.valueOf(keyArr[1]);
            if (position == 0) {
                continue;
            }
            String value = entry.getValue();
            Map<String, String> userMap = JsonUtil.json2Object(value, new TypeReference<Map<String, String>>() {
            });
            String simIds = userMap.get("simids");
            String[] simidArr = simIds.split("#");
            List<String> idList = new ArrayList<>();
            for (String simId : simidArr) {
                if (!simId.contains("clusterId_")) {
                    simId = "clusterId_" + simId;
                }
                idList.add(simId);
            }
            neighborClick.put(position, idList);
        }
        return neighborClick;
    }


    /**
     * 查询与uid最近的item
     *
     * @param uid
     * @return
     */
    public static Map<String, String> getNeighborItem(String uid) {
        long startTime = System.currentTimeMillis();
        String rowKey = getRowKey(uid);
        Map<String, String> itemMap = getResultByColumn("recom_usercf_itemvec", rowKey, "f", "item");

        long cost = System.currentTimeMillis() - startTime;
        if (cost > 50) {
            ServiceLogUtil.debug("uservechbase {} cost:{}", uid, cost);
        }

        if (itemMap == null || itemMap.isEmpty()) {
            return null;
        }

        return itemMap;
    }

    public static UserCF getKttNeighborClick(String uid) {
        String rowKey = getRowKey(uid);
        HashMap<String, String> clickMap = getResultByColumns("recom_usercf_ktt_merge", rowKey, familyName, HBASE_COLUMN);

        Map<Integer, List<String>> neighborClick = transformHbaseToMap(clickMap);
        if (neighborClick.isEmpty()) {
            return null;
        } else {
            return new UserCF(uid, neighborClick);
        }
    }


    public static UserCF getNeighborClick_als(String uid) {
        String rowKey = getRowKey(uid);
        HashMap<String, String> clickMap = getResultByColumns("recom_usercf_als", rowKey, familyName, HBASE_COLUMN);

        Map<Integer, List<String>> neighborClick = transformHbaseToMap(clickMap);
        UserCF userCF = new UserCF(uid, neighborClick);

        return userCF;
    }

    public static UserCF getNeighborClickAlsFromRedis(String uid) {
        JedisCluster userCfJedis = RedisUtils.getJedisUserCfAls();
        UserCF userCf = null;
        try {
            Map<String, String> neighborClickMap = userCfJedis.hgetAll(uid);
            Map<Integer, List<String>> neighborClick = transformRedisToMap(neighborClickMap);
            userCf = new UserCF(uid, neighborClick);
        } catch (Exception e) {
            logger.error("uid:{} get usercf from redis err:{}", uid, e);
        }

        return userCf;
    }

    public static UserCF getNeighborClick_dssm(String uid) {
        String rowKey = getRowKey(uid);
        HashMap<String, String> clickMap = getResultByColumns("recom_usercf_dssm", rowKey, familyName, HBASE_COLUMN);

        Map<Integer, List<String>> neighborClick = transformHbaseToMap(clickMap);
        UserCF userCF = new UserCF(uid, neighborClick);

        return userCF;
    }

    public static void main(String[] args) {
//        Map<String, String> map = getNeighborItem("865736031565925");
//        System.out.println(map.get("item"));

//        UserCF userCF = getNeighborClick("865736031565925");
//        System.out.println(userCF);
//        System.out.println(userCF.getNeighborClick().get(1).toString());

//        UserCF userCF = getNeighborClick_als("867305035296545");

        UserCF userCfRedis = getNeighborClickAlsFromRedis("99001008030221");
        System.out.println(GsonUtil.object2json(userCfRedis));

        UserCF usercf = getNeighborClick_als("99001008030221");
        System.out.println(GsonUtil.object2json(usercf));

    }
}
