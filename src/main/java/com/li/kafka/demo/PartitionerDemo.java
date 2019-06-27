package com.li.kafka.demo;

import kafka.utils.VerifiableProperties;
import kafka.producer.Partitioner;

public class PartitionerDemo implements Partitioner {

    public PartitionerDemo(VerifiableProperties props) {
        // 该构造函数必须添加
        // props其实就是Producer连接Kafka服务的配置参数
    }

    //    @Override
    public int partition(Object key, int numPartitions) {
        // 假设key的偶数到分区0，奇数到分区1,
        // 如果分区数量等于1，那么全部数据到分区0
        if (numPartitions == 1) {
            return 0;
        } else {
            String str = (String) key;
            int num = Integer.valueOf(str.replace("key_", ""));
            return num % 2;
        }
    }
}
