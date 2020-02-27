package com.ifeng.recom.mixrecall.negative;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static boolean isEmptyOrComment(String line) {
		line = line.trim();

		if (line.length() == 0) {
			return true;
		}
		if (line.startsWith("#")){
			return true;
		}
		return false;
	}

	public static boolean isEmpty(String str) {
		return (str == null) || (str.trim().length() == 0)
				|| (str.equals("\\N")) || (str.equals("-"))
				|| (str.equals("null"));
	}

	public static boolean isNum(String str) {
		return (str != null) && (str.trim().length() > 0)
				&& (str.trim().matches("\\d{1,}"));
	}

	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isDouble(String str) {
		if ((str == null) || (str.trim().length() == 0)) {
			return false;
		}
		if (str.contains(".")) {
			return Pattern
					.compile("([1-9]+[0-9]*|0)(\\.[\\d]+)?").matcher(str)
					.matches();
		}
		return str.trim().matches("\\d{1,}");
	}


	public static Object getByKey(Map<Object, Object> m, Object key,
			Object defaultvalue) {
		if (!m.containsKey(key)) {
			return defaultvalue;
		}
		return m.get(key);
	}

	public static String replaceSpecialSymbol(String str) {
		if ((str == null) || str.isEmpty()) {
			return null;
		}
		if (str.contains("\r")) {
			str = str.replaceAll("\r", "");
		}
		if (str.contains("\n")) {
			str = str.replaceAll("\n", "");
		}
		if (str.contains("\001")) {
			str = str.replaceAll("\001", "");
		}
		Pattern p = Pattern.compile("\\\\");
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		str = sb.toString();
		return str;
	}

	public static String isnull(String src, String defaultvalue) {
		if ((src == null) || (src.trim().equals("")) || (src.equals("\\N"))
				|| (src.equals("-")) || (src.equals("null"))) {
			return defaultvalue;
		}
		return src;
	}

	public static int parseInt(String src, int defaultvalue) {
		if (isNum(src)) {
			return Integer.parseInt(src);
		}
		return defaultvalue;
	}

	public static long parseLong(String src, long defaultvalue) {
		if (isNum(src)) {
			return Long.parseLong(src);
		}
		return defaultvalue;
	}

	public static byte parseByte(String src, byte defaultvalue) {
		if (isNum(src)) {
			return Byte.parseByte(src);
		}
		return defaultvalue;
	}

	public static double parseDouble(String src, double defaultvalue) {
		if (isDouble(src)) {
			return Double.parseDouble(src);
		}
		return defaultvalue;
	}

	public static String findMarach(String partten, String content) {
		Matcher m2 = Pattern.compile(partten,
				34).matcher(content);

		String strret = "-";
		if (m2.find()) {
			strret = m2.group(1);
		}

		return strret;
	}

	public static java.util.List<String> findAllMarach(String partten,
			String content) {
		Matcher m2 = Pattern.compile(partten,
				34).matcher(content);

		java.util.List<String> list = new java.util.ArrayList<String>();
		while (m2.find()) {
			list.add(m2.group(1));
		}
		return list;
	}

	public static String replaceIllegalChar(String inputString) {
		if ((inputString == null) || (inputString.length() == 0)) {
			return "";
		}

		String outputString = inputString;

		outputString = outputString.replace(" ", "");
		outputString = outputString.replace("\001", "");
		outputString = outputString.replace("\r", "");
		outputString = outputString.replace("\n", "");

		return outputString;
	}


	/**
	 * 防止在分段聚类中 有冲突
	 * @param attr
	 * @return
	 */
	public static String replaceIllgeSymbol(String attr) {
		if (attr == null || attr.isEmpty() || attr.equals("-")) {
			return attr;
		}
		if (attr.contains(",")) {
			attr = attr.replaceAll(",", "_");
		}
		if (attr.contains("|")) {
			attr = attr.replaceAll("\\|", "_");
		}
		return attr;
	}

	/**
	 * 空值判断
	 * 
	 * @param attr
	 * @return
	 */
	public static boolean isNullVal(String attr) {
		if (attr == null || attr.equals("\\N") || attr.equals("-")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否包含数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean hasDigital(String str) {
		Pattern pattern = Pattern.compile(".*\\d+.*");
		Matcher matcher = pattern.matcher((CharSequence) str);
		return matcher.matches();
	}

	/**
	 * 计算两个字符串的Levenshtein距离
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static int calLevenshtein(String str1, String str2) {
		
		if (null == str1 || null == str2) {
			return -1;
		}
		
		int len1 = str1.length();
		int len2 = str2.length();
		
		if (0 == len1) {
			return len2;
		}
		if (0 == len2) {
			return len1;
		}
		
		int[][] matrix = new int[len1+1][len2+1];
		
		int i, j;
		for (i = 0; i < len1+1; i++) {
			matrix[i][0] = i;
		}
		for (j = 0; j < len2+1; j++) {
			matrix[0][j] = j;
		}
		
		for (i = 1; i < len1+1; i++) {
			for (j = 1; j < len2+1; j++) {
				if (str1.charAt(i-1) == str2.charAt(j-1)) {
					matrix[i][j] = Math.min(Math.min(matrix[i-1][j]+1, matrix[i][j-1]+1), matrix[i-1][j-1]);
				} else { 
					matrix[i][j] = Math.min(Math.min(matrix[i-1][j]+1, matrix[i][j-1]+1), matrix[i-1][j-1]+1);
				}
			}
		}
		
		return matrix[len1][len2];
	}
	

	
	/**
	 * 计算两个字符串的相似度
	 * @param sent1
	 * @param sent2
	 * @return
	 */
	public static double calSentSimilarity(String sent1, String sent2) {
		if (null == sent1 || null == sent2 || sent1.isEmpty() || sent2.isEmpty()) {
			return 0;
		}
		double ret = 1 - 1.0 * calLevenshtein(sent1, sent2) / Math.max(sent1.length(), sent2.length());
		return ret;
	}
	
	/**
	 * 将byte数组转化为
	 * @param data
	 * @return
	 */
	public static String convertToHexString(byte data[]) {
		  StringBuffer strBuffer = new StringBuffer();
		  for (int i = 0; i < data.length; i++) {
		   strBuffer.append(Integer.toHexString(0xff & data[i]));
		  }
		  return strBuffer.toString();
	}
	
	/**
	 * 
	 * @param str
	 * @return 出现异常返回-1，正常情况返回long的绝对值，2的31次幂个正数。
	 * @战硕 若字符长度大于58位，会跑异常
	 */
	public static long str2long(String str){
		if( null == str ){
			return -1l;
		}
		long result = -1l;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("md5");
			md.update(str.getBytes("utf8"));
			byte[] data = md.digest();
			String hexStr = convertToHexString(data);
			result = hashCodeAbsLong(hexStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("字符转化异常：" + e.toString());
		}
		return result;
	}
	/**
	 * 将0-9、a-f组成的字符串转化为无负数的long
	 * @param value
	 * @return
	 */
	private static long hashCodeAbsLong(String value) {
		long h = 0l;
        if (h == 0l && value.length() > 0) {

            for (int i = 0; i < value.length(); i++) {
                h = 31 * h + value.charAt(i);
            }
        }
        return Math.abs(h);
    }
	
	/**
	 * 对薪资范围参数进行处理
	 * @param valueStr
	 * @return
	 */
	public static String getUniqAdsalary(String valueStr) {
		if (valueStr == null || valueStr.isEmpty()) {
			return valueStr;
		}
		if (valueStr.contains("面议")) {
			valueStr = "面议";
		} else if (valueStr.contains("-")) {
			valueStr = valueStr.replace("-", "_");
		}
		return valueStr;
	}
	
	/**
	 * 福利保障特殊处理：保证 1|2  和 2|1 相同  (10种福利任意组合，保证有序)
	 * @param valueStr
	 * @return
	 */
	public static String getUniqFuliBaoZhang(String valueStr) {
		if (valueStr == null || valueStr.isEmpty() || !valueStr.contains("|")) {
			return valueStr;
		}
		StringBuffer res = new StringBuffer();
		String[] vals = valueStr.split("\\|");
		Arrays.sort(vals);
		for (String val: vals) {
			res.append(val);
			res.append("_");
		}
		return res.toString().substring(0, res.toString().length()-1);
	}

	/**
	 * 判断字符串是否为空
	 * @return
	 */
	public static boolean isNullString(String str) {
		if (str == null || str.isEmpty() || str.equals("-") || str.toLowerCase().equals("null")
		  || str.toLowerCase().equals("\\n") || str.trim().length() == 0)
			return true;
		else
			return false;
	}

	public static void main(String[] args) {
		System.out.println(str2long("科技_数码"));
		System.out.println(str2long("科技_IT"));
	}
	
}
