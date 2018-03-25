package com.li.hive;

import java.sql.*;

public class HiveJdbcCli {

    /**
     * hive的jdbc驱动类
     */
    private static String dirverName = "org.apache.hive.jdbc.HiveDriver";
    /**
     * 连接hive的URL hive1.2.1版本需要的是jdbc:hive2，而不是 jdbc:hive
     */
    private static String url = "jdbc:hive2://192.168.233.134:10000/default";
    /**
     * 登录linux的用户名  一般会给权限大一点的用户，否则无法进行事务形操作
     */
    private static String user = "root";
    /**
     * 登录linux的密码
     */
    private static String pass = "121029";

    /**
     * 创建连接
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConn() {
        Connection conn = null;
        try {
            Class.forName(dirverName);
            conn = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }

    public static PreparedStatement prepare(Connection conn, String sql) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ps;
    }
}
