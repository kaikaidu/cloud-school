package com.amway.acti.dao;

import com.amway.acti.model.DrawCuts;
import com.amway.acti.model.DrawResult;
import com.amway.acti.model.DrawResultData;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DrawResultMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DrawResult record);

    int insertSelective(DrawResult record);

    DrawResult selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DrawResult record);

    int updateByPrimaryKey(DrawResult record);

    DrawResult selectByClassIdAndUserId(DrawResult drawResult);

    List<DrawResult> selectByClassId(@Param("classId") Integer classId,@Param("userId") Integer userId);

    List<DrawCuts> selectDrawUser(@Param("courseId") Integer courseId);

    int selectDrawCount(@Param("name") String name,@Param("courseId") Integer courseId);

    void deleteByClassId(Integer id);

    List<DrawResult> selectDrawResByClassId(Integer id);

    List<DrawResult> selectDrawResultByCourseId(Integer courseId);

    DrawCuts selectUserDrawResult(@Param("userId") Integer userId, @Param("classId") Integer classId);

    DrawResult selectDrawByClassIdAndUserId(@Param("classId") Integer classId, @Param("userId") Integer userId);

    List<DrawResultData> selectDrawResult(@Param("courseId") Integer courseId);

    List<DrawResultData> selectDrawResultCount(@Param("courseId") Integer courseId);

    void deleteByClassIdList(@Param("list") List<Integer> classIdList);

    void insertList(@Param("list") List<DrawResult> drawResults);

    DrawCuts selectClassUserInfo(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    void updateClassIdForCourseIdAndUserId(@Param("userId") Integer userId, @Param("courseId") Integer courseId,@Param("classId") Integer classId);

    List<String> selectDrawTeacher(@Param("classId") Integer classId);

    void deleteUserByClassId(@Param("userId") Integer userId, @Param("classId") Integer classId);

    void updateDrawResultByCourseId(@Param("courseId") Integer courseId);

    int selectBallotByUserIdAndCourseId(@Param("courseId") Integer sysId, @Param("userId") Integer id);

    int selectSitemByUserId(@Param("userId") Integer id,@Param("courseId") Integer courseId);

    List<Map<String, Object>> selectStuInfosByClassId(int classId);

    List<Map<String, Object>> selectTeaInfosByClassId(int classId);

    List<Map<String, Object>> selectStuBaseInfosByClassId(int classId);

    List<Map<String, Object>> selectStuScoreInfos(@Param("courseId") int courseId, @Param("classId") int classId, @Param("userId") int userId );

    List<Map<String, Object>> selectTeaScoreInfos(@Param("courseId") int courseId, @Param("classId") int classId, @Param("userId") int userId );


    int deleteByUserIdAndCourseId(@Param("courseId") Integer courseId,@Param("userId") Integer userId);

    DrawResult selectByCourseIdAndUserId(@Param("courseId") Integer courseId,@Param("userId") Integer userId);

    List<DrawResultData> selectDrawResultByCoursePreview(@Param("courseId") Integer courseId);

    Integer selectDrawResultCountByCoursePreview(@Param("courseId") Integer courseId);

    void deleteByCourseId(@Param("courseId") Integer courseId);

}
