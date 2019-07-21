package com.amway.acti.dao;

import com.amway.acti.model.TestPaper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TestPaperMapper {
    ////////////////李伟新增////////////////
    List<TestPaper> selectOnByCourseId(int courseId);

    //////////////////后台李伟新增///////////////////

    int insert(TestPaper testPaper);

    int update(TestPaper testPaper);

    List<TestPaper> selectByUserId(@Param( "userId" ) int userId,
                                   @Param( "courseId" ) int courseId,
                                   @Param( "search" ) String search,
                                   @Param( "state" ) int state);

    List<TestPaper> selectAll(@Param( "courseId" ) int courseId,
                              @Param( "search" ) String search,
                              @Param( "state" ) int state);

    int countByUserId(@Param( "userId" ) int userId,
                      @Param( "courseId" ) int courseId,
                      @Param( "state" ) int state);

    int countAll(@Param( "courseId" ) int courseId, @Param( "state" ) int state);

    int updateStateById(@Param( "id" ) int id, @Param( "state" ) int state);

    TestPaper selectById(int id);

    int countByCourseIdAndName(@Param( "courseId" ) int courseId,
                               @Param( "name") String name );

    List<TestPaper> selectByTempId(int tempId);

    List<Map<String, Object>> selectDetailByTempId(int tempId);

    List<TestPaper> selectAllByCourseId(int courseId);

    List<Map<String, Object>> selectScoreResultByInfoWithNotApproed(Map<String, Object> info);

    List<Map<String, Object>> selectScoreResultByInfoWithApproed(Map<String, Object> info);

}