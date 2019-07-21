package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class Mclass {
    private Integer id;

    private String name;

    private Integer number;

    private Integer courseId;

    private Date createTime;

    private Short state;

    private Integer initNumber;

}