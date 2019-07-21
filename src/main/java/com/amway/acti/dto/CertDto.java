package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @Description:证书
 * @Date:2018/6/5 20:56
 * @Author:wsc
 */
@Data
public class CertDto implements Serializable{
    private static final long serialVersionUID = 1L;

    //证书ID
    private Integer certId;

    //证书名称
    @NotBlank(message = "证书名称不能为空！")
    @Size(max=100,message="证书名称不能超过100字符!")
    private String name;

    //证书模板ID
    @NotNull(message = "证书模板ID不能为空！")
    private Integer certTempId;

    //证书模板名称
    private String tempName;

    //证书模板预览地址
    @NotBlank(message = "预览地址不能为空!")
    private String url;

    //课程id
    @NotNull(message = "课程ID不能为空！")
    private Integer courseId;
}
