package com.amway.acti.dao;

import com.amway.acti.model.CourseSitem;

import java.util.List;

public interface CourseSitemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseSitem record);

    int insertSelective(CourseSitem record);

    CourseSitem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseSitem record);

    int updateByPrimaryKey(CourseSitem record);

    List<CourseSitem> selectByCourseId(Integer courseId);

}
