package com.amway.acti.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Description:参数对象
 * @Date:2018/3/15 10:29
 * @Author:wsc
 */
@Data
public class CourseUser {
    private String ids;         //拼接的id
    private Integer courseId;   //课程id
    private String name;        //课程名称
    private String sex;         //性别
    private String signState;   //报名状态
    private String viaState;    //通过状态
    private List<String> list;  //id集合
    private Date currentTime;   //当前时间

}
