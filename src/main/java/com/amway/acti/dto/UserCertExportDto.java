package com.amway.acti.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Date:2018/6/7 10:18
 * @Author:wsc
 */
@Data
public class UserCertExportDto{
    private String name;
    private String adaNumber;
    private String sex;
    private String sign;
    private String via;
    private String awaward;
}
