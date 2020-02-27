package com.ifeng.recom.mixrecall.common.dao.elastic;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;


import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by geyl on 2017/7/5.
 */
public class EsClientFactory {
    private static Settings settings;
    private static TransportClient client;

    static {
        initEsClient();
    }

    private static void initEsClient() {
        settings = Settings.builder().put("cluster.name", "recom").build();
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.80.85.139"), 9300))
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.80.71.142"), 9300))
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.80.87.139"), 9300))
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.80.77.140"), 9300))
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.80.88.140"), 9300))
            ;
        } catch (UnknownHostException e) {
            System.out.println("connect to es cluster error: " + e);
        }
    }

    public static TransportClient getClient() {
        if (client == null) {
            System.out.println("get es client error, client is null");
            return null;
        } else {
            return client;
        }
    }

    public static Settings getSettings() {
        if (client == null) {
            System.out.println("get es setting error, client is null");
            return null;
        } else {
            return settings;
        }
    }

    public static void main(String[] args) {

        System.out.println(client.listedNodes());

        GetResponse response = client.prepareGet("preload-news", "_doc", "69533996").get();

        System.out.println(response.getSourceAsString());
    }
}