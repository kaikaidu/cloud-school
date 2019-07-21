package com.amway.acti.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:课程关联证书信息
 * @Date:2018/6/6 15:13
 * @Author:wsc
 */
@Data
public class CourseCertDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer certId; //证书ID
    private String name;    //证书名称
    private String url;     //预览地址
    private Integer isDel;  //是否可以删除
    private Integer isStart;//课程是否开始 1：已开始 0：未开始
}
