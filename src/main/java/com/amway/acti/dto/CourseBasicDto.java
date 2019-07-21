package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class CourseBasicDto {

    private Integer courseId;
    /**
     * 课程名称
     */
    @NotBlank(message = "课程名称为必填项")
    @Length(max = 50,message = "课程名称过长")
    private String title;
    /**
     * 课程概述
     */
    @NotBlank(message = "课程概述为必填项")
    @Length(max = 500,message = "课程概述过长")
    private String description;
    /**
     * 课程配图
     */
    private String picture;
    /**
     * 上课开始时间
     */
    @NotBlank(message = "上课开始时间为必填项")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",message = "日期格式错误")
    private String startTime;
    /**
     * 结束时间
     */
    @NotBlank(message = "上课结束时间为必填项")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",message = "日期格式错误")
    private String endTime;
    /**
     * 标签
     */
    @NotNull(message = "课程标签为必填项")
    private Integer label;
    /**
     * 课程代码
     */
    @NotBlank(message = "课程代码为必填项")
    @Length(max = 50,message = "课程代码超长")
    private String code;
    /**
     * 讲师名单,多个账号以逗号区分
     */
    private String[] teachers;

    /**
     * 助教名单,多个账号以逗号区分
     */
    private String[] assists;
    /**
     * 上课地点
     */
    @Length(max = 100,message = "上课地点超长")
    private String address;
    /**
     * 学习群二维码
     */
    private String qrCode;
    /**
     * 是否允许分享
     */
    @NotNull(message = "是否允许分享不能为空")
    private Integer isShare;
    /**
     * 报名开始时间
     */
    private String signStartTime;
    /**
     * 报名结束时间
     */
    private String signEndTime;
    /**
     * 是否需要审批
     */
    private Integer isAppr;
    /**
     * 直播地址
     */
    @Length(max = 100,message = "直播链接地址超长")
    private String liveUrl;

    /**
     * 报名人数
     */
    private String maxApplyNum;
}
