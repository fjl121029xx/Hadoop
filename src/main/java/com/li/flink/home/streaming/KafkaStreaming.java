package com.li.flink.home.streaming;

import com.li.flink.home.streaming.util.StreamingUtils;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

/**
 * --input-topic kafka-record --output-topic flink_out --bootstrap.servers 192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:100.68:2181,192.168.100.70:2181,192.168.100.72:2181 --group.id k1
 */
public class KafkaStreaming {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment streamEnv = StreamingUtils.getEnv();

        DataStreamSource<StreamingBean> source = StreamingUtils.getSource(streamEnv, args);

//        source.print();
        /**
         * Tumbling Windows
         */
//        AllWindowedStream<StreamingBean, TimeWindow> windowAll = source.windowAll(TumblingEventTimeWindows.of(Time.seconds(10), Time.hours(-8)));
        /**
         * Sliding Windows
         */
//        AllWindowedStream<StreamingBean, TimeWindow> windowAll = source.windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)));
        /**
         * Session Windows
         */
//        WindowedStream<Tuple2<Long, Integer>, Tuple, TimeWindow> window = source.map(new MapFunction<StreamingBean, Tuple2<Long, Integer>>() {
//            @Override
//            public Tuple2<Long, Integer> map(StreamingBean value) throws Exception {
//                return new Tuple2<>(value.getKey(), value.getValue());
//            }
//        }).keyBy(0)
//                .window(EventTimeSessionWindows.withDynamicGap(new SessionWindowTimeGapExtractor<Tuple2<Long, Integer>>() {
//                    @Override
//                    public long extract(Tuple2<Long, Integer> t) {
//
//                        if (t.f0 == 0L) {
//                            return 4000;
//                        } else {
//                            return 2000;
//                        }
//
//                    }
//                }));
//        DataStream<Tuple2<Long, Integer>> apply = window.apply(new WindowFunction<Tuple2<Long, Integer>, Tuple2<Long, Integer>, Tuple, TimeWindow>() {
//            @Override
//            public void apply(Tuple tuple, TimeWindow window, Iterable<Tuple2<Long, Integer>> input, Collector<Tuple2<Long, Integer>> out) throws Exception {
//
//                int sum = 0;
//                for (Tuple2<Long, Integer> t : input) {
//                    sum += 1;
//                }
//                out.collect(new Tuple2<>(Long.parseLong(tuple.getField(0).toString()), sum));
//
//            }
//        });
        /**
         * Global Windows
         */
        WindowedStream<Tuple2<Long, Integer>, Tuple, GlobalWindow> global = StreamingUtils.getGlobal(source, args);;

        SingleOutputStreamOperator<Tuple2<Long, Integer>> apply = global.apply(new WindowFunction<Tuple2<Long, Integer>, Tuple2<Long, Integer>, Tuple, GlobalWindow>() {
            @Override
            public void apply(Tuple tuple, GlobalWindow window, Iterable<Tuple2<Long, Integer>> input, Collector<Tuple2<Long, Integer>> out) throws Exception {

                int sum = 0;
                for (Tuple2<Long, Integer> t : input) {
                    sum += 1;
                }

                out.collect(new Tuple2<>(Long.parseLong(tuple.getField(0).toString()), sum));
            }
        });

        apply.print();
        streamEnv.execute("kafka streaming");
    }
}
