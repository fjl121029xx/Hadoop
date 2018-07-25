package com.li.kafka;

import java.util.*;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import redis.clients.jedis.Jedis;

/**
 * https://www.cnblogs.com/jun1019/p/6656223.html
 */
public class ProducerDemo {

    private static Map<String, String> result = new HashMap<>();

    private static final String topic = "animate2";
    private static final Integer threads = 1;
    private static final Properties props = new Properties();

    static {
        props.put("zookeeper.connect", "192.168.100.26:2188");
        props.put("metadata.broker.list", "192.168.100.26:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("num.partitions", "3");
    }

    public static void main(String[] args) throws Exception {

        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);

        String taskId = UUID.randomUUID().toString();

        String aus = "{\"usa\":" +
                "[" +
                "{\"conditionKey\":\"subject\",\"isEqual\":1,\"conditionValue\":\"1,100100594\"}," +
                "{\"conditionKey\":\"area\",\"isEqual\":0,\"conditionValue\":\"-9\"}," +
                "{\"conditionKey\":\"terminal\",\"isEqual\":1,\"conditionValue\":\"2\"}]}";
        int logic = 1;
        long begDate = 1530604662956L;
        long endDate = 1531795857526L;
        String dateType = "shi";

        String key = aus + "+" + logic + "+" + begDate + "+" + endDate + "+" + dateType;
        Jedis jedis = new Jedis("192.168.100.26", 6379);
        String result = jedis.get(key);
        if (result != null && !result.equals("")) {

            System.out.println(result);
        } else {
            producer.send(new KeyedMessage<String, String>("animate",
                    "" + taskId + "=/bin/sh /sparkwork/animate.sh " +
                            aus + " " +
                            logic + " " +
                            begDate + " " +
                            endDate + " " +
                            dateType + " "));

            String re;
            while (true) {

                String s = jedis.get(key);
                if (s != null && !s.equals("")) {
                    re = s;
                    break;
                }
            }

            System.out.println(re);
        }


    }
}