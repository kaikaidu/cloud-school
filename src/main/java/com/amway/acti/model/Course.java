package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class Course {
    private Integer sysId;

    private String status;

    private String title;

    private Integer label;

    private String code;

    private String picture;

    private String overview;

    private String address;

    private Date startTime;

    private Date endTime;

    private String qrCode;

    private String url;

    private Byte isShare;

    private Byte isVerify;

    private Integer maxApplyNum;

    private Byte isSign;

    private Byte isQuestionnaire;

    private Byte isTest;

    private Byte isBallot;

    private Byte isScore;

    private Byte isDownload;

    /**
     * 默认下架状态，需要手动上架
     */
    private Short isShelve;

    private Short state;

    private Date createTime;

    private Date signUpBeginTime;

    private Date signUpEndTime;

    private Integer createMan;

    private Integer sitemTempId;

    private Integer classState;

}