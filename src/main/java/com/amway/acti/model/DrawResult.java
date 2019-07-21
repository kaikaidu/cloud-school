package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class DrawResult {
    private Integer id;

    private Integer userId;

    private Integer classId;

    private Date createTime;

    private Short state;

    private Integer classDrawId;

    private Integer courseId;
}