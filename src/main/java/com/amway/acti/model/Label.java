package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class Label {
    private Integer id;

    private String type;

    private String name;

    private Date createTime;
}