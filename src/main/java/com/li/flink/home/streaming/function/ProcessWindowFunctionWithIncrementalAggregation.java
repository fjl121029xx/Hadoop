package com.li.flink.home.streaming.function;

import com.li.flink.home.streaming.StreamingBean;
import com.li.flink.home.streaming.util.StreamingUtils;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

/**
 * ProcessWindowFunction with Incremental Aggregation
 * A ProcessWindowFunction can be combined with either a ReduceFunction, an AggregateFunction, or a FoldFunction to incrementally
 * aggregate elements as they arrive in the window. When the window is closed, the ProcessWindowFunction will be provided with the
 * aggregated result. This allows it to incrementally compute windows while having access to the additional window meta information of the
 * ProcessWindowFunction.
 *
 * --input-topic kafka-record --output-topic flink_out --bootstrap.servers 192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:100.68:2181,192.168.100.70:2181,192.168.100.72:2181 --group.id k1
 */
public class ProcessWindowFunctionWithIncrementalAggregation {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment streamEnv = StreamingUtils.getEnv();

        DataStreamSource<StreamingBean> source = StreamingUtils.getSource(streamEnv, args);

        WindowedStream<Tuple2<Long, Integer>, Tuple, GlobalWindow> global = StreamingUtils.getGlobal(source, args);

        SingleOutputStreamOperator<Tuple2<Long, Integer>> reduce = global.reduce(new MyReduceFunction(), new MyProcessWindowFunction());
        reduce.print();
        streamEnv.execute("kafka streaming");
    }

    private static class MyReduceFunction implements ReduceFunction<Tuple2<Long, Integer>> {

        @Override
        public Tuple2<Long, Integer> reduce(Tuple2<Long, Integer> r1, Tuple2<Long, Integer> r2) throws Exception {

            return new Tuple2<>(r1.f0, r1.f1 > r2.f1 ? r2.f1 : r1.f1);
        }
    }

    private static class MyProcessWindowFunction extends ProcessWindowFunction<Tuple2<Long, Integer>, Tuple2<Long,Integer>, Tuple, GlobalWindow> {

        @Override
        public void process(Tuple tuple, Context context, Iterable<Tuple2<Long, Integer>> elements, Collector<Tuple2<Long, Integer>> out) throws Exception {
            for (Tuple2<Long, Integer> t :elements) {
                out.collect(t);
            }
        }
    }
}
