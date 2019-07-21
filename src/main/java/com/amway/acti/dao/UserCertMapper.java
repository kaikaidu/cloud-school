package com.amway.acti.dao;

import com.amway.acti.model.UserCert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCertMapper {

    List<UserCert> selectByUserId(int userId);

    UserCert selectById(int id);

    UserCert selectByCourseIdAndUserId(@Param("courseId") int couseId, @Param("userId") int userId);

    void updateIsRead(int id);

    /////////////上面是liwei小程序接口//////////////////

    int deleteByPrimaryKey(Integer id);

    int insert(UserCert record);

    int insertSelective(UserCert record);

    UserCert selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserCert record);

    int updateByPrimaryKey(UserCert record);

    int selectIsAwardByCourseId(@Param("courseId") int courseId);

    Integer selectCountByCourseIdAndUserId(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    void delByCertId(@Param("certIds")List<Integer> certIds);

    void delByCourseIdAndUid(@Param("courseId") Integer courseId, @Param("userId") Integer userId);
}
