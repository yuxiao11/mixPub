package com.ifeng.recom.mixrecall.common.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static PoolingHttpClientConnectionManager poolConnManager;
    private static CloseableHttpClient httpClient = null;
    private final static Object syncLock = new Object();
    private static RequestConfig config = null;

    static {
        initPoolCm();
    }

    private static void initPoolCm() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(50);
        cm.closeIdleConnections(100, TimeUnit.SECONDS);

        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(2000)
                .setSoKeepAlive(true) //开启监视TCP连接是否有效
                .build();
        cm.setDefaultSocketConfig(socketConfig);


        poolConnManager = cm;
        config = RequestConfig.custom()
                .setConnectionRequestTimeout(300)
                .setConnectTimeout(300)
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .setSocketTimeout(300)
                .build();
    }

    private static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = HttpClients.custom()
                            .setConnectionManager(poolConnManager)
                            .setDefaultRequestConfig(config)
                            .build();
                }
            }
        }
        return httpClient;
    }

    public static String httpPost(String url, List<NameValuePair> data, int timeout) {
        CloseableHttpClient httpClient = getHttpClient();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));
            response = httpClient.execute(httpPost);
            if (response != null) {
                String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                return responseStr;
            }
        } catch (IOException e) {
            logger.error("http post error, url:" + url + " " + e.toString(), e);
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("http response close error, url:" + url + " " + e.toString(), e);
                }
            }
        }
        return null;
    }

    public static String httpGet(String url) {
        HttpGet httpget = new HttpGet(url);
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(entity);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    /**
     * 转换map到NameValuePair
     *
     * @param postParam
     * @return
     */
    public static List<NameValuePair> transMapToPairs(Map<String, String> postParam) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : postParam.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return nameValuePairs;
    }

    public static void checkPoolStatus() {
        logger.debug("http pool stat:" + poolConnManager.getTotalStats().toString());
//        logger.debug("http pool stat:" + poolConnManager.getRoutes());

        Set<HttpRoute> httpRoutes = poolConnManager.getRoutes();
        for (HttpRoute httpRoute : httpRoutes) {
            logger.debug("http pool stat:" + httpRoute.toString() + " " + poolConnManager.getStats(httpRoute));
        }
    }

    public static void main(String[] args) {
        String url = "http://local.recom.bloom.collector1.ifengidc.com/filter/check?uid=865297031580893";

        Map<String, String> map = new HashMap<>();
        map.put("uid", "865297031580893");
        map.put("simids", "clusterId_41853889,clusterId_20006415");

        String rt = httpPost(url, transMapToPairs(map), 500);
        String rt1 = httpPost(url, transMapToPairs(map), 500);
        String rt2 = httpPost(url, transMapToPairs(map), 500);

        System.out.println(rt);
        System.out.println(rt2);
        System.out.println(rt1);

        checkPoolStatus();


    }
}