package com.amway.acti.dto;


import lombok.Data;

import java.util.List;

/**
 * @Description:题目列表
 * @Date:2018/3/8 13:28
 * @Author:wsc
 */
@Data
public class InvesInfoDto {
    //问卷名称
    private String name;
    //问卷描述
    private String desc;
    //题目列表
    private List<QuestListDto> questList;
}
