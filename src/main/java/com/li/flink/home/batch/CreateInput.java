package com.li.flink.home.batch;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.types.Row;


public class CreateInput {

    public static void main(String[] args) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        TypeInformation[] fieldTypes = {
                BasicTypeInfo.STRING_TYPE_INFO
        };

        RowTypeInfo rowTypeInfo = new RowTypeInfo(fieldTypes);

//        JDBCInputFormat jdbcInputFormat = JDBCInputFormat.buildJDBCInputFormat()
//                .setDrivername("com.mysql.jdbc.Driver")
//                .setDBUrl("jdbc:mysql://192.168.100.18/vhuatu")
//                .setUsername("vhuatu")
//                .setPassword("vhuatu_2013")
//                .setQuery("select uname from v_qbank_user")
//                .setRowTypeInfo(rowTypeInfo)
//                .finish();
//
//        DataSource<Row> s = env.createInput(jdbcInputFormat);
//
//        BatchTableEnvironment tableEnv = new BatchTableEnvironment(env, TableConfig.DEFAULT());
//
//        tableEnv.registerDataSet("t2",s,"uname");
//        tableEnv.sqlQuery("select * from t2").printSchema();
//
//        Table t = tableEnv.sqlQuery("select * from t2");
//        DataSet<Row> result = tableEnv.toDataSet(t, Row.class);
//
//        result.print();

//        System.out.println(s.count());
    }


}
