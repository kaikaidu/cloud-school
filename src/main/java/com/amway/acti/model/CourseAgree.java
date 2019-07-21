package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class CourseAgree {
    private Integer id;

    private Integer courseId;

    private Integer userId;

    private Date agreeTime;
}