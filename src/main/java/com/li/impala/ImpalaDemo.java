package com.li.impala;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImpalaDemo {

    static String JDBC_DRIVER = "com.cloudera.impala.jdbc41.Driver";
    static String CONNECTION_URL = "jdbc:impala://192.168.100.3:21050/test";

    public static void main(String[] args) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(CONNECTION_URL);
            ps = con.prepareStatement("select max(dtime),count(dtime) from achi");
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1) + '\t' + rs.getLong(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭rs、ps和con
        }
    }

}
