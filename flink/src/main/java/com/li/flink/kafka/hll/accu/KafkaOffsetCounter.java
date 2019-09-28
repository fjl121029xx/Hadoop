package com.li.flink.kafka.hll.accu;

import org.apache.flink.api.common.accumulators.Accumulator;
import org.apache.flink.api.common.accumulators.SimpleAccumulator;


public class KafkaOffsetCounter implements SimpleAccumulator<Long> {

    private String topic;
    private int partition;
    private long offset;

    public KafkaOffsetCounter() {
    }


    public KafkaOffsetCounter(String topic, int partition, long offset) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
    }

    @Override
    public void add(Long newoffset) {
        this.offset = newoffset;
    }

    @Override
    public Long getLocalValue() {
        return offset;
    }

    @Override
    public void resetLocal() {
        offset = 0L;
    }

    @Override
    public void merge(Accumulator<Long, Long> other) {
        Long otherOffset = other.getLocalValue();
        if (otherOffset > offset) {
            this.offset = otherOffset;
        }

    }

    @Override
    public KafkaOffsetCounter clone() {
        KafkaOffsetCounter counter = new KafkaOffsetCounter(
                topic, partition, offset
        );
        return counter;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
