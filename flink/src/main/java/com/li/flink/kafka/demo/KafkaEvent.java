package com.li.flink.kafka.demo;

import com.alibaba.fastjson.JSON;
//import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter

public class KafkaEvent {

    private String userId;
    private int sumplay;
    private long timestamp;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm");

    public static KafkaEvent fromString(String eventStr) throws ParseException {

        String[] split = eventStr.split("=");

        String userId = split[2];
        int userPlayTime = Integer.parseInt(JSON.parseObject(split[3]).get("userPlayTime").toString());


        return new KafkaEvent(userId, userPlayTime, sdf.parse(split[4]).getTime());
//        return new KafkaEvent(split[0],Integer.parseInt(split[1]),Long.parseLong(split[2]));
    }
}
