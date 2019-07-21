package com.amway.acti.dao;

import com.amway.acti.dto.InvesAnswerDto;
import com.amway.acti.model.InvesResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface InvesResultMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InvesResult record);

    int insertSelective(InvesResult record);

    InvesResult selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InvesResult record);

    int updateByPrimaryKey(InvesResult record);

    /////////////李伟新增///////////////

    /**
     * 根据userId和paperId获取所有invesResult
     * @param userId
     * @param paperId
     * @return
     */
    List<InvesResult> selectByUserIdAndPaperId(@Param( "userId" ) int userId, @Param( "paperId" ) int paperId);

    InvesResult selectByUserIdAndPaperIdAndQuestId(@Param( "userId" ) int userId, @Param( "paperId" ) int paperId, @Param( "questId" ) Integer questId);

    int countUserAnswerByPaperId(int paperId);

    void insertList(@Param("list") List<InvesAnswerDto> invesAnswerDtos, @Param("invesId") int invesId, @Param("userId") int userId, @Param("date") Date date);

    Integer selectCountByCourseIdAndUserId(@Param("courseId") Integer courseId, @Param("userId") Integer userId);


    List<Map<String, String>> selectAllAnswerByQuest(@Param("paperId") int paperId, @Param("userId") int userId, @Param("tempId") int tempId);
}
