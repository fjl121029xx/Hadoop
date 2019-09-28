package com.li.flink.home.libraries.cep;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.util.List;
import java.util.Map;

public class CEPExample {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);


        // (Event, timestamp)
        DataStream<Event> input = env.fromElements(
                Tuple2.of(new Event(1, "start", 1.0), 5L),
                Tuple2.of(new Event(2, "middle", 2.0), 1L),
                Tuple2.of(new Event(3, "end", 3.0), 3L),
                Tuple2.of(new Event(4, "end", 4.0), 10L), //触发2，3，1
                Tuple2.of(new Event(5, "middle", 5.0), 7L),
                // last element for high final watermark
                Tuple2.of(new Event(5, "middle", 5.0), 100L) //触发5，4
        ).assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<Tuple2<Event, Long>>() {

            @Override
            public long extractTimestamp(Tuple2<Event, Long> element, long previousTimestamp) {
                return element.f1; //定义Eventtime
            }

            @Override
            public Watermark checkAndGetNextWatermark(Tuple2<Event, Long> lastElement, long extractedTimestamp) {
                return new Watermark(lastElement.f1 - 5); //定义watermark
            }

        }).map(new MapFunction<Tuple2<Event, Long>, Event>() {
            @Override
            public Event map(Tuple2<Event, Long> value) throws Exception {
                return value.f0;
            }
        });

        Pattern<Event, ?> pattern = Pattern.<Event>begin("start").where(new SimpleCondition<Event>() {

            @Override
            public boolean filter(Event value) throws Exception {
                return value.getType().equals("start");
            }
        }).followedByAny("middle").where(new SimpleCondition<Event>() {

            @Override
            public boolean filter(Event value) throws Exception {
                return value.getType().equals("middle");
            }
        }).followedByAny("end").where(new SimpleCondition<Event>() {

            @Override
            public boolean filter(Event value) throws Exception {
                return value.getType().equals("end");
            }
        });

        DataStream<String> result =  CEP.pattern(input,pattern).select(
                new PatternSelectFunction<Event, String>() {
                    @Override
                    public String select(Map<String, List<Event>> pattern) throws Exception {
                        StringBuilder builder = new StringBuilder();
                        System.out.println(pattern);
                        builder.append(pattern.get("start").get(0).getId()).append(",")
                                .append(pattern.get("middle").get(0).getId()).append(",")
                                .append(pattern.get("end").get(0).getId());
                        return builder.toString();
                    }
                }
        );

        result.print();
        env.execute();
    }

    public static class Event {

        public int id;
        public String type;
        public double score;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public Event() {
        }

        public Event(int id, String type, double score) {
            this.id = id;
            this.type = type;
            this.score = score;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", score=" + score +
                    '}';
        }
    }
}
