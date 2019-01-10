package com.li.flink.answer.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class QuesPointMap {

    public Integer questionId;
    public Integer pointId;
}
