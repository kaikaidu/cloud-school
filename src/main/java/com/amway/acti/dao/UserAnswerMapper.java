package com.amway.acti.dao;

import com.amway.acti.model.UserAnswer;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface UserAnswerMapper {

    //////////////李伟新增///////////////
    Integer batchInsert(@Param( "userAnswers" ) List<UserAnswer> userAnswers);

    List<UserAnswer> selectByUserIdAndPaperId(@Param( "userId" ) Integer userId, @Param( "paperId" ) Integer paperId);


    List<UserAnswer> selectdk001(@Param( "userId" ) Integer userId);

    void insert(UserAnswer userAnswer);

    UserAnswer selectByUserIdAndPaperIdAndQuestId(@Param( "userId" ) int userId,
                                                  @Param( "paperId" ) int paperId,
                                                  @Param( "questId" ) int questId);

    List<Integer> selectByPaperId(int paperId);

    int updateScoreById(@Param( "score" ) BigDecimal score, @Param( "id" ) int id);

    int countUserAnswerByPaperId(int paperId);

    Integer selectCountByCourseIdAndUserId(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    int deleteByPrimaryKey(Integer id);
}
