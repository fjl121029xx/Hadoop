package com.li.stormWordCount;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;


public class WordReader extends BaseRichSpout {

    private String inputPath;
    private SpoutOutputCollector collector;


    @Override
    public void open(java.util.Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        inputPath = (String) conf.get("INPUT_PATH");
    }

    @Override
    public void nextTuple() {
        Collection<File> files = FileUtils.listFiles(new File(inputPath),
                FileFilterUtils.notFileFilter(FileFilterUtils.suffixFileFilter(".bak")), null);
        for (File f : files) {
            try {
                List<String> lines = FileUtils.readLines(f, "UTF-8");
                for (String line : lines) {
                    collector.emit(new Values(line));
                }
                FileUtils.moveFile(f, new File(f.getPath() + System.currentTimeMillis() + ".bak"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line"));
    }
}
