package com.amway.acti.dao;

import com.amway.acti.model.CourseAssist;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseAssistMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseAssist record);

    int insertSelective(CourseAssist record);

    CourseAssist selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseAssist record);

    int updateByPrimaryKey(CourseAssist record);

    Integer deleteByCourse(@Param("courseId") Integer courseId);

    Integer insertList(@Param("courseAssists") List<CourseAssist> courseTeachers);

    List<String> selectAssistByCourseId(@Param("courseId") Integer courseId);

    List<Integer> selectAssistIdByCourse(@Param("courseId") Integer courseId);


}
