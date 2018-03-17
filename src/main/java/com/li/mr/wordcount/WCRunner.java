package com.li.mr.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WCRunner {
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        Job wcjob = Job.getInstance(conf);

        //设置整个job所用的那些类在哪个jar包
        wcjob.setJarByClass(WCRunner.class);

        //指定本job使用的mapper和reduce的类
        wcjob.setMapperClass(WCMapper.class);
        wcjob.setReducerClass(WCReducer.class);

        //指定reduce的输出数据kvleix
        wcjob.setOutputKeyClass(Text.class);
        wcjob.setOutputValueClass(LongWritable.class);

        //指定mapper的输出数据类型kv类型
        wcjob.setMapOutputKeyClass(Text.class);
        wcjob.setMapOutputValueClass(LongWritable.class);

        //指定要处理的输入数据存放路径
        FileInputFormat.setInputPaths(wcjob, new Path("hdfs://192.168.233.134:9000/wordcount/in/wordcount.txt"));

        //指定处理结果的输出数据存放路径
        FileOutputFormat.setOutputPath(wcjob, new Path("d:/wordcount/out2/"));

        //将job提交给集群运行
        wcjob.waitForCompletion(true);
    }
}
