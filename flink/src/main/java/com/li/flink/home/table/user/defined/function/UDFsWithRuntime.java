package com.li.flink.home.table.user.defined.function;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.ScalarFunction;

public class UDFsWithRuntime {


    public static void main(String[] args) {


        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tEnv = TableEnvironment.getTableEnvironment(env);

        // set job parameter
        Configuration conf = new Configuration();
        conf.setString("hashcode_factor", "31");
        env.getConfig().setGlobalJobParameters(conf);


        DataSet<AB> input = env.fromElements(
                new AB("a", 1, 2),
                new AB("a", 1, 3),
                new AB("b", 2, 8));

        tEnv.registerDataSet("ab", input, "word, a, b");
        Table table = tEnv.sqlQuery("select word ,wAvg(a, b) as avgPoints from ab group by word");


        // register the function
        tEnv.registerFunction("hashCode", new HashCode());

        // use the function in Java Table API
        table.select("string, string.hashCode(), hashCode(string)");

        // use the function in SQL
        tEnv.sqlQuery("SELECT string, HASHCODE(string) FROM MyTable");
    }


    public static class AB {

        public String word;
        public Long a;
        public Integer b;

        public AB() {
        }

        public AB(String word, long a, Integer b) {
            this.word = word;
            this.a = a;
            this.b = b;
        }
    }

    public static class HashCode extends ScalarFunction {

        private int factor = 0;

        @Override
        public void open(FunctionContext context) throws Exception {
            // access "hashcode_factor" parameter
            // "12" would be the default value if parameter does not exist
            factor = Integer.valueOf(context.getJobParameter("hashcode_factor", "12"));
        }

        public int eval(String s) {
            return s.hashCode() * factor;
        }
    }
}
