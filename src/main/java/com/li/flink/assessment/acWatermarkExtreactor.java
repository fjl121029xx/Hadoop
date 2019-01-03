package com.li.flink.assessment;

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

public class acWatermarkExtreactor implements AssignerWithPeriodicWatermarks<KafkaAnswerCard> {


    private long currentTimestamp = Long.MIN_VALUE;

    @Override
    public long extractTimestamp(KafkaAnswerCard element, long previousElementTimestamp) {
        return element.getCreateTime();
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {

        return new Watermark(currentTimestamp == Long.MIN_VALUE ? Long.MIN_VALUE : currentTimestamp - 1);
    }
}
