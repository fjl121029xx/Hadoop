package com.li.flink.home.streaming.function;

import com.li.flink.home.streaming.StreamingBean;
import com.li.flink.home.streaming.util.StreamingUtils;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;

public class AverageAggregate {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment streamEnv = StreamingUtils.getEnv();

        DataStreamSource<StreamingBean> source = StreamingUtils.getSource(streamEnv, args);

        WindowedStream<Tuple2<Long, Integer>, Tuple, GlobalWindow> global = StreamingUtils.getGlobal(source, args);

        SingleOutputStreamOperator<Tuple2<Long, Double>> operator = global.aggregate(new Aggregate());

        operator.print();
        streamEnv.execute("kafka streaming");
    }

    private static class Aggregate implements AggregateFunction<Tuple2<Long, Integer>, Tuple2<Long, Tuple2<Long, Long>>, Tuple2<Long, Double>> {

        @Override
        public Tuple2<Long, Tuple2<Long, Long>> createAccumulator() {

            return new Tuple2<>(0L, new Tuple2<>(0L, 0L));
        }

        @Override
        public Tuple2<Long, Tuple2<Long, Long>> add(Tuple2<Long, Integer> value, Tuple2<Long, Tuple2<Long, Long>> accumulator) {

            Long key = value.f0;
            Integer right = value.f1;

            long l0 = accumulator.f1.f0 + right;
            long l1 = accumulator.f1.f1 + 1L;

            return new Tuple2<>(key, new Tuple2<>(l0, l1));
        }

        @Override
        public Tuple2<Long, Double> getResult(Tuple2<Long, Tuple2<Long, Long>> accumulator) {

            return new Tuple2<>(accumulator.f0, (double) accumulator.f1.f0 / accumulator.f1.f1);
        }

        @Override
        public Tuple2<Long, Tuple2<Long, Long>> merge(Tuple2<Long, Tuple2<Long, Long>> a, Tuple2<Long, Tuple2<Long, Long>> b) {

            return new Tuple2<>(a.f0, new Tuple2<>(a.f1.f0 + b.f1.f0, a.f1.f1 + b.f1.f1));
        }
    }

}
