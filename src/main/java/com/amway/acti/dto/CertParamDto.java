package com.amway.acti.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description:学员证书列表 入参
 * @Date:2018/6/6 18:53
 * @Author:wsc
 */
@Data
public class CertParamDto implements Serializable{
    private static final long serialVersionUID = 1L;

    //当前页
    @NotNull(message = "当前页不能为空")
    private Integer pageNo;

    //每页数量
    @NotNull(message = "每页数量不能为空")
    private Integer pageSize;

    //课程id
    @NotNull(message = "课程id不能为空")
    private Integer courseId;

    //学员姓名或者安利卡号
    private String value;

    //性别
    private Integer sex;

    //通过状态
    private Integer viaState;

    //颁发状态
    private Integer awardState;
}
