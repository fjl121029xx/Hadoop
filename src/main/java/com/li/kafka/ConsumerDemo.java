package com.li.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class ConsumerDemo {
    private static final String topic = "animate2";
    private static final Integer threads = 1;

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.100.26:2188");
        props.put("group.id", "group-1");
        props.put("serializer.class", "kafka.serializer.StringEncoder");

        ConsumerConfig config = new ConsumerConfig(props);
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(config);
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, threads);

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get("animate2");

        for (final KafkaStream<byte[], byte[]> kafkaStream : streams) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (MessageAndMetadata<byte[], byte[]> mm : kafkaStream) {
                        String msg = new String(mm.message());
                        System.out.println(msg);
                    }
                }

            }).start();

        }
    }
}