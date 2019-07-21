package com.amway.acti.dao;

import com.amway.acti.dto.CertParamDto;
import com.amway.acti.model.UserCert;
import com.amway.acti.model.UserCertCustom;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Description:
 * @Date:2018/6/6 17:31
 * @Author:wsc
 */
public interface UserCertCustomMapper {
    Integer selectUserCertByCourseId(@Param("courseId") Integer courseId);

    List<UserCertCustom> selectUserCert(@Param("certParamDto") CertParamDto certParamDto,@Param("userIds") String[] userIds);

    void updateUserCertByList(@Param("list") List<UserCert> userCertList);

    List<UserCert> findUserCertByCourseId(@Param("courseId") Integer courseId);

    UserCert selectUserCertByCourseIdAndUserId(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    void insertList(@Param("list") List<UserCert> userCertList);

    void deleteUserCertByCertId(@Param("certId") int certId);

    Integer selectUserCertCount(@Param("courseId") Integer courseId);

    void updateUserCertByBean(UserCert userCert);

    void updateUserCertByCourseId(@Param("courseId") Integer courseId, @Param("certId") Integer certId, @Param("name") String name);

    void upUserCertByCourseId(@Param("courseId") Integer courseId);

    void updateUserCertByCourseIdForName(@Param("courseId") Integer courseId, @Param("name") String name);
}
