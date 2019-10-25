package com.li.kafka;

import java.util.*;
import java.util.concurrent.Future;

//import com.kenai.jaffl.annotations.In;
import org.apache.kafka.clients.producer.*;

/**
 * https://www.cnblogs.com/jun1019/p/6656223.html
 * https://blog.csdn.net/u010886217/article/details/83154773
 */
public class ProducerDemo {

    private static Map<String, String> result = new HashMap<>();

    private static final String topic = "bill";
    private static final Integer threads = 1;
    private static final Properties props = new Properties();

    private static final String[] arr = {"a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g",
            "h",
            "i",
            "j",
            "k",
            "l",
            "m",
            "n",
            "o",
            "p",
            "q",
            "r",
            "s",
            "t",
            "u",
            "v",
            "w",
            "x",
            "y",
            "z"};

    static {
        props.put("metadata.broker.list", "172.20.119.175:9092,172.20.47.150:9092,172.20.101.187:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
//        props.put("num.partitions", "3");
    }

    public static void main(String[] args) throws Exception {


        Random r = new Random(arr.length);

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.20.119.175:9092,172.20.47.150:9092,172.20.101.187:9092");
//        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "MsgProducer");// 自定义客户端id
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");// key
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");// value
        // 序列号方式
        // properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,CustomPartitioner.class.getCanonicalName());//自定义分区函数

        // properties.load("properties配置文件");
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);


        int count = 0;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String value = arr[0];
//            String value = arr[r.nextInt(arr.length)];
            ProducerRecord<String, String> record = new ProducerRecord<>("bill", Integer.toString((i / 3)), value);
            Future<RecordMetadata> h = producer.send(record, new MsgProducerCallback(System.currentTimeMillis(), "h", value));
            RecordMetadata recordMetadata = h.get();
            count++;
            System.out.println(count);
//            System.out.println(recordMetadata.offset());
        }
    }
}

/**
 * 消息发送后的回调函数
 */
class MsgProducerCallback implements Callback {

    private final long startTime;
    private final String key;
    private final String msg;

    public MsgProducerCallback(long startTime, String key, String msg) {
        this.startTime = startTime;
        this.key = key;
        this.msg = msg;
    }

    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (recordMetadata != null) {
            System.out.println(msg + " be sended to partition no : " + recordMetadata.partition());
        }
    }
}
