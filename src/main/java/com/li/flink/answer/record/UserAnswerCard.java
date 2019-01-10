package com.li.flink.answer.record;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class UserAnswerCard {

    public Long userId;
    public Integer subject;
    public String points;
    public String questions;
    public String corrects;
    public String time;
    public String createTime;


    public UserAnswerCard(Long userId, Integer subject, String points, String questions, String corrects, String time, Long createTime) {
        this.userId = userId;
        this.subject = subject;
        this.points = points;
        this.questions = questions;
        this.corrects = corrects;
        this.time = time;
        this.createTime = sdf.format(new Date(createTime));
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static UserAnswerCard fromString(String eventStr) throws ParseException {

        JSONObject jsonObject = JSONObject.parseObject(eventStr);

        return new UserAnswerCard(Long.parseLong(getJsonValue(jsonObject, "userId")),
                Integer.parseInt(getJsonValue(jsonObject, "subject")),
                getJsonValue(jsonObject, "points"),
                getJsonValue(jsonObject, "questions"),
                getJsonValue(jsonObject, "corrects"),
                getJsonValue(jsonObject, "times"),
                getJsonValue(jsonObject, "createTime"));
    }

    private static String getJsonValue(JSONObject jsonObject, String key) {
        return jsonObject.getString(key).replaceAll("\\[", "").replaceAll("\\]", "");
    }

}
