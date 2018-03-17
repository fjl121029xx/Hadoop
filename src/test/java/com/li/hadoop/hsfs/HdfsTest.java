package com.li.hadoop.hsfs;

import com.li.hadoop.hdfs.Tools;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.*;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 2017-09-26  weekend02
 */
public class HdfsTest {

    private FileSystem fs = null;

    /**
     * 初始化文件系统
     * @throws Exception
     */
    @Before
    public void init() throws Exception {
        fs = FileSystem.get(Tools.Configuration);
    }

    /**
     * 文件上传
     * IO
     * @throws IOException
     */
    @Test
    public void upload() throws IOException {

        Path path = new Path("/resouce/aa/qingshu.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fs.create(path));

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream("c:/qingshu.txt"));

        IOUtils.copy(bis, bos);
    }
    /**
     * 文件上传2
     * copyFromLocalFile();
     * @throws IOException
     */
    @Test
    public void upload2() throws IOException {

        Path src = new Path("D:/work/mysql-connector-java-5.1.45.jar");
        Path dst = new Path("hdfs://192.168.233.134:9000/");

        fs.copyFromLocalFile(src,dst);
    }
    /**
     * 文件下载
     */
    @Test
    public void download() throws IOException {

        Path src = new Path("hdfs://hadoop:9000/bb/qingshu.txt");
        Path dst = new Path("d:/qingshu.txt");

        fs.copyToLocalFile(false,src, dst,true);
    }

    /**
     * 查看文件信息
     */
    @Test
    public void listFiles() throws Exception {

        Path path = new Path("/");
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(path, true);

        while (listFiles.hasNext()){

            LocatedFileStatus next = listFiles.next();

            Path path1 = next.getPath();
            String name = path1.getName();
            System.out.println(name);
        }

        System.out.println("----------------------------------");
        FileStatus[] status = fs.listStatus(path);
        for (FileStatus f: status) {

            String name = f.getPath().getName();
            System.out.println(name + (f.isDirectory()?" is dir":" is file"));
        }
    }
    /**
     * 创建文件夹
     */
    @Test
    public void mkdir() throws Exception {

        Path path = new Path("/cc");
        fs.mkdirs(path);
    }
    /**
     * 删除文件或文件夹
     */
    @Test
    public void rm() throws Exception {

        Path path = new Path("/userflow/sort");
        fs.delete(path, true);
    }

}