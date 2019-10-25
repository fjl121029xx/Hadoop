package com.li.kafka.kafka10;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class KafkaProducerDemo {

    private final Producer<String, String> kafkaProdcer;
    public final static String TOPIC = "bd_canal_order_tbl_order_master";

    private KafkaProducerDemo() {
        kafkaProdcer = createKafkaProducer();
    }

    private Producer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "172.20.119.175:9092,172.20.47.150:9092,172.20.101.187:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);
        return kafkaProducer;
    }

    private void produce() {

        String[] arr = {"a", "b"};
        for (int i = 0; i < 100000; i++) {
            int i1 = Math.random() > 0.5 ? 1 : 0;
            System.out.println(i);
            kafkaProdcer.send(new ProducerRecord<String, String>(TOPIC, arr[i1])
                    , (recordMetadata, e) -> System.out.println(e));
        }
    }

    public static void main(String[] args) {

        KafkaProducerDemo kafkaProducerDemo = new KafkaProducerDemo();
        kafkaProducerDemo.produce();
    }
}
