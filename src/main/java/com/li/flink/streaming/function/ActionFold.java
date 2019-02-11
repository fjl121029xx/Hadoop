package com.li.flink.streaming.function;

import com.li.flink.streaming.StreamingBean;
import com.li.flink.streaming.util.StreamingUtils;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;

public class ActionFold {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment streamEnv = StreamingUtils.getEnv();

        DataStreamSource<StreamingBean> source = StreamingUtils.getSource(streamEnv, args);

        WindowedStream<Tuple2<Long, Integer>, Tuple, GlobalWindow> global = StreamingUtils.getGlobal(source, args);
        SingleOutputStreamOperator<String> fold = global.fold("", new Fold());

        fold.print();
        streamEnv.execute("kafka streaming");
    }

    private static class Fold implements FoldFunction<Tuple2<Long, Integer>, String> {

        @Override
        public String fold(String acc, Tuple2<Long, Integer> value) throws Exception {
            acc = Long.toString(value.f0);
            return acc + value.f1;
        }
    }
}
