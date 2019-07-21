package com.amway.acti.dao;

import com.amway.acti.model.CourseTeacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseTeacherMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseTeacher record);

    int insertSelective(CourseTeacher record);

    CourseTeacher selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseTeacher record);

    int updateByPrimaryKey(CourseTeacher record);

    List<String> selectTeacherByCourse(@Param("courseId") Integer courseId);

    CourseTeacher selectByUserAndCourse(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    Integer insertList(@Param("courseTeachers") List <CourseTeacher> courseTeachers);

    List<Integer> selectTeacherIdsByCourse(@Param("courseId") Integer courseId);

    Integer deleteByCourse(@Param("courseId") Integer courseId);

    void deleteByCourseIdAndClassId(@Param("classId") Integer classId, @Param("courseId") Integer courseId);

    List<CourseTeacher> selectByCourseIdAndClassId(@Param("courseId") Integer courseId, @Param("id") Integer id);

    void deleteByCourseId(Integer courseId);

    List<CourseTeacher> selectTeacByCourseId(@Param("courseId") Integer courseId);

    List<String> selectTeacherByCourseIdAndClassId(@Param("classId") Integer classId, @Param("courseId") Integer courseId);

    void deleteByClassId(@Param("classId") Integer classId);
}
