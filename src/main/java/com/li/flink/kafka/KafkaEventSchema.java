package com.li.flink.kafka;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.text.ParseException;

public class KafkaEventSchema implements DeserializationSchema<KafkaEvent>, SerializationSchema<KafkaEvent> {


    @Override
    public byte[] serialize(KafkaEvent event) {

        return event.toString().getBytes();
    }

    @Override
    public KafkaEvent deserialize(byte[] message) throws IOException {

        try {
            return KafkaEvent.fromString(new String(message));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new KafkaEvent();
    }

    @Override
    public boolean isEndOfStream(KafkaEvent nextElement) {

        return false;
    }

    @Override
    public TypeInformation<KafkaEvent> getProducedType() {

        return TypeInformation.of(KafkaEvent.class);
    }
}
