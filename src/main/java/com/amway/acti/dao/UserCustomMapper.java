package com.amway.acti.dao;

import com.amway.acti.model.UserCustom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Date:2018/4/20 13:36
 * @Author:wsc
 */
public interface UserCustomMapper {
    List<UserCustom> selectUserByName(@Param("name") String name);

    List<UserCustom> selectApprByCourseId(@Param("courseId") Integer courseId, @Param("name") String name);

    List<UserCustom> selectSignupByCourseId(@Param("courseId") Integer courseId, @Param("name") String name);
}
