package com.li.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by x6 on 2018/5/10.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseProcessDTO {

    private String rid;

    /**
     * 播放时长（单位：秒）
     */
    private Long playTime;

    /**
     * 视频总时长（单位：秒）
     */
    private Long wholeTime;

    /**
     * 用户实际播放用时（单位：秒）
     */
    private Long userPlayTime;

    /**
     * 房间id(直播回放会有该字段)
     */
    private String roomId;

    /**
     * sessionId（直播回放+长期房间会有该字段）
     */
    private String sessionId;
    /**
     * 有老师时的视频id（录播课程会有该字段）
     */
    private String videoIdWithTeacher;
    /**
     * 无老师时的视频id（录播课程会有该字段）
     */
    private String videoIdWithoutTeacher;

    /**
     * 展示url
     */
    private String joinCode;


    /**
     * 2018-07-03 新增参数  大纲id
     */
    private Long syllabusId;

    /**
     * 课程ID
     */
    private Long classId;

    /**
     * 课件ID
     */
    private Long coursewareId;


}
