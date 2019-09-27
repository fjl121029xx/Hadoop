package com.li.flink.kafka.hll;

import com.li.flink.kafka.demo.KafkaEvent;
import com.li.flink.kafka.hll.pojo.BillPojo;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

public class BillWatermarkExtractor implements AssignerWithPeriodicWatermarks<BillPojo> {

    private long currentTimestamp = Long.MIN_VALUE;

    @Override
    public long extractTimestamp(BillPojo bill, long l) {


        this.currentTimestamp = bill.getMaster().getReportDate();
        return bill.getMaster().getReportDate();
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(currentTimestamp == Long.MIN_VALUE ? Long.MIN_VALUE : currentTimestamp - 1);
    }

}
