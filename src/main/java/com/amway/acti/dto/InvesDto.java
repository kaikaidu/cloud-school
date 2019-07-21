package com.amway.acti.dto;

import lombok.Data;

/**
 * @Description:问卷
 * @Date:2018/3/8 13:28
 * @Author:wsc
 */
@Data
public class InvesDto {
    //问卷id
    private int id;
    //模板ID
    private int tempId;
    //问卷名称
    private String name;
    //是否提交：0-未提交 1-已提交
    private int isSubmit;
}
