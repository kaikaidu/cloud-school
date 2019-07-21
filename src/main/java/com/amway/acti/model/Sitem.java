package com.amway.acti.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Sitem {
    private Integer id;

    private String question;

    private Date createTime;

    private Short state;

    private Integer sitemTempId;

    private Integer order;

    private String options;

    private String title;

    private BigDecimal rate;

}
