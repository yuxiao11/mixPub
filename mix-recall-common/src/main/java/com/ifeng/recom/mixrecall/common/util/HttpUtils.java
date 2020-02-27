package com.ifeng.recom.mixrecall.common.util;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 处理post请求
     *
     * @param url
     * @param postParam
     * @param timeout
     * @return
     */
    public static String httpPost(String url, Map<String, String> postParam, int timeout) {
        org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
        PostMethod method = new PostMethod(url);
        try {

            method.getParams().setParameter("http.socket.timeout", new Integer(timeout));
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);

			/* 参数处理 */
            for (Map.Entry<String, String> entry : postParam.entrySet()) {
                method.addParameter(entry.getKey(), entry.getValue());
            }

            method.getParams().setContentCharset("UTF-8");
            String urltest;
            int statusCode = httpClient.executeMethod(method);
            if (statusCode == HttpStatus.SC_OK) {
                String responseStr = new String(method.getResponseBody(),
                        "UTF-8");
                return responseStr;
            } else {
                String responseStr = new String(method.getResponseBody(),
                        "UTF-8");
                urltest = url;
                for (Map.Entry<String, String> entry : postParam.entrySet()) {
                    urltest = urltest.concat("&").concat(entry.getKey()).concat("=").concat(entry.getValue());
                }
//                logger.info("urltest:\n" + urltest);
//                logger.info("statusCode:" + statusCode + "responseStr:" + responseStr);
            }
        } catch (Exception e) {
            logger.error("post error url:" + url + " error:" + e);
        } finally {
            method.releaseConnection();
        }
        return "";
    }

    public static String doPostDefault(String url, String postData, int connectionTimeout, int readTimeOut) throws Exception {
        DataInputStream inputStream = null;
        HttpURLConnection con = null;
        DataOutputStream outputStream = null;
        try {
            URL dataUrl = new URL(url);
            con = (HttpURLConnection) dataUrl.openConnection();
            con.setConnectTimeout(connectionTimeout);
            con.setReadTimeout(readTimeOut);
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            outputStream = new DataOutputStream(con.getOutputStream());
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException e) {
            logger.error("http post error " + e);
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (Exception e) {
                logger.error("http post error " + e);
            }
        }

        try {
            inputStream = new DataInputStream(con.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();

        } catch (IOException e) {
            logger.error("http post error " + e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (Exception e) {
                logger.error("http post error " + e);
            }
        }
        return "";
    }

    public static String doGet(String url, int connectionTimeout, int readTimeOut, Map<String, String> requestProperties) throws Exception {
        HttpURLConnection con;

        try {
            StringBuilder requestBuilder = new StringBuilder(url);
            requestBuilder.append("?");
            for (Map.Entry entry : requestProperties.entrySet()) {
                requestBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), "UTF-8")).append("&");
            }

            URL dataUrl = new URL(requestBuilder.toString());
            con = (HttpURLConnection) dataUrl.openConnection();
            for (String key : requestProperties.keySet()) {
                con.setRequestProperty(key, requestProperties.get(key));
            }
            con.setRequestMethod("GET");
            con.setConnectTimeout(connectionTimeout);
            con.setReadTimeout(readTimeOut);
            con.setRequestProperty("accept", "*/*");
            con.setRequestProperty("connection", "Keep-Alive");
            con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            con.connect();

//            Map<String, List<String>> map = con.getHeaderFields();
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw e;
        }
    }

    public static String doGet(String url, int connectionTimeout, int readTimeOut, String proxyIp, String proxyPort, Map<String, String> requestProperties) throws Exception {
        InputStream inputStream = null;
        HttpURLConnection con = null;

        try {
            URL dataUrl = new URL(url);
            Proxy proxy = null;
            if (proxyIp != null && proxyPort != null) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(proxyIp), Integer.valueOf(proxyPort)));
            }


            if (proxy != null) {
                con = (HttpURLConnection) dataUrl.openConnection(proxy);
            } else {
                con = (HttpURLConnection) dataUrl.openConnection();
            }
            con.setUseCaches(false);
            if (requestProperties != null) {
                for (String key : requestProperties.keySet()) {
                    con.setRequestProperty(key, requestProperties.get(key));
                }
            }


            con.setConnectTimeout(connectionTimeout);
            con.setReadTimeout(readTimeOut);
            con.setDoOutput(true);
            con.setDoInput(true);
            String coding = con.getContentEncoding();
            if (coding != null && coding.equals("gzip")) {
                inputStream = new GZIPInputStream(con.getInputStream());
            } else {
                inputStream = new DataInputStream(con.getInputStream());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
