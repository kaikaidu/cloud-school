package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class Cert {

    /**
     * 主键
     */
    private Integer id;
    /**
     * 课程ID
     */
    private Integer courseId;
    /**
     * 证书模板id
     */
    private Integer certTempId;
    /**
     * 证书名称
     */
    private String name;
    /**
     * 证书地址
     */
    private String url;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 状态 1可用 0不可用
     */
    private Integer state;

}
