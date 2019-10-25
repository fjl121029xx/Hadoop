package com.li.influxdb;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CodeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String code;
    private String descr;
    private String descrE;
    private String createdBy;
    private Long createdAt;

    private String time;
    private String tagCode;
    private String tagName;
}
