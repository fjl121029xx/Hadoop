package com.li.mr.hbase;

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
//        conf.set("hbase.zookeeper.quorum","192.168.233.137:2181,192.168.233.138:2181,192.168.233.139:2181");


        HBaseAdmin hBaseAdmin = new HBaseAdmin(conf);

        TableName name = TableName.valueOf("feifei");
        HTableDescriptor descriptor = new HTableDescriptor(name);

        HColumnDescriptor base_info = new HColumnDescriptor("base_info");
        base_info.setMaxVersions(3);
        descriptor.addFamily(base_info);

        HColumnDescriptor extra_info = new HColumnDescriptor("extra_info");
        base_info.setMaxVersions(3);
        descriptor.addFamily(extra_info);

        hBaseAdmin.createTable(descriptor);
        System.out.println("1111");

    }
}
