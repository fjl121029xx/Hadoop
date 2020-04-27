package com.li.c3p0;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestDemo {
    public static DataSource ds = null;

    static {
        //使用c3p0-config.xml配置文件中的named-config节点中name属性的值
        ComboPooledDataSource cpds = new ComboPooledDataSource("db");
        ds = cpds;
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(ds.getConnection());
    }
}
