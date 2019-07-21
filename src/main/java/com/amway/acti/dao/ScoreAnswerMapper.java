package com.amway.acti.dao;

import com.amway.acti.model.ScoreAnswer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScoreAnswerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ScoreAnswer record);

    int insertSelective(ScoreAnswer record);

    ScoreAnswer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ScoreAnswer record);

    int updateByPrimaryKey(ScoreAnswer record);

    ScoreAnswer selectByStuIdAndSitemId(ScoreAnswer scoreAnswer);

    void deleteScoreAnswerByCourseIdAndUserId(@Param("courseId") Integer courseId, @Param("list") List<Integer> strList);

    int countScoreBySitemId(int courseId);

    Integer selectCountByStuIdAndCourseId(@Param("courseId") Integer courseId,@Param("stuId") Integer stuId);

    Integer selectCountByUserIdAndCourseId(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    void deleteByCourseId(@Param("courseId") Integer courseId);

}
