package com.amway.acti.transform.impl;

import com.amway.acti.base.exception.AmwayParamException;
import com.amway.acti.model.CourseAssist;
import com.amway.acti.model.CourseTeacher;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.transform.CourseTeacherTransform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class CourseTeacherTransformImpl implements CourseTeacherTransform {

    @Autowired
    private BackendUserService backendUserService;

    @Override
    public List<CourseTeacher> transformStringToModel(String[] teacherIds,Integer courseId) {
        log.info("teachers:{},courseId:{}",Arrays.toString(teacherIds),courseId);
        List<CourseTeacher> courseTeachers = new ArrayList<>();
        if (null != teacherIds && teacherIds.length > 0) {
            for(String teacherIdStr : teacherIds) {
                int teacherId;
                try {
                    teacherId = Integer.valueOf(teacherIdStr);
                } catch (NumberFormatException ex) {
                    log.error(ex.getMessage(), ex);
                    throw new AmwayParamException("讲师取值错误");
                }
                CourseTeacher courseTeacher = new CourseTeacher();
                courseTeacher.setUserId(teacherId);
                courseTeacher.setCourseId(courseId);
                courseTeachers.add(courseTeacher);
            }
            log.info("courseTeachers:{}",courseTeachers);
            if(!backendUserService.checkTeachersExist(Arrays.asList(teacherIds))){
                throw new AmwayParamException("讲师取值错误");
            }
        }
        return courseTeachers;
    }

    @Override
    public List<CourseAssist> transformStringToModelByAssists(String[] assistIds, Integer courseId) {
        log.info("teachers:{},courseId:{}",Arrays.toString(assistIds),courseId);
        List<CourseAssist> courseAssists = new ArrayList<>();
        if (null != assistIds && assistIds.length > 0) {
            for(String assistsIdStr : assistIds) {
                int assistsId;
                try {
                    assistsId = Integer.valueOf(assistsIdStr);
                } catch (NumberFormatException ex) {
                    log.error(ex.getMessage(), ex);
                    throw new AmwayParamException("讲师取值错误");
                }
                CourseAssist courseAssist = new CourseAssist();
                courseAssist.setUserId(assistsId);
                courseAssist.setCourseId(courseId);
                courseAssists.add(courseAssist);
            }
        }
        log.info("courseTeachers:{}",courseAssists);
        return courseAssists;
    }
}
