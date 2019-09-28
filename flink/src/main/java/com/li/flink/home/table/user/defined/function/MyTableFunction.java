package com.li.flink.home.table.user.defined.function;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.sinks.CsvTableSink;

public class MyTableFunction extends TableFunction<Tuple2<String, Integer>> {

    private String separator = ",";

    public MyTableFunction(String separator) {
        this.separator = separator;
    }

    public void eval(String str) {
        for (String s : str.split(separator)) {
            // use collect(...) to emit a row
            collect(new Tuple2<>(s, s.length()));
        }
    }

    public static void main(String[] args) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tEnv = TableEnvironment.getTableEnvironment(env);

        tEnv.registerFunction("fun", new MyTableFunction(","));

        DataSet<WC> input = env.fromElements(
                new WC("Hello,a,b", 1),
                new WC("Ciao,c,d", 1),
                new WC("Hello,e,f", 1));

        // register the DataSet as table "WordCount"
        tEnv.registerDataSet("WordCount", input, "word, frequency");

        // run a SQL query on the Table and retrieve the result as a new Table
        Table table = tEnv.sqlQuery(
                "SELECT word,w,l from WordCount, lateral table(fun(word)) as T(w,l)");

        CsvTableSink csvSink = new CsvTableSink("path/to/file", "|", 1, FileSystem.WriteMode.OVERWRITE);

        String[] fieldNames = {"a", "b", "c"};
        TypeInformation[] fieldTypes = {Types.STRING, Types.STRING, Types.STRING};

        tEnv.registerTableSink("CsvSinkTable", fieldNames, fieldTypes, csvSink);

        table.writeToSink(csvSink);

        env.execute();
    }

    public static class WC {
        public String word;
        public long frequency;

        // public constructor to make it a Flink POJO
        public WC() {
        }

        public WC(String word, long frequency) {
            this.word = word;
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return "AB " + word + " " + frequency;
        }
    }
}
