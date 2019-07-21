package com.amway.acti.dao;

import com.amway.acti.model.TestTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Wei.Li1
 * @create 2018-03-13 15:52
 **/
public interface TestTempMapper {

    List<TestTemp> selectByUserId(@Param( "userId" ) int userId, @Param( "search" ) String search, @Param( "isDesc" ) boolean isDesc);

    List<TestTemp> selectAll(@Param( "search" ) String search, @Param( "isDesc" ) boolean isDesc);

    int countByUserId(int userId);

    int countAll();

    int updateStateById(int id);

    int insert(TestTemp testTemp);

    TestTemp selectById(int id);

    int update(TestTemp testTemp);

    int countByUserIdAndName(@Param( "userId" ) int userId, @Param( "name" ) String name);

    int countByName(String name);


}
