package com.li.flink.home.streaming;

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
public class StreamingBean {

    private Long key;
    private Integer value;
    private Long recordTime;

    public static StreamingBean fromString(String eventStr) throws ParseException {

        JSONObject jsonObject = JSONObject.parseObject(eventStr);

        return new StreamingBean(Long.parseLong(getJsonValue(jsonObject, "key")),
                Integer.parseInt(getJsonValue(jsonObject, "value")),
                Long.parseLong(getJsonValue(jsonObject, "recordTime")));
    }

    private static String getJsonValue(JSONObject jsonObject, String key) {
        return jsonObject.getString(key).replaceAll("\\[", "").replaceAll("\\]", "");
    }



    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
