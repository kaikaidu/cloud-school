package com.amway.acti.transform;

import com.amway.acti.dto.UserInfoDto;
import com.amway.acti.model.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface UserInfoTransform {

    /**
     * 转换成UserinfoDto
     * @param user
     * @return
     */
    UserInfoDto transformUserToUserInfoDto(User user) throws GeneralSecurityException, IOException;
}
