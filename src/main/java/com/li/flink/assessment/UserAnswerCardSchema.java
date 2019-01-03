package com.li.flink.assessment;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.text.ParseException;

public class UserAnswerCardSchema implements DeserializationSchema<UserAnswerCard>, SerializationSchema<UserAnswerCard> {

    @Override
    public byte[] serialize(UserAnswerCard element) {
        return element.toString().getBytes();
    }

    @Override
    public UserAnswerCard deserialize(byte[] message) throws IOException {

        try {
            return UserAnswerCard.fromString(new String(message));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new UserAnswerCard();
    }

    @Override
    public boolean isEndOfStream(UserAnswerCard nextElement) {

        return false;
    }


    @Override
    public TypeInformation<UserAnswerCard> getProducedType() {

        return TypeInformation.of(UserAnswerCard.class);
    }
}
