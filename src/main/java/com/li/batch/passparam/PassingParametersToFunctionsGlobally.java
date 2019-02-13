package com.li.batch.passparam;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public class PassingParametersToFunctionsGlobally {


    public static void main(String[] args) throws Exception {

        Configuration config = new Configuration();
        config.setString("mykey", "myvalue");

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(config);


        DataSource<String> toFilter = env.fromElements("");

        toFilter.flatMap(new Tokenizer()).withParameters(config).print();

    }

    public static final class Tokenizer extends RichFlatMapFunction<String, Tuple2<String, Integer>> {

        private String mykey;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            ExecutionConfig.GlobalJobParameters globalParams = getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
            Configuration globConf = (Configuration) globalParams;
            mykey = globConf.getString("mykey", null);
        }


        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {

        }
    }
}