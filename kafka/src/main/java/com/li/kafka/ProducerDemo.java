package com.li.kafka;

import java.text.SimpleDateFormat;
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

    private static final String topic = "test";
    private static final Integer threads = 1;


    public static void main(String[] args) throws Exception {


        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.126.143:9092");
//        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "MsgProducer");// 自定义客户端id
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");// key
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");// value
        // 序列号方式
        // properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,CustomPartitioner.class.getCanonicalName());//自定义分区函数

        // properties.load("properties配置文件");
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {

            String value = "jesyck,3";
//            String value = "{\\\"ts\\\":\\\"\"+sdf.format(new Date())+\"\\\",\\\"item_id\\\":\\\"123456\\\", \\\"behavior\\\":\\\"68193426\"}";
//            String value = "{\"createTime\":\""+sdf.format(new Date())+"\",\"mtWmPoiId\":\"123456\",\"platform\":\"3\",\"sessionId\":\"3444444\",\"shopName\":\"店名\",\"source\":\"shoplist\",\"userId\":\"268193426\"}";
//            String value = arr[0];
//            String value = arr[r.nextInt(arr.length)];
            ProducerRecord<String, String> record = new ProducerRecord<>("test", Integer.toString((i / 3)), value);
            Future<RecordMetadata> h = producer.send(record, new MsgProducerCallback(System.currentTimeMillis(), "h", value));
            RecordMetadata recordMetadata = h.get();
            count++;
            System.out.printf("第%d次 send：%s%n  -->offset【%d】\r\n", count, value, recordMetadata.offset());
            Thread.sleep(10*1000);
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

    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (recordMetadata != null) {
            System.out.println(msg + " be sended to partition no : " + recordMetadata.partition());
        }
    }
}
