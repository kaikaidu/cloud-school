package com.amway.acti.model;

import lombok.Data;

@Data
public class MclassTeacSpee {

    private Integer id; //班级id

    private String name; //班级名称

    private Integer number; //班级人数

    private Integer isAssignTeac; //是否分配讲师 0否1是

    private Integer isCreateSub; //是否创建主题 0否1是
}
