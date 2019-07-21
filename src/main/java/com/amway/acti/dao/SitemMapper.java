package com.amway.acti.dao;

import com.amway.acti.model.Sitem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import java.util.List;

public interface SitemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Sitem record);

    int insertSelective(Sitem record);

    Sitem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Sitem record);

    int updateByPrimaryKey(Sitem record);

    int addSitem(@Param("sitemList")List<Sitem> sitemList);

    List<Sitem> selectBySitemTempId(@Param("sitemTempId") Integer sitemTempId);

    int batchUpdataSitemState(Sitem record);
}
