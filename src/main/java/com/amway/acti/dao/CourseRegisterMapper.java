package com.amway.acti.dao;

import com.amway.acti.model.CourseRegister;
import org.apache.ibatis.annotations.Param;

public interface CourseRegisterMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseRegister record);

    int insertSelective(CourseRegister record);

    CourseRegister selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseRegister record);

    int updateByPrimaryKey(CourseRegister record);

    CourseRegister selectByUserAndCourse(@Param("userId")int userId,@Param("courseId")int courseId);
}
