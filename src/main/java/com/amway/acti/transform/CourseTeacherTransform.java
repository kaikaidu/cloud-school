package com.amway.acti.transform;

import com.amway.acti.model.CourseAssist;
import com.amway.acti.model.CourseTeacher;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface CourseTeacherTransform {
    List<CourseTeacher> transformStringToModel(String[] teacherStr, Integer courseId);

    List<CourseAssist> transformStringToModelByAssists(String[] assistIds, Integer courseId);
}
