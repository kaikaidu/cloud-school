package com.amway.acti.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TestTemp {
    private Integer id;

    private String name;

    private BigDecimal totalScore;

    private Date createTime;

    private Short state;

    private Integer userId;

}