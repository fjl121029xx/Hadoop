package com.li.flink.mongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.mapred.MongoInputFormat;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.hadoop.mapred.HadoopInputFormat;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ZtkAnswerCard {

    private static final Logger LOG = LoggerFactory.getLogger(ZtkAnswerCard.class);
    private static final String MONGO_URI = "mongodb://huatu_ztk:wEXqgk2Q6LW8UzSjvZrs@192.168.100.153:27017,192.168.100.153:27017,192.168.100.155:27017/huatu_ztk.ztk_answer_card";

    public static void main(String[] args) throws Exception {
        //获取条件参数
        final ParameterTool parameterTool = ParameterTool.fromArgs(args);
        long createTime = parameterTool.getLong("createTime", 1545926400000L);

        String condition = String.format("{'createTime': { $gte : %s}}", createTime);
        //创建运行环境
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //将mongo数据转化为Hadoop数据格式
        HadoopInputFormat<BSONWritable, BSONWritable> hdIf =
                new HadoopInputFormat<>(new MongoInputFormat(), BSONWritable.class, BSONWritable.class, new JobConf());
        hdIf.getJobConf().set("mongo.input.split.create_input_splits", "false");
        hdIf.getJobConf().set("mongo.input.uri", MONGO_URI);
        hdIf.getJobConf().set("mongo.input.query", condition);


        MapOperator<Tuple2<BSONWritable, BSONWritable>, String> input = env.createInput(hdIf)

                .map(new MapFunction<Tuple2<BSONWritable, BSONWritable>, String>() {
                    private static final long serialVersionUID = 3007225486599619846L;

                    @Override
                    public String map(Tuple2<BSONWritable, BSONWritable> value) throws Exception {
                        BSONWritable v = value.getField(1);
                        JSONObject s = JSON.parseObject(v.getDoc().toString());
                        return s.toJSONString();
                    }
                });

        input.print();

//        env.execute("iron man");
    }

}
