package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:是否分配讲师
 * @Date:2018/3/22 13:53
 * @Author:wsc
 */
@Data
public class Teacher extends Mclass{
    private Integer isDistribution;//该班级是否已分配讲师
}
