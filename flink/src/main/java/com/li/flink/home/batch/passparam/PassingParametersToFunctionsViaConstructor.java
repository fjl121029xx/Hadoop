package com.li.flink.home.batch.passparam;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;

public class PassingParametersToFunctionsViaConstructor {

    public static void main(String[] args) throws Exception {


        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSource<Integer> toFilter = env.fromElements(1, 2, 3);

        toFilter.filter(new MyFilter(2)).print();

    }

    private static class MyFilter implements FilterFunction<Integer> {

        private final int limit;

        public MyFilter(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean filter(Integer value) throws Exception {
            return value > limit;
        }
    }
}
