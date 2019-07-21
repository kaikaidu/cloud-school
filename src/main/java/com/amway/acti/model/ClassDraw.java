package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class ClassDraw {
    private Integer id;

    private String code;

    private String content;

    private Integer classId;

    private Date createTime;

    private Date updateTime;

    private Integer courseId;


}