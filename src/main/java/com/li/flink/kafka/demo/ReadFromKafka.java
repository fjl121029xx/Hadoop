package com.li.flink.kafka.demo;

import org.apache.commons.net.ntp.TimeStamp;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Date;

public class ReadFromKafka {

    public static void main(String[] args) throws Exception {

        final ParameterTool parameterTool = ParameterTool.fromArgs(args);
        StreamExecutionEnvironment env = KafkaFlinkUtil.prepareExecutionEnv(parameterTool);

        SingleOutputStreamOperator<KafkaEvent> input = env
                .addSource(
                        new FlinkKafkaConsumer010<>(
                                parameterTool.getRequired("input-topic"),
                                new KafkaEventSchema(),
                                parameterTool.getProperties())
                                .assignTimestampsAndWatermarks(new CustomWatermarkExtractor()))
                .keyBy("userId")

                .map(new RollingAdditionMapper());
//        input.print();



        input.addSink(
                new FlinkKafkaProducer010<KafkaEvent>(
                        parameterTool.getRequired("output-topic"),
                        new KafkaEventSchema(),
                        parameterTool.getProperties()
                )
        );

        env.execute("kafka 0.10 Hello World");
    }



}
