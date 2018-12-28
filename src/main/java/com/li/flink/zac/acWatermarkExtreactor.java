package com.li.flink.zac;

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

public class acWatermarkExtreactor implements AssignerWithPeriodicWatermarks<AnswerCard> {


    private long currentTimestamp = Long.MIN_VALUE;

    @Override
    public long extractTimestamp(AnswerCard element, long previousElementTimestamp) {
        return element.getCreateTime();
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {

        return new Watermark(currentTimestamp == Long.MIN_VALUE ? Long.MIN_VALUE : currentTimestamp - 1);
    }
}
