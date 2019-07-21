package com.amway.acti.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CourseDocSearchDto {
    private String name;

    private String orderType;

    private Integer type;

    private Integer addType;

    private Integer shelve;

    @NotNull(message = "课程id不能为空")
    private Integer courseId;

    private Integer pageNo;

    private Integer pageSize;
}
