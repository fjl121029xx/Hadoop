package com.li.flink.home.streaming.asyncio;

import com.li.flink.home.streaming.StreamingBean;
import com.li.flink.home.streaming.util.StreamingUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * --input-topic kafka-record --output-topic flink_out --bootstrap.servers 192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:100.68:2181,192.168.100.70:2181,192.168.100.72:2181 --group.id k1
 */
public class AsynDatabaseRequest extends RichAsyncFunction<Tuple2<Long, Integer>, Tuple2<String, Integer>> {

    private transient Map<Long, String> databaseClient;

    @Override
    public void open(Configuration parameters) throws Exception {
        databaseClient = new HashMap<>();
        databaseClient.put(0L, "339");
        databaseClient.put(1L, "tony");
        databaseClient.put(2L, "tom");
        databaseClient.put(3L, "rose");
    }

    @Override
    public void close() throws Exception {
        databaseClient.clear();
    }

    @Override
    public void asyncInvoke(Tuple2<Long, Integer> key, ResultFuture<Tuple2<String, Integer>> resultFuture) throws Exception {

        final String result = databaseClient.get(key.f0);

        CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {

                try {
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).thenAccept(dbResult -> resultFuture.complete(Collections.singleton(new Tuple2<>(dbResult, key.f1))));

    }

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment streamEnv = StreamingUtils.getEnv();

        DataStreamSource<StreamingBean> s1 = StreamingUtils.getSource(streamEnv, args);
        SingleOutputStreamOperator<Tuple2<Long, Integer>> map = s1.map(new MapFunction<StreamingBean, Tuple2<Long, Integer>>() {
            @Override
            public Tuple2<Long, Integer> map(StreamingBean value) throws Exception {
                return new Tuple2<>(value.getKey(), value.getValue());
            }
        });

        SingleOutputStreamOperator<Tuple2<String, Integer>> wait = AsyncDataStream.unorderedWait(map, new AsynDatabaseRequest(), 1000, TimeUnit.MILLISECONDS, 100);
        wait.print();

        streamEnv.execute("CountWithTimestamp");
    }
}
