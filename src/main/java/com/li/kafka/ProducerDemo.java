package com.li.kafka;

import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * https://www.cnblogs.com/jun1019/p/6656223.html
 * https://blog.csdn.net/u010886217/article/details/83154773
 */
public class ProducerDemo {

    private static Map<String, String> result = new HashMap<>();

    private static final String topic = "animate2";
    private static final Integer threads = 1;
    private static final Properties props = new Properties();

    static {
        props.put("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181");
        props.put("metadata.broker.list", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("num.partitions", "3");
    }

    public static void main(String[] args) throws Exception {


        String taskId = UUID.randomUUID().toString();


        Properties kafkaProps = new Properties();
        kafkaProps.put("Bootstarp.servers", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");
        kafkaProps.put("Key.serializer", "kafka.serializer.StringEncoder");
        kafkaProps.put("Value.serializer", "kafka.serializer.StringEncoder");

        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);

        ProducerRecord<String, String> record = new ProducerRecord<>("kafka-10", "2", taskId);
        producer.send(record);


    }
}