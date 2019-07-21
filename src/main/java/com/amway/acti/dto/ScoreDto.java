/**
 * Created by dk on 2018/3/5.
 */

package com.amway.acti.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScoreDto {
    private Integer sitemId;
    private BigDecimal score;
    private BigDecimal rate;
}
