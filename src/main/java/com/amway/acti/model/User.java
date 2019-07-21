package com.amway.acti.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;

    private String openId;

    private String unionId;

    private String name;

    private String password;

    private String email;

    private Short sex;

    private Short ident;

    private String phone;

    private String adainfoMd5;

    private String address;

    private String provCode;

    private String cityCode;

    private String adaNumber;

    private Short state;

    private String grouping;

    private Short isLegalperson;

    private String vocationalLevel;

    private String shop;

    private String idNumber;

    private String identityType;

    private String zipCode;

    private Date createTime;

    private String ssoName;

    private String remark;

    private String regionCode;

    private Integer viaState;//通过状态 0-未通过 1-通过

    private Date startTime;//上课开始时间

    private Date endTime;//上课结束时间

    private Integer signState;//报名状态

    private Integer isVerify;//是否需要审核 1：需要 0：不需要

    private Integer isDistribution;//讲师是否分配班级

    private Integer classId;//班级id

    private boolean assign;//判断置灰

    private boolean selected;//判断选中

    private String ssoAdanumber;

    private String areaCode;

    private Short age;

    private String shopProvince;

    private String shopCity;

    private String shopProvinceCode;

    private String shopCityCode;

    private boolean deblock;
}
