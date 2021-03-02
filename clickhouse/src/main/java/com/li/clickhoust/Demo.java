package com.li.clickhoust;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

public class Demo {

    public static void main(String[] args) throws Exception {

        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://192.168.60.131:9000");

        PreparedStatement pstmt = connection.prepareStatement("insert into test.jdbc_example values(?, ?, ?)");
        // insert 10 records
        for (int i = 0; i < 10; i++) {
            pstmt.setDate(1, (java.sql.Date) new Date(System.currentTimeMillis()));
            pstmt.setString(2, "panda_" + (i + 1));
            pstmt.setInt(3, 18);
            pstmt.addBatch();
        }
        pstmt.executeBatch();
    }
}
