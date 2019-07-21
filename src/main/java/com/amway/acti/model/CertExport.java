package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:自定义报表证书
 * @Date:2018/5/30 16:05
 * @Author:wsc
 */
@Data
public class CertExport {
    private String createTime;  //日期
    private int courseId;       //课程id
    private String name;        //学员姓名
    private String adaNumber;   //安利卡号
    private String sex;         //性别
    private String state;       //是否颁发证书
}
