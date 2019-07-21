package com.amway.acti.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserMark {
    private Integer id;

    private Integer userId;

    private Integer paperId;

    private BigDecimal score;

    private Short state;

    private String timeLength;

    private Date createTime;

}