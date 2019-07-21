package com.amway.acti.dao;

import com.amway.acti.model.CourseDoc;
import com.amway.acti.model.Doc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseDocMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseDoc record);

    int insertSelective(CourseDoc record);

    CourseDoc selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseDoc record);

    int updateByPrimaryKey(CourseDoc record);

    Integer deleteByDocs(@Param("docs")List<Doc> docs);

    Integer deleteByDoc(@Param("docId")Integer docId);

    Integer countByCourseId(@Param("courseId")Integer courseId);

    Integer addCourseDocs(@Param("courseId")Integer courseId,@Param("docIds")List<Integer> docIds);

    List<Integer> selectDocIdsByCourse(@Param("courseId")Integer courseId);

    Integer updateShelveByCourseAndDoc(@Param("shelve")Integer shelve,@Param("courseId")Integer courseId,@Param("docIds")Integer[] docIds);

    Integer deleteByCourseAndDoc(@Param("courseId")Integer courseId,@Param("docIds")Integer[] docIds);

    Integer countByCourseIdAndShelve(@Param("courseId")Integer courseId);
}