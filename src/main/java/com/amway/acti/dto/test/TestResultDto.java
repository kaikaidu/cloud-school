package com.amway.acti.dto.test;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Wei.Li1
 * @create 2018-03-28 20:52
 **/
@Data
@Validated
public class TestResultDto {
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;
    @NotNull(message = "试卷ID不能为空")
    private Integer paperId;
    @NotNull(message = "考试时间不能为空")
    private String timeLength;
    @Valid
    @NotNull(message = "答案对象不能为空")
    @Size(min = 1, message = "答案对象不能为空")
    private List<AnswerResultDto> answerResult;
}
