package com.li.flink.streaming.function;

import com.li.flink.streaming.StreamingBean;
import com.li.flink.streaming.util.StreamingUtils;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class ProcessWindowFunctionWithIncrementalAggregation {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment streamEnv = StreamingUtils.getEnv();

        DataStreamSource<StreamingBean> source = StreamingUtils.getSource(streamEnv, args);


        streamEnv.execute("kafka streaming");
    }

    private static class MyReduceFunction implements ReduceFunction<Tuple2<Long, Integer>> {

        @Override
        public Tuple2<Long, Integer> reduce(Tuple2<Long, Integer> value1, Tuple2<Long, Integer> value2) throws Exception {
            return null;
        }
    }

    private static class MyProcessWindowFunction extends ProcessWindowFunction<Tuple2<Long, Integer>, Tuple2<Long, Long>, Long, TimeWindow> {

        @Override
        public void process(Long aLong, Context context, Iterable<Tuple2<Long, Integer>> elements, Collector<Tuple2<Long, Long>> out) throws Exception {

        }
    }
}
