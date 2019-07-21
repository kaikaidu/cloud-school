package com.amway.acti.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:学员证书列表 出参
 * @Date:2018/6/6 18:59
 * @Author:wsc
 */
@Data
public class UserCertDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer userId;     //学员ID
    private String name;        //学员名称
    private String adaNumber;   //安利卡号
    private Integer sex;        //性别【0：男 1：女】
    private Integer signState;  //报名状态【0：报名失败 1：报名成功 2：审核中 3：已完成】
    private Integer viaState;   //通过状态【0：未通过 1：已通过】
    private Integer awardState; //证书状态【0：未颁发 1：已颁发】
    private String url;         //证书地址

    private String sexName;        //性别【0：男 1：女】
    private String signStateName;  //报名状态【0：报名失败 1：报名成功 2：审核中 3：已完成】
    private String viaStateName;   //通过状态【0：未通过 1：已通过】
    private String awardStateName; //证书状态【0：未颁发 1：已颁发】
}
