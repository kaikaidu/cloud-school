package com.amway.acti.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Date:2018/6/6 20:06
 * @Author:wsc
 */
@Data
public class UserCertCustom implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String adaNumber;
    private Integer sex;
    private Integer isVerify;
    private Integer apprResult;
    private Integer viaState;
    private Integer isAward;
    private Date endTime;
    private String url;
}
