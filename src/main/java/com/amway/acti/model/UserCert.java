package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserCert {

    /**
     * 主键
     */
    private Integer id;
    /**
     * 课程id
     */
    private Integer courseId;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 是否颁发证书 0:未颁发 1：已颁发
     */
    private Integer isAward;
    /**
     * 证书地址
     */
    private String url;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 颁发时间
     */
    private Date awardTime;
    /**
     * 状态 1可用 0不可用
     */
    private Integer state;
    /**
     * 证书id
     */
    private Integer certId;
    /**
     * 证书名称
     */
    private String name;
    /**
     * 是否已读0-未读1-已读
     */
    private Integer isRead;

}
