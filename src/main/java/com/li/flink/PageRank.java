package com.li.flink;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;

public class PageRank {

    public static void main(String[] args) {


        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSource<Tuple2<Long, Double>> pagesWithRanks = env.readCsvFile("").types(Long.class, Double.class);

    }
}
