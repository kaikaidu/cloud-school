package com.amway.acti.transform;

import com.amway.acti.dto.TeacherBackendDto;
import com.amway.acti.model.User;

import java.util.List;

public interface UserTransform {
    List<TeacherBackendDto> modelsToTeacherDtos(List<User> users);
}
