package com.li.flink.kafka.msg;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;

import java.io.IOException;

public class TypedKeyedDeserializationSchema implements KeyedDeserializationSchema<KafkaMsg> {
    @Override
    public KafkaMsg deserialize(byte[] key, byte[] value, String topic, int partition, long offset) throws IOException {
        return new KafkaMsg(
                new String(key,"UTF-8"),
                new String(value,"UTF-8"),
                topic, partition, offset);
    }

    @Override
    public boolean isEndOfStream(KafkaMsg nextElement) {
        return false;
    }


    @Override
    public TypeInformation getProducedType() {

        TypeInformation<KafkaMsg> info = TypeInformation.of(KafkaMsg.class);
        return info;
    }

}
