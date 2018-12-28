package com.li.flink.zac;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.text.ParseException;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class AnswerCard {

    private String userId;
    private String subject;
    private String questions;
    private String corrects;
    private long createTime;

    public static AnswerCard fromString(String eventStr) throws ParseException {

        JSONObject jsonObject = JSONObject.parseObject(eventStr);

        return new AnswerCard(getJsonValue(jsonObject, "userId"),
                getJsonValue(jsonObject, "subject"),
                getJsonValue(jsonObject, "questions"),
                getJsonValue(jsonObject, "corrects"),
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
