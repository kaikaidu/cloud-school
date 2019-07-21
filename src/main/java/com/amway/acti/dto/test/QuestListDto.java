package com.amway.acti.dto.test;

import lombok.Data;

import java.util.List;

/**
 * @author Wei.Li1
 * @create 2018-03-05 14:26
 **/
@Data
public class QuestListDto {
    private Integer id;

    /**
     * 题干
     */
    private String content;
    /**
     * 试题顺序
     */
    private Integer order;
    /**
     * 试题类型:1-单选  2-多选  3-问答
     */
    private Integer type;
    /**
     * 试题选项
     */
    private List<QuestItemListDto> questItem;

    /**
     * 是否必答：0-选答，1-必答
     */
    private int required;

}
