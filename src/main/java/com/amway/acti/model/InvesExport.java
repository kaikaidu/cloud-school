package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:问卷导出
 * @Date:2018/3/29 17:22
 * @Author:wsc
 */
@Data
public class InvesExport {
    private String createTime;//日期
    private Integer courseId;//课程id
    private String userName;//学员姓名
    private String adaNumber;//安利卡号
    private String sex;//性别
    private String invesName;//问卷名称
    private String state;//状态
}
