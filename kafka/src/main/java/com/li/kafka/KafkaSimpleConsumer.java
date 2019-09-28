package com.li.kafka;


import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.cluster.Broker;
import kafka.cluster.BrokerEndPoint;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.Message;
import kafka.message.MessageAndOffset;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;


public class KafkaSimpleConsumer {

    public static void main(String[] args) {

        BrokerEndPoint leadder = findLeadder("", "", 3);
        if (leadder == null) {
            System.out.println("未找到leader的信息");
            return;
        }

        SimpleConsumer simpleConsumer = new SimpleConsumer(
                leadder.host(), leadder.port(), 20000, 1000, "mySimpleConsumer"
        );
        long startOffet = 0;
        int fetchSize = 10000;

        while (true) {
            // 添加fetch制定目标topic，分区，起始pffset及fetchSize(字节),可以添加多个fetch
            long offset = startOffet;
            FetchRequest req = new FetchRequestBuilder().addFetch("", 3, startOffet, fetchSize).build();
            // 拉取消息
            FetchResponse fetchResponse = simpleConsumer.fetch(req);
            ByteBufferMessageSet messageSet = fetchResponse.messageSet("TOPIC", 3);
            if (messageSet.sizeInBytes() == 0) {
                continue;
            }
            for (MessageAndOffset messageAndOffset : messageSet) {

                Message mess = messageAndOffset.message();
                ByteBuffer payload = mess.payload();
                byte[] bytes = new byte[payload.limit()];
                payload.get(bytes);
                String msg = new String(bytes);
                offset = messageAndOffset.offset();
                System.out.println("Thread : " + Thread.currentThread().getId() + " partition : " + ", offset : " + offset);
            }
            startOffet = offset + 1;
        }

    }

    public static PartitionMetadata findPartitionMetadata(String brokerHosts, String topic, int partition) {

        PartitionMetadata returnMetaData = null;
        for (String brokerHost : brokerHosts.split(",")) {

            SimpleConsumer consumer = null;
            String[] splits = brokerHost.split(":");
            consumer = new SimpleConsumer(splits[0], Integer.valueOf(splits[1]), 100000, 64 * 1024, "leaderLookup");

            List<String> topics = Collections.singletonList(topic);

            TopicMetadataRequest request = new TopicMetadataRequest(topics);
            /**
             * 获取topic的元数据
             */
            TopicMetadataResponse response = consumer.send(request);

            List<TopicMetadata> topicMetadatas = response.topicsMetadata();

            for (TopicMetadata topicMetadata : topicMetadatas) {

                for (PartitionMetadata PartitionMetadata : topicMetadata.partitionsMetadata()) {

                    if (PartitionMetadata.partitionId() == partition) {

                        returnMetaData = PartitionMetadata;
                        break;//找到元数据，程序可以退出了
                    }
                }
            }
            if (consumer != null)
                consumer.close();
        }
        return returnMetaData;
    }

    public static BrokerEndPoint findLeadder(String brokerHosts, String topic, int partition) {

        PartitionMetadata partitionMetadata = findPartitionMetadata(brokerHosts, topic, partition);
        if (partitionMetadata == null) {
            System.out.println("未找到leader信息");
            return null;
        }
        BrokerEndPoint leader = partitionMetadata.leader();
        System.out.println(String.format("Leader tor topic %s, partition %d is %s:%d", topic, partition, leader.host(), leader.port()));
        return leader;
    }
}
