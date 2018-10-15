package com.li.mapreduce.maxtemperature;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MaxTemperature {
    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage : MaxTemperature <input path> <output path>");
            System.exit(-1);
        }

        try {
            Configuration conf = new Configuration();

            Job job = Job.getInstance(conf);
            job.setJarByClass(MaxTemperature.class);
            job.setJobName("maxTemperature");

            FileInputFormat.setInputPaths(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            job.setMapperClass(MaxTemperatureMapper.class);
            job.setReducerClass(MaxTemperatureReducer.class);

            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(IntWritable.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);

            System.exit(job.waitForCompletion(true) ? 0 : 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
