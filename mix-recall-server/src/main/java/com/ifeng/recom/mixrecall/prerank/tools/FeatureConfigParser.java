package com.ifeng.recom.mixrecall.prerank.tools;

import com.ifeng.recom.mixrecall.negative.StringUtils;
import com.ifeng.recom.mixrecall.prerank.entity.Feature;
import com.ifeng.recom.mixrecall.prerank.executor.FeatureConfigException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;


import java.io.*;
import java.util.*;


public class FeatureConfigParser {
	
	protected static final Log LOG = LogFactory.getLog(FeatureConfigParser.class);
	
	/**
	 * 加载特征配置文件，详细特征内容
	 * @param configfile
	 * @return
	 * @throws IOException
	 * @throws FeatureConfigException
	 */
	@SuppressWarnings("resource")
	public static List<Feature> loadFeatureConfig(String configfile)
            throws IOException, FeatureConfigException {
    	File configFile=new File(configfile);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
        List<Feature> features = new ArrayList<Feature>();
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                if(!StringUtils.isEmptyOrComment(line)) {
                    try {
						LOG.debug("line:"+line);
                    	Feature feature = Feature.parse(line);
                        if(null != feature && feature.getStatus()){
                        	features.add(feature);
                        }
                    }catch (Exception e) {
                    	e.printStackTrace();
                    	LOG.error("特征配置初始化失败: "+"line: " + line, e);
                    	throw new FeatureConfigException("特征配置初始化失败: "+"line: " + line, e);
                    }
                }
            }
            br.close();
            return features;
        } catch (Exception e) {
        	 throw new FeatureConfigException("[feature]配置文件初始化错误.", e);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	 }
	
	
	/**
	 * 读取特征的配置文件；
	 * @param featureConfigpath
	 * @param reg 特征对应模型名字
	 * @return
	 * @throws IOException
	 * @throws FeatureConfigException
	 */
	public static Map<String,List<Feature>> loadFeatureConfigs(String featureConfigpath, String reg)
            throws IOException, FeatureConfigException {
		File file = new File(featureConfigpath);
        if(!file.exists()) {
        	LOG.error("模型特征配置文件不存在:" + featureConfigpath);
            throw new FileNotFoundException("模型特征配置文件不存在: " + featureConfigpath);
        }

        Map<String, List<Feature>> proxies = new HashMap<String, List<Feature>>();

        proxies.put(reg, loadFeatureConfig(featureConfigpath));
        return proxies;
	}

	
	/**
	 * 连续分段函数的初始化
	 * @param fis
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, ArrayList<Double>> get_Lx_valueStr2IdMap(InputStream fis) throws IOException {

		HashMap<String, ArrayList<Double>> resultMap = new HashMap<String, ArrayList<Double>>();

		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			int length = line.length();
			if( length < 2){
				continue;
			}
			String[] items = line.trim().split(",");
			if (null == items || items.length < 2) {
				continue;
			}
			
			String setId = items[0].trim();
			ArrayList<Double> midList = new ArrayList<Double>();
			String[] valueStrs = items[1].trim().split("\\|");
			for(String valueStr:valueStrs){
				midList.add(Double.parseDouble(valueStr));
			}
			Collections.sort(midList);
			ArrayList<Double> tmpList = new ArrayList<Double>();
			for(int i = 0; i < midList.size(); i++) {
				tmpList.add(midList.get(i));
			}			
			resultMap.put(setId, tmpList);
		}
		br.close();
		return  resultMap;
	}

	
	 public static void main(String[] args){
//	    	try{
//	    		Map<String, List<Feature>> a=loadFeatureConfigs(FmConstant.featureListConfigDir);
//
//	    		System.out.println(a);
//	    		System.out.println(FmConstant.fileDirectory);
//	    		System.out.println(a.keySet().toArray()[1]);
//	    		System.out.println(a.keySet().toArray()[0]);
//	    		System.out.println(a.get(a.keySet().toArray()[0]).get(0).getAttrDimension());
//	    	}catch(Exception e){
//	    		e.printStackTrace();
//	    	}
	    	
	    	
	    }

}
