package com.li.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

public class HBaseDao {

    public static void main(String[] args) throws IOException {


        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum","192.168.126.138:2181");


        HBaseAdmin hBaseAdmin = new HBaseAdmin(conf);

        TableName name = TableName.valueOf("user_time_shaft_test");
        HTableDescriptor descriptor = new HTableDescriptor(name);

        HColumnDescriptor base_info = new HColumnDescriptor("base");
        base_info.setMaxVersions(1);
        descriptor.addFamily(base_info);

        HColumnDescriptor base_info2 = new HColumnDescriptor("video");
        base_info.setMaxVersions(1);
        descriptor.addFamily(base_info2);

        HColumnDescriptor base_info3 = new HColumnDescriptor("answer");
        base_info.setMaxVersions(1);
        descriptor.addFamily(base_info3);

        HColumnDescriptor base_info4 = new HColumnDescriptor("match");
        base_info.setMaxVersions(1);
        descriptor.addFamily(base_info4);

        HColumnDescriptor base_info5 = new HColumnDescriptor("order");
        base_info.setMaxVersions(1);
        descriptor.addFamily(base_info5);


        hBaseAdmin.createTable(descriptor);

    }
}
