package elasticsearch.plugin.level;

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
 * <p>
 * 2018.01.30 统计
 * util:A num:1981 sumScore:17881.057450000004 avgScore:9.02627836951035
 * util:B num:1823 sumScore:11583.506039999998 avgScore:6.354089983543608
 * util:C num:54631 sumScore:245697.46304999886 avgScore:4.4974000668118626
 * util:S num:499 sumScore:4577.583559999998 avgScore:9.17351414829659
 * util:D num:147751 sumScore:537270.5891100216 avgScore:3.6363245535395468
 * util:E num:17731 sumScore:30133.416819991035 avgScore:1.6994764435164984
 */


public class SourceLevelNativeScript extends Plugin implements ScriptPlugin {
    private static final Map<String, Double> levelScoreMap = new HashMap<>();

    static {
        initLevelScoreMap();
    }

    private static void initLevelScoreMap() {
        levelScoreMap.put("A", 0.9);
        levelScoreMap.put("B", 0.6);
        levelScoreMap.put("C", 0.45);
        levelScoreMap.put("D", 0.36);
        levelScoreMap.put("E", 0.17);
        levelScoreMap.put("S", 0.9);
    }

    @Override
    public List<NativeScriptFactory> getNativeScripts() {
        return Collections.singletonList(new MyNativeScriptFactory());
    }

    public static class MyNativeScriptFactory implements NativeScriptFactory {
        @Override
        public ExecutableScript newScript(@Nullable Map<String, Object> params) {
            return new MyNativeScript();
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        @Override
        public String getName() {
            return "source-level-score-script";
        }
    }

    public static class MyNativeScript extends AbstractDoubleSearchScript {
        @Override
        public double runAsDouble() {
            double rt = 0.1;

            try {
                if (source().containsKey("level")) {
                    String level = (String) source().get("level");

                    if (levelScoreMap.containsKey(level)) {
                        return levelScoreMap.get(level);
                    }
                }
                return rt;
            } catch (Exception e) {
                e.printStackTrace();
                return 0.1;
            }
        }
    }
}