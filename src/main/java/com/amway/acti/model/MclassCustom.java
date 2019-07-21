package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:班级扩展类
 * @Date:2018/4/29 17:16
 * @Author:wsc
 */
@Data
public class MclassCustom extends Mclass{
    private String isDistribution;//该班级是否已关联学员
}
