package com.li.flink.home.table.external.systems;

import org.apache.flink.api.java.io.jdbc.JDBCAppendTableSink;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.*;
import org.apache.flink.table.sinks.CsvTableSink;

public class TableExample {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tEnv = TableEnvironment.getTableEnvironment(env);

        String path = "external/systems/kafka2csv";

        tEnv.connect(new Kafka()
                .version("0.10")
                .topic("test-topic")
                .startFromEarliest()
                .property("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181")
                .property("bootstrap.servers", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092"))
                .withFormat(new Json()
                        .failOnMissingField(true)
                        .jsonSchema("{" +
                                "          type: 'object'," +
                                "          properties: {" +
                                "            key: {" +
                                "              type: 'number'" +
                                "            }," +
                                "            value: {" +
                                "              type: 'number'" +
                                "            }," +
                                "            recordTime: {" +
                                "              type: 'string'," +
                                "              format: 'date-time'" +
                                "            }" +
                                "          }" +
                                "        }")
                        .deriveSchema()

                )
                .withSchema(new Schema()
                        .field("key", Types.LONG())
                        .field("value", Types.INT())
                        .field("rd", Types.SQL_TIMESTAMP()).from("recordTime"))
                .inAppendMode()
                .registerTableSource("MyUserTable");


        Table table = tEnv.sqlQuery("select * from MyUserTable");

//        JDBCAppendTableSink sink = JDBCAppendTableSink.builder()
//                .setDrivername("com.jdbc.mysql.Driver")
//                .setDBUrl("jdbc:mysql://")
//                .setQuery("INSERT INTO books (id) VALUES (?)")
//                .setParameterTypes(Types.INT())
//                .build();

        table.writeToSink(new CsvTableSink(path,
                "|",
                1,
                FileSystem.WriteMode.OVERWRITE));
        env.execute();
    }

    /**
     * new Csv()
     *     .field("f1", Types.STRING())
     *     .field("f2", Types.SQL_TIMESTAMP())
     *     .fieldDelimiter(",")
     *     .lineDelimiter("\n")
     *     .quoteCharacter('"')
     *     .commentPrefix("#")
     *     .ignoreFirstLine()
     *     .ignoreParseErrors()
     */
}
