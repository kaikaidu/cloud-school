package com.amway.acti.model;

import java.util.Date;

public class Doc {
    private Integer id;

    private String url;

    private Short type;

    private Date createTime;

    private Short state;

    private String name;

    private Short addType;

    private Short isShelve;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Short getAddType() {
        return addType;
    }

    public void setAddType(Short addType) {
        this.addType = addType;
    }

    public Short getIsShelve() {
        return isShelve;
    }

    public void setIsShelve(Short isShelve) {
        this.isShelve = isShelve;
    }

    @Override
    public String toString() {
        return "Doc{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", type=" + type +
                ", createTime=" + createTime +
                ", state=" + state +
                ", name='" + name + '\'' +
                ", addType=" + addType +
                ", isShelve=" + isShelve +
                '}';
    }
}