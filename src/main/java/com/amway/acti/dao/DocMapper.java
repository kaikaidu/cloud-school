package com.amway.acti.dao;

import com.amway.acti.model.Doc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DocMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Doc record);

    int insertSelective(Doc record);

    Doc selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Doc record);

    int updateByPrimaryKey(Doc record);

    List<Doc> selectByCourseId(Integer courseId);

    Integer countDoc();

    Integer countByCondition(@Param("name")String name,@Param("type")Short type,@Param("addType")Short addType);

    List<Doc> selectByCondition(@Param("name")String name,@Param("type")Short type,@Param("addType")Short addType,@Param("orderType")String orderType);

    Integer deleteDocs(@Param("exitsDocs")List<Doc> exitsDocs);

    Integer countCourseDocByCondition(@Param("name")String name,@Param("type")Integer type,@Param("addType")Integer addType,@Param("shelve")Integer shelve,@Param("courseId")Integer courseId);

    List<Doc> selectCourseDocByCondition(@Param("name")String name,@Param("type")Integer type,@Param("addType")Integer addType,@Param("orderType")String orderType,@Param("shelve")Integer shelve,@Param("courseId")Integer courseId);

    Integer countByParam(Doc record);

    List<Doc> selectCourseInconnect(@Param("docIds")List<Integer> docIds);

    List<Doc> selectInIds(@Param("docIds")List<Integer> docIds);
}