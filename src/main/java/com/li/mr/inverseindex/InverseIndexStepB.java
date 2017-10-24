package com.li.mr.inverseindex;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class InverseIndexStepB {

    public static class InverseIndexStepBMapper extends Mapper<Text,LongWritable,Text,Text>{

        @Override
        protected void map(Text key, LongWritable value, Context context) throws IOException, InterruptedException {

            String line = value.toString();
            String[] fields = StringUtils.split(line, "\t");

            String[] fields2 = StringUtils.split(fields[0], "--->");

            context.write(new Text(fields2[0]),new Text(fields2[1] + "--->" + value.toString()));
        }
    }

    public static class InverseIndexStepBReducer extends Reducer<Text,Text,Text,Text>{

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            String str = "";

            for (Text text : values) {
                str += text.toString() + ",";
            }
            str.substring(0, str.length() - 2);


            context.write(key,new Text(str));

        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(InverseIndexStepB.class);

        job.setMapperClass(InverseIndexStepBMapper.class);
        job.setReducerClass(InverseIndexStepBReducer.class);

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
