package com.li.flink.kafka.demo;

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

public class CustomWatermarkExtractor implements AssignerWithPeriodicWatermarks<KafkaEvent> {

    private long currentTimestamp = Long.MIN_VALUE;

    @Override
    public long extractTimestamp(KafkaEvent event, long l) {

        this.currentTimestamp = event.getTimestamp();
        return event.getTimestamp();
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(currentTimestamp == Long.MIN_VALUE ? Long.MIN_VALUE : currentTimestamp - 1);
    }

}
