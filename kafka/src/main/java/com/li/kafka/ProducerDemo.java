package com.li.kafka;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

//import com.kenai.jaffl.annotations.In;
import org.apache.kafka.clients.producer.*;

/**
 * https://www.cnblogs.com/jun1019/p/6656223.html
 * https://blog.csdn.net/u010886217/article/details/83154773
 */
public class ProducerDemo {

    private static final Map<String, String> result = new HashMap<>();

    private static final String topic = "test";
    private static final Integer threads = 1;


    public static void send(String topic, String value, long sleep, int loop) throws Exception {


        int count = 0;
        synchronized (result) {

        }
    }

    public static void main(String[] args) throws Exception {


        int corePoolSize = 5;
        int maximumPoolSize = 10;
        long keepActiveTime = 200;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(5);

        //创建ThreadPoolExecutor线程池对象，并初始化该对象的各种参数
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepActiveTime, timeUnit, workQueue);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Properties properties = new Properties();
                    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "flinkice:9092");
                    //   properties.put(ProducerConfig.CLIENT_ID_CONFIG, "MsgProducer");// 自定义客户端id
                    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                            "org.apache.kafka.common.serialization.StringSerializer");// key
                    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                            "org.apache.kafka.common.serialization.StringSerializer");// value
                    // properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,CustomPartitioner.class.getCanonicalName());//自定义分区函数
                    // properties.load("properties配置文件");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {

//                        int a =(int) (Math.random()-1);
                        int b = (int) (10000 * Math.random());

                        int id = (int) (10 * Math.random());
//
                        String value = "{\"groupID\": 1,\"shopID\": 2 ,\"paidAmount\": 3.0 ,\"reportDate\": \"20210508174700\"  }";

                        send(producer, "latency-test", value);
                        Thread.sleep(b);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /*executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Properties properties = new Properties();
                    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.126.143:9092");
                    //   properties.put(ProducerConfig.CLIENT_ID_CONFIG, "MsgProducer");// 自定义客户端id
                    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                            "org.apache.kafka.common.serialization.StringSerializer");// key
                    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                            "org.apache.kafka.common.serialization.StringSerializer");// value
                    // properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,CustomPartitioner.class.getCanonicalName());//自定义分区函数
                    // properties.load("properties配置文件");
                    KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
                    String[] colors = {"RED", "GREEN", "YELLOW"};
                    while (true) {

                        String value = "{\"id\":1,\"color\":\"" + colors[(int) (3 * Math.random())] + "\"}";
                        send(producer, "tp_rule", value);
                        Thread.sleep(1500);
                        value = "{\"id\":2,\"color\":\"" + colors[(int) (3 * Math.random())] + "\"}";
                        send(producer, "tp_rule", value);
                        Thread.sleep(1500);
                        value = "{\"id\":0,\"color\":\"" + colors[(int) (3 * Math.random())] + "\"}";
                        send(producer, "tp_rule", value);
                        Thread.sleep(10000);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

    }

    public static void send(KafkaProducer<String, String> producer, String topic, String value) throws Exception {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "a", value);
        Future<RecordMetadata> h = producer.send(record, new MsgProducerCallback(System.currentTimeMillis(), "h", value));
        RecordMetadata recordMetadata = h.get();
        System.out.println(Thread.currentThread().getName() + "-----------");
        System.out.printf("send：%s%n  -->offset【%d】\r\n", value, recordMetadata.offset());
        System.out.println(recordMetadata.offset());
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
