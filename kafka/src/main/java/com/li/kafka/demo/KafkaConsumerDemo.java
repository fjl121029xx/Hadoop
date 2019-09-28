package com.li.kafka.demo;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.Decoder;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumerDemo {

    private ConsumerConnector connector = null;
    private String topicName = null;
    private int numThreads = 0;

    public KafkaConsumerDemo(String groupId, String zookeeperUrl, boolean largest, String topicName, int numThreads) {
        this.topicName = topicName;
        this.numThreads = numThreads;

        // 1. 给定Consumer连接的相关参数
        Properties props = new Properties();
        // a. 给定group id
        props.put("group.id", groupId);
        // b. 给定zk连接url
        props.put("zookeeper.connect", zookeeperUrl);
        // c. 给定自动提交offset偏移量间隔时间修改为2s(默认60s)
        props.put("auto.commit.interval.ms", "2000");
        // d. 给定初始化consumer时候的offset值（该值只有在第一次consumer消费数据的时候有效 --> 只要zk中保存了该consumer的offset偏移量信息，那么该参数就无效了）
        if (largest) {
            props.put("auto.offset.reset", "largest");
        } else {
            props.put("auto.offset.reset", "smallest");
        }
        // 2. 创建Consumer上下文
        ConsumerConfig config = new ConsumerConfig(props);
        // 3. 创建Consumer连接器
        this.connector = Consumer.createJavaConsumerConnector(config);
    }

    public void shutdown() {
        if (this.connector != null) {
            // 当调用shutdown后，KafkaStream所产生的ConsumerIterator迭代器就没有数据了
            this.connector.shutdown();
        }
    }

    public void run() {
        // TODO: topicCountMap给定消费者消费的Topic名称以及消费该Topic使用多少个线程进行数据消费操作；一个消费者可以消费多个Topic的数据 ==> key为topic名称，value为该topic数据消费的线程数
        final Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topicName, numThreads);
        Decoder<String> keyDecoder = new StringDecoder(new VerifiableProperties());
        Decoder<String> valueDecoder = new StringDecoder(new VerifiableProperties());

        // 2. 根据参数创建数据读取流
        // TODO: 该API返回的集合中的数据是一个以Topic名称为Key，以该Topic的读取数据流集合为Value的一个Map集合；
        // TODO: List<KafkaStream<String, String>> ==> 指的其实就是对应Topic消费数据的流，该List集合中的流对象数目和给定的topicCountMap中该topic对应的count值一样
        // TODO: List<KafkaStream<String, String>> ==> 如果一个Topic有多个分区，而且在topicCountMap中该topic给定的count值大于分区数，那么其实表示一个KafkaStream流消费一个Topic分区的数据；这里类似Kafka的Consumer Group Rebalance ===> 一个分区的数据只允许一个KafkaStream消费，但是一个KafkaStream可以消费多个分区的数据(>=0)
        Map<String, List<KafkaStream<String, String>>> consumerStreamsMap = this.connector.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);

        // 3. 获取对应Topic的数据消费流
        List<KafkaStream<String, String>> streams = consumerStreamsMap.get(topicName);

        // 4. 数据消费
        int k = 0;
        for (final KafkaStream<String, String> stream : streams) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    String threadNames = Thread.currentThread().getName();
                    ConsumerIterator<String, String> iter = stream.iterator();
                    while (iter.hasNext()) {
                        // 获取数据
                        MessageAndMetadata<String, String> messageAndMetadata = iter.next();

                        // 处理数据
                        StringBuilder sb = new StringBuilder();
                        sb.append("线程").append(threadNames);
                        // 1. 获取元数据
                        long offset = messageAndMetadata.offset();
                        int partitionID = messageAndMetadata.partition();
                        String topicName = messageAndMetadata.topic();
                        // TODO: 元数据存储，方便做容错
                        // TODO: 这里可以将元数据保存zk/redis/mysql...(可以考虑一下怎么保存)
                        sb.append(";元数据=>[").append("offset=").append(offset).append("; partitionID=").append(partitionID).append("; topicName=").append(topicName).append("]");

                        // 2. 获取消息(key/value键值对)
                        String value = messageAndMetadata.message();
                        String key = messageAndMetadata.key();
                        sb.append("; 消息=>[key=").append(key).append("; value=").append(value).append("]");

                        System.out.println(sb.toString());
                        count++;
                    }
                    System.out.println("线程" + threadNames + "总共消费数据" + count + "条!!!");
                }
            }, "Thread-[" + k + "]-[" + topicName + "]").start();
            k++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String groupId = "170505_2";
        String zookeeperUrl = "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181";
        boolean largest = true;
        String topicName = "beifeng1";
        int numThreads = 1;
        KafkaConsumerDemo demo = new KafkaConsumerDemo(groupId, zookeeperUrl, largest, topicName, numThreads);
        demo.run();

        // 休息一段数据后关闭
        Thread.sleep(2000);

        demo.shutdown();
    }
}
