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
        conf.set("hbase.zookeeper.quorum","192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181");


        HBaseAdmin hBaseAdmin = new HBaseAdmin(conf);

        TableName name = TableName.valueOf("grasp");
        HTableDescriptor descriptor = new HTableDescriptor(name);

        HColumnDescriptor base_info = new HColumnDescriptor("base_info");
        base_info.setMaxVersions(3);
        descriptor.addFamily(base_info);


        hBaseAdmin.createTable(descriptor);

    }
}
