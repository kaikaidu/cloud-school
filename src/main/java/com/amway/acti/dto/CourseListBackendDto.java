package com.amway.acti.dto;

import lombok.Data;

@Data
public class CourseListBackendDto {
    private Integer courseId;

    private String title;

    private String createTime;

    private String startTime;

    private String endTime;

    private String label;

    private Short shelve;

    private String createMan;
}
