package com.amway.acti.dao;

import com.amway.acti.model.Course;
import com.amway.acti.model.CourseApprStatus;
import com.amway.acti.model.CourseExport;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CourseMapper {
    int deleteByPrimaryKey(Integer sysId);

    int insert(Course record);

    int insertSelective(Course record);

    Course selectByPrimaryKey(Integer sysId);

    int updateByPrimaryKeySelective(Course record);

    int updateBasicInfo(Course record);

    List<Course> selectPartCourse(@Param("adainfoMd5")String adainfoMd5,@Param("localTime")Date localTime);

    List<Course> selectRegistedCourse(@Param("adaInfoMd5")String adaInfoMd5,@Param("localTime")Date localTime);

    List<Course> selectFinishedCourse(@Param("adainfoMd5")String adaInfoMd5,@Param("localTime")Date localTime);

    List<Course> selectByTeacher(@Param("userId")int userId);

    List<Course> selectCourseList(Map<String,Object> param);

    Long countCourse(Map<String,Object> param);

    Integer countUserWithinCourses(@Param("userId")Integer userId,@Param("courseIds")List<Integer> courseIds);

    Integer updateShelveByIds(@Param("shelve") Boolean shelve,@Param("courseIds") List<Integer> courseIds);

    Integer deleteByIds(@Param("courseIds") List<Integer> courseIds);

    Integer selectSitemTempBySysId(@Param("sysId") Integer sysId);

    Integer updateCourseActivity(@Param("activityName")String activityName,@Param("value")Integer value,@Param("courseId")Integer courseId);

    int selectCourseCount(@Param("name") String name,@Param("label") String label);

    List<CourseExport> selectCourseByName(@Param("name") String name, @Param("sort") String sort, @Param("label") String label);

    CourseApprStatus selectApplyStatusByCourseAndUser(@Param("courseId")Integer courseId,@Param("uid")Integer uid,@Param("adaInfoMd5")String adaInfoMd5);

    List<Course> selectCourseBySitemTemp(@Param("sitemTempId") Integer sitemTempId);

    Integer countByTitle(@Param("title")String title,@Param("courseId")Integer courseId);

    List<Course> selectCourseByLabel(@Param("labelId") Integer labelId);

    String getLabelByCourseId(@Param("courseId") Integer courseId);

    List<Course> selectCourseByDocId(Integer docId);

    List<Course> selectByUserId(@Param("userId") Integer userId);

    void updateCourseForClassState(@Param("courseId") Integer courseId);

    String selectTitleBySysId(@Param("sysId") Integer sysId);


}
