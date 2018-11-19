package com.li.flink.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter

public class KafkaEvent {

    private String word;
    private int frequency;
    private long timestamp;

    public static KafkaEvent fromString(String eventStr){

        String[] split = eventStr.split("=");
        return new KafkaEvent(split[1],1,System.currentTimeMillis());
//        return new KafkaEvent(split[0],Integer.parseInt(split[1]),Long.parseLong(split[2]));
    }
}
