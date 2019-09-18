package com.li.flink.home.table.streaming.concepts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.li.flink.home.streaming.util.StreamingUtils;
import com.li.flink.home.table.bean.Pojo;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sources.DefinedProctimeAttribute;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.table.util.TableConnectorUtil;
import org.apache.flink.types.Row;
import org.apache.flink.table.api.Types;

import javax.annotation.Nullable;

public class UserActionSource implements StreamTableSource<Row>, DefinedProctimeAttribute {

    private final TableSchema schema;

    public UserActionSource(TableSchema schema) {
        this.schema = schema;
    }

    @Nullable
    @Override
    public String getProctimeAttribute() {
        return "recordTime";
    }

    @Override
    public DataStream<Row> getDataStream(StreamExecutionEnvironment execEnv) {

        DataStream<Row> stream = StreamingUtils.getKafkaSource(execEnv).map((MapFunction<String, Row>) value -> {

            JSONObject j = JSON.parseObject(value);
            return Row.of(j.getLong("key"), j.getInteger("value"));
        });
        return stream;
    }

    @Override
    public TypeInformation<Row> getReturnType() {
        String[] names = new String[]{"key", "value"};
        TypeInformation[] types = new TypeInformation[]{Types.STRING(), Types.STRING()};
        return Types.ROW(names, types);
    }

    @Override
    public TableSchema getTableSchema() {
        return this.schema;
    }

    @Override
    public String explainSource() {
        return TableConnectorUtil.generateRuntimeName(this.getClass(), schema.getColumnNames());
    }

    public static void main(String[] args) {

        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tEnv = TableEnvironment.getTableEnvironment(sEnv);
//
//        tEnv.registerTableSource("UserActions", new StreamTableSource<Pojo>() {
//            @Override
//            public DataStream<Pojo> getDataStream(StreamExecutionEnvironment execEnv) {
//                return null;
//            }
//
//            @Override
//            public TableSchema getTableSchema() {
//                return null;
//            }
//
//            @Override
//            public TypeInformation<Pojo> getReturnType() {
//                return null;
//            }
//
//            @Override
//            public String explainSource() {
//                return TableConnectorUtil.generateRuntimeName(this.getClass(), schema.getColumnNames());
//            }
//        });


    }
}
