package com.ifeng.recom.mixrecall.common.service;

import com.ifeng.recom.mixrecall.common.tool.ServiceLogUtil;
import com.ifeng.recom.mixrecall.common.util.GsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.common.collect.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * 远端的集中过滤
 * Created by geyl on 2017/10/30.
 */
public class BloomFilterClient {

    public enum Status {
        OK, ERROR
    }

    private static final Logger logger = LoggerFactory.getLogger(BloomFilterClient.class);
    private static final String URL = "http://local.recom.bloom.collector1.ifengidc.com/filter/check";
    private static final String STATUS_OK = "ok";

    private static PoolingHttpClientConnectionManager poolConnManager;
    private static CloseableHttpClient httpClient = null;
    private static RequestConfig config = null;

    static {
        poolConnManager = new PoolingHttpClientConnectionManager();
        poolConnManager.setMaxTotal(600);
        poolConnManager.setDefaultMaxPerRoute(50);
        poolConnManager.closeIdleConnections(60, TimeUnit.SECONDS);

        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(2000)
                // 开启监视TCP连接是否有效
//                .setSoKeepAlive(true)
//                .setRcvBufSize(8192)
//                .setSndBufSize(8192)
                .build();

        poolConnManager.setDefaultSocketConfig(socketConfig);
        config = RequestConfig.custom()
                .setConnectionRequestTimeout(100)
                .setConnectTimeout(100)
                .setSocketTimeout(100)
                .build();

        httpClient = HttpClients.custom()
                .setConnectionManager(BloomFilterClient.poolConnManager)
                .setDefaultRequestConfig(config)
                .setConnectionManagerShared(true)
                .build();
    }

    /**
     * 通过http获取bloom数据
     *
     * @param uid
     * @param simIds
     * @return
     */
    public static Tuple<Status, Set<String>> requestBloomFilter(String uid, Collection<String> simIds) {
        if (CollectionUtils.isEmpty(simIds)) {
            return Tuple.tuple(Status.OK, Collections.EMPTY_SET);
        }
        String url = URL + "?uid=" + uid;
        String simId = String.join(",", simIds);
        HttpPost post = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("uid", uid));
        nvps.add(new BasicNameValuePair("simids", simId));
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (Exception e) {
            logger.error("request error, uid:{}, {}", uid, e);
            return Tuple.tuple(Status.ERROR, toSet(simIds));
        }
        long start = System.currentTimeMillis();


        try (CloseableHttpResponse response = httpClient.execute(post)) {
            String rt = EntityUtils.toString(response.getEntity());
            long cost = System.currentTimeMillis() - start;
            if (cost > 50) {
                ServiceLogUtil.debug("bloom {} cost:{}, size:{}", uid, cost, simIds.size());
            }
            if (StringUtils.isBlank(rt)) {
                return Tuple.tuple(Status.ERROR, toSet(simIds));
            }
            BloomResult result = GsonUtil.json2Object(rt, BloomResult.class);
            if (STATUS_OK.equalsIgnoreCase(result.getStatus())) {
                List<String> filteredSimIds = result.getSimids();
                if (filteredSimIds == null) {
                    // 没有数据算作失败
                    return Tuple.tuple(Status.ERROR, Collections.EMPTY_SET);
                }
                return Tuple.tuple(Status.OK, new HashSet<>(filteredSimIds));
            }
        } catch (Exception e) {
            ServiceLogUtil.debug("bloome {} cost:{}, size:{}", uid, System.currentTimeMillis() - start, simIds.size());
            logger.error("request bloom error, uid:{}, e:{}", uid, e.toString());
            return Tuple.tuple(Status.ERROR, toSet(simIds));
        }
        return Tuple.tuple(Status.ERROR, toSet(simIds));
    }

    private static Set<String> toSet(Collection<String> simIds) {
        return simIds instanceof Set ? (Set<String>) simIds : new HashSet<>(simIds);
    }
}
