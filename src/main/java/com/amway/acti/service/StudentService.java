package com.amway.acti.service;

import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.User;
import com.amway.acti.model.UserCustom;
import com.github.pagehelper.PageInfo;

/**
 * @Description:学员管理
 * @Date:2018/3/29 19:34
 * @Author:wsc
 */
public interface StudentService {
    CommonResponseDto<PageInfo<UserCustom>> studentServicestudentList(Integer pageNo, Integer pageSize, String name);

    CommonResponseDto getStudentCount(String name);

    CommonResponseDto getStudentById(Integer id);

    User updateStudent(User user);

    CommonResponseDto provinceList();

    CommonResponseDto getAddr(String code);

    CommonResponseDto updateStudentById(String ids);
}
