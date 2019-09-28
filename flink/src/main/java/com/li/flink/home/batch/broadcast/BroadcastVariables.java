package com.li.flink.home.batch.broadcast;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.configuration.Configuration;

import java.util.List;

public class BroadcastVariables {

    public static void main(String[] args) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSource<Integer> toBroadcast = env.fromElements(1, 2, 3);

        DataSource<String> data = env.fromElements("a", "b");

        env.registerCachedFile("hdfs:///path/to/your/file", "hdfsFile");

        MapOperator<String, String> set = data.map(new RichMapFunction<String, String>() {

            private List<Integer> broadcastSet;

            @Override
            public void open(Configuration parameters) throws Exception {
                broadcastSet = getRuntimeContext().getBroadcastVariable("broadcastSetName");
            }

            @Override
            public String map(String s) throws Exception {
                return s + "~" + broadcastSet.get((int) (Math.random() * 3));
            }
        }).withBroadcastSet(toBroadcast, "broadcastSetName");

        set.print();
    }
}
