package com.amway.acti.dto.test;

import lombok.Data;

import java.util.List;

/**
 * @author Wei.Li1
 * @create 2018-03-05 14:24
 **/
@Data
public class PaperInfoDto {

    /**
     * 试卷名称
     */
    private String name;
    /**
     * 试卷描述
     */
    private String desc;
    /**
     * 试题列表
     */
    private List<QuestListDto> questList;
}
