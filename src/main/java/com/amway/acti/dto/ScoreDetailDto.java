package com.amway.acti.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScoreDetailDto {

    private String name;

    private Short ident;

    private BigDecimal score;
}
