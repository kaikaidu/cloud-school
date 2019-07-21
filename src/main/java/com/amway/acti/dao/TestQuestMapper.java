package com.amway.acti.dao;

import com.amway.acti.model.TestQuest;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface TestQuestMapper {

    //////////////////后台李伟新增/////////////////////
    TestQuest selectById(int id);

    int insert(TestQuest testQuest);

    List<TestQuest> selectByTempId(int tempId);

    int deleteBytTempId(int tempId);

    int update(TestQuest testQuest);

    List<Integer> selectIdsByTempId(int tempId);

    int deleteById(int id);

    List<Map<String, Object>> selectQuestInfo(int tempId);

}