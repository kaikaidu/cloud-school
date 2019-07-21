package com.amway.acti.dao;

import com.amway.acti.model.CoursePaper;

public interface CoursePaperMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CoursePaper record);

    int insertSelective(CoursePaper record);

    CoursePaper selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CoursePaper record);

    int updateByPrimaryKey(CoursePaper record);
}