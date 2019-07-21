package com.amway.acti.model;

import lombok.Data;

@Data
public class CourseTeacher {
    private Integer id;

    private Integer courseId;

    private Integer userId;

    private Integer classId;
}