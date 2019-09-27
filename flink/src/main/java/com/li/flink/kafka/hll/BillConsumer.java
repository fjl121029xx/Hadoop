package com.li.flink.kafka.hll;

import com.li.flink.kafka.hll.pojo.BillPojo;
import com.li.flink.kafka.utils.KafkaFlinkUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

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
                return billPojo.toString();
            }
        });

        map.print();
        env.execute("bill stream programmer");
        System.out.println("-------------------------");
    }
}
