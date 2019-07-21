package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class CertTemp {

    /**
     * 主键
     */
    private Integer id;
    /**
     * 证书模板名称
     */
    private String name;
    /**
     * 上传证书zip压缩包地址
     */
    private String zip;
    /**
     * 证书模板地址
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

    /**
     * 证书模板html页面地址
     */
    private String htmlUrl;

}
