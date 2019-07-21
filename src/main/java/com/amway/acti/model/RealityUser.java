package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:线下学员导出model
 * @Date:2018/3/14 17:07
 * @Author:wsc
 */
@Data
public class RealityUser {
    private String name;            //姓名
    private String adaNumber;       //安利用户卡号
    private String sex;             //性别
    private String apprResult;      //报名状态
    private String viaState;        //通过状态
}
