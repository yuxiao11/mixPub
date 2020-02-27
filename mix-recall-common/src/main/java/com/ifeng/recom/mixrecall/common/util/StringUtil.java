package com.ifeng.recom.mixrecall.common.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class StringUtil {
    private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);



    private static Pattern numberPattern = Pattern.compile("[0-9]+.*");

    /**
     * 统计数据格式化
     * 0-99999：数字显示
     * 10万-99.9万：小数点后显示一位
     * 100万-999万：数字整数+万
     * 1千万-9999万：1-9千万
     * 1亿-999亿：数字整数+亿
     *
     * @param num
     * @return
     */
    public static String statNumFormat(long num) {
        String str = "";
        if (num >= 0 && num < Math.pow(10, 5)) {
            str = NumberFormat.getInstance().format(num) + "";

        } else if (num >= Math.pow(10, 5) && num < Math.pow(10, 6)) {
            if (num / Math.pow(10, 4) % 1 == 0) {
                str = num / ((Double) Math.pow(10, 4)).intValue() + "万";
            } else {
                DecimalFormat df = new DecimalFormat("0.0");
                str = df.format(num / Math.pow(10, 4)) + "万";
            }

        } else if (num >= Math.pow(10, 6) && num < Math.pow(10, 7)) {
            str = num / ((Double) Math.pow(10, 4)).intValue() + "万";

        } else if (num >= Math.pow(10, 7) && num < Math.pow(10, 8)) {
            str = num / ((Double) Math.pow(10, 7)).intValue() + "千万";

        } else if (num >= Math.pow(10, 8)) {
            str = num / ((Double) Math.pow(10, 8)).intValue() + "亿";
        }

        return str;
    }


    /**
     * 字符串分隔转换成整形List
     *
     * @param str
     * @return
     */
    public static List<Integer> strToIntegerList(String str) {
        return strToIntegerList(str, ",");
    }

    /**
     * 字符串分隔转换成整形List
     *
     * @param str
     * @param sep
     * @return
     */
    private static List<Integer> strToIntegerList(String str, String sep) {
        List<Integer> list = new ArrayList<Integer>();
        if (StringUtils.isBlank(str)) return list;
        for (String s : str.split(sep)) {
            int i = NumberUtils.toInt(s, 0);
            if (i > 0) list.add(i);
        }
        return list;
    }

    /**
     * 字符串分隔转换成List<String>
     *
     * @param str
     * @return
     */
    public static List<String> strToStringList(String str, String sep) {
        List<String> list = Lists.newArrayList();
        if (StringUtils.isBlank(str)) {
            return list;
        }
        for (String s : str.split(sep)) {
            if (StringUtils.isNotBlank(s)) {
                list.add(s);
            }
        }
        return list;
    }


    public static String ListString2String(List<String> list) {
        StringBuilder sb = new StringBuilder(128);
        String value = "";
        if (CollectionUtils.isEmpty(list)) {
            return value;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            sb.append(list.get(i));
            if (i != (size - 1)) {
                sb.append(",");
            }
        }
        return sb.toString();

    }

    /**
     * 将lastDoc转换成 guid的List
     * <p>
     * 【正常情况】lastDoc=<111111111,010067b9>|<111111111,010067b9>|<111111111,010067b9>
     * lastDoc=<simid,acticleId>|<simid,acticleId>|<simid,acticleId>|<simid,acticleId>
     * 【异常情况】lastDoc=,,,
     *
     * @return
     */
    public static List<String> convertLastDoc2List(String lastDocStr) {
        logger.info("lastDocStr={}", lastDocStr);

        List<String> list = Lists.newArrayList();
        if (StringUtils.isBlank(lastDocStr)) {
            return list;
        }
        String[] arrayTemp = lastDocStr.split("\\|");
        if (arrayTemp == null || arrayTemp.length <= 0) {
            return list;
        }

//		(<([\w-]{1,}),[\w_]{1,}>)|([|]<([\w-]{1,}),[\w_]{1,}>)

        String value = "";
        String[] valueTemp = null;
        for (int i = 0; i < arrayTemp.length; i++) {
/////		value = arrayRemp[i].replace("<","").replace(">","");
            value = arrayTemp[i].replaceAll("[<>]", "");
//			System.out.println("value "+ i +"="+value);
            valueTemp = value.split(",");
            if (valueTemp != null && valueTemp.length > 0) {
                if (StringUtils.isNotBlank(valueTemp[1])) {
                    list.add(valueTemp[1]);
                }
            }
        }
        return list;

    }


    /**
     * 格式化数字为千分位显示；
     *
     * @param text 要格式化的数字；
     * @return
     */
    public static String fmtMicrometer(String text) {
        DecimalFormat df = null;
        if (text.indexOf(".") > 0) {
            if (text.length() - text.indexOf(".") - 1 == 0) {
                df = new DecimalFormat("###,##0.");
            } else if (text.length() - text.indexOf(".") - 1 == 1) {
                df = new DecimalFormat("###,##0.0");
            } else {
                df = new DecimalFormat("###,##0.00");
            }
        } else {
            df = new DecimalFormat("###,##0");
        }
        double number = 0.0;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }

    /**
     * 去除String中所有中英文标点
     *
     * @param text
     * @return
     */
    public static String removeStringMark(String text) {
        return text.replaceAll("[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]", "");
    }


    /**
     * 判断字符串 以数字开头
     * @param str
     * @return
     */
    public static boolean startWithNum(String str) {
        return numberPattern.matcher(str).matches();
    }


}