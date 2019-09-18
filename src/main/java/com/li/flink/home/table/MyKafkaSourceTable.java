package com.li.flink.home.table;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.li.flink.home.streaming.connector.KafkaConsumer;
import com.li.flink.home.table.bean.Pojo;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sinks.CsvTableSink;
import scala.Tuple3;

import java.util.Properties;

public class MyKafkaSourceTable {


    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");
        prop.setProperty("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181");
        prop.setProperty("group.id", "k1");

        FlinkKafkaConsumer010<String> kc = new FlinkKafkaConsumer010<>("kafka-record", new SimpleStringSchema(), prop);

        DataStream<Pojo> pojp = env.addSource(kc).map(new toPojo());

        StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);
        Table t = tableEnv.fromDataStream(pojp, "key, value, recordTime");

        Table result = tableEnv.sqlQuery("SELECT * FROM " + t );


        tableEnv.toAppendStream(result, Pojo.class).print();
        // execute
        env.execute();
    }

    private static class toPojo implements MapFunction<String, Pojo> {

        @Override
        public Pojo map(String s) throws Exception {
            JSONObject j = JSON.parseObject(s);
            return new Pojo(j.getLong("key"), j.getInteger("value"), j.getLong("recordTime"));
        }
    }

}
