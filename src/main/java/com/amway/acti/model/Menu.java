package com.amway.acti.model;

import java.io.Serializable;

public class Menu implements Serializable{
    private Integer id;

    private String code;

    private String name;

    private String url;

    private String parentCode;

    private String icon;

    private MenuChild menuChild;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public MenuChild getMenuChild() {
        return menuChild;
    }

    public void setMenuChild(MenuChild menuChild) {
        this.menuChild = menuChild;
    }
}
