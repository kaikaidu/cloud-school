package com.amway.acti.dto;

import lombok.Data;

@Data
public class CourseBackendDto extends CourseCommonDto {
    private String endTime;

    private String code;

    private String[] teachers;

    private String[] assists;

    private String address;

    private String qrCode;

    private Integer isShare;

    private Integer isAppr;

    private String signStartTime;

    private String signEndTime;

    private Integer applyMaxNum;

    private String liveUrl;

    private Integer courseId;

    private Boolean started;
}
