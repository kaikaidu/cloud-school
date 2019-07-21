package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:班级id
 * @Date:2018/3/19 14:59
 * @Author:wsc
 */
@Data
public class DrawCuts extends Draw{
    private Integer classId;    //班级id
    private Integer userId;     //用户id
}
