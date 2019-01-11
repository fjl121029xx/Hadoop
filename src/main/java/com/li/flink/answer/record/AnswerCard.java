package com.li.flink.answer.record;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.mapred.MongoInputFormat;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.hadoop.mapred.HadoopInputFormat;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.ListTypeInfo;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.hadoop.mapred.JobConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 *
 */
public class AnswerCard {


    private static final Logger LOG = LoggerFactory.getLogger(AnswerCard.class);
    private static final String MONGO_URI = "mongodb://huatu_ztk:wEXqgk2Q6LW8UzSjvZrs@192.168.100.153:27017,192.168.100.153:27017,192.168.100.155:27017/huatu_ztk.ztk_question_new";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static void main(String[] args) throws Exception {


        final ParameterTool parameterTool = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment streamEnv = StreamExecutionEnvironment.getExecutionEnvironment();
//        streamEnv.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
        streamEnv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        int subject = parameterTool.getInt("subject", 1);
        String condition = String.format("{}", subject);
        HadoopInputFormat<BSONWritable, BSONWritable> hdIf =
                new HadoopInputFormat<>(new MongoInputFormat(), BSONWritable.class, BSONWritable.class, new JobConf());
        hdIf.getJobConf().set("mongo.input.split.create_input_splits", "false");
        hdIf.getJobConf().set("mongo.input.uri", MONGO_URI);
        hdIf.getJobConf().set("mongo.input.query", condition);

        /**
         * patterns
         */
        DataStream<QuesPointMap> ruleStream = streamEnv.createInput(hdIf)
                .filter(new FilterFunction<Tuple2<BSONWritable, BSONWritable>>() {
                    private static final long serialVersionUID = -2434517374971686279L;

                    @Override
                    public boolean filter(Tuple2<BSONWritable, BSONWritable> value) throws Exception {

                        BSONWritable v = value.getField(1);
                        JSONObject s = JSON.parseObject(v.getDoc().toString());

                        return s.getJSONArray("points") != null && s.getJSONArray("points").size() > 0;
                    }
                })
                .map(new MapFunction<Tuple2<BSONWritable, BSONWritable>, QuesPointMap>() {
                    private static final long serialVersionUID = 3007225486599619846L;

                    @Override
                    public QuesPointMap map(Tuple2<BSONWritable, BSONWritable> value) throws Exception {

                        BSONWritable v = value.getField(1);
                        JSONObject s = JSON.parseObject(v.getDoc().toString());

                        return new QuesPointMap(s.getIntValue("_id"), Integer.parseInt(s.getJSONArray("points").get(0).toString()));
                    }
                });


        MapStateDescriptor<Void, QuesPointMap> ruleStateDescriptor = new MapStateDescriptor<>("RulesBroadcastState",
                BasicTypeInfo.VOID_TYPE_INFO,
                TypeInformation.of(new TypeHint<QuesPointMap>() {
                }));


        BroadcastStream<QuesPointMap> ruleBroadcastStream = ruleStream
                .broadcast(ruleStateDescriptor);

        /**
         * actions
         */
        SingleOutputStreamOperator<UserAnswerCard> input = streamEnv
                .addSource(
                        new FlinkKafkaConsumer010<>(
                                parameterTool.getRequired("input-topic"),
                                new KafkaAnswerCardSchema(),
                                parameterTool.getProperties())
                                .assignTimestampsAndWatermarks(new acWatermarkExtreactor()))
                .filter(new FilterFunction<KafkaAnswerCard>() {
                    private static final long serialVersionUID = -3935377725994973948L;

                    @Override
                    public boolean filter(KafkaAnswerCard value) throws Exception {

                        try {
                            String questions = value.getQuestions();
                            String[] qArr = questions.split(",");
                            for (String s : qArr) {

                                int qid = Integer.parseInt(s);
                            }
                        } catch (Exception e) {
                            return false;
                        }
                        return true;

                    }
                })
                .keyBy(new KeySelector<KafkaAnswerCard, Long>() {
                    private static final long serialVersionUID = 909879664216185791L;

                    @Override
                    public Long getKey(KafkaAnswerCard value) throws Exception {
                        return value.getUserId();
                    }
                })
                .connect(ruleBroadcastStream)
                .process(new KeyedBroadcastProcessFunction<QuesPointMap, KafkaAnswerCard, QuesPointMap, UserAnswerCard>() {

                    private static final long serialVersionUID = 8694654656959988809L;

                    // store partial matches, i.e. first elements of the pair waiting for their second element
                    // we keep a list as we may have many first elements waiting
                    private final MapStateDescriptor<Integer, List<KafkaAnswerCard>> mapStateDesc =
                            new MapStateDescriptor<>(
                                    "KafkaAnswerCard",
                                    BasicTypeInfo.INT_TYPE_INFO,
                                    new ListTypeInfo<>(KafkaAnswerCard.class));

                    // identical to our ruleStateDescriptor above
                    private final MapStateDescriptor<Integer, QuesPointMap> ruleStateDescriptor =
                            new MapStateDescriptor<>(
                                    "RulesBroadcastState",
                                    BasicTypeInfo.INT_TYPE_INFO,
                                    TypeInformation.of(new TypeHint<QuesPointMap>() {
                                    }));

                    /**
                     * 责处理广播流中的传入元素
                     */
                    @Override
                    public void processBroadcastElement(QuesPointMap value, Context ctx, Collector<UserAnswerCard> out) throws Exception {
                        
                        ctx.getBroadcastState(ruleStateDescriptor).put(value.questionId, value);
                    }

                    /**
                     * 负责处理非广播流中的传入元素
                     */
                    @Override
                    public void processElement(KafkaAnswerCard value, ReadOnlyContext ctx, Collector<UserAnswerCard> out) throws Exception {

                        MapState<Integer, List<KafkaAnswerCard>> state = getRuntimeContext().getMapState(mapStateDesc);

                        String questions = value.getQuestions();
                        String[] qArr = questions.split(",");
                        StringBuilder sb = new StringBuilder();

                        ReadOnlyBroadcastState<Integer, QuesPointMap> broadcastState = ctx.getBroadcastState(ruleStateDescriptor);

                        for (String s : qArr) {

                            int qid = Integer.parseInt(s);
                            QuesPointMap quesPointMap = broadcastState.get(qid);
                            Integer pointId = quesPointMap == null ? -1 : quesPointMap.pointId;
                            sb.append(pointId).append(",");
                        }

                        String points = sb.deleteCharAt(sb.length() - 1).toString();


                        out.collect(new UserAnswerCard(value.getUserId(), value.getSubject(), points, value.getQuestions(), value.getCorrects(), value.getTimes(), value.getCreateTime()));
                    }
                });


        input.print();
//        60 s用户做题次数
//        DataStream<Tuple2<String, Integer>> dataStream = input.map(new Map1())
//                .keyBy(0)
//                .timeWindow(Time.seconds(60))
//                .sum(1);
//        dataStream.print();
//
        streamEnv.execute("answer card");

    }

    public static class Map1 implements MapFunction<UserAnswerCard, Tuple2<String, Integer>> {

        @Override
        public Tuple2<String, Integer> map(UserAnswerCard ua) throws Exception {
            return new Tuple2<>(Long.toString(ua.getUserId()), 1);
        }
    }
}
