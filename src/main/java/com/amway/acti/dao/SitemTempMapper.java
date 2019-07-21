package com.amway.acti.dao;

import com.amway.acti.model.SitemTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SitemTempMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SitemTemp record);

    int insertSelective(SitemTemp record);

    SitemTemp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SitemTemp record);

    int updateByPrimaryKey(SitemTemp record);

    List<SitemTemp> selectSitemTempList(@Param("name") String name,@Param("createTimeType") String createTimeTye);

    SitemTemp selectSitemByName(@Param("name") String name);

}