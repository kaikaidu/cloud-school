package com.amway.acti.dao;

import com.amway.acti.model.Mclass;
import com.amway.acti.model.MclassTeacSpee;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface MclassMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Mclass record);

    int insertSelective(Mclass record);

    Mclass selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Mclass record);

    int updateByPrimaryKey(Mclass record);

    List<Mclass> selectByCourseId(Integer courseId);

    void deleteClassByCourseId(Integer courseId);

    List<Mclass> selectByCourseIdAndOrder(Integer courseId);


    List<MclassTeacSpee> selectSpeedAndDrawList(@Param("courseId") Integer courseId);

    void deleteClassById(@Param("classId") Integer classId);

    void updateMclassForList(@Param("list") List<Mclass> mclassList);

    List<Mclass> selectClassByCourseId(Integer courseId);

    void updateMClassById(@Param("courseId") Integer courseId, @Param("userId") String userId);

    void inserList(@Param("list") List<Mclass> mclassList);
}