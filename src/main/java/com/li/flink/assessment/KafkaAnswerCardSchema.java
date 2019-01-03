package com.li.flink.assessment;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.text.ParseException;

public class KafkaAnswerCardSchema implements DeserializationSchema<KafkaAnswerCard>, SerializationSchema<KafkaAnswerCard> {

    @Override
    public byte[] serialize(KafkaAnswerCard element) {
        return element.toString().getBytes();
    }

    @Override
    public KafkaAnswerCard deserialize(byte[] message) throws IOException {

        try {
            return KafkaAnswerCard.fromString(new String(message));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new KafkaAnswerCard();
    }

    @Override
    public boolean isEndOfStream(KafkaAnswerCard nextElement) {

        return false;
    }


    @Override
    public TypeInformation<KafkaAnswerCard> getProducedType() {

        return TypeInformation.of(KafkaAnswerCard.class);
    }
}
