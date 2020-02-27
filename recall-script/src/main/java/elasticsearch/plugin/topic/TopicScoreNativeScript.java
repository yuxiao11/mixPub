package elasticsearch.plugin.topic;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.ScriptPlugin;
import org.elasticsearch.script.AbstractDoubleSearchScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * elasticsearch: Function Score Query, script_score,  Native (Java) Scripts
 * https://www.elastic.co/guide/en/elasticsearch/reference/5.4/modules-scripting-native.html
 * 自定义的评分脚本，使用native script方式，作为plugin安装到es中:https://www.elastic.co/guide/en/elasticsearch/plugins/5.4/plugin-authors.html
 * <p>
 */

public class TopicScoreNativeScript extends Plugin implements ScriptPlugin {

    @Override
    public List<NativeScriptFactory> getNativeScripts() {
        return Collections.singletonList(new MyNativeScriptFactory());
    }

    public static class MyNativeScriptFactory implements NativeScriptFactory {
        @Override
        public ExecutableScript newScript(@Nullable Map<String, Object> params) {
            return new MyNativeScript(params);
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        @Override
        public String getName() {
            return "topic-score-script";
        }
    }

    public static class MyNativeScript extends AbstractDoubleSearchScript {

        private Map<String, Object> inputTags;

        public MyNativeScript(Map<String, Object> inputTags) {
            this.inputTags = inputTags;
        }

        @Override
        public double runAsDouble() {
            double rt = 0.5;
            double score = 0.01;
            try {
                if (source().containsKey("topic3")) {
                    String topic3 = (String) source().get("topic3");

                    Map<String, Double> topicScoreMap = parseTopicStr(topic3);

                    for (String tagName : inputTags.keySet()) {
                        if (topicScoreMap.containsKey(tagName)) {
                            double inputTagScore = (double) inputTags.get(tagName);
                            double docTagScore = topicScoreMap.get(tagName);

                            score += inputTagScore * docTagScore;
                        }
                    }
                }
                return score;
            } catch (Exception e) {
                e.printStackTrace();
                return 0.01;
            }
        }
    }

    public static Map<String, Double> parseTopicStr(String topicStr) {
        Map<String, Double> topicScore = new HashMap<>();
        try {
            String[] topicArr = topicStr.split(" ");
            for (String topic : topicArr) {
                try {
                    String[] nameScore = topic.split("\\^");
                    topicScore.put(nameScore[0], Double.valueOf(nameScore[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(topicStr);
        }
        return topicScore;
    }

    public static void main(String[] args) {

        System.out.println(parseTopicStr("嘉实基金^0.5 暴跌^1.0 加息^0.5 内在价值^0.5 亚太股市^1.0 恒指^1.0 恒生指数^0.5 美股^0.5 全线^0.5"));
    }
}