package com.li.flink.kafka.utils;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


/**
 *  --input-topic 192.168.65.128 -bootstrap.servers bill --group.id dwDohkoTestGroup
 */
public class KafkaFlinkUtil {

    public static StreamExecutionEnvironment prepareExecutionEnv(ParameterTool parameterTool) throws Exception {

        if (parameterTool.getNumberOfParameters() < 3) {

            System.out.println("Missing parameters!\n" +
                    "Usage: Kafka --input-topic <topic> \r\n" +
                    "--bootstrap.servers <kafka brokers> \r\n" +
                    "--group.id <some id>");
            throw new Exception("Missing parameters!\n" +
                    "Usage: Kafka --input-topic <topic> " +
                    "--bootstrap.servers <kafka brokers> " +
                    "--group.id <some id>");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().disableSysoutLogging();
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));
        env.enableCheckpointing(5000);
        env.getConfig().setGlobalJobParameters(parameterTool);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        return env;
    }
}
