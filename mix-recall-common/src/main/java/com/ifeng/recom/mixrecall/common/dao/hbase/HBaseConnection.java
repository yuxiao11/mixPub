package com.ifeng.recom.mixrecall.common.dao.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class HBaseConnection {
    private static final String ZOOKEEPER_QURUM = "10.80.71.148,10.80.72.148,10.80.73.148,10.80.74.148,10.80.75.148";
    private static Configuration configuration = null;
    private static Connection connection = null;

    static {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", ZOOKEEPER_QURUM);
        configuration.set("hbase.client.retries.number", "1");
        configuration.set("hbase.rpc.timeout", "500");
        configuration.set("hbase.client.operation.timeout", "800");

        try {
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Connection getConnection() {
        if (connection == null) {
            synchronized (HBaseConnection.class) {
                if (connection == null) {
                    try {
                        connection = ConnectionFactory.createConnection(configuration);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return connection;
    }
}