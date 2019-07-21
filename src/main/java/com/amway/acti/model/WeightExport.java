package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:评分导出字段
 * @Date:2018/3/28 20:22
 * @Author:wsc
 */
@Data
public class WeightExport {
    private String createTime;//创建时间
    private Integer courseId;//课程id
    private String userName;//学员姓名
    private String adaNumber;//安利卡号
    private String sex;//性别
    private String state;//状态
    private String answer;//得分
}
