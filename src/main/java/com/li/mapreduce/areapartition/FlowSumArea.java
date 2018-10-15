package com.li.mapreduce.areapartition;

import com.li.mapreduce.flowsum.FlowBean;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 对流量原始日志进行流量统计，讲不同省份的用户统计输出到不同的文件
 * <p>
 * 需要自定义改造两个机制
 * 1，改造分区逻辑，自定义一个partitioner
 * 2，自定义reducer task的并发任务数
 */
public class FlowSumArea {

    public static class FlowSumAreaMapper extends Mapper<LongWritable, Text, Text, FlowBean> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString();
            String[] fields = StringUtils.split(line, " ");

            String phone = fields[1];
            long up_flow = Long.parseLong(fields[7]);
            long down_flow = Long.parseLong(fields[8]);

            FlowBean flowBean = new FlowBean(phone, up_flow, down_flow);

            context.write(new Text(phone), flowBean);
        }
    }

    public static class FlowSumAreaReducer extends Reducer<Text, FlowBean, FlowBean, NullWritable> {

        @Override
        protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {

            long sum_up_flow = 0;
            long sum_down_flow = 0;

            for (FlowBean flowBean : values) {

                sum_up_flow += flowBean.getUp_flow();
                sum_down_flow += flowBean.getDown_flow();
            }

            FlowBean flowBean = new FlowBean(key.toString(), sum_up_flow, sum_down_flow);

            context.write(flowBean, NullWritable.get());
        }
    }

    /**
     * http://blog.csdn.net/ukakasu/article/details/47302279
     * MapReduce中自定义文件输出名
     *
     * https://my.oschina.net/leejun2005/blog/94706
     * MapReduce中的自定义多目录/文件名输出HDFS
     *
     * http://blog.csdn.net/wen3011/article/details/54907731
     * hadoop win10环境配置
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(FlowSumArea.class);

        job.setMapperClass(FlowSumAreaMapper.class);
        job.setReducerClass(FlowSumAreaReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        job.setOutputKeyClass(FlowBean.class);
        job.setOutputValueClass(NullWritable.class);

        //分组
        job.setPartitionerClass(AreaPartitioner.class);
        //reduce执行任务数
        job.setNumReduceTasks(7);

        String in = "hdfs://192.168.233.134:9000/userflow/in/HTTP_20130313143750.dat";
        String out = "d:/userflow/area";
        FileInputFormat.setInputPaths(job, new Path(in));
        FileOutputFormat.setOutputPath(job, new Path(out));


        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
