package com.li.flink.home.table.group.window;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.util.Properties;

public class SQLQueryOnGroupWindows {

    public static void main(String[] args) {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = TableEnvironment.getTableEnvironment(env);

        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");
        prop.setProperty("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181");
        prop.setProperty("group.id", "k1");

        FlinkKafkaConsumer010<String> kc = new FlinkKafkaConsumer010<>("kafka-record", new SimpleStringSchema(), prop);

        DataStream<Tuple3<Long, Integer, Long>> ds = env.addSource(kc).map(new MapT3());

        tEnv.registerDataStream("kv", ds, "k,v,t,proctime.proctime,rowtime.rowtime");

        Table result1 = tEnv.sqlQuery(
                "select k," +
                        " tumble_start(rowtime,interval '1' day) as wStart, " +
                        " sum(v) from kc" +
                        " grou[ by tumble(rowtime,interval '1' day),user");

        Table result2 = tEnv.sqlQuery(
                "select k,sum(v) from kv group by tumble(proctime,interval '1' day),user"
        );

        Table result3 = tEnv.sqlQuery("" +
                "select k,sum(v) from kv group by hop(rowtime,interval '1' hour,interval '1' day),product");

        Table result4 = tEnv.sqlQuery(
                "select user," +
                        " session_start(rowtime,interval '12' hour) as sStart," +
                        " session_rowtime(rowtime,interval '12' hour) as snd," +
                        " sum(v) " +
                        "from kv " +
                        "group by session(rowtime,interval '12' hour),user");


    }

    private static class MapT3 implements MapFunction<String, Tuple3<Long, Integer, Long>> {

        @Override
        public Tuple3<Long, Integer, Long> map(String s) throws Exception {

            JSONObject j = JSON.parseObject(s);
            return new Tuple3<>(j.getLong("k"), j.getInteger("v"), j.getLong("t"));
        }
    }
}
