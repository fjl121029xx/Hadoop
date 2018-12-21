package com.li.flink.kafka.demo;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;

public class RollingAdditionMapper extends RichMapFunction<KafkaEvent, KafkaEvent> {

    private transient ValueState<Integer> currentTotalCount;

    @Override
    public KafkaEvent map(KafkaEvent event) throws Exception {

        Integer totalCount = currentTotalCount.value();

        if (totalCount == null) {
            totalCount = 0;
        }
        totalCount += event.getUserPlayTime();

        currentTotalCount.update(totalCount);

        return new KafkaEvent(event.getUserId(), totalCount, event.getTimestamp());
    }

    @Override
    public void open(Configuration parameters) throws Exception {

        currentTotalCount=getRuntimeContext().getState(new ValueStateDescriptor<Integer>("currentTotalCount",Integer.class));
    }
}
