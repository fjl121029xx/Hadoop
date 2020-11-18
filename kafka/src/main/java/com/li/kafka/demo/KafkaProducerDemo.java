package com.li.kafka.demo;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;
import java.util.Random;

public class KafkaProducerDemo {

    public static void main(String[] args) {

//        System.setProperty("java.security.auth.login.config", "D:\\kafka_client_jaas.conf");

        final char[] chars = "abcwsxs".toCharArray();
        final int charLength = chars.length;
        final String topic = "shyue";

        Properties props = new Properties();
        props.put("metadata.broker.list", "shiyue:9092");
        props.put("request.required.acks", "0");
        props.put("producer.type", "sync");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("partitioner.class", "com.li.kafka.demo.PartitionerDemo");

        ProducerConfig config = new ProducerConfig(props);
        final Producer<String, String> producer = new Producer<String, String>(config);
        // 2. 多线程的形式发送数据
        final Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 初始化一个发送数据的值, [100, 1099]
                    int events = random.nextInt(1000) + 100;
                    String threadName = Thread.currentThread().getName();
                    for (int j = 0; j < events; j++) {
                        if (j != 0 && j % 100 == 0) {
                            System.out.println("线程[" + threadName + "]已经发送" + j + "条数据!");
                        }

                        // 1. 初始化key和message
                        String key = "key_" + random.nextInt(100);
                        // 假设message由单词构成，单词数量范围为:[1, 10]
                        StringBuilder sb = new StringBuilder();
                        int wordNums = random.nextInt(10) + 1;
                        for (int k = 0; k < wordNums; k++) {
                            // 单词由单个字符构成，数量范围为: [1, 10]
                            StringBuilder sb2 = new StringBuilder();
                            int charNums = random.nextInt(10) + 1;
                            for (int l = 0; l < charNums; l++) {
                                sb2.append(chars[random.nextInt(charLength)]);
                            }
                            sb.append(sb2.toString().trim()).append(" ");
                        }
                        String value = sb.toString().trim();

                        KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(topic, key, value);

                        producer.send(keyedMessage);

                        try {
                            Thread.sleep(random.nextInt(50) + 10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("线程[" + threadName + "]总共发送" + events + "条数据!!!!");
                }
            }, "Thread-" + i).start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("关闭Producer.....");
                producer.close();
            }
        }));

    }
}
