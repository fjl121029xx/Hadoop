package com.li.mr.flowsort;

import com.li.mr.flowsum.FlowBean;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

import java.io.IOException;

public class SortMR {

    public static class SortMapper extends Mapper<LongWritable,Hamlet.TEXTAREA,FlowBean,NullWritable>{

        @Override
        protected void map(LongWritable key, Hamlet.TEXTAREA value, Context context) throws IOException, InterruptedException {

            String line = value.toString();
            String[] fields = StringUtils.split(line, "\t");

            String phone = fields[0];
            long up_flow = Long.parseLong(fields[1]);
            long down_flow = Long.parseLong(fields[2]);

            FlowBean flowBean = new FlowBean(phone,up_flow,down_flow);

            context.write(flowBean,NullWritable.get());
        }
    }

    public static class SortReducer extends Reducer<FlowBean,NullWritable,FlowBean,NullWritable>{

        @Override
        protected void reduce(FlowBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {

            context.write(key,NullWritable.get());
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(SortMR.class);

        job.setMapperClass(SortMapper.class);
        job.setReducerClass(SortReducer.class);

//        job.setMapOutputKeyClass(FlowBean.class);
//        job.setMapOutputValueClass(NullWritable.class);

        job.setOutputKeyClass(FlowBean.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));


        System.exit(job.waitForCompletion(true)?0:1);

    }
}
