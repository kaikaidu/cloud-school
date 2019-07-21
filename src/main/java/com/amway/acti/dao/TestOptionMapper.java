package com.amway.acti.dao;

import com.amway.acti.model.TestOption;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestOptionMapper {

    //////////////////后台李伟新增/////////////////////

    int batchInsert(@Param("testOptions") List <TestOption> testOptions);

    List<TestOption> selectByQuestId(int questId);

    int deleteBytQuestId(int questId);

    int deleteByTempId(int tempId);

    int update(TestOption testOption);

    int insert(TestOption testOption);

    List<Integer> selectIdsByQuestId(int questId);

    int deleteById(int id);

}