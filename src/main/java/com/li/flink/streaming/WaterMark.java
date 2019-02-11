package com.li.flink.streaming;

import com.li.flink.answer.record.KafkaAnswerCard;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

public class WaterMark implements AssignerWithPeriodicWatermarks<StreamingBean> {

    private long currentTimestamp = Long.MIN_VALUE;

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {

        return new Watermark(currentTimestamp == Long.MIN_VALUE ? Long.MIN_VALUE : currentTimestamp - 1);
    }

    @Override
    public long extractTimestamp(StreamingBean element, long previousElementTimestamp) {

        return element.getRecordTime();
    }
}
