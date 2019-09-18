package com.li.flink.home.table;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.li.flink.home.streaming.util.StreamingUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sinks.CsvTableSink;

public class CsvSink {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        DataStreamSource s1 = StreamingUtils.getKafkaSource(env);

        DataStream<Pojo> pojp = s1.map(new toPojo());

        StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);

        /**
         * Convert a DataStream or DataSet into a Table
         */
        Table result = tableEnv.fromDataStream(pojp);


        CsvTableSink csvSink = new CsvTableSink("path/to/file", "|", 3, FileSystem.WriteMode.OVERWRITE);

        String[] fieldNames = {"key", "value", "value"};
        TypeInformation[] fieldTypes = {Types.LONG, Types.INT, Types.LONG};

        tableEnv.registerTableSink("CsvSinkTable", fieldNames, fieldTypes, csvSink);

        result.writeToSink(csvSink);
        env.execute();
    }

    private static class toPojo implements MapFunction<String, Pojo> {

        @Override
        public Pojo map(String s) throws Exception {
            JSONObject j = JSON.parseObject(s);
            return new Pojo(j.getLong("key"), j.getInteger("value"), j.getLong("recordTime"));
        }
    }

    public static class Pojo {
        public Long key;
        public Integer value;
        public Long recordTime;

        public Pojo() {
        }

        public Pojo(Long key, Integer value, Long recordTime) {
            this.key = key;
            this.value = value;
            this.recordTime = recordTime;
        }

        @Override
        public String toString() {
            return "Pojo{" +
                    "key=" + key +
                    ", value=" + value +
                    ", recordTime=" + recordTime +
                    '}';
        }
    }
}
