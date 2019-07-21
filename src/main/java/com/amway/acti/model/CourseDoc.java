package com.amway.acti.model;

public class CourseDoc {
    private Integer id;

    private Integer courseId;

    private Integer docId;

    private Short isShelve;

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

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Short getIsShelve() {
        return isShelve;
    }

    public void setIsShelve(Short isShelve) {
        this.isShelve = isShelve;
    }
}