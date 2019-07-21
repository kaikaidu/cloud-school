package com.amway.acti.model;

public class CourseSitem {
    private Integer id;

    private Integer courseId;

    private Integer sitemId;

    private Integer order;

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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}