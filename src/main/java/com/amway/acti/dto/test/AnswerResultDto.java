package com.amway.acti.dto.test;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * @author Wei.Li1
 * @create 2018-03-08 18:15
 **/
@Data
@Validated
public class AnswerResultDto {

    /**
     * 试题ID
     */
    @NotNull(message = "试题ID不能为空")
    private Integer questId;
    /**
     * 答案
     */
    @NotNull(message = "答案不能为空")
    private String answer;
}
