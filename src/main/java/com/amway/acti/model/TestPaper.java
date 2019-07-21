package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class TestPaper {
    private Integer id;

    private String name;

    private String describe;

    private Date createTime;

    private Short state;

    private Integer tempId;

    private Integer userId;

    private Integer courseId;


}