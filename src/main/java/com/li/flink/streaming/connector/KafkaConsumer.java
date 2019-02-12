package com.li.flink.streaming.connector;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import javax.annotation.Nullable;
import java.util.Properties;

/**
 * Kafka Consumers and Fault Tolerance
 * Kafka Consumers Topic and Partition Discovery
 */
public class KafkaConsumer {

    public static void main(String[] args) throws Exception {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");
        prop.setProperty("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181");
        prop.setProperty("group.id", "k1");

        FlinkKafkaConsumer010<String> kc = new FlinkKafkaConsumer010<>("kafka-record", new SimpleStringSchema(), prop);
        kc.assignTimestampsAndWatermarks(new CustomWatermarkEmitter());
        DataStreamSource<String> stream = env.addSource(kc);
        stream.print();


        env.execute("KafkaConsumer");
    }

    private static class CustomWatermarkEmitter implements AssignerWithPeriodicWatermarks<String> {

        /**
         *
         * @return
         */
        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return null;
        }

        /**
         *  assign a timestamp to the record and the Watermark
         * @param element
         * @param previousElementTimestamp
         * @return
         */
        @Override
        public long extractTimestamp(String element, long previousElementTimestamp) {
            return 0;
        }
    }
}
