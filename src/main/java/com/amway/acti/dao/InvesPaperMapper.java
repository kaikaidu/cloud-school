package com.amway.acti.dao;

import com.amway.acti.dto.InvesDto;
import com.amway.acti.model.InvesPaper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface InvesPaperMapper {
    //////////////////前端王随超新增//////////////////////
    List<InvesDto> selectInvesByCourseId(@Param("courseId") Integer courseId,
                                         @Param("state") short state,
                                         @Param("userId") Integer userId);

    InvesPaper selectInvesResult(@Param("courseId") Integer courseId,
                                 @Param("state") short state,
                                 @Param("id") Integer id);

    //////////////////后台李伟新增///////////////////

    int insert(InvesPaper invesPaper);

    int update(InvesPaper invesPaper);

    List<InvesPaper> selectByUserId(@Param( "userId" ) int userId,
                                   @Param( "courseId" ) int courseId,
                                   @Param( "search" ) String search,
                                   @Param( "state" ) int state);

    List<InvesPaper> selectAll(@Param( "courseId" ) int courseId,
                              @Param( "search" ) String search,
                              @Param( "state" ) int state);

    int countByUserId(@Param( "userId" ) int userId,
                      @Param( "courseId" ) int courseId,
                      @Param( "state" ) int state);

    int countAll(@Param( "courseId" ) int courseId, @Param( "state" ) int state);

    int updateStateById(@Param( "id" ) int id, @Param( "state" ) int state);

    InvesPaper selectById(int id);

    List<InvesPaper> selectOnByCourseId(int courseId);

    int countByCourseIdAndName(@Param( "courseId" ) int courseId,
                                        @Param( "name") String name );

    List<InvesPaper> selectByTempId(int tempId);

    List<Map<String, Object>> selectDetailByTempId(int tempId);

    List<InvesPaper> selectAllByCourseId(int courseId);

    List<Map<String, Object>> selectResultByInfoWithApproed(Map<String, Object> map);

    List<Map<String, Object>> selectResultByInfoWithNotApproed(Map<String, Object> map);
}