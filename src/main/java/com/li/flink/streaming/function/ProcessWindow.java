package com.li.flink.streaming.function;

import com.li.flink.streaming.StreamingBean;
import com.li.flink.streaming.util.StreamingUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class ProcessWindow {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment streamEnv = StreamingUtils.getEnv();

        DataStreamSource<StreamingBean> source = StreamingUtils.getSource(streamEnv, args);


        SingleOutputStreamOperator<String> process =
                source.map(new MapFunction<StreamingBean, Tuple2<Long, Integer>>() {
                    @Override
                    public Tuple2<Long, Integer> map(StreamingBean value) throws Exception {
                        return new Tuple2<>(value.getKey(), value.getValue());
                    }
                }).keyBy(0).timeWindow(Time.seconds(10))
                        .process(new MyProcesssWindowFunction());


        process.print();
        streamEnv.execute("kafka streaming");
    }

    private static class MyProcesssWindowFunction extends ProcessWindowFunction<Tuple2<Long, Integer>, String, Tuple, TimeWindow> {

        @Override
        public void process(Tuple tuple, Context context, Iterable<Tuple2<Long, Integer>> input, Collector<String> out) throws Exception {

            long count = 0;
            for (Tuple2<Long, Integer> in : input) {
                count++;
            }
            out.collect("Window: " + context.window() + "count: " + count);
        }
    }

}
