package com.li.hadoop.hdfs;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.io.*;

public class HdfsUtil {
    public static void main(String[] args) throws IOException {

        Configuration entries = new Configuration();
        //文件系统
        FileSystem fs = FileSystem.get(entries);

        Path path = new Path("hdfs://hadoop:9000/jdk-7u65-linux-i586.tar.gz");
        FSDataInputStream fsis = fs.open(path);

        BufferedInputStream bis = new BufferedInputStream(fsis);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("c:/"));

        IOUtils.copy(bis, bos);
    }


}
