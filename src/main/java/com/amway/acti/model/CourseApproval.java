package com.amway.acti.model;

import java.util.Date;
import lombok.Data;

@Data
public class CourseApproval {
    private Integer id;

    private Integer courseId;

    private Integer userId;

    private Short apprResult;

    private String apprOptrUsr;

    private Date apprOptrTs;

    private String apprOpinion;

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Short getApprResult() {
        return apprResult;
    }

    public void setApprResult(Short apprResult) {
        this.apprResult = apprResult;
    }

    public String getApprOptrUsr() {
        return apprOptrUsr;
    }

    public void setApprOptrUsr(String apprOptrUsr) {
        this.apprOptrUsr = apprOptrUsr == null ? null : apprOptrUsr.trim();
    }

    public Date getApprOptrTs() {
        return apprOptrTs;
    }

    public void setApprOptrTs(Date apprOptrTs) {
        this.apprOptrTs = apprOptrTs;
    }

    public String getApprOpinion() {
        return apprOpinion;
    }

    public void setApprOpinion(String apprOpinion) {
        this.apprOpinion = apprOpinion == null ? null : apprOpinion.trim();
    }
}
