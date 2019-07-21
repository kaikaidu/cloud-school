package com.amway.acti.model;

import lombok.Data;

@Data
public class Addr {
    private Integer id;

    private String code;

    private String name;

    private Short level;

    private String parentCode;
}