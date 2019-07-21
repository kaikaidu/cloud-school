package com.amway.acti.dao;

import com.amway.acti.model.UserMark;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface UserMarkMapper {

    //////////////李伟新增////////////////
    Integer insert(UserMark userMark);

    UserMark selectByUserIdAndPaperId(@Param("userId") int userId, @Param("paperId") int paperId);
    List<UserMark> selectdk002(@Param("userId") int userId);

    int updateScoreByUserIdAndPaperId(@Param( "score" )BigDecimal score ,
                                      @Param( "userId" ) int userId,
                                      @Param( "paperId" ) int paperId);

    int deleteByPrimaryKey(Integer id);

}
