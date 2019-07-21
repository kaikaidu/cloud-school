package com.amway.acti.service;

import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.User;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description:讲师
 * @Date:2018/3/23 11:23
 * @Author:wsc
 */
@Validated
public interface TeacherService {
    CommonResponseDto addTeacher(String name, String email, String password) throws Exception;

    CommonResponseDto getTeacherCount(String name);

    PageInfo<User> findTeacherByName(Integer pageNo, Integer pageSize, String name) throws Exception;

    List<User> findTeacherByIdent(short ident);

    CommonResponseDto updateTeacherById(String ids);

    CommonResponseDto getTeacherById(@NotNull(message = "讲师id不能为空")Integer id) throws Exception;

    User updateTeacher(@NotNull(message = "讲师id不能为空") Integer id, String name, String email, String pwd) throws Exception;

    CommonResponseDto getTeacherByEmail(String email);

}
