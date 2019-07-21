package com.amway.acti.model;

import lombok.Data;

/**
 * @Description:用户扩展类
 * @Date:2018/4/12 19:00
 * @Author:wsc
 */
@Data
public class UserCustom extends User{
    private String province;//省
    private String city;//城市
    private String region;//区
    private String isAssociatedClass;//该学员是否已关联班级
}
