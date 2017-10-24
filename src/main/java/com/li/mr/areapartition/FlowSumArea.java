package com.li.mr.areapartition;

import com.li.mr.flowsum.FlowBean;
import com.sun.tools.javac.comp.Flow;
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

public class FlowSumArea {

    public static class FlowSumAreaMapper extends Mapper<LongWritable,Text,Text,FlowBean>{

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString();
            String[] fields = StringUtils.split(line, "\t");

            String phone = fields[1];
            long up_flow = Long.parseLong(fields[7]);
            long down_flow = Long.parseLong(fields[8]);

            FlowBean flowBean = new FlowBean(phone,up_flow,down_flow);

            context.write(new Text(phone),flowBean);
        }
    }

    public static class FlowSumAreaReducer extends Reducer<Text,FlowBean,FlowBean,NullWritable>{

        @Override
        protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {

            long sum_up_flow = 0;
            long sum_down_flow = 0;

            for (FlowBean flowBean: values) {

                sum_up_flow += flowBean.getUp_flow();
                sum_down_flow += flowBean.getDown_flow();
            }

            FlowBean flowBean = new FlowBean(key.toString(),sum_up_flow,sum_down_flow);

            context.write(flowBean,NullWritable.get());
        }
    }

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

        job.setPartitionerClass(AreaPartitioner.class);
        job.setNumReduceTasks(4);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));


        System.exit(job.waitForCompletion(true)?0:1);
    }

}
