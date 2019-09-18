package com.li.flink.answer.record;

import com.alibaba.fastjson.JSONObject;
import lombok.*;
//import com.fasterxml.jackson.annotation.JsonInclude;

import java.text.ParseException;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class KafkaAnswerCard {

    private Long userId;
    private Integer subject;
    private String questions;
    private String corrects;
    private String Times;
    private Long createTime;


    public KafkaAnswerCard() {
    }

    public KafkaAnswerCard(Long userId, Integer subject, String questions, String corrects, String times, Long createTime) {
        this.userId = userId;
        this.subject = subject;
        this.questions = questions;
        this.corrects = corrects;
        Times = times;
        this.createTime = createTime;
    }

    public static KafkaAnswerCard fromString(String eventStr) throws ParseException {

        JSONObject jsonObject = JSONObject.parseObject(eventStr);

        return new KafkaAnswerCard(Long.parseLong(getJsonValue(jsonObject, "userId")),
                Integer.parseInt(getJsonValue(jsonObject, "subject")),
                getJsonValue(jsonObject, "questions"),
                getJsonValue(jsonObject, "corrects"),
                getJsonValue(jsonObject, "times"),
                Long.parseLong(getJsonValue(jsonObject, "createTime")));
    }

    private static String getJsonValue(JSONObject jsonObject, String key) {
        return jsonObject.getString(key).replaceAll("\\[", "").replaceAll("\\]", "");
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
