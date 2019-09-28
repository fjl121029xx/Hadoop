package com.li.flink.kafka.msg;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class KafkaOffset {

    private String topic;
    private int partition;
    private long offset;
}
