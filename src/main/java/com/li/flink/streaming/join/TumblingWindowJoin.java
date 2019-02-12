package com.li.flink.streaming.join;

import com.li.flink.streaming.StreamingBean;
import com.li.flink.streaming.util.StreamingUtils;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * stream.join(otherStream)
 * .where(<KeySelector>)
 * .equalTo(<KeySelector>)
 * .window(<WindowAssigner>)
 * .apply(<JoinFunction>)
 * <p>
 * --input-topic kafka-record --output-topic flink_out --bootstrap.servers 192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:100.68:2181,192.168.100.70:2181,192.168.100.72:2181 --group.id k1
 */
public class TumblingWindowJoin {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment streamEnv = StreamingUtils.getEnv();

        DataStreamSource<StreamingBean> s1 = StreamingUtils.getSource(streamEnv, args);
        DataStreamSource<StreamingBean> s2 = StreamingUtils.getSource(streamEnv, args);

        DataStream<String> j = s1.join(s2)
                .where(new MyKeySelector())
                .equalTo(new MyKeySelector())
                .window(TumblingEventTimeWindows.of(Time.seconds(10l)))
                .apply(new MyJoinFun());
        j.print();

        streamEnv.execute("Window Join");

    }

    private static class MyKeySelector implements KeySelector<StreamingBean, Long> {

        @Override
        public Long getKey(StreamingBean s) throws Exception {
            return s.getKey();
        }
    }

    private static class MyJoinFun implements JoinFunction<StreamingBean, StreamingBean, String> {

        @Override
        public String join(StreamingBean a, StreamingBean b) throws Exception {
            return a.getKey() + "~" + a.getValue() + "~" + b.getValue();
        }
    }
}
