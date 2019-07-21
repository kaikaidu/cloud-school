package com.amway.acti.service;

import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.Course;

import javax.validation.constraints.NotNull;

/**
 * @Description:分班设置
 * @Date:2018/4/27 10:12
 * @Author:wsc
 */
public interface ClassSettingService {
    CommonResponseDto getStudentScore(@NotNull(message = "课程id不能为空")Integer courseId);

    CommonResponseDto addClassManual(String className,@NotNull(message = "课程id不能为空")Integer courseId);

    Course getCourse(@NotNull(message = "课程id不能为空")Integer courseId);

    CommonResponseDto deleteClass(@NotNull(message = "课程id不能为空")Integer courseId,Integer classId);

    CommonResponseDto classList(@NotNull(message = "课程id不能为空") Integer courseId);

    CommonResponseDto deleteClassByCourseId(@NotNull(message = "课程id不能为空") Integer courseId);

    CommonResponseDto findUser(@NotNull(message = "课程id不能为空") Integer courseId,String name);

    CommonResponseDto addCourseTeacher(Integer[] userIds, Integer classId,@NotNull(message = "课程id不能为空") Integer courseId,Integer studentLength);

}
