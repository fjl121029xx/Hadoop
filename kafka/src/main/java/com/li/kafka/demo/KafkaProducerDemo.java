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
//        final char[] chars = "qazwsxedcrfvtgbyhnujmikolp".toCharArray();
        final char[] chars = "abcwsxs".toCharArray();
        final int charLength = chars.length;
        final String topic = "beifeng1";

        // 1. 创建一个Producer对象
        // 1.1 构建一个Properties以及给定连接kafka的相关producer的参数
        Properties props = new Properties();
        // a. 给定kafka的服务路径信息
//        props.put("metadata.broker.list", "hadoop-senior01.ibeifeng.com:9092,hadoop-senior01.ibeifeng.com:9093,hadoop-senior01.ibeifeng.com:9094,hadoop-senior01.ibeifeng.com:9095");
        props.put("metadata.broker.list", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");

        // b. 给定数据发送是否等待broker返回结果, 默认为0表示不等待
        props.put("request.required.acks", "0");
        // c. 给定数据发送方式，默认是sync==>同步发送数据，可以修改为异步发送数据(async)
        props.put("producer.type", "sync");
        // d. 给定消息序列化为byte数组的方式，默认为: kafka.serializer.DefaultEncoder, 默认情况下，要求Producer发送的数据类型是byte数组；如果发送string类型的数据，需要给定另外的Encoder编码器
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        // e. 数据发送默认采用hash的机制决定消息发送到那一个分区中，默认值为: kafka.producer.DefaultPartitioner, 参数为: partitioner.class
        // TODO: 有时候需要根据业务的需要自定义一个数据分区器
//        props.put("partitioner.class", "com.ibeifeng.kafka.producer.PartitionerDemo");PartitionerDemo
        props.put("partitioner.class", "com.li.kafka.demo.PartitionerDemo");
        // 1.2 构建ProducerConfig
        ProducerConfig config = new ProducerConfig(props);
        // 1.3 构建Producer
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

                        // 2. 初始化要发送的数据
                        KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(topic, key, value);

                        // 3. 发送数据
                        producer.send(keyedMessage);

                        // 4. 发送完数据后，休息一下
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

        // 3.  数据发送完成后，需要关闭连接
        // 一般情况下，是添加一个jvm的钩子，当jvm退出的时候进行数据关闭操作
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("关闭Producer.....");
                producer.close();
            }
        }));

    }
}
