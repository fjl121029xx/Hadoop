package com.li.flink.home.streaming;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.text.ParseException;

public class StreamingBeanSchema implements DeserializationSchema<StreamingBean>, SerializationSchema<StreamingBean> {

    @Override
    public byte[] serialize(StreamingBean element) {
        return element.toString().getBytes();
    }

    @Override
    public StreamingBean deserialize(byte[] message) throws IOException {

        try {
            return StreamingBean.fromString(new String(message));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new StreamingBean();
    }

    @Override
    public boolean isEndOfStream(StreamingBean nextElement) {

        return false;
    }


    @Override
    public TypeInformation<StreamingBean> getProducedType() {

        return TypeInformation.of(StreamingBean.class);
    }
}
