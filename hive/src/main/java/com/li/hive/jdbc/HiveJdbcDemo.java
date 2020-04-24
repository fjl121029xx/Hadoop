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
//        String sql = "select row_number() over(order by split(key,'_')[0] ) as `key`, split(key,'_')[0] AS `report_date_1586318433859`,cast(value as DECIMAL(15,2)) as `pay_amt_1586318438860` from (select compare(ARRAY(cast(report_date as string)),pay_amt,'ym','0','avg') as m from `db_yqs_b_5700`.`standard_summary_bill_shop_pay_day_8` where 1=1) t LATERAL VIEW explode(t.m) tt as key ,value  order by report_date_1586318433859 ASC";

//        String sql ="create  TEMPORARY  function row_col_stat_test as 'com.hll.udaf.v2.RowColStatistics' using jar 'hdfs:/test/hive-udf-1.0.jar'" ;
        String sql = "" +
                "show functions";

        System.out.println(sdf.format(new Date(System.currentTimeMillis())) + " Running: " + sql);
        long a = System.currentTimeMillis();
        ResultSet res = stmt.executeQuery(sql);

        while (res.next()) {

            System.out.println(res.getObject(1));
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
