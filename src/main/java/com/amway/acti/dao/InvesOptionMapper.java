package com.amway.acti.dao;

import com.amway.acti.model.InvesOption;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InvesOptionMapper {

    //////////////////后台李伟新增/////////////////////

    int batchInsert(@Param("invesOptions") List <InvesOption> invesOptions);

    List<InvesOption> selectByQuestId(int questId);

    int deleteBytQuestId(int questId);

    int deleteByTempId(int tempId);

    int update(InvesOption invesOption);

    int insert(InvesOption invesOption);

    List<Integer> selectIdsByQuestId(int questId);

    int deleteById(int id);

}