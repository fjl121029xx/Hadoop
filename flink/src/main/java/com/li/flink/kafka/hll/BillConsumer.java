package com.li.flink.kafka.hll;

import com.alibaba.fastjson.JSON;
import com.li.flink.kafka.hll.accu.KafkaOffsetCounter;
import com.li.flink.kafka.hll.pojo.BillPojo;
import com.li.flink.kafka.msg.KafkaMsg;
import com.li.flink.kafka.msg.TypedKeyedDeserializationSchema;
import com.li.flink.kafka.tools.java.JavaOffsetTools;
import com.li.flink.kafka.utils.KafkaFlinkUtil;
import com.li.flink.kafka.utils.RedisUtils;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import redis.clients.jedis.Jedis;
import scala.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BillConsumer {


    public static void main(String[] args) throws Exception {

        final ParameterTool parameterTool = ParameterTool.fromArgs(args);
        StreamExecutionEnvironment env = KafkaFlinkUtil.prepareExecutionEnv(parameterTool);


//        Map<KafkaTopicPartition, Long> specificStartOffsets = new HashMap<>();
//        specificStartOffsets.put(new KafkaTopicPartition("myTopic", 0), 23L);


        FlinkKafkaConsumer010<KafkaMsg> consumer = new FlinkKafkaConsumer010<>(
                parameterTool.getRequired("input-topic"),
                new TypedKeyedDeserializationSchema(),
                parameterTool.getProperties());
//        FlinkKafkaConsumer010<BillPojo> consumer = new FlinkKafkaConsumer010<>(
//                parameterTool.getRequired("input-topic"),
//                new BillSchema(),
//                parameterTool.getProperties());

        Map<KafkaTopicPartition, Long> offsets = JavaOffsetTools.getSpecificStartOffsets();
        if (offsets != null) {
            consumer.setStartFromSpecificOffsets(offsets);
        } else {
            consumer.setStartFromLatest();
        }
//        consumer.setCommitOffsetsOnCheckpoints(true);
        DataStream<KafkaMsg> input = env
                .addSource(
                        consumer);


        SingleOutputStreamOperator<BillPojo> getBill = input.map(new RichMapFunction<KafkaMsg, BillPojo>() {
            Jedis jedis = null;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                jedis = RedisUtils.getJedis();
            }

            @Override
            public BillPojo map(KafkaMsg msg) throws Exception {

                BillPojo bill = JSON.parseObject(msg.getValue(), BillPojo.class);
                jedis.hset(msg.getTopic() + "_topic", "partition_" + msg.getPartition(), msg.getOffset() + "");
                return bill;
            }
        });


        getBill.print();
//        FlinkJedisPoolConfig config = new FlinkJedisPoolConfig.Builder()
//                .setHost("192.168.65.128")
//                .setPort(6379)
//                .build();
////        map.print();
//
//        map.addSink(new RedisSink<String>(config, new RedisExampleMapper()));

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
//            System.out.println(str);
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
