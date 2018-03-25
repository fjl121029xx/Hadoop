package com.li.kafka.stormkafka.topology;

import com.li.kafka.stormkafka.bolt.WordSpliter;
import com.li.kafka.stormkafka.bolt.WriterBolt;
import com.li.kafka.stormkafka.spout.MessageScheme;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class KafkaTopo {

	public static void main(String[] args) throws Exception {
		
		String topic = "wordcount";
		String zkRoot = "/kafka-storm";
		String spoutId = "KafkaSpout";
		BrokerHosts brokerHosts = new ZkHosts("weekend01:2181,weekend02:2181,weekend03:2181"); 
		SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, "wordcount", zkRoot, spoutId);
		spoutConfig.forceFromStart = true;
		spoutConfig.scheme = new SchemeAsMultiScheme(new MessageScheme());
		TopologyBuilder builder = new TopologyBuilder();
		//设置一个spout用来从kaflka消息队列中读取数据并发送给下一级的bolt组件，此处用的spout组件并非自定义的，而是storm中已经开发好的KafkaSpout
		builder.setSpout("KafkaSpout", new KafkaSpout(spoutConfig));
		builder.setBolt("word-spilter", new WordSpliter()).shuffleGrouping(spoutId);
		builder.setBolt("writer", new WriterBolt(), 4).fieldsGrouping("word-spilter", new Fields("word"));
		Config conf = new Config();
		conf.setNumWorkers(4);
		conf.setNumAckers(0);
		conf.setDebug(false);
		
		//LocalCluster用来将topology提交到本地模拟器运行，方便开发调试
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("WordCount", conf, builder.createTopology());
		
		//提交topology到storm集群中运行
//		StormSubmitter.submitTopology("sufei-topo", conf, builder.createTopology());
	}

}
