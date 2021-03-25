package com.li.utils;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.net.URI;

public class HdfsUtil {





    private static Configuration getConfiguration(String nameService,
                                                  String address1,
                                                  String address2) {

        Configuration conf = new Configuration();
        conf.set("dfs.nameservices", nameService);
        conf.set("dfs.ha.namenodes." + nameService, "nn1,nn2");
        conf.set("dfs.namenode.rpc-address." + nameService + ".nn1", address1);
        conf.set("dfs.namenode.rpc-address." + nameService + ".nn2", address2);
        conf.set("dfs.client.failover.proxy.provider." + nameService,
                "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
        conf.set("fs.defaultFS", "hdfs://" + nameService);
        return conf;
    }


    public static FileSystem getFileSystem(String nameservice,
                                           String address1,
                                           String address2,
                                           String user) throws Exception {
        Configuration conf = getConfiguration(nameservice, address1, address2);
        return FileSystem.get(new URI(conf.get("fs.defaultFS")), conf, user);
    }


    public static void main(String[] args) {

        try {
            FileSystem fs = getFileSystem("cluster", "bigdata-0001:8020", "bigdata-0005:8020", "hadoop");
            Path path = new Path("/");
            long totalSize = fs.getContentSummary(path).getLength();
            System.out.println(totalSize);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


}
