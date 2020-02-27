package com.ifeng.recom.mixrecall.common.util.bloomfilter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LocalBloomFilter  {
	
	private static final Logger LOG = LoggerFactory.getLogger(LocalBloomFilter.class);
	
	private static MasterSlaveBloomFilter masterSlaveBloomFilter;

	private static final String configFileDir= "/data/prod/service/mix-recall/output/bloomdump/";
	
	static {
        masterSlaveBloomFilter = new MasterSlaveBloomFilter(configFileDir);
	}

	public static boolean checkIsInAndPut(String uid, String docID) {
		return masterSlaveBloomFilter.checkIsInAndPut(uid, docID);
	}

	/**
	 * 如果已经存在 则返回true
	 * @param uid
	 * @param docID
	 * @return
	 */
	public static boolean onlyCheck(String uid, String docID) {
		return masterSlaveBloomFilter.onlyCheck(uid,docID);
	}

	/**
	 * 将展现结果插入布隆过滤器
	 * 运维监控的测uid都是以 _test结尾，如果以 _test结尾，则不走布隆过滤器
	 * @param uid
	 * @param docID
	 */
	public static void onlyPut(String uid, String docID) {
		masterSlaveBloomFilter.onlyPut(uid,docID);
	}

}
