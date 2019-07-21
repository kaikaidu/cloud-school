package com.amway.acti.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SpeeMark {
    private Integer id;

    private Integer courseId;

    private Integer classId;

    private Integer userId;

    private BigDecimal score;

    private Short state;

    private Date createTime;
}