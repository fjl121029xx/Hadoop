package com.li.kafka.kafka10;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class KafkaProducerDemo {

    private final Producer<String, String> kafkaProdcer;
    public final static String TOPIC = "test";

    private KafkaProducerDemo() {
        kafkaProdcer = createKafkaProducer();
    }

    private Producer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.126.138:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }

    private void produce() throws InterruptedException {

        String[] arr = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        int i = 0;
        while (true) {
            int i1 = (int) (arr.length * Math.random());
            System.out.println(i++);
            Thread.sleep(500*i1);
            kafkaProdcer.send(new ProducerRecord<String, String>(TOPIC, arr[i1])
                    , (recordMetadata, e) -> System.out.println(recordMetadata));
        }
    }

    public static void main(String[] args) throws InterruptedException {

        KafkaProducerDemo kafkaProducerDemo = new KafkaProducerDemo();
        kafkaProducerDemo.produce();
    }
}
