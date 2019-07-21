package com.amway.acti.dao;

import com.amway.acti.model.CourseCollect;
import org.apache.ibatis.annotations.Param;

public interface CourseCollectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseCollect record);

    int insertSelective(CourseCollect record);

    CourseCollect selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseCollect record);

    int updateByPrimaryKey(CourseCollect record);

    int countByCourseId(@Param("courseId")int courseId);
}
