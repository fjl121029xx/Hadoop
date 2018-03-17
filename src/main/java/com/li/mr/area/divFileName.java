package com.li.mr.area;

import com.li.mr.areapartition.FlowSumArea;
import com.li.mr.flowsum.FlowBean;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class divFileName {

    public static class DivAreaMapper extends Mapper<LongWritable, Text, Text, FlowBean> {

        private MultipleOutputs<Text, FlowBean> mos;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            mos = new MultipleOutputs<Text, FlowBean>(context);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            super.cleanup(context);
            mos.close();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString();
            String[] fields = StringUtils.split(line, " ");
            String phone = fields[1];
            long up_flow = Long.parseLong(fields[7]);
            long down_flow = Long.parseLong(fields[8]);

            FlowBean flowBean = new FlowBean(phone, up_flow, down_flow);

            mos.write("MOSTest", new Text(phone), flowBean, phone.subSequence(0, 2).toString() + "/" + phone.subSequence(0, 3).toString() + "/" + phone);
//          context.write(new Text(phone), flowBean);
        }
    }

    public static class DivAreaReducer extends Reducer<Text, FlowBean, FlowBean, NullWritable> {

        private MultipleOutputs<FlowBean, NullWritable> mos;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            mos = new MultipleOutputs<FlowBean, NullWritable>(context);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            super.cleanup(context);
            mos.close();
        }

        @Override
        protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {

            long sum_up_flow = 0;
            long sum_down_flow = 0;

            for (FlowBean flowBean : values) {

                sum_up_flow += flowBean.getUp_flow();
                sum_down_flow += flowBean.getDown_flow();
            }

            FlowBean flowBean = new FlowBean(key.toString(), sum_up_flow, sum_down_flow);

//            context.write(flowBean, NullWritable.get());
            mos.write(flowBean, NullWritable.get(), flowBean.getPhone());
        }
    }

    /**
     * http://blog.csdn.net/ukakasu/article/details/47302279
     * MapReduce中自定义文件输出名
     * <p>
     * https://my.oschina.net/leejun2005/blog/94706
     * MapReduce中的自定义多目录/文件名输出HDFS
     * <p>
     * http://blog.csdn.net/wen3011/article/details/54907731
     * hadoop win10环境配置
     *
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(FlowSumArea.class);

        job.setMapperClass(DivAreaMapper.class);
        job.setReducerClass(DivAreaReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        job.setOutputKeyClass(FlowBean.class);
        job.setOutputValueClass(NullWritable.class);

        /*//分组
        job.setPartitionerClass(AreaPartitioner.class);
        //reduce执行任务数
        job.setNumReduceTasks(7);*/


        String in = "hdfs://192.168.233.134:9000/userflow/in/HTTP_20130313143750.dat";
        String out = "d:/userflow/divname7";

        LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(in));
        FileOutputFormat.setOutputPath(job, new Path(out));

        MultipleOutputs.addNamedOutput(job, "MOSTest", TextOutputFormat.class, Text.class, FlowBean.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
