package com.amway.acti.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TestQuest {
    private Integer id;

    private Integer type;

    private String question;

    private Integer sequence;

    private Integer tempId;

    private BigDecimal score;

}