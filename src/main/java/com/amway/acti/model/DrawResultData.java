package com.amway.acti.model;

import lombok.Data;

@Data
public class DrawResultData {

    private Integer id;

    private Integer classDrawId;//演讲主题id

    private String className;//班级名称

    private String adaNumber;//ada卡号

    private String student;//学员姓名

    private String sex;//性别

    private Integer classId;//班级id

    private String code;//排序号码

    private String teacher;//讲师

    private Integer index;

    private Integer number;//班级人数


}
