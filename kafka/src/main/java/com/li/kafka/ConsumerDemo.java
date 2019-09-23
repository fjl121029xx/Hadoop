package com.li.kafka;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;


/**
 * Created by x6 on 2018/5/10.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
class CourseProcessDTO {

    private String rid;

    /**
     * 播放时长（单位：秒）
     */
    private Long playTime;

    /**
     * 视频总时长（单位：秒）
     */
    private Long wholeTime;

    /**
     * 用户实际播放用时（单位：秒）
     */
    private Long userPlayTime;

    /**
     * 房间id(直播回放会有该字段)
     */
    private String roomId;

    /**
     * sessionId（直播回放+长期房间会有该字段）
     */
    private String sessionId;
    /**
     * 有老师时的视频id（录播课程会有该字段）
     */
    private String videoIdWithTeacher;
    /**
     * 无老师时的视频id（录播课程会有该字段）
     */
    private String videoIdWithoutTeacher;

    /**
     * 展示url
     */
    private String joinCode;


    /**
     * 2018-07-03 新增参数  大纲id
     */
    private Long syllabusId;

    /**
     * 课程ID
     */
    private Long classId;

    /**
     * 课件ID
     */
    private Long coursewareId;


}

public class ConsumerDemo {
    private static final String topic = "video-record";
    private static final Integer threads = 1;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy_MM_dd_HH_mm");

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.100.68:2181,192.168.100.70:2181,192.168.100.72:2181");
        props.put("metadata.broker.list", "192.168.100.68:9092,192.168.100.70:9092,192.168.100.72:9092");
        props.put("group.id", "group-1");
        props.put("serializer.class", "kafka.serializer.StringEncoder");

        ConsumerConfig config = new ConsumerConfig(props);
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(config);
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, threads);

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get("video-record");

        for (final KafkaStream<byte[], byte[]> kafkaStream : streams) {
            new Thread(() -> {
                for (MessageAndMetadata<byte[], byte[]> mm : kafkaStream) {
                    String msg = new String(mm.message());
                    String[] split = msg.split("=");

                    Connection connection = null;
                    Statement statement = null;
                    try {

                        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
                        connection = DriverManager.getConnection(
                                "jdbc:presto://huatu68:9999/hive/default", "hive", "");

                        statement = connection.createStatement();

                        Date date = sdf3.parse(split[4]);


                        Map<String, Object> map = new HashMap<>();
                        map.put("terminal", split[1]);
                        map.put("cv", split[0]);
                        CourseProcessDTO courseProcessDTO = JSONObject.parseObject(split[3], CourseProcessDTO.class);
                        String rowKey = getRowKey(courseProcessDTO, split[2]);
                        map.put("uname", rowKey);


                        map.put("playhour", sdf.format(date));
                        map.put("playday", sdf2.format(date));

                        map.put("recordtime", sdf2.format(new Date(System.currentTimeMillis())));

                        map.put("userPlayTime", courseProcessDTO.getUserPlayTime() == null ? 0 : courseProcessDTO.getUserPlayTime());

                        System.out.println(map);
                        Map<String, Object> result = new HashMap<>();

                        String sql2 = "insert into videoplay2(cv,terminal,uname,playlength,playhour,playday,recordtime) " +
                                "values ('" + map.get("cv").toString() + "'," +
                                "" + map.get("terminal").toString() + "," +
                                "'" + map.get("uname").toString() + "'," +
                                "" + map.get("userPlayTime").toString() + "," +
                                "" + map.get("playhour").toString() + "," +
                                "'" + map.get("playday").toString() + "'," +
                                "'" + map.get("recordtime").toString() + "')";

                        statement.execute(sql2);

                        System.out.println(result);

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }


                    System.out.println(msg);
                }
            }).start();

        }
    }

    private static String getRowKey(CourseProcessDTO courseProcessDTO, String uname) {
        String videoIdWithoutTeacher = courseProcessDTO.getVideoIdWithoutTeacher();
        String videoIdWithTeacher = courseProcessDTO.getVideoIdWithTeacher();
        String roomId = courseProcessDTO.getRoomId();
        String sessionId = courseProcessDTO.getSessionId();
        String joinCode = courseProcessDTO.getJoinCode();

        Long classId = courseProcessDTO.getClassId();
        Long coursewareId = courseProcessDTO.getCoursewareId();


        StringBuilder rowKey = new StringBuilder();
        rowKey.append(uname);

        // 录播视频
        if (StringUtils.isNotEmpty(videoIdWithoutTeacher) || StringUtils.isNotEmpty(videoIdWithTeacher)) {
            if (StringUtils.isNotEmpty(videoIdWithoutTeacher)) {
                rowKey.append("-videoIdWithoutTeacher-").append(videoIdWithoutTeacher);
            } else {
                rowKey.append("-videoIdWithTeacher-").append(videoIdWithTeacher);
            }
            //直播回放
        } else if (StringUtils.isNotEmpty(roomId)) {
            rowKey.append("-roomId-").append(roomId);
            if (StringUtils.isNotEmpty(sessionId)) {
                rowKey.append("-sessionId-").append(sessionId);

                //展示视频
            } else if (StringUtils.isNotEmpty(joinCode)) {
                rowKey.append("-gensee-").append(joinCode);
            } else if (classId != null && coursewareId != null) {

                rowKey.append("-classId-").append(classId).append("-coursewareId-").append(coursewareId);
            } else {
//            log.warn("参数错误");
//            return "1";
            }

        }
        return rowKey.toString();
    }
}