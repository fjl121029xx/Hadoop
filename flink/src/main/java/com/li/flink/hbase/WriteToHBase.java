package com.li.flink.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;

public class WriteToHBase {


    private static String hbaseZookeeperQuorum = "huatu68,huatu70,huatu72";
    private static String hbaseZookeeperClinentPort = "2181";
    private static TableName tableName = TableName.valueOf("t_f_videoplay");
    private static final String columnFamily = "playinfo";


    public static void main(String[] args) {


    }

    public static void writeIntoHBase(String m) throws Exception {


        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", hbaseZookeeperQuorum);
        config.set("hbase.master", "huatu68:16000");
        config.set("hbase.zookeeper.property.clientPort", hbaseZookeeperClinentPort);
        config.setInt("hbase.rpc.timeout", 20000);
        config.setInt("hbase.client.operation.timeout", 30000);
        config.setInt("hbase.client.scanner.timeout.period", 200000);



    }

}
