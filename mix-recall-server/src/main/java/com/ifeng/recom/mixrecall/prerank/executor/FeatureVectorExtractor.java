package com.ifeng.recom.mixrecall.prerank.executor;

import com.ifeng.recom.mixrecall.prerank.FeatureItem;
import com.ifeng.recom.mixrecall.prerank.tools.FeatureConfigParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ifeng.recom.mixrecall.prerank.entity.FeatureContext;
import com.ifeng.recom.mixrecall.prerank.entity.Feature;
import java.io.*;
import java.util.*;

/**
 * 
 * @author 58
 * 注：实验框架传入模型名称一定要和配置文件名称一致
 */
public class FeatureVectorExtractor implements Serializable {
	private static final long serialVersionUID = -5119447037917245813L;
	private static final Log LOG = LogFactory.getLog(FeatureVectorExtractor.class);

	private static Map<String,List<Feature>> modelSceneFeatureListmap = new HashMap<String, List<Feature>>();	//根据业务场景，存放特征list

	public FeatureVectorExtractor(){
		
	}

	/**
	 * MR作业，根据传入Hbase获取到的每条特征Map，进行特征抽取
	 * @param featureMap
	 * @param reg
	 * @return
	 */

	/**
	 * 根据待抽取的特征列表，进行特征抽取
	 * @param featureContext
	 * @param reg 实验框架传入的特征配置文件名
	 * @return
	 */
	public static List<FeatureItem> extractor(FeatureContext featureContext, String reg){
		return extractor(featureContext, reg, "LR");
	}

	/**
	 * 根据待抽取的特征列表，进行特征抽取
	 * @param featureContext
	 * @param reg 实验框架传入的特征配置文件名
	 * @return
	 */
	public static List<FeatureItem> extractor(FeatureContext featureContext, String reg, String modelType){
		if(featureContext==null){
			LOG.error("featureContext is null:" + featureContext);
			return null;
		}
		List<Feature> featureList=null;
		if(modelSceneFeatureListmap.containsKey(reg)){
			featureList = modelSceneFeatureListmap.get(reg);
		}
		if (featureList == null || featureList.isEmpty()) {
			LOG.error("modelScene not exists:" + reg);
			//System.out.println("modelScene not exists:" + reg);
			return null;
		}
		LOG.debug("featureList size:" + featureList.size());

		//计算特征指标
		List<FeatureItem> featureItemList=new ArrayList<>();
		List<FeatureItem> sonList;
		for (Feature feature : featureList) {
			try {
				sonList = feature.getOperator().compute(featureContext, feature.getFeatureId(), feature.getFeatureName(),feature.getType(),feature.getAttrDimension());
				if (null != sonList) {
					featureItemList.addAll(sonList);
				}
			} catch (Exception e) {
				LOG.error("[cvr-feature]FeatureVectorExtractor  id:"+feature.getFeatureId()  ,e);
				//出现特征抽取异常 对该条数据做抛弃处理
				return null;
			}
		}

        return featureItemList;
	}
	
	
	/**
	 * 读取一个目录下面的所有特征配置文件(线上ctr工程调用)；
	 * @param configDirs
	 * @return
	 * @throws IOException
	 * @throws FeatureConfigException
	 */
	public static boolean loadFeatureConfigs(String configDirs)
            throws IOException, FeatureConfigException {
		File dir = new File(configDirs);
        if(!dir.isDirectory()) {
            throw new FileNotFoundException("模型特征配置文件目录不是一个目录: " + configDirs);
        }

        //只支持一层目录, 如果想要支持多层, 还需要区分不同层的同名文件
        File[] configFiles = dir.listFiles();

        modelSceneFeatureListmap = new HashMap<String, List<Feature>>();

        for(File configFile: configFiles) {
        	if (!configFile.getName().endsWith(".properties")) {
        		LOG.error("Feature ConfigDir contains illegal file:" + configFile.getName());
        		continue;
        	}
        	modelSceneFeatureListmap.put(configFile.getName().split("\\.")[0], FeatureConfigParser.loadFeatureConfig(configFile.toString()));
        }
        LOG.debug("FeatureVectorExtractor loadFeatureConfigs keySet:" + modelSceneFeatureListmap.keySet().toString());
        LOG.debug("FeatureVectorExtractor loadFeatureConfigs featureList:" + modelSceneFeatureListmap.toString());
        return true;
	}
	
	
	/**
	 * 按照场景初始化特征配置文件；(离线加载单模型调用)
	 * @param featureConfigpath
	 * @param reg 模型名称
	 * @throws IOException
	 * @throws FeatureConfigException
	 */
	public static void initialize(String featureConfigpath, String reg) {		
		
		try{
			LOG.info( "解析特征工程配置文件......" );
			modelSceneFeatureListmap=FeatureConfigParser.loadFeatureConfigs(featureConfigpath, reg);
			LOG.info( "解析特征工程配置文件完毕......" );
		}catch(Exception e){
			LOG.fatal( "特征工程配置文件解析错误，请重新检查格式",e);
			System.exit(-1);
		}
    }
	
	////////////////////////////////////////////////////////////////////////////////////
	                         /* 在hdfs或配置文件数据流离线加载特征配置用 */
	////////////////////////////////////////////////////////////////////////////////////
	
	
//	/**
//	 * 按照场景初始化特征配置文件；(离线Job调用)
//	 * @param confiKey 配置名字
//	 * @param featureListConfigFile 配置文件hdfs路径
//	 */
//	public static void initializeOffline(String confiKey,String featureListConfigFile) {
//
//		try{
//			LOG.info( "解析特征工程配置文件......" );
//			modelSceneFeatureListmap.put(confiKey, FeatureConfigPaserOffline.loadFeatureConfig(featureListConfigFile));
//			LOG.info( "解析特征工程配置文件完毕......" );
//		}catch(Exception e){
//			LOG.fatal( "特征工程配置文件解析错误，请重新检查格式",e);
//			System.exit(-1);
//		}
//    }
//
//	/**
//	 * 按照场景加载指标特征配置文件；(离线Job调用)
//	 * @param confiKey 离线实验名称offline
//	 * @param inputStream 配置文件数据流
//	 * @throws IOException
//	 */
//	public static void initializeOffline(String confiKey,InputStream inputStream) throws IOException {
////		if( modelSceneFeatureList != null && modelSceneFeatureList.size() != 0 ){
////			modelSceneFeatureList.clear();
////		}
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		byte[] buffer = new byte[10240];
//		int len;
//		while ((len = inputStream.read(buffer)) > -1) {
//			baos.write(buffer, 0, len);
//		}
//		baos.flush();
//		try{
//			LOG.info( "解析特征工程配置文件......" );
//			modelSceneFeatureListmap.put(confiKey, FeatureConfigPaserOffline.loadFeatureConfig(new ByteArrayInputStream(baos.toByteArray())));
//
//			LOG.info( "解析特征工程配置文件完毕......" );
//		}catch(Exception e){
//			LOG.fatal( "特征工程配置文件解析错误，请重新检查格式",e);
//			System.exit(-1);
//		}
//    }


//	/****
//	 * ***************************************************************************
//	 * 下面的函数均只用于离线Spark特征值抽取（由于Spark无法序列化静态变量） By zhaohh
//	 * ***************************************************************************
//	 */
//	private Map<String,List<Feature>> keyFeatureListmap = new HashMap<String, List<Feature>>();	//根据业务场景，存放特征list
//	public Map<String,List<Feature>> getKeyFeatureListmap() {
//		return keyFeatureListmap;
//	}
//	/**
//	 * 按照场景加载指标特征配置文件；(离线Job调用)
//	 * @param confiKey 离线实验名称offline
//	 * @param inputStream 配置文件数据流
//	 * @throws IOException
//	 */
//	public void initOffline(String confiKey,InputStream inputStream) throws IOException {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		byte[] buffer = new byte[10240];
//		int len;
//		while ((len = inputStream.read(buffer)) > -1) {
//			baos.write(buffer, 0, len);
//		}
//		baos.flush();
//		try{
//			LOG.info( "解析特征工程配置文件......" );
//			this.keyFeatureListmap.put(confiKey, FeatureConfigPaserOffline.loadFeatureConfig(new ByteArrayInputStream(baos.toByteArray())));
//			LOG.info( "解析特征工程配置文件完毕......" );
//		}catch(Exception e){
//			LOG.fatal( "特征工程配置文件解析错误，请重新检查格式",e);
//			System.exit(-1);
//		}
//	}


	//TEST
	public static void main(String[] args) throws IOException, FeatureConfigException {

    }

}
