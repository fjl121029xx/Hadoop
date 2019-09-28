package com.li.flink.kafka.hll;

import com.li.flink.kafka.demo.KafkaEvent;
import com.li.flink.kafka.hll.pojo.BillPojo;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class BillWatermarkExtractor implements AssignerWithPeriodicWatermarks<BillPojo> {

    private long currentTimestamp = Long.MIN_VALUE;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Override
    public long extractTimestamp(BillPojo bill, long l) {

        try {
            long time = sdf.parse(bill.getMaster().getReportDate().toString()).getTime();
            this.currentTimestamp = time;
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(currentTimestamp == Long.MIN_VALUE ? Long.MIN_VALUE : currentTimestamp - 1);
    }

}
