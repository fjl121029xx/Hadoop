package com.li.flink.home.table.specifying.quesy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.li.flink.home.table.MyKafkaSourceTable;
import com.li.flink.home.table.bean.Pojo;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sinks.CsvTableSink;

import java.util.Properties;

public class SpecifyingQuery {

    public static void main(String[] args) throws Exception {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = TableEnvironment.getTableEnvironment(env);

        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");
        prop.setProperty("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181");
        prop.setProperty("group.id", "k1");

        FlinkKafkaConsumer010<String> kc = new FlinkKafkaConsumer010<>("kafka-record", new SimpleStringSchema(), prop);

        DataStream<Tuple3<Long, Integer, Long>> stream = env.addSource(kc).map(new toPojo());

        Table table = tEnv.fromDataStream(stream, "k,v,time");
        Table result = tEnv.sqlQuery(String.format("select sum(v) from %s where k=1", table));

        tEnv.registerDataStream("kv1", stream, "k,v,t");
        Table result2 = tEnv.sqlQuery(
                "select v,t from kv1 where k=2"
        );

        CsvTableSink csvSink = new CsvTableSink("path/to/file", "|", 1, FileSystem.WriteMode.OVERWRITE);
        String[] fieldNames = {"v", "t"};
        TypeInformation[] fieldTypes = {Types.INT, Types.LONG};
        tEnv.registerTableSink("rubber", fieldNames, fieldTypes, csvSink);
        tEnv.sqlUpdate(
                "insert into rubber select v,t from kv1 where k=2"
        );

        env.execute();
    }

    private static class toPojo implements MapFunction<String, Tuple3<Long, Integer, Long>> {

        @Override
        public Tuple3<Long, Integer, Long> map(String s) throws Exception {

            JSONObject j = JSON.parseObject(s);
            return new Tuple3<>(j.getLong("k"), j.getInteger("v"), j.getLong("t"));
        }
    }
}
