package com.li.storm.officialinstance;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;

/**
 * Bolt创建

     Bolt是一个使用元组作为输入，处理元组，并产生新的元组作为输出的组件。Bolts将实现IRichBolt接口。在此程序中，使用两个Bolts
     类CallLogCreatorBolt和CallLogCounterBolt来执行操作。
     IRichBolt接口有以下方法 -
     prepare -为bolt提供要执行的环境。执行器将运行此方法来初始化spout。
     execute -处理单个元组的输入
     cleanup -当spout要关闭时调用。
     declareOutputFields -声明元组的输出模式。
 */
public class CallLogCreatorBolt implements IRichBolt {

    //Create instance for OutputCollector which collects and emits tuples to produce output
    private OutputCollector collector;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String from = tuple.getString(0);
        String to = tuple.getString(1);
        Integer duration = tuple.getInteger(2);
        collector.emit(new Values(from + " - " + to, duration));
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("call", "duration"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
