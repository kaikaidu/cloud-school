package com.amway.acti.dao;

import com.amway.acti.model.CourseAgree;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

public interface CourseAgreeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseAgree record);

    int insertSelective(CourseAgree record);

    CourseAgree selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseAgree record);

    int updateByPrimaryKey(CourseAgree record);

    int countByCourseId(@Param("courseId")int courseId);

    boolean selectIsAgreed(@Param("courseId")int courseId,@Param("userId")int userId);

    CourseAgree selectByCourseAndUser(@Param("courseId")int courseId,@Param("userId")int userId);

}
