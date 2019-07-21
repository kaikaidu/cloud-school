package com.amway.acti.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description:问卷题目详情
 * @Date:2018/3/8 13:35
 * @Author:wsc
 */
@Data
public class QuestListDto {
    //题目id
    private Integer id;
    //题干
    private String content;
    //试题顺序
    private int order;
    //试题类型:1-单选  2-多选  3-问答
    private int type;
    //试题选项
    private List<QuestItemListDto> questItem;

    /**
     * 是否必答：0-选答，1-必答
     */
    private int required;
}
