package com.amway.acti.model;

public class RoleMenu {
    private Integer id;

    private Short ident;

    private String code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Short getIdent() {
        return ident;
    }

    public void setIdent(Short ident) {
        this.ident = ident;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }
}