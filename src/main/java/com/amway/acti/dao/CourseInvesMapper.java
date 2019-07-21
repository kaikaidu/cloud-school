package com.amway.acti.dao;

import com.amway.acti.model.CourseInves;

public interface CourseInvesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseInves record);

    int insertSelective(CourseInves record);

    CourseInves selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseInves record);

    int updateByPrimaryKey(CourseInves record);
}