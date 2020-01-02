package com.li.hive.jdbc;

import java.sql.*;

public class HiveJdbcDemo {

    private static String driverName =
            "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args)
            throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Connection con = DriverManager.getConnection(
                "jdbc:hive2://bigdata-0001:2181,bigdata-0002:2181,bigdata-0003:2181,bigdata-0004:2181,bigdata-0006:2181/default;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2",
                "hadoop", "hadoop123");
        Statement stmt = con.createStatement();
//        String tableName = "wyphao";
//        stmt.execute("drop table if exists " + tableName);
//        stmt.execute("create table " + tableName +
//                " (key int, value string)");
//        System.out.println("Create table success!");
        // show tables
        String sql = "describe wyphao ";
        System.out.println("Running: " + sql);
        ResultSet res = stmt.executeQuery(sql);
        if (res.next()) {
            System.out.println(res.getString(1));
        }


    }
}
