package com.li.flink.kafka.hll;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.li.flink.kafka.hll.pojo.BillPojo;
import com.li.flink.kafka.utils.KafkaFlinkUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

public class BillConsumer {


    public static void main(String[] args) throws Exception {

        final ParameterTool parameterTool = ParameterTool.fromArgs(args);
        StreamExecutionEnvironment env = KafkaFlinkUtil.prepareExecutionEnv(parameterTool);

        SingleOutputStreamOperator<BillPojo> input = env
                .addSource(
                        new FlinkKafkaConsumer010<>(
                                parameterTool.getRequired("input-topic"),
                                new BillSchema(),
                                parameterTool.getProperties())
                                .assignTimestampsAndWatermarks(new BillWatermarkExtractor()));


        DataStream<String> map = input.map(new MapFunction<BillPojo, String>() {
            @Override
            public String map(BillPojo billPojo) throws Exception {
                return JSON.toJSONString(billPojo);
            }
        });

        FlinkJedisPoolConfig config = new FlinkJedisPoolConfig.Builder()
                .setHost("192.168.65.128")
                .setPort(6379)
                .build();
//        map.print();

        map.addSink(new RedisSink<String>(config, new RedisExampleMapper()));

        env.execute("bill stream programmer");
        System.out.println("-------------------------");
    }

    public static final class RedisExampleMapper implements RedisMapper<String> {


        @Override
        public RedisCommandDescription getCommandDescription() {
            return new RedisCommandDescription(RedisCommand.HSET, "flink");
        }

        @Override
        public String getKeyFromData(String str) {
            System.out.println(str);
            BillPojo billPojo = JSON.parseObject(str, BillPojo.class);
            return billPojo.getMaster().getGroupID().toString();
        }

        @Override
        public String getValueFromData(String str) {
            BillPojo billPojo = JSON.parseObject(str, BillPojo.class);
            return billPojo.getMaster().getShopID().toString();
        }
    }
}
