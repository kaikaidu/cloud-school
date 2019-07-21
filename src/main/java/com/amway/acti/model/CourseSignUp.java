package com.amway.acti.model;

import lombok.Data;

import java.util.Date;

@Data
public class CourseSignUp {
    private Integer id;

    private Integer courseId;

    private String adainfoMd5;

    private Date singupTime;

    private Short viaState;

    private String formid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getAdainfoMd5() {
        return adainfoMd5;
    }

    public void setAdainfoMd5(String adainfoMd5) {
        this.adainfoMd5 = adainfoMd5 == null ? null : adainfoMd5.trim();
    }

    public Date getSingupTime() {
        return singupTime;
    }

    public void setSingupTime(Date singupTime) {
        this.singupTime = singupTime;
    }

    public Short getViaState() {
        return viaState;
    }

    public void setViaState(Short viaState) {
        this.viaState = viaState;
    }

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid == null ? null : formid.trim();
    }
}