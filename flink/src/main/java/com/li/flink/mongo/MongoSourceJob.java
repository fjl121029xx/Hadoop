package com.li.flink.mongo;

import com.alibaba.fastjson.JSONObject;
import lombok.*;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.DataSource;
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

import java.util.*;
import java.util.function.Consumer;

/**
 * flink 读取 mongo
 * 广播变量
 */
public class MongoSourceJob {

    private static final Logger LOG = LoggerFactory.getLogger(MongoSourceJob.class);
    private static final String MONGO_URI = "mongodb://huatu_ztk:wEXqgk2Q6LW8UzSjvZrs@192.168.100.153:27017,192.168.100.153:27017,192.168.100.155:27017/huatu_ztk.ztk_question_new";

    public static void main(String[] args) throws Exception {

        //获取条件参数
        final ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int subject = parameterTool.getInt("subject", 1);
        int year = parameterTool.getInt("year", 2018);

        String condition = String.format("{}", subject);
//        String condition = String.format("{'source':'%s','year':{'$regex':'^%d'}}", webSource, year);
        //创建运行环境
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //将mongo数据转化为Hadoop数据格式
        HadoopInputFormat<BSONWritable, BSONWritable> hdIf =
                new HadoopInputFormat<>(new MongoInputFormat(), BSONWritable.class, BSONWritable.class, new JobConf());
        hdIf.getJobConf().set("mongo.input.split.create_input_splits", "false");
        hdIf.getJobConf().set("mongo.input.uri", MONGO_URI);
        hdIf.getJobConf().set("mongo.input.query", condition);

        DataSet<Q2p> map = env.createInput(hdIf)
                .filter(new FilterFunction<Tuple2<BSONWritable, BSONWritable>>() {
                    private static final long serialVersionUID = -2434517374971686279L;

                    @Override
                    public boolean filter(Tuple2<BSONWritable, BSONWritable> value) throws Exception {

                        BSONWritable v = value.getField(1);
                        JSONObject s = JSON.parseObject(v.getDoc().toString());

                        return s.getJSONArray("points") != null && s.getJSONArray("points").size() > 0;
                    }
                })
                .map(new MapFunction<Tuple2<BSONWritable, BSONWritable>, Q2p>() {
                    private static final long serialVersionUID = 3007225486599619846L;

                    @Override
                    public Q2p map(Tuple2<BSONWritable, BSONWritable> value) throws Exception {

                        BSONWritable v = value.getField(1);
                        JSONObject s = JSON.parseObject(v.getDoc().toString());


                        return new Q2p(s.getIntValue("_id"), Integer.parseInt(s.getJSONArray("points").get(0).toString()));
                    }
                });



        DataSource<Integer> dataSource = env.fromElements(66851, 259617, 266083, 60494, 60493, 60492, 60491, 60490, 60489, 60488, 60487, 60486, 60485, 60484, 60482, 60481,
                263082, 56992, 263331, 55307
        );


        dataSource.map(new RichMapFunction<Integer, Q2p>() {

            private static final long serialVersionUID = 4799067759420137658L;

            private  Map<Integer,Integer> source = new HashMap<>();

            @Override
            public void open(Configuration parameters) throws Exception {

                super.open(parameters);
                List<Q2p>  questionInfo = getRuntimeContext().getBroadcastVariable("questionInfo");
                questionInfo.forEach(new Consumer<Q2p>() {
                    @Override
                    public void accept(Q2p q2p) {
                        source.put(q2p.questionId,q2p.pointId);
                    }
                });
            }

            @Override
            public Q2p map(Integer qid) throws Exception {

                return new Q2p(qid, source.getOrDefault(qid,0));

            }
        }).withBroadcastSet(map, "questionInfo").print();


//        env.execute("iron man");
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Setter
    private static class Q2p {

        public Integer questionId;
        public Integer pointId;
    }

}

