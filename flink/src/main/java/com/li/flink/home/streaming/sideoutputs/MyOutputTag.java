package com.li.flink.home.streaming.sideoutputs;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Properties;

public class MyOutputTag {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        OutputTag<String> outputTag = new OutputTag<String>("side-output"){};

        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");
        prop.setProperty("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181");
        prop.setProperty("group.id", "k1");

        FlinkKafkaConsumer010<String> kc = new FlinkKafkaConsumer010<>("kafka-record", new SimpleStringSchema(), prop);
        DataStreamSource<String> stream = env.addSource(kc);

        SingleOutputStreamOperator<String> process = stream.process(new ProcessFunction<String, String>() {
            @Override
            public void processElement(String value, Context ctx, Collector<String> out) throws Exception {

                out.collect(value);

                ctx.output(outputTag, "sideout-" + String.valueOf(value));
            }
        });
        process.getSideOutput(outputTag).print();

        env.execute("KafkaConsumer");
    }
}
