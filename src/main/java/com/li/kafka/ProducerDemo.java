package com.li.kafka;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class ProducerDemo {
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put("zk.connect", "192.168.233.137:2181,192.168.233.138:2181,192.168.233.139:2181");
		props.put("metadata.broker.list","192.168.233.137:9092,192.168.233.138:9092,192.168.233.139:9092");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		ProducerConfig config = new ProducerConfig(props);
		Producer<String, String> producer = new Producer<String, String>(config);

		// ����ҵ����Ϣ
		// ��ȡ�ļ� ��ȡ�ڴ����ݿ� ��socket�˿�
		for (int i = 1; i <= 100; i++) {
			Thread.sleep(500);
			producer.send(new KeyedMessage<String, String>("wordcount",
					"i said i love you baby for" + i + "times,will you have a nice day with me tomorrow"));
		}

	}
}