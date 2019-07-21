package com.amway.acti.dto.v2.fronted;

import lombok.Data;

import java.util.Date;

@Data
public class CertDetailDto {

    /**
     * 证书名称
     */
    private String name;
    /**
     * 证书地址
     */
    private String url;
    /**
     * 证书颁发时间
     */
    private Date awardTime;
}
