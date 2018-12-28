package com.li.flink.zac;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.text.ParseException;

public class AnswerCardSchema implements DeserializationSchema<AnswerCard>, SerializationSchema<AnswerCard> {

    @Override
    public byte[] serialize(AnswerCard element) {
        return element.toString().getBytes();
    }

    @Override
    public AnswerCard deserialize(byte[] message) throws IOException {

        try {
            return AnswerCard.fromString(new String(message));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new AnswerCard();
    }

    @Override
    public boolean isEndOfStream(AnswerCard nextElement) {
        return false;
    }


    @Override
    public TypeInformation<AnswerCard> getProducedType() {
        return TypeInformation.of(AnswerCard.class);
    }
}
