package com.amway.acti.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SitemTempDto {
    private Integer id;

    private String name;

    private String stand;

    private Date createTime;

    private Short state;

    private String type;

    private boolean apply;
}
