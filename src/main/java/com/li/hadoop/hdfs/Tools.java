package com.li.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;

public class Tools {

    public static final Configuration Configuration =
            new Configuration();

    static {
        Configuration.set("fs.defaultFS","hdfs://ns1/");
        System.setProperty("HADOOP_USER_NAME","root");
    }
}
