package com.li.mr.storm.stormdemo;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class UpperBolt extends BaseBasicBolt{


	//业务处理逻辑
	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {

		//先获取到上一个组件传递过来的数据,数据在tuple里面
		String godName = tuple.getString(0);

		//将商品名转换成大写
		String godName_upper = godName.toUpperCase();

		//将转换完成的商品名发送出去
		collector.emit(new Values(godName_upper));
		
	}



	//声明该bolt组件要发出去的tuple的字段
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
		declarer.declare(new Fields("uppername"));
	}

}
