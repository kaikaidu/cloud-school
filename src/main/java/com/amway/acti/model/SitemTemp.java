package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class SitemTemp {
    private Integer id;

    private String name;

    private String stand;

    private Date createTime;

    private Short state;

    private String type;
}