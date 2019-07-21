package com.amway.acti.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserAnswer {
    private Integer id;

    private Integer userId;

    private Integer paperId;

    private Integer questId;

    private String answer;

    private BigDecimal score;

    private Date createTime;
}