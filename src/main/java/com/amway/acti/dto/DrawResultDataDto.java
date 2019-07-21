package com.amway.acti.dto;

import lombok.Data;

@Data
public class DrawResultDataDto {
    private Integer id;

    private String className;//班级名称

    private String adaNumber;//ada卡号

    private String student;//学员姓名

    private String sex;//性别

    private String code;//排序号码

    private String teacher;//讲师

    private String content;//演讲内容
}
