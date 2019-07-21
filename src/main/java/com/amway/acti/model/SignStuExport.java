package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:自定义报表报名学员
 * @Date:2018/5/30 11:33
 * @Author:wsc
 */
@Data
public class SignStuExport {
    private String createTime;  //报名时间
    private int courseId;       //课程id
    private String name;        //学员姓名
    private String adaNumber;   //安利卡号
    private String sex;         //性别
    private String signType;    //报名状态
    private String viaState;    //通过状态
}
