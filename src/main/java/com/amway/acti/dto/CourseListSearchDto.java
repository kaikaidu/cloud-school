package com.amway.acti.dto;

import lombok.Data;


@Data
public class CourseListSearchDto {

    private String title;

    private String createTime;

    private String startTime;

    private String endTime;

    private Integer label;

    private Integer shelve;

    private Integer pageNum;

    private Integer pageSize;

    private Integer createTimeOrder;
}
