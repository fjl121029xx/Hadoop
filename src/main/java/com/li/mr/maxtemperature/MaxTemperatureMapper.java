package com.li.mr.maxtemperature;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Test;

import java.io.IOException;

public class MaxTemperatureMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final int MISSING = 9999;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        String line = value.toString();
        String year = line.substring(15, 19);

        int airTemperature;
        if (line.charAt(87) == '+') {
            airTemperature = Integer.parseInt(line.substring(88, 92));
        } else {
            airTemperature = Integer.parseInt(line.substring(87, 92));
        }

        String quality = line.substring(92, 93);

        if( airTemperature != MISSING && quality.matches("[01459]")){
            context.write(new Text(year),new IntWritable(airTemperature));
        }

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
    }

    @Test
    public void processValidRecord() {

        Text value = new Text("0207010010999992010010100004+70933-008667FM-12+0009ENJA V0202601N003010090019N0200001N1-00611-00811102721ADDAA106000091AY121061AY211061GF105991051081004501001001MA1999999102601MD1510001+9999MW1021REMSYN088AAXX  01001 01001 11470 52603 11061 21081 30260 40272 55000 60001 70221 85800 333 91105;EQDQ01  00002PRCP06");

        try {
            new MapDriver<LongWritable,Text,Text,IntWritable>()
                    .withMapper(new MaxTemperatureMapper())
                    .withInput(new LongWritable(0),value)
                    .withOutput(new Text("1950"),new IntWritable(-11))
                    .runTest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
