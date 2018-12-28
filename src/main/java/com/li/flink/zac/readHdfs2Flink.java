package com.li.flink.zac;

import com.li.flink.kafka.demo.CustomWatermarkExtractor;
import com.li.flink.kafka.demo.KafkaEvent;
import com.li.flink.kafka.demo.KafkaEventSchema;
import com.li.flink.kafka.demo.RollingAdditionMapper;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

public class readHdfs2Flink {

    public static void main(String[] args) throws Exception {


        final ParameterTool parameterTool = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KeyedStream<AnswerCard, Tuple> input = env
                .addSource(
                        new FlinkKafkaConsumer010<>(
                                parameterTool.getRequired("input-topic"),
                                new AnswerCardSchema(),
                                parameterTool.getProperties())
                                .assignTimestampsAndWatermarks(new acWatermarkExtreactor()))
                .keyBy("userId");

        input.print();
        env.execute("answer card");

    }
}
