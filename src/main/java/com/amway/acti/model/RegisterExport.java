package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:导出签到字段
 * @Date:2018/3/28 20:47
 * @Author:wsc
 */
@Data
public class RegisterExport {
    private String createTime;//日期
    private Integer courseId;//课程id
    private String userName;//学员姓名
    private String adaNumber;//安利卡号
    private String sex;//性别
    private String state;//状态
}
