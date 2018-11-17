package com.li.flink.kafka;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;

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
                                .assignTimestampsAndWatermarks(new CustomWatermarkExtractor())).keyBy("word")
                .map(new RollingAdditionMapper());
        input.print();
//        input.addSink(
//                new FlinkKafkaProducer010<KafkaEvent>(
//                        parameterTool.getRequired("output-topic"),
//                        new KafkaEventSchema(),
//                        parameterTool.getProperties()
//                )
//        );

        env.execute("kafka 0.10 Hello World");
    }
}
