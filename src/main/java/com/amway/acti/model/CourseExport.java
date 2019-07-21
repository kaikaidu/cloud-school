package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

/**
 * @Description:报表自定义-课程导出字段
 * @Date:2018/3/28 18:11
 * @Author:wsc
 */
@Data
public class CourseExport {
    private Integer id;//课程id
    private String title;//课程名称
    private String createTime;//创建时间
    private String startTime;//开始时间
    private String endTime;//结束时间
    private String label;//标签
    private String isShelve;//课程状态
    private String address;//地址
    private String maxApplyNum;//人数限制
    private Integer signNum;//报名人数
    private Integer successNum;//报名成功人数
    private Integer registerNum;//签到人数
    private Integer agreeNum;//点赞次数
}
