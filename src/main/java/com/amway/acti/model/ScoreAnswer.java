package com.amway.acti.model;

import java.math.BigDecimal;

public class ScoreAnswer {
    private Integer id;

    private Integer courseId;

    private Integer sitemId;

    private Integer stuId;

    private Integer userId;

    private BigDecimal score;

    private Short state;

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

    public Integer getSitemId() {
        return sitemId;
    }

    public void setSitemId(Integer sitemId) {
        this.sitemId = sitemId;
    }

    public Integer getStuId() {
        return stuId;
    }

    public void setStuId(Integer stuId) {
        this.stuId = stuId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }
}