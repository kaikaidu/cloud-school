/**
 * Created by dk on 2018/3/6.
 */

package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SubmitScoreDto {
    @NotNull(message = "课程编号不能为空")
    private Integer courseId;
    @NotNull(message = "被评分人不能为空")
    private Integer traineeId;
    @NotBlank(message = "评分答案不能为空")
    private List<ScoreDto> scores;

}
