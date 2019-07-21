/**
 * Created by dk on 2018/3/5.
 */

package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SitemDto {

    @NotNull(message = "评分项编号不能为空")
    private Integer id;
    @NotNull(message = "评分项顺序不能为空")
    private Integer order;
    @NotBlank(message = "题干不能为空")
    private String question;

    @NotNull(message = "得分不能为空")
    private BigDecimal score;

    @NotNull(message = "占比不能为空")
    private BigDecimal rate;

}
