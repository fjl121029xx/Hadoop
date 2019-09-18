package com.li.flink.official.streaming.overview;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class MyDataSources {


    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.addSource(new SourceFunction<Object>() {
            @Override
            public void run(SourceContext<Object> ctx) throws Exception {

            }

            @Override
            public void cancel() {

            }
        });
    }

    public static class M extends RichParallelSourceFunction {

        @Override
        public void run(SourceContext ctx) throws Exception {

        }

        @Override
        public void cancel() {

        }
    }
}
