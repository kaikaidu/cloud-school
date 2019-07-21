package com.amway.acti.dto.test;

import lombok.Data;

/**
 * @author Wei.Li1
 * @create 2018-03-05 14:23
 **/
@Data
public class PaperResultDto {

    /**
     * 动作：0.未做过试卷,选择paperInfo加载页面 1.已做过试卷，选择stuMark加载页面
     */
    private Integer to;
    /**
     * 试卷详情
     */
    private PaperInfoDto paperInfo;
    /**
     * 学员成绩
     */
    private StuMarkDto stuMark;


}
