package com.li.hbase.coprocessor;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class CountDemo {

    public static void main(String[] args) throws Throwable {


        TableName tablename = TableName.valueOf("grasp");


        String coprocessClassName = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
        HBaseAdmin admin = new HBaseAdmin(HBaseConfiguration.create());
        admin.disableTable(tablename);

        HTableDescriptor htd = admin.getTableDescriptor(tablename);
        htd.addCoprocessor(coprocessClassName);
        admin.modifyTable(tablename, htd);
        admin.enableTable(tablename);

        Scan s = new Scan();
        s.addColumn(Bytes.toBytes(""), Bytes.toBytes(""));
        AggregationClient ac = new AggregationClient(HBaseConfiguration.create());

        System.out.println(ac.rowCount(tablename, new LongColumnInterpreter(), s));

    }
}
