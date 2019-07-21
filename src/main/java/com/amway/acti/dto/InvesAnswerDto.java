package com.amway.acti.dto;

import lombok.Data;

/**
 * @Description:问卷调查结果集合
 * @Date:2018/3/8 18:21
 * @Author:wsc
 */
@Data
public class InvesAnswerDto {
    //题目ID
    private int questId;
    //答案
    private String answer;
}
