package com.li.presto;

import java.sql.*;

import com.facebook.presto.jdbc.PrestoDriver;


public class HelloPresto {


    public static void main(String[] args) {


        long startTime = System.currentTimeMillis();

        Connection connection = null;
        Statement statement = null;
        try {

            Class.forName("com.facebook.presto.jdbc.PrestoDriver");
            connection = DriverManager.getConnection(
                    "jdbc:presto://huatu68:9999/hive/default", "hive", "");

            //connect mysql server tutorials database here
            statement = connection.createStatement();

            long time2 = System.currentTimeMillis();
            System.out.println(time2 - startTime);

            String sql;
            sql = "  select distinct count(distinct userId) from v_huatu_order where title like '%六点%' and userId in ( select distinct userId from v_huatu_order where title like '%时政%' )";

            //select mysql table author table two columns
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
//                int id = resultSet.getInt("auth_id");
//                String name = resultSet.getString("auth_name");
//                String userId = resultSet.getString("userId");
//                int count = resultSet.getInt(2);
//                System.out.print("userId: " + userId + ";\ncount: " + count + "\n");
                System.out.println(resultSet.getInt(1));
            }
            long endTime = System.currentTimeMillis();

            System.out.println(endTime - time2);
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}
