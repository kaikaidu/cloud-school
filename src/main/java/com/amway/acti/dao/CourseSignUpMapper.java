package com.amway.acti.dao;

import com.amway.acti.model.CourseApproval;
import com.amway.acti.model.CourseSignUp;
import com.amway.acti.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseSignUpMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseSignUp record);

    int insertSelective(CourseSignUp record);

    CourseSignUp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseSignUp record);

    int updateByPrimaryKey(CourseSignUp record);

    CourseSignUp selectByUserAndCourse(@Param(value = "courseId") Integer courseId, @Param(value = "adaInfoMd5") String adainfoMd5);


    List<CourseSignUp> selectByUserAndCourseList(@Param(value = "courseId") Integer courseId, @Param(value = "adaInfoMd5") String adainfoMd5);

    List<CourseSignUp> selectByUserAndCourseList000(@Param(value = "adaInfoMd5") String adainfoMd5);


    int countByCourseId(Integer courseId);

    int selectSignupCount(Integer courseId);

    void insertByList(List <CourseSignUp> addCourseSignUpList);

    int deleteByCourseAndAdainfoMd5(@Param(value = "courseId") Integer courseId, @Param(value = "adaInfoMd5") String adainfoMd5);

    void updateAdainfoMd5(@Param(value = "adaInfoO") String adaInfoO, @Param(value = "adaInfoN") String adaInfoN);

    List<CourseApproval> selectSignUpByCourseId(@Param("courseId") Integer courseId);

    void deleteByAdainfoMd5List(@Param("userList") List<User> userList);

    List<CourseSignUp> selectSignByCourseId(@Param("courseId") Integer courseId);
}
