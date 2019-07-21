package com.amway.acti.dao;

import com.amway.acti.model.Weight;
import com.amway.acti.model.WeightResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WeightMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Weight record);

    int insertSelective(Weight record);

    Weight selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Weight record);

    int updateByPrimaryKey(Weight record);

    List<WeightResult> queryWeightList(WeightResult weightResult);

    List<Weight> queryWeightListByCourseId(@Param("courseId") Integer courseId);

    void deleteWeightByCourseId(Integer courseId);
}