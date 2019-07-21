package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class InvesTemp {
    private Integer id;

    private String name;

    private Date createTime;

    private Short state;

    private Integer userId;

}