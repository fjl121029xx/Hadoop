package com.li.mr.maxtemperature;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class MaxTemperatureReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

        int maxValue = Integer.MIN_VALUE;
        for (IntWritable value : values) {
            maxValue = Math.max(maxValue, value.get());
        }

        context.write(key, new IntWritable(maxValue));
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
    }

    @Test
    public void returnMaximumInteger() {

        try {
            new ReduceDriver<Text,IntWritable,Text,IntWritable>()
                    .withReducer(new MaxTemperatureReducer())
                    .withInput(new Text("1950"), Arrays.asList(new IntWritable(10),new IntWritable(12)))
                    .withOutput(new Text("1950"),new IntWritable(10))
                    .runTest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
