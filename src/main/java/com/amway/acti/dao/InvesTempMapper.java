package com.amway.acti.dao;

import com.amway.acti.model.InvesTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Wei.Li1
 * @create 2018-03-13 15:52
 **/
public interface InvesTempMapper {

    List<InvesTemp> selectByUserId(@Param("userId") int userId, @Param("search") String search, @Param("isDesc") boolean isDesc);

    List<InvesTemp> selectAll(@Param("search") String search, @Param("isDesc") boolean isDesc);

    int countByUserId(int userId);

    int countAll();

    int updateStateById(int id);

    int insert(InvesTemp invesTemp);

    InvesTemp selectById(int id);

    int update(InvesTemp invesTemp);

    int countByUserIdAndName(@Param( "userId" ) int userId, @Param( "name" ) String name);

    int countByName(String name);

}
