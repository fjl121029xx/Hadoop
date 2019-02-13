package com.li.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class WordCountExample {

    public static void main(String[] args) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSource<String> text = env.fromElements(
                "Who's there?",
                "I think I hear them. Stand, ho! Who's there?"
        );

        DataSet<Tuple2<String, Integer>> wordCount = text.flatMap(new LineSplitter())
                .groupBy(0)
                .sum(1);
        wordCount.print();

    }

    private static class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String line, Collector<Tuple2<String, Integer>> out) throws Exception {

            for (String work : line.split(" ")) {
                out.collect(new Tuple2<>(work, 1));
            }
        }
    }
}
