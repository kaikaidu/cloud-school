package com.amway.acti.transform.impl;

import com.amway.acti.dto.TeacherBackendDto;
import com.amway.acti.model.User;
import com.amway.acti.transform.UserTransform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserTransformImpl implements UserTransform {
    @Override
    public List<TeacherBackendDto> modelsToTeacherDtos(List<User> users) {
        log.info("users:{}",users.toString());
        if(CollectionUtils.isEmpty(users)){
            return Collections.emptyList();
        }
        List<TeacherBackendDto> dtos = new ArrayList<>();
        for(User user : users){
            dtos.add(modelToTeacherDto(user));
        }
        log.info("teacherDtos:{}",dtos.toString());
        return dtos;
    }

    public TeacherBackendDto modelToTeacherDto(User user){
        TeacherBackendDto dto = new TeacherBackendDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }
}
