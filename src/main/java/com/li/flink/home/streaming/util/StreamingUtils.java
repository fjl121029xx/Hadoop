package com.li.flink.home.streaming.util;

import com.li.flink.home.streaming.StreamingBean;
import com.li.flink.home.streaming.StreamingBeanSchema;
import com.li.flink.home.streaming.WaterMark;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
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

import java.util.Properties;

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

    public static DataStreamSource getKafkaSource( StreamExecutionEnvironment env){

        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");
        prop.setProperty("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181");
        prop.setProperty("group.id", "k1");

        FlinkKafkaConsumer010<String> kc = new FlinkKafkaConsumer010<>("kafka-record", new SimpleStringSchema(), prop);

        DataStreamSource<String> s1 = env.addSource(kc);

        return s1;
    }
}
