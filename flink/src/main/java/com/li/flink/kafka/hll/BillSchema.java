package com.li.flink.kafka.hll;

import com.alibaba.fastjson.JSON;
import com.li.flink.kafka.hll.pojo.BillPojo;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.text.ParseException;

public class BillSchema implements DeserializationSchema<BillPojo>, SerializationSchema<BillPojo> {

    @Override
    public byte[] serialize(BillPojo bill) {

        return JSON.toJSONString(bill).getBytes();
    }

    @Override
    public BillPojo deserialize(byte[] message) {

        return JSON.parseObject(new String(message), BillPojo.class);
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
