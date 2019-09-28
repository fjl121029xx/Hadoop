package com.li.flink.home.batch.passparam;

import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.configuration.Configuration;

public class PassingParametersToFunctionsViaConfiguration {

    public static void main(String[] args) throws Exception {

        Configuration config = new Configuration();
        config.setInteger("limit", 1);

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSource<Integer> toFilter = env.fromElements(1, 2, 3);

        toFilter.filter(new MyFilter2()).withParameters(config).print();

    }

    private static class MyFilter2 extends RichFilterFunction<Integer> {

        private  int limit;

        @Override
        public void open(Configuration parameters) throws Exception {
            limit = parameters.getInteger("limit", 0);
        }

        @Override
        public boolean filter(Integer value) throws Exception {
            return value > limit;
        }
    }
}
