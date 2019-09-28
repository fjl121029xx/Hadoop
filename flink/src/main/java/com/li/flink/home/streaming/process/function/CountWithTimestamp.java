package com.li.flink.home.streaming.process.function;

import com.li.flink.home.streaming.StreamingBean;
import com.li.flink.home.streaming.util.StreamingUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

/**
 *
 */
public class CountWithTimestamp {

    public long key;
    public int count;
    public long lastModified;

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment streamEnv = StreamingUtils.getEnv();

        DataStreamSource<StreamingBean> s1 = StreamingUtils.getSource(streamEnv, args);

        SingleOutputStreamOperator<Tuple2<Long, Integer>> process = s1.map(new MapFunction<StreamingBean, Tuple2<Long, Integer>>() {
            @Override
            public Tuple2<Long, Integer> map(StreamingBean value) throws Exception {
                return new Tuple2<>(value.getKey(), value.getValue());
            }
        }).keyBy(0).process(new CountWithTimeoutFunction());
        process.print();

        streamEnv.execute("CountWithTimestamp");
    }

    private static class CountWithTimeoutFunction extends ProcessFunction<Tuple2<Long, Integer>, Tuple2<Long, Integer>> {

        private static final long serialVersionUID = -8654083892589121460L;
        private ValueState<CountWithTimestamp> state;

        @Override
        public void open(Configuration parameters) throws Exception {
            state = getRuntimeContext().getState(new ValueStateDescriptor<>("myState", CountWithTimestamp.class));
        }

        @Override
        public void processElement(Tuple2<Long, Integer> value, Context ctx, Collector<Tuple2<Long, Integer>> out) throws Exception {

            CountWithTimestamp current = state.value();
            if (current == null) {
                current = new CountWithTimestamp();
                current.key = value.f0;
            }

            current.count++;
            current.lastModified = ctx.timestamp();

            state.update(current);

            ctx.timerService().registerEventTimeTimer(current.lastModified + 60000);
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<Long, Integer>> out) throws Exception {

            CountWithTimestamp result = state.value();

            if (timestamp == result.lastModified + 60000) {
                out.collect(new Tuple2<Long, Integer>(result.key, result.count));
            }
        }
    }
}


