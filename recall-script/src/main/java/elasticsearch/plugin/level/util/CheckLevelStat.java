package elasticsearch.plugin.level.util;

import redis.clients.jedis.Jedis;

import java.util.*;

import static elasticsearch.plugin.level.util.RedisForLevel.getSourceLevelClient;

/**
 * Created by geyl on 2018/1/30.
 */
public class CheckLevelStat {
    private static final int REDIS_DB = 14;


    public static void main(String[] args) {
        Jedis jedis = getSourceLevelClient();
        if (jedis == null) {
            return;
        }

        jedis.select(REDIS_DB);

        Set<String> keys = jedis.keys("*");

        Map<String, List<Double>> levelScoreMap = new HashMap<>();
        for (String key : keys) {
            try {
                List<String> result = jedis.hmget(key, "evalLevel", "evalScore");

                String level = result.get(0);
                Double score = Double.valueOf(result.get(1));

                if (levelScoreMap.containsKey(level)) {
                    levelScoreMap.get(level).add(score);
                } else {
                    levelScoreMap.put(level, new ArrayList<>());
                    levelScoreMap.get(level).add(score);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        for (Map.Entry<String, List<Double>> entry : levelScoreMap.entrySet()) {
            String level = entry.getKey();
            List<Double> scores = entry.getValue();

            int num = scores.size();
            double sumScore = 0d;

            for (Double score : scores) {
                sumScore += score;
            }


            System.out.println("util:" + level + " num:" + num + " sumScore:" + sumScore + " avgScore:" + sumScore / num);
        }
    }
}
