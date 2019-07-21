package com.amway.acti.model;

import lombok.Data;

@Data
public class Register {

    //用户ID
    private Integer uId;

    //课程ID
    private Integer courseId;

    //学员名称
    private String name;

    //学员性别
    private String sex;

    //安利卡号
    private String adaNumber;

    //签到状态
    private String registerStatus;
}
