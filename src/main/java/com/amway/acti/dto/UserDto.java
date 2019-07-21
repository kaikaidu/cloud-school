/**
 * Created by dk on 2018/2/21.
 */

package com.amway.acti.dto;

public class UserDto {
    private Integer uid;
    private String openId;

    private String unionId;

    private String name;

    private String password;

    private String email;

    private Short sex;

    private Short ident;

    private String phone;

    public String getOpenId() {
        return openId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Short getSex() {
        return sex;
    }

    public void setSex(Short sex) {
        this.sex = sex;
    }

    public Short getIdent() {
        return ident;
    }

    public void setIdent(Short ident) {
        this.ident = ident;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
