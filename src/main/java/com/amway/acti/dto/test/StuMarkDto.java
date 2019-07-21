package com.amway.acti.dto.test;

import lombok.Data;

/**
 * @author Wei.Li1
 * @create 2018-03-05 14:35
 **/
@Data
public class StuMarkDto {

    /**
     * 学员的得分
     */
    private Double score;
    /**
     * 试卷总分数
     */
    private Double totalScore;
    /**
     * 考试用时
     */
    private String timeLength;
}
