package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 课程ID
     */
    private Integer courseId;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 0:未读 1：已读
     */
    private Integer isRead;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 类型：1.证书模板
     */
    private Integer type;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 消息标题
     */
    private String title;
}
