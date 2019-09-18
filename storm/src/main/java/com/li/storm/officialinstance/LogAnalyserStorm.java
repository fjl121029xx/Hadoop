package com.li.storm.officialinstance;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

//import storm configuration packages
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

/**
 * 创建拓扑
 * <p>
 * Storm拓扑基本上是一个Thrift结构。
 * TopologyBuilder类提供了简单而容易的方法来创建复杂的拓扑。
 * TopologyBuilder类具有设置spout（setSpout）和设置bolt（setBolt）的方法。
 * 最后，TopologyBuilder有createTopology来创建拓扑。使用以下代码片段创建拓扑
 */
public class LogAnalyserStorm {

    public static void main(String[] args) throws Exception {
        //Create Config instance for cluster configuration
        Config config = new Config();
        config.setDebug(true);

        //
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("call-log-reader-spout", new FakeCallLogReaderSpout());

        builder.setBolt("call-log-creator-bolt", new CallLogCreatorBolt())
                .shuffleGrouping("call-log-reader-spout");

        builder.setBolt("call-log-counter-bolt", new CallLogCounterBolt())
                .fieldsGrouping("call-log-creator-bolt", new Fields("call"));

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("LogAnalyserStorm", config, builder.createTopology());
        Thread.sleep(10000);

        //Stop the topology

        cluster.shutdown();
    }
}
