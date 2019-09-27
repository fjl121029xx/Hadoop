package com.li.flink.kafka.hll;

import com.li.flink.kafka.hll.pojo.BillPojo;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.text.ParseException;

public class BillSchema implements DeserializationSchema<BillPojo>, SerializationSchema<BillPojo> {

    @Override
    public byte[] serialize(BillPojo event) {

        return event.toString().getBytes();
    }

    @Override
    public BillPojo deserialize(byte[] message) throws IOException {

        try {
            return BillPojo.fromString(new String(message));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new BillPojo();
    }

    @Override
    public boolean isEndOfStream(BillPojo nextElement) {

        return false;
    }

    @Override
    public TypeInformation<BillPojo> getProducedType() {

        return TypeInformation.of(BillPojo.class);
    }
}
