package com.li.flink.mongo;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.mapred.MongoInputFormat;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.hadoop.mapred.HadoopInputFormat;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.hadoop.mapred.JobConf;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class MongoSourceJob {

    private static final Logger LOG = LoggerFactory.getLogger(MongoSourceJob.class);
    private static final String MONGO_URI = "mongodb://huatu_ztk:wEXqgk2Q6LW8UzSjvZrs@192.168.100.153:27017,192.168.100.153:27017,192.168.100.155:27017/huatu_ztk.ztk_question_new";

    public static void main(String[] args) throws Exception {
        //获取条件参数
        final ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int subject = parameterTool.getInt("subject", 1);
        int year = parameterTool.getInt("year", 2018);

        String condition = String.format("{'subject': %s}", subject);
//        String condition = String.format("{'source':'%s','year':{'$regex':'^%d'}}", webSource, year);
        //创建运行环境
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //将mongo数据转化为Hadoop数据格式
        HadoopInputFormat<BSONWritable, BSONWritable> hdIf =
                new HadoopInputFormat<>(new MongoInputFormat(), BSONWritable.class, BSONWritable.class, new JobConf());
        hdIf.getJobConf().set("mongo.input.split.create_input_splits", "false");
        hdIf.getJobConf().set("mongo.input.uri", MONGO_URI);
        hdIf.getJobConf().set("mongo.input.query", condition);

        Map<Integer, Integer> q2p = new HashMap<>();

        env.createInput(hdIf)

                .filter(new FilterFunction<Tuple2<BSONWritable, BSONWritable>>() {
                    private static final long serialVersionUID = -2434517374971686279L;

                    @Override
                    public boolean filter(Tuple2<BSONWritable, BSONWritable> value) throws Exception {

                        BSONWritable v = value.getField(1);
                        JSONObject s = JSON.parseObject(v.getDoc().toString());

                        return s.getJSONArray("points") != null;
                    }
                })
                .map(new RichMapFunction<Tuple2<BSONWritable, BSONWritable>, Object>() {
                    private static final long serialVersionUID = 3598949113836868260L;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        List<Object> broadcastSetName = getRuntimeContext().getBroadcastVariable("broadcastSetName");
                    }

                    @Override
                    public Object map(Tuple2<BSONWritable, BSONWritable> value) throws Exception {
                        return null;
                    }
                });
//                .map(new MapFunction<Tuple2<BSONWritable, BSONWritable>, Tuple2<Integer, Integer>>() {
//                    private static final long serialVersionUID = 3007225486599619846L;
//
//                    @Override
//                    public Tuple2<Integer, Integer> map(Tuple2<BSONWritable, BSONWritable> value) throws Exception {
//                        BSONWritable v = value.getField(1);
//                        JSONObject s = JSON.parseObject(v.getDoc().toString());
//                        System.out.println(s.getJSONArray("points"));
//                        return Tuple2.of(s.getIntValue("_id"), Integer.parseInt(s.getJSONArray("points").get(0).toString()));
//                    }
//                }).collect().forEach(new Consumer<Tuple2<Integer, Integer>>() {
//            @Override
//            public void accept(Tuple2<Integer, Integer> t) {
//
//                q2p.put(t.f0, t.f1);
//
//            }
//        });

        System.out.println(q2p);

//        env.execute("iron man");
    }

}
