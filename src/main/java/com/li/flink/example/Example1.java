package com.li.flink.example;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Example1 {

    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> text = env.readTextFile("H:\\workspaces\\ideaProject\\Hadoop\\src\\main\\java\\com\\li\\flink\\example\\Example1.java");

        DataStream<Integer> parsed = text.map((MapFunction<String, Integer>) value -> value.length())
                ;
        parsed.print();
        env.execute("Example1");
    }
}
