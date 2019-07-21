package com.amway.acti.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Weight {
    private Integer id;

    private Short ident;

    private BigDecimal weight;

    private Short state;

    private Integer tempId;

    private Short release;

    private Integer courseId;
}