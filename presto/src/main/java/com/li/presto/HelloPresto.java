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
//            System.out.println(time2 - startTime);
// ;
            String sql;
//            sql = " select playhour,count(distinct uname) from videoplay where recordtime = '20181212'  and terminal=1 group by playhour";
            sql = " descselect sum(playlength) as sumlength,uname from videoplay where recordtime = '20181213'   group by uname order by sumlength  ";



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

//            System.out.println(endTime - time2);
            resultSet.close();
            statement.close();
            connection.close();

        }  catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}
