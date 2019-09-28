package com.li.flink.home.table.streaming.concepts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.li.flink.home.streaming.util.StreamingUtils;
import com.li.flink.home.table.bean.Pojo;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.StreamQueryConfig;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.WindowedTable;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.api.java.Tumble;

public class DataStreamToTable {

    public static void main(String[] args) {

        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Pojo> stream = StreamingUtils.getKafkaSource(sEnv).map(new toPojo());

        StreamTableEnvironment tEnv = TableEnvironment.getTableEnvironment(sEnv);
        StreamQueryConfig qConfig = tEnv.queryConfig();

        qConfig.withIdleStateRetentionTime(Time.hours(12),Time.hours(24));


        Table table = tEnv.fromDataStream(stream, "key,value,recordTime.proctime");

        WindowedTable windowedTable = table.window(Tumble.over("1.minutes").on("recordTime").as("userActionWindow"));



    }



    private static class toPojo implements MapFunction<String, Pojo> {

        @Override
        public Pojo map(String s) throws Exception {
            JSONObject j = JSON.parseObject(s);
            return new Pojo(j.getLong("key"), j.getInteger("value"), j.getLong("recordTime"));
        }
    }
}
