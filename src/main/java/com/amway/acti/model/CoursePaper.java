package com.amway.acti.model;

public class CoursePaper {
    private Integer id;

    private Integer courseId;

    private Integer parerId;

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

    public Integer getParerId() {
        return parerId;
    }

    public void setParerId(Integer parerId) {
        this.parerId = parerId;
    }
}