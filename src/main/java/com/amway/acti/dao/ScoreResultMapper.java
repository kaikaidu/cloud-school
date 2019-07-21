package com.amway.acti.dao;

import com.amway.acti.dto.ScoreDetailDto;
import com.amway.acti.model.ScoreResult;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ScoreResultMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ScoreResult record);

    int insertSelective(ScoreResult record);

    ScoreResult selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ScoreResult record);

    int updateByPrimaryKey(ScoreResult record);

    ScoreResult selectByCourseIdAndStuIdAndUid(ScoreResult scoreResult);

    BigDecimal selectTotalScoreByStuId(@Param("courseId") Integer courseId, @Param("stuId") Integer stuId);

    Integer selectClassIdByCourseIdAndUid(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    List<ScoreDetailDto> selectScoreDetail(@Param("courseId") String courseId,@Param("adaNumber") String adaNumber);

    void deleteScoreResultByCourseIdAndUserId(@Param("courseId") Integer courseId, @Param("list") List<Integer> strList);

    List<ScoreResult> selectByCourseIdAndStuId(@Param("courseId") Integer courseId, @Param("stuId") Integer stuId);

    List<ScoreResult> selectScoreResultByCourseId(@Param("courseId") Integer courseId);

    void deleteByCourseId(@Param("courseId") Integer courseId);
}
