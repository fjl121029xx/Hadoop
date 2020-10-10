package com.li.presto;

import java.sql.*;

import com.facebook.presto.jdbc.PrestoDriver;

public class HelloPresto {


    public static void main(String[] args) {

        Connection connection = null;
        Statement statement = null;
        try {

            Class.forName("com.facebook.presto.jdbc.PrestoDriver");
            connection = DriverManager.getConnection(
                    "jdbc:presto://192.168.101.152:9000/hive/gauss", "dw@hualala.com", "");

            statement = connection.createStatement();

            String sql;
            sql = " select * from gauss.aggr_order_ordertype_onlinedining_day where pt = '20200830' order by num ";

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                System.out.print(resultSet.getInt(1) + "\t");
                System.out.print(resultSet.getString(2) + "\t");
                System.out.print(resultSet.getInt(3) + "\t");
                System.out.print(resultSet.getInt(4) + "\t");
                System.out.print(resultSet.getBigDecimal(5) + "\t");
                System.out.print(resultSet.getInt("order_cnt") + "\t");
                System.out.print(resultSet.getBigDecimal("price") + "\t");
                System.out.print(resultSet.getBigDecimal("precent") + "\t");
                System.out.println(resultSet.getString("pt"));

            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
