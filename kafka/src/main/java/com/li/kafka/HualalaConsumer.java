package com.li.kafka;

import java.util.Properties;

public class HualalaConsumer {

    public static void main(String[] args) {


        Properties props = new Properties();
//        props.put("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181");
        props.put("metadata.broker.list", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");
        props.put("group.id", "group-1");
        props.put("serializer.class", "kafka.serializer.StringEncoder");

    }
}
