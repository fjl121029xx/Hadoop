package com.li.flink.official.streaming.overview;

import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;

import java.io.IOException;

public class MonitorFile {

    public static void main(String[] args) throws Exception {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.readFile(new FileInputFormat<String>() {
            @Override
            public boolean reachedEnd() throws IOException {
                return false;
            }

            @Override
            public String nextRecord(String reuse) throws IOException {
                System.out.println(reuse);
                return reuse;
            }
        }, "doc/hive_metastore/hivadata.txt", FileProcessingMode.PROCESS_CONTINUOUSLY, 30L)
                .print();

        env.execute("1");
    }
}
