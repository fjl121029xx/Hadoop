package com.li.hive.jdbc;

import java.sql.*;

public class HiveJdbcDemo {

    private static String driverName =
            "org.apache.hive.jdbc.HiveDriver";

    public static void runJob() throws SQLException {

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Connection con = DriverManager.getConnection(
                "jdbc:hive2://172.20.44.8:10016",
                "hadoop", "");

        Statement stmt = con.createStatement();
        System.setProperty("spark.sql.crossJoin.enabled", "true");
        String sql =
                "select   * from db_yqs_b_505.tbl_pos_bill_master union  all (select   * from db_yqs_b_505.tbl_pos_bill_master)" +
                        "  union  all (select   * from db_yqs_b_505.tbl_pos_bill_master)" +
                        " union  all (select   * from db_yqs_b_505.tbl_pos_bill_master)  ";
        System.out.println("Running: " + sql);
        long a = System.currentTimeMillis();
        ResultSet res = stmt.executeQuery(sql);
        long b = System.currentTimeMillis();
        System.out.println((b - a) / 1000);

        if (res.next()) {
            System.out.print(res.getObject(1));
        }
    }

    public static void main(String[] args)
            throws SQLException {
        for (int i = 0; i < 1; i++) {
            runJob();
        }


    }
}
