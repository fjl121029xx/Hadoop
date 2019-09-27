package com.li.flink.kafka.hll;

import com.li.flink.kafka.demo.KafkaEvent;
import com.li.flink.kafka.hll.pojo.BillPojo;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class RollingAdditionMapper extends RichMapFunction<BillPojo, BillPojo> {


    @Override
    public BillPojo map(BillPojo billPojo) throws Exception {
        return null;
    }
}
