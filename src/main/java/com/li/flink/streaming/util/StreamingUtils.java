package com.li.flink.streaming.util;

import com.li.flink.streaming.StreamingBean;
import com.li.flink.streaming.StreamingBeanSchema;
import com.li.flink.streaming.WaterMark;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

public class StreamingUtils {

    public static StreamExecutionEnvironment getEnv() {

        StreamExecutionEnvironment streamEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        streamEnv.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
        return streamEnv;
    }

    public static DataStreamSource<StreamingBean> getSource(StreamExecutionEnvironment streamEnv, String[] args) {

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        return streamEnv.addSource(
                new FlinkKafkaConsumer010<>(
                        parameterTool.getRequired("input-topic"),
                        new StreamingBeanSchema(),
                        parameterTool.getProperties())
                        .assignTimestampsAndWatermarks(new WaterMark()));
    }

    public static WindowedStream<Tuple2<Long, Integer>, Tuple, GlobalWindow> getGlobal(DataStreamSource<StreamingBean> source, String[] args) {

        WindowedStream<Tuple2<Long, Integer>, Tuple, GlobalWindow> global = source.map(new MapFunction<StreamingBean, Tuple2<Long, Integer>>() {
            @Override
            public Tuple2<Long, Integer> map(StreamingBean value) throws Exception {
                return new Tuple2<>(value.getKey(), value.getValue());
            }
        }).keyBy(0).window(GlobalWindows.create()).trigger(new Trigger<Tuple2<Long, Integer>, GlobalWindow>() {
            @Override
            public TriggerResult onElement(Tuple2<Long, Integer> element, long timestamp, GlobalWindow window, TriggerContext ctx) throws Exception {

                long l = System.currentTimeMillis();

                return l > 1549869180000L ? TriggerResult.FIRE : TriggerResult.CONTINUE;
            }

            @Override
            public TriggerResult onProcessingTime(long time, GlobalWindow window, TriggerContext ctx) throws Exception {

                return TriggerResult.CONTINUE;
            }

            @Override
            public TriggerResult onEventTime(long time, GlobalWindow window, TriggerContext ctx) throws Exception {
                return TriggerResult.CONTINUE;
            }

            @Override
            public void clear(GlobalWindow window, TriggerContext ctx) throws Exception {

            }
        });

        return global;
    }
}
