package com.li.hive.jdbc;

import java.sql.*;
import java.text.SimpleDateFormat;

public class HiveJdbcDemo {

    private static String driverName =
            "org.apache.hive.jdbc.HiveDriver";

    public static void runJob() throws SQLException {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Connection con = DriverManager.getConnection(
                "jdbc:hive2://172.20.44.8:10016",
                "hadoop", "");
//        Connection con = DriverManager.getConnection(
//                "jdbc:hive2://192.168.101.51:10016",
//                "olap", "");

        Statement stmt = con.createStatement();

        System.setProperty("spark.sql.crossJoin.enabled", "true");

        String sql = " SELECT sum(col_6)         AS `col_6_1587884373536`," +
                "                   CASE" +
                "                       WHEN col_1 REGEXP '^内蒙古' THEN '内蒙古'" +
                "                       WHEN col_1 REGEXP '^西藏' THEN '西藏'" +
                "                       WHEN col_1 REGEXP '^新疆' THEN '新疆'" +
                "                       WHEN col_1 REGEXP '^宁夏' THEN '宁夏'" +
                "                       WHEN col_1 REGEXP '^广西' THEN '广西'" +
                "                       WHEN col_1 REGEXP '^澳门' THEN '澳门'" +
                "                       WHEN col_1 REGEXP '^香港' THEN '香港'" +
                "                       WHEN col_1 REGEXP '省$' THEN SUBSTRING_INDEX(col_1, '省', 1)" +
                "                       WHEN col_1 REGEXP '市$' THEN SUBSTRING_INDEX(col_1, '市', 1)" +
                "                       ELSE col_1 END AS `col_1_1587884356039`," +
                "                   col_1              AS `originProvince`" +
                "            FROM `db_yqs_p_505`.`tbl_p_79467_1587884332`" +
                "            GROUP BY originProvince, col_1_1587884356039" +
                "            LIMIT 1000 ";

        System.out.println(sdf.format(new Date(System.currentTimeMillis())) + " Running: \r" + sql);
        long a = System.currentTimeMillis();
        ResultSet res = stmt.executeQuery(sql);

        while (res.next()) {

            System.out.println(res.getObject(1)+"\t"+res.getObject(2));
//            System.out.println(res.getObject(1));
        }
        long b = System.currentTimeMillis();
        System.out.println((b - a) / 1000);
    }

    public static void main(String[] args)
            throws SQLException {
        for (int i = 0; i < 1; i++) {
            try {
                runJob();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
