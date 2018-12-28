package com.li.flink.zac;

import com.li.flink.kafka.demo.CustomWatermarkExtractor;
import com.li.flink.kafka.demo.KafkaEvent;
import com.li.flink.kafka.demo.KafkaEventSchema;
import com.li.flink.kafka.demo.RollingAdditionMapper;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.ArrayList;

public class AssessmentReport {

    public static void main(String[] args) throws Exception {


        final ParameterTool parameterTool = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<Tuple2<Long, Tuple5<Integer, ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Long>>> input = env
                .addSource(
                        new FlinkKafkaConsumer010<>(
                                parameterTool.getRequired("input-topic"),
                                new AnswerCardSchema(),
                                parameterTool.getProperties())
                                .assignTimestampsAndWatermarks(new acWatermarkExtreactor()))
                .map(new MapFunction<AnswerCard, Tuple2<Long, Tuple5<Integer, ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Long>>>() {

                    private static final long serialVersionUID = -8582093371946012225L;

                    @Override
                    public Tuple2<Long, Tuple5<Integer, ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Long>> map(AnswerCard value) throws Exception {

                        return Tuple2.of(
                                value.getUserId(),
                                Tuple5.of(
                                        value.getSubject(),
                                        value.getQuestions(),
                                        value.getCorrects(),
                                        value.getTimes(),
                                        value.getCreateTime()));
                    }
                });




        input.print();
        env.execute("answer card");

    }
}
