package com.li.flink.kafka.tools.java;

import com.li.flink.kafka.utils.RedisUtils;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class JavaOffsetTools {

    public static Map<KafkaTopicPartition, Long> getSpecificStartOffsets() {

        Jedis jedis = RedisUtils.getJedis();
        String offset_0 = jedis.hget("bill_topic", "partition_0");
        if (offset_0 != null & offset_0.length() > 0) {

            Map<KafkaTopicPartition, Long> offsetMap = new HashMap<>();
            offsetMap.put(new KafkaTopicPartition("bill_topic", 0), Long.parseLong(offset_0));
            return offsetMap;
        } else {
            return null;
        }

    }
}
