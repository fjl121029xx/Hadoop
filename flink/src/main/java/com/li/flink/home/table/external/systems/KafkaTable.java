package com.li.flink.home.table.external.systems;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Avro;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Rowtime;
import org.apache.flink.table.descriptors.Schema;

public class KafkaTable {

    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tEnv = TableEnvironment.getTableEnvironment(env);

        tEnv.connect(new Kafka()
                .version("0.10")
                .topic("test-topic")
                .startFromEarliest()
                .property("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181")
                .property("bootstrap.servers", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092"))
                .withFormat(new Avro()
                        .avroSchema("{" +
                                "\"type\":\"record\"," +
                                "\"name\":\"UserMessage\"," +
                                "\"fields\":[" +
                                "{\"name\":\"recordtime\",\"type\":\"long\"}," +
                                "{\"name\":\"user\",\"type\":\"long\"}," +
                                "{\"name\":\"message\",\"type\":[\"string\",\"null\"]}" +
                                "]" +
                                "}"))
                .withSchema(new Schema()
                        .field("recordtime", Types.SQL_TIMESTAMP())
                        .rowtime(new Rowtime()
                                .timestampsFromField("ts")
                                .watermarksPeriodicBounded(60000))
                        .field("user", Types.LONG())
                        .field("message", Types.STRING())
                )
                .inAppendMode()
                .registerTableSource("MyUserTable");
    }
}
