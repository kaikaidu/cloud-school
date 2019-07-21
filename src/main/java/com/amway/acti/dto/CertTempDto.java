package com.amway.acti.dto;

import lombok.Data;

/**
 * @Description:证书模板列表
 * @Date:2018/6/5 19:04
 * @Author:wsc
 */
@Data
public class CertTempDto {
    private int certTempId;     //证书模板ID
    private String name;        //证书模板名称
    private String createTime;  //创建时间
    private String url;         //预览地址
    private int isEdit;
    private int isDel;
}
