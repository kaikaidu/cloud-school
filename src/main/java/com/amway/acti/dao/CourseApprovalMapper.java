package com.amway.acti.dao;

import com.amway.acti.model.CourseApproval;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CourseApprovalMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseApproval record);

    int insertSelective(CourseApproval record);

    CourseApproval selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseApproval record);

    int updateByPrimaryKey(CourseApproval record);

    Integer countApprovedNum(@Param("courseId") Integer courseId);

    Integer selectApprResultByUserIdAndCourseId(@Param("courseId") int courseId, @Param("userId") int userId);

    void updateApprResult(@Param("courseId") Integer courseId,
                          @Param("siState") Integer siState,
                          @Param("id") String id);

    int selectSignupCount(Integer courseId);

    void insertByList(List <CourseApproval> addCourseApprovalList);

    CourseApproval selectApprResultByCourseId(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    int selectAppreovalCountByCourseId(@Param("courseId") Integer courseId);

    Integer deleteApprResultByCourseId(@Param("courseId") int courseId, @Param("userId") int userId);

    List<CourseApproval> selectApprovalByCourseId(@Param("courseId") Integer courseId);

    void deleteApprotalByCourseId(@Param("courseId") Integer courseId);
}
