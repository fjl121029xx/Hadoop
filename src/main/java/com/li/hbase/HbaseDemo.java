package com.li.hbase;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HbaseDemo {
    public static Configuration conf = null;
    private static Connection connection;

    private static final String ZK = "192.168.100.68,192.168.100.70,192.168.100.72";
    private static final String CL = "2181";
    private static final String DIR = "/hbase";


    @Before
    public void init() {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", HbaseDemo.ZK);
        conf.set("hbase.zookeeper.property.clientPort", HbaseDemo.CL);
        conf.set("hbase.rootdir", HbaseDemo.DIR);

        try {
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void list() throws Exception {

        HBaseAdmin admin = new HBaseAdmin(conf);

        TableName[] tableNames = admin.listTableNames();

        for (int i = 0; i < tableNames.length; i++) {
            TableName tn = tableNames[i];
            System.out.println(new String(tn.getName()));
        }
        System.out.println(tableNames);
    }

    @Test
    public void testDrop() throws Exception {

        HBaseAdmin admin = new HBaseAdmin(conf);
        admin.disableTable("test_tr_accuracy_analyze");
        admin.deleteTable("test_tr_accuracy_analyze");
        admin.close();
    }


    @Test
    public void testScan() throws Exception {
        HTablePool pool = new HTablePool(conf, 10);
        HTableInterface table = pool.getTable("test_tr_accuracy_analyze");
        Scan scan = new Scan(Bytes.toBytes("rk0001"), Bytes.toBytes("rk0002"));
        scan.addFamily(Bytes.toBytes("info"));
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            /**
             for(KeyValue kv : r.list()){
             String family = new String(kv.getFamily());
             System.out.println(family);
             String qualifier = new String(kv.getQualifier());
             System.out.println(qualifier);
             System.out.println(new String(kv.getValue()));
             }
             */
            byte[] value = r.getValue(Bytes.toBytes("info"), Bytes.toBytes("name"));
        }
        pool.close();
    }


    @Test
    public void testDel() throws Exception {
        HTable table = new HTable(conf, "test_tr_accuracy_analyze2");
        Delete del = new Delete(Bytes.toBytes("9"));
//        del.deleteColumn(Bytes.toBytes("data"), Bytes.toBytes("pic"));
        table.delete(del);
        table.close();
    }

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", HbaseDemo.ZK);
        conf.set("hbase.zookeeper.property.clientPort", HbaseDemo.CL);
        conf.set("hbase.rootdir", HbaseDemo.DIR);

        HBaseAdmin admin = new HBaseAdmin(conf);
//
        HTableDescriptor table = new HTableDescriptor("sc_a_a");
        HColumnDescriptor columnFamily = new HColumnDescriptor("i");
        columnFamily.setMaxVersions(1);
        table.addFamily(columnFamily);


        admin.createTable(table);


//        admin.deleteTable("videoplaylength");
        admin.close();
    }

    @Test
    public void testPut() throws Exception {

        HTable table = new HTable(conf, "test_tr_accuracy_analyze2");


        Put put = new Put(Bytes.toBytes("101"));
        put.addColumn(Bytes.toBytes("accuracy_result"), Bytes.toBytes("correct"), Bytes.toBytes("138"));
        put.addColumn(Bytes.toBytes("accuracy_result"), Bytes.toBytes("error"), Bytes.toBytes("113"));
        put.addColumn(Bytes.toBytes("accuracy_result"), Bytes.toBytes("sum"), Bytes.toBytes("251"));
        put.addColumn(Bytes.toBytes("accuracy_result"), Bytes.toBytes("accuracy"), Bytes.toBytes("0.55"));
        put.addColumn(Bytes.toBytes("accuracy_result"), Bytes.toBytes("submitTime"), Bytes.toBytes("2018-06-13"));
        put.addColumn(Bytes.toBytes("accuracy_result"), Bytes.toBytes("evaluationAnswerTime"), Bytes.toBytes("483"));
        put.addColumn(Bytes.toBytes("accuracy_result"), Bytes.toBytes("evaluationAnswerTime"), Bytes.toBytes("483"));

        table.put(put);
        table.close();
    }


    public static Object getObject(String tablename, String course, Class clazz) throws Exception {

        HTable table = new HTable(conf, tablename);
        Get get = new Get(Bytes.toBytes(course));
        get.setMaxVersions(1);
        Result result = table.get(get);

        List<Cell> cells = result.listCells();
        if (cells == null || cells.size() < 1) {

            return null;
        }
        Map<String, String> map = new HashMap<>();
        for (Cell c : cells) {
            byte[] qualifier = c.getQualifier();
            byte[] valueArray = c.getValueArray();

            map.put(new String(qualifier), new String(valueArray, c.getValueOffset(), c.getValueLength()));
        }

        Object o = clazz.newInstance();

        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method m : declaredMethods) {
            String mName = m.getName();
            if (mName.startsWith("set")) {

                String set = mName.replace("set", "");
                Class pType = m.getParameterTypes()[0];

                String s1 = set.substring(0, 1).toLowerCase();
                String s2 = set.substring(1, set.length());
                if (pType.getName().equals("java.lang.Long")) {

                    if (map.get(s1 + s2) != null) {

                        m.invoke(o, Long.parseLong(map.get(s1 + s2)));
                    }
                } else if (pType.getName().equals("java.lang.String")) {

                    m.invoke(o, map.get(s1 + s2));
                } else if (pType.getName().equals("java.lang.Double")) {

                    m.invoke(o, Double.parseDouble(map.get(s1 + s2)));
                }
            }
        }

        table.close();

        return o;
    }

}
