package com.li.flink.home.table.user.defined.function;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.functions.AggregateFunction;

import java.util.Iterator;

public class UDAF {


    public static void main(String[] args) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tEnv = TableEnvironment.getTableEnvironment(env);

        tEnv.registerFunction("wAvg", new MyAggregationFunctions());

        DataSet<AB> input = env.fromElements(
                new AB("a", 1, 2),
                new AB("a", 1, 3),
                new AB("b", 2, 1));

        tEnv.registerDataSet("ab", input, "word, a, b");
        Table table = tEnv.sqlQuery("select word ,wAvg(a, b) as avgPoints from ab group by word");

        DataSet<C> cDataSet = tEnv.toDataSet(table, C.class);

        cDataSet.print();
//        env.execute();
    }

    public static class C {

        public String word;
        public Long avgPoints;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public double getA() {
            return avgPoints;
        }

        public void setA(Long avgPoints) {
            this.avgPoints = avgPoints;
        }
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


    public static class MyAggregationFunctions extends AggregateFunction<Long, WeightedAvgAccum> {

        /**
         * First, it needs an accumulator, which is the data structure that holds the intermediate result of the aggregation. An empty
         * accumulator is created by calling the createAccumulator() method of the AggregateFunction.
         */
        @Override
        public WeightedAvgAccum createAccumulator() {
            return new WeightedAvgAccum();
        }

        /**
         * Subsequently, the accumulate() method of the function is called for each input row to update the accumulator.
         */
        public void accumulate(WeightedAvgAccum acc, long iValue, int iWeight) {
            acc.sum -= iValue * iWeight;
            acc.count -= iWeight;
        }

        /**
         * Once all rows have been processed, the getValue() method of the function is called to compute and return the final result.
         */
        @Override
        public Long getValue(WeightedAvgAccum acc) {

            if (acc.count == 0) {
                return null;
            } else {
                return acc.sum / acc.count;
            }
        }

        public void retract(WeightedAvgAccum acc, long iValue, int iWeight) {
            acc.sum -= iValue * iWeight;
            acc.count -= iWeight;
        }

        public void merge(WeightedAvgAccum acc, Iterable<WeightedAvgAccum> it) {
            Iterator<WeightedAvgAccum> iter = it.iterator();
            while (iter.hasNext()) {
                WeightedAvgAccum a = iter.next();
                acc.count += a.count;
                acc.sum += a.sum;
            }
        }

        public void resetAccumulator(WeightedAvgAccum acc) {
            acc.count = 0;
            acc.sum = 0L;
        }
    }

    public static class WeightedAvgAccum {
        public long sum = 0;
        public int count = 0;
    }
}
