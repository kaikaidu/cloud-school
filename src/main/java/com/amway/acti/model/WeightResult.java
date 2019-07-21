package com.amway.acti.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WeightResult extends Weight{
    private String createTime;

    private Integer id; //用户id

    private String name;

    private String adaNumber;

    private String sex;

    private String status;

    private BigDecimal score;

    private Integer courseId;

    private String code;

    private String className;

    private Integer classId;

    private Integer index;

    private Integer number;
}
