package com.ifeng.recom.mixrecall.prerank.constant;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constant {
	
	public static String FEATURE_SPLIT_TAG="\001";
	public static String FEATURE_VALUE_TAG="-";
	public static String FEATURE_SPLIT="\t";
	
	public static String NULLVAL = "-1";
	
	
	public static String GENERAL_FEATURE_PACKAGE="recom.rank.ctr.feature.headlines";

	public static int FEATURE_LIST_THRESHOLD = -1;

//	/**
//	 * 获取当前目录
//	 */
//	public static String fileDirectory =  Path.getCurrentPath();
//
//	/**
//	 * 配置文档所在路径
//	 */
//	public static String featureConfigFile = fileDirectory + "/feature_config.conf";
//	public static String featureListConfigFile = fileDirectory + "/featureList_config.conf";
//	public static String featureListConfigDir = fileDirectory + "/feature_config";

	public static final String USER_T1_DICT_REDIS_KEY = "user_t1_dict_redis_key";
	public static final String USER_T2_DICT_REDIS_KEY = "user_t2_dict_redis_key";
	public static final String USER_SUB_DICT_REDIS_KEY = "user_sub_dict_redis_key";
	public static final String USER_CITY_DICT_REDIS_KEY = "user_city_dict_redis_key";

	public static final String ITEM_WORD_DICT_REDIS_KEY = "item_word_dict_redis_key";
	public static final String ITEM_SOURCE_DICT_REDIS_KEY = "item_source_dict_redis_key";


	public static final Set<String> MAIN_CITY = new HashSet<>(Arrays.asList("北京市", "上海市", "广州市", "深圳市", "成都市",
			"天津市", "武汉市", "西安市", "重庆市", "杭州市", "苏州市", "南京市", "长沙市", "郑州市", "佛山市", "青岛市", "沈阳市",
			"昆明市", "福州市", "东莞市", "无锡市", "泉州市", "合肥市", "宁波市", "哈尔滨市", "南宁市", "大连市", "石家庄市",
			"济南市", "太原市", "长春市", "厦门市", "温州市", "乌鲁木齐市", "南昌市", "惠州市", "贵阳市", "潍坊市", "烟台市",
			"兰州市", "广东省", "保定市", "南通市", "金华市", "海口市", "常州市", "廊坊市", "呼和浩特市", "台州市", "嘉兴市",
			"汕头市", "珠海市", "唐山市", "徐州市", "赣州市", "绍兴市", "中山市", "洛阳市", "扬州市", "临沂市", "江门市", "咸阳市",
			"包头市", "济宁市", "盐城市", "襄阳市", "邯郸市", "南阳市", "岳阳市", "衡阳市", "泰安市", "镇江市", "湛江市", "吉林市",
			"绵阳市", "淄博市", "宜昌市", "柳州市", "常德市", "桂林市", "三亚市", "荆州市", "银川市", "漳州市", "沧州市", "泰州市",
			"揭阳市", "西宁市", "宝鸡市", "芜湖市", "株洲市", "遵义市", "黄冈市", "大同市", "湖州市", "孝感市", "威海市", "九江市",
			"张家口市", "信阳市", "鞍山市", "莆田市", "南充市", "梅州市", "邢台市", "郴州市", "淮安市", "湘潭市", "安庆市", "新乡市",
			"榆林市", "德阳市", "邵阳市", "菏泽市", "大庆市", "渭南市", "肇庆市", "晋中市", "连云港市", "秦皇岛市", "清远市",
			"聊城市", "商丘市", "平顶山市", "德州市", "龙岩市", "驻马店市", "玉林市", "鄂尔多斯市", "潮州市", "上饶市", "运城市",
			"茂名市", "临汾市", "东营市", "韶关市", "赤峰市", "安阳市", "滁州市", "宜春市", "抚顺市", "十堰市", "六安市", "阜阳市",
			"丹东市", "枣庄市", "马鞍山市", "吉安市", "许昌市", "益阳市", "宁德市", "汉中市", "怀化市", "乐山市", "河源市",
			"永州市", "北海市", "宿迁市", "长治市", "周口市", "达州市", "呼伦贝尔市", "娄底市", "开封市", "曲靖市", "牡丹江市",
			"日照市", "齐齐哈尔市", "黄石市", "蚌埠市", "吕梁市", "锦州市", "衡水市", "焦作市", "滨州市", "南平市", "海南省",
			"汕尾市", "恩施土家族苗族自治州", "荆门市", "延边朝鲜族自治州", "三明市", "巴音郭楞蒙古自治州", "伊犁哈萨克自治州",
			"红河哈尼族彝族自治州", "营口市", "宜宾市", "宣城市", "眉山市", "大理白族自治州", "忻州市", "梧州市", "贵港市", "宿州市",
			"泸州市", "承德市", "衢州市", "铁岭市", "四平市"));

	
}
