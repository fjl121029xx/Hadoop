package com.li.kafka;

import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class KafkaProducerUtil extends Thread {

    private String topic;


    public KafkaProducerUtil(String topic) {
        super();
        this.topic = topic;
    }

    private Producer<String, String> createProducer() {
        // 通过Properties类设置Producer的属性
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.126.143:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<String, String>(properties);
    }

    @SneakyThrows
    @Override
    public void run() {
        Producer<String, String> producer = createProducer();
        Random random = new Random();
        Random random2 = new Random();

        int i = 0;
        while (true) {
            int nums = random.nextInt(10);
            int nums2 = random2.nextInt(10);

            String time = System.currentTimeMillis() / 1000 + 5 + "";
            String type = "pv";
            try {
                if (nums2 % 2 == 0) {
                    type = "pv";
                } else {
                    type = "uv";
                }
                String kaifa_log = "{\"code\":\"" + type + "\",\"total_emp\":\"1" + "\",\"ts\":" + time + "}";
                System.out.println("kaifa_log = " + kaifa_log);
                producer.send(new ProducerRecord<String, String>(this.topic, kaifa_log));

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("=========循环" + (i++) + "次==========");
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new KafkaProducerUtil("flink_dwd_test4").run();

    }
}
