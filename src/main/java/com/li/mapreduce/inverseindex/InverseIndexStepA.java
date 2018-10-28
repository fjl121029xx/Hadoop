package com.li.mapreduce.inverseindex;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class InverseIndexStepA {

    public static class InverserIndexStepAMapper extends Mapper<LongWritable,Text,Text,LongWritable>{

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString();

            String[] words = StringUtils.split(line, "\t");

            FileSplit fileSplit = (FileSplit) context.getInputSplit();

            Path path = fileSplit.getPath();
            String fileName = path.getName();

            for (String word: words) {
                context.write(new Text(word + "--->" + fileName),new LongWritable(1));
            }


        }
    }

    public static class InverserIndexStepAReducer extends Reducer<Text,LongWritable,Text,LongWritable>{

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {

            long counter = 0;
            for (LongWritable lw : values) {
                counter += lw.get();
            }

            context.write(key,new LongWritable(counter));
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(InverseIndexStepA.class);

        job.setMapperClass(InverserIndexStepAMapper.class);
        job.setReducerClass(InverserIndexStepAReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        TextInputFormat.setInputPaths(job,new Path(args[0]));

        FileSystem fileSystem = FileSystem.get(conf);

        Path path = new Path(args[1]);
        boolean exists = fileSystem.exists(path);
        if (exists){
            fileSystem.delete(new Path(args[1]),true);
        }

        TextOutputFormat.setOutputPath(job,path);


        System.exit(job.waitForCompletion(true)?0:1);

    }
}
