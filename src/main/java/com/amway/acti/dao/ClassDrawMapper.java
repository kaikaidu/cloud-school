package com.amway.acti.dao;

import com.amway.acti.model.ClassDraw;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClassDrawMapper {

    ClassDraw selectByPrimaryKey(Integer id);
    int deleteByPrimaryKey(Integer id);

    int insert(ClassDraw record);

    int insertSelective(ClassDraw record);

    List<ClassDraw> selectClassDrawListByClassId(@Param("mClassId") Integer mClassId);

    int updateByClassIdSelective(ClassDraw record);

    List<String> selectClassDrawListByCourseId(@Param("courseId") Integer courseId);

    int updateByPrimaryKeySelective(ClassDraw record);

    int updateByPrimaryKey(ClassDraw record);

    void deleteClassDrawByClassId(Integer classId);

    void deleteClassDrawByList(List<Integer> classIdList);

    int deleteClassDrawByCourseId(@Param("courseId") Integer courseId);
}
