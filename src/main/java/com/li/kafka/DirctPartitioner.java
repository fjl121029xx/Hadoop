package com.li.kafka;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

public class DirctPartitioner implements Partitioner {

    public DirctPartitioner(VerifiableProperties props) {
        // 该构造函数必须添加
        // props其实就是Producer连接Kafka服务的配置参数
    }

    @Override
    public int partition(Object key, int numPartitions) {
        return 0;
    }
}
