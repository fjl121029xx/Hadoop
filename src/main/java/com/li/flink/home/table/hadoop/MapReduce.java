package com.li.flink.home.table.hadoop;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.hadoop.mapreduce.HadoopInputFormat;
import org.apache.flink.api.java.hadoop.mapreduce.HadoopOutputFormat;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.hadoopcompatibility.mapred.HadoopMapFunction;
import org.apache.flink.hadoopcompatibility.mapred.HadoopReduceCombineFunction;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.Iterator;

public class MapReduce {

    public static void main(String[] args) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        Job job = Job.getInstance();
        HadoopInputFormat<LongWritable, Text> hadoopIF = new HadoopInputFormat<>(new TextInputFormat()
                , LongWritable.class, Text.class, job);
        TextInputFormat.addInputPath(job, new Path(""));

        DataSource<Tuple2<LongWritable, Text>> text = env.createInput(hadoopIF);


        DataSet<Tuple2<Text, LongWritable>> result = text
                // use Hadoop Mapper (Tokenizer) as MapFunction
                .flatMap(new HadoopMapFunction<LongWritable, Text, Text, LongWritable>(
                        new WordCountMapper()
                ))
                .groupBy(0)
                // use Hadoop Reducer (Counter) as Reduce- and CombineFunction
                .reduceGroup(new HadoopReduceCombineFunction<Text, LongWritable, Text, LongWritable>(
                        new WordCountReducer(),
                        new WordCountCombiner()
                ));

        // Set up the Hadoop TextOutputFormat.
        HadoopOutputFormat<Text, LongWritable> hadoopOF =
                new HadoopOutputFormat<>(
                        new TextOutputFormat<>(), job
                );
        hadoopOF.getConfiguration().set("mapreduce.output.textoutputformat.separator", " ");
        TextOutputFormat.setOutputPath(job, new Path(""));

        // Emit data using the Hadoop TextOutputFormat.
        result.output(hadoopOF);

        // Execute Program
        env.execute("Hadoop WordCount");

    }


    private static class WordCountMapper implements Mapper<LongWritable, Text, Text, LongWritable> {

        @Override
        public void map(LongWritable key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException {

        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void configure(JobConf job) {

        }
    }

    private static class WordCountReducer implements Reducer<Text, LongWritable, Text, LongWritable> {

        @Override
        public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException {

        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void configure(JobConf job) {

        }
    }

    private static class WordCountCombiner implements Reducer<Text, LongWritable, Text, LongWritable> {

        @Override
        public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException {

        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void configure(JobConf job) {

        }
    }
}
