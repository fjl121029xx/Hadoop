package com.li.flink.kafka.demo;

import org.apache.commons.net.ntp.TimeStamp;
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
import java.util.Date;

public class RollingAdditionMapper extends RichMapFunction<KafkaEvent, KafkaEvent> {

    private transient ValueState<Integer> sumplay;


    private static final TableName tableName = TableName.valueOf("videoplaylength");

    private static Table t;

    static {
        org.apache.hadoop.conf.Configuration config = HBaseConfiguration.create();

        config.set("hbase.zookeeper.quorum", "192.168.100.68,192.168.100.70,192.168.100.72");
        config.set("hbase.master", "192.168.100.68:16000");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.setInt("hbase.rpc.timeout", 20000);
        config.setInt("hbase.client.operation.timeout", 30000);
        config.setInt("hbase.client.scanner.timeout.period", 200000);
        config.set(TableOutputFormat.OUTPUT_TABLE, "videoplaylength");

        try {
            Connection c = ConnectionFactory.createConnection(config);
            Admin admin = c.getAdmin();
            if (!admin.tableExists(tableName)) {
                admin.createTable(new HTableDescriptor(tableName).addFamily(new HColumnDescriptor("playinfo")));
            }
            t = c.getTable(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TableName tableName = TableName.valueOf("videoplaylength");
    }


    @Override
    public KafkaEvent map(KafkaEvent event) throws Exception {

        Integer time_length = sumplay.value();

        if (time_length == null) {
            time_length = 0;
        }
        time_length += event.getSumplay();

        sumplay.update(time_length);

        Put put = new Put(Bytes.toBytes(event.getUserId().toString()));

        put.addColumn(Bytes.toBytes("playinfo"), Bytes.toBytes("time_length"), Bytes.toBytes(time_length.toString()));
        t.put(put);


        return new KafkaEvent(event.getUserId(), time_length, event.getTimestamp());
    }

    @Override
    public void open(Configuration parameters) throws Exception {

        sumplay = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("sumplay", Integer.class));
    }


}
