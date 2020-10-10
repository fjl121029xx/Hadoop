package com.li.hive.jdbc;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        Connection con = DriverManager.getConnection(
//                "jdbc:hive2://172.20.44.8:10016",
//                "hadoop", "");
        Connection con = DriverManager.getConnection(
                "jdbc:hive2://192.168.101.119:10010",
                "olap", "");
//        Connection con = DriverManager.getConnection(
//                "jdbc:hive2://192.168.101.75:10016",
//                "olap", "");

        Statement stmt = con.createStatement();

        System.setProperty("spark.sql.crossJoin.enabled", "true");

        System.out.println("--------------------------------------------------------------------------------------------");
        String sql = " select * from `test`.`client_create_test2`";

        System.out.println(sdf.format(new Date(System.currentTimeMillis())) + " Running: \r" + sql);
        System.out.println("--------------------------------------------------------------------------------------------");

        long a = System.currentTimeMillis();

        stmt.execute(sql);
        ResultSet rs = stmt.executeQuery(sql);

        List list = new ArrayList(rs.getFetchSize());
        ResultSetMetaData metaData = rs.getMetaData();

        while (rs.next()) {
            Map map = new HashMap<>();
            List columns = new ArrayList(metaData.getColumnCount());
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                int columnType = metaData.getColumnType(i + 1);
                switch (columnType) {
                    case Types.DECIMAL:
                        columns.add(rs.getBigDecimal(i + 1));
                        break;
                    case Types.FLOAT:
                        columns.add(rs.getFloat(i + 1));
                        break;
                    case Types.DOUBLE:
                        columns.add(rs.getDouble(i + 1));
                        break;
                    case Types.DATE:
                        columns.add(rs.getDate(i + 1));
                        break;
                    case Types.TIMESTAMP:
                        columns.add(rs.getTimestamp(i + 1));
                        break;
                    default:
                        columns.add(rs.getString(i + 1));
                }
            }
            System.out.println(columns);
            map.put("metaData", metaData);
            map.put("data", columns);
            list.add(map);
        }

        long b = System.currentTimeMillis();
        System.out.println("running time --> " + (b - a) / 1000);
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
