package com.amway.acti.dao;

import com.amway.acti.model.SpeeMark;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpeeMarkMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SpeeMark record);

    int insertSelective(SpeeMark record);

    SpeeMark selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpeeMark record);

    int updateByPrimaryKey(SpeeMark record);

    SpeeMark selectByCourseIdAndClassIdAndUid(SpeeMark speeMark);

    List<SpeeMark> selectByCourseId(@Param("courseId") String courseId);

    void deleteSpeeMarkByCourseIdAndUserId(@Param("courseId") Integer courseId, @Param("list") List<Integer> strList);

    void deleteByCourseId(@Param("courseId") Integer courseId);
}
