package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

/**
 * @Description:学员统计
 * @Date:2018/5/29 16:23
 * @Author:wsc
 */
@Data
public class StuStatisticsExport {
    private String dateTime;      //日期
    private String name;        //学员姓名
    private String adaNumber;   //安利卡号
    private String sex;         //性别
    private int signupNum;      //报名课程数
    private int approvalNum;    //审核通过数
    private int regNum;         //签到课程数
    private int testNum;        //测试完成数
    private int invesNum;       //问卷完成数
    private int certNum;        //颁发证书数
}
