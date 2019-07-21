package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:分班抽签
 * @Date:2018/3/20 11:28
 * @Author:wsc
 */
@Data
public class Draw {
    private String className;   //班级名称
    private String lecturer;    //讲师
    private Integer code;       //演讲序号
    private String content;     //抽签的内容
    private String adaNumber;   //安利用户卡号
    private String name;        //学员姓名
    private String sex;         //性别
}
