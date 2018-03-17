package com.li.mr.flowsum;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowSumMapper extends Mapper<LongWritable, Text, Text, FlowBean> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        String line = value.toString();

        String[] fields = StringUtils.split(line, " ");


        String phone = fields[1];
        long up_flow = Long.parseLong(fields[7]);
        long down_flow = Long.parseLong(fields[8]);

        context.write(new Text(phone), new FlowBean(phone, up_flow, down_flow));


    }
}
