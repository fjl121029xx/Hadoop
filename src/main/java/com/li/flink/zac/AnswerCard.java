package com.li.flink.zac;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class AnswerCard {

    private Long userId;
    private Integer subject;
    private String questions;
    private String corrects;
    private String Times;
    private Long createTime;

    public static AnswerCard fromString(String eventStr) throws ParseException {

        JSONObject jsonObject = JSONObject.parseObject(eventStr);

        return new AnswerCard(Long.parseLong(getJsonValue(jsonObject, "userId")),
                Integer.parseInt(getJsonValue(jsonObject, "subject")),
                getJsonValue(jsonObject, "questions"),
                getJsonValue(jsonObject, "corrects"),
                getJsonValue(jsonObject, "times"),
                Long.parseLong(getJsonValue(jsonObject, "createTime")));
    }

    private static String getJsonValue(JSONObject jsonObject, String key) {
        return jsonObject.getString(key).replaceAll("\\[", "").replaceAll("\\]", "");
    }

    public ArrayList<Integer> getQuestions() {

        List<String> list = Arrays.asList(questions.split(","));

        ArrayList<Integer> re = new ArrayList<>(list.size());

        for (String s : list) {
            re.add(Integer.parseInt(s));
        }

        return re;
    }

    public ArrayList<Integer> getCorrects() {

        List<String> list = Arrays.asList(corrects.split(","));

        ArrayList<Integer> re = new ArrayList<>(list.size());

        for (String s : list) {
            re.add(Integer.parseInt(s));
        }

        return re;
    }

    public ArrayList<Integer> getTimes() {

        List<String> list = Arrays.asList(Times.split(","));

        ArrayList<Integer> re = new ArrayList<>(list.size());

        for (String s : list) {
            re.add(Integer.parseInt(s));
        }

        return re;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
