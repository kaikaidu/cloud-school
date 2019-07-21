package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:在线测试导出
 * @Date:2018/3/29 17:09
 * @Author:wsc
 */
@Data
public class TestExport {
    private String createTime;//日期
    private Integer courseId;//课程id
    private String userName;//学员姓名
    private String adaNumber;//安利卡号
    private String testName;//测试名字
    private String sex;//性别
    private String state;//状态
    private String answer;//得分
}
