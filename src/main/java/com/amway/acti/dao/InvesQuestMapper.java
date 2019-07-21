package com.amway.acti.dao;

import com.amway.acti.model.InvesQuest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface InvesQuestMapper {
    ////////////前端wsc新增/////////////
    List<InvesQuest> selectInvesQuestByInvesId(Integer tempId);

    List<Integer> selectRequiredIdsByPaperId(int paperId);

    //////////////////后台李伟新增/////////////////////
    int insert(InvesQuest invesQuest);

    int batchInsert(@Param("invesQuests") List <InvesQuest> invesQuests);

    InvesQuest selectById(int id);

    List<InvesQuest> selectByTempId(int tempId);

    int update(InvesQuest invesQuest);

    int deleteById(int id);

    int deleteBytTempId(int tempId);

    List<Integer> selectIdsByTempId(int tempId);

    List<Map<String, Object>> selectInfoByTempId(int tempId);

}