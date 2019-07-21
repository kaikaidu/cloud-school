package com.amway.acti.transform.impl;

import com.amway.acti.base.util.EncrypDES;
import com.amway.acti.dao.AddrMapper;
import com.amway.acti.dto.UserInfoDto;
import com.amway.acti.model.User;
import com.amway.acti.transform.UserInfoTransform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@Service
public class UserInfoTransformImpl implements UserInfoTransform {

    @Autowired
    private AddrMapper addrMapper;

    @Override
    public UserInfoDto transformUserToUserInfoDto(User user) throws GeneralSecurityException, IOException {
        UserInfoDto userInfoDto = new UserInfoDto();
        if(!StringUtils.isEmpty(user.getName())){
            userInfoDto.setName(EncrypDES.getInstance().setEncString(user.getName()));
        }
        if (!StringUtils.isEmpty(user.getAdaNumber())) {
            userInfoDto.setAda(EncrypDES.getInstance().setEncString(user.getAdaNumber()));
        }
        if (!StringUtils.isEmpty(user.getPhone())) {
            userInfoDto.setPhone(EncrypDES.getInstance().setEncString(user.getPhone()));
        }
        if (!StringUtils.isEmpty(user.getProvCode())) {
            userInfoDto.setProvince(user.getProvCode());
        }
        if (!StringUtils.isEmpty(user.getProvCode())) {
            userInfoDto.setProvinceName(addrMapper.selectNameByCode(user.getProvCode()));
        }
        if (!StringUtils.isEmpty(user.getCityCode())) {
            userInfoDto.setCity(user.getCityCode());
        }
        if (!StringUtils.isEmpty(user.getCityCode())) {
            userInfoDto.setCityName(addrMapper.selectNameByCode(user.getCityCode()));
        }
        if (!StringUtils.isEmpty(user.getRegionCode())) {
            userInfoDto.setRegion(user.getRegionCode());
        }
        if (!StringUtils.isEmpty(user.getRegionCode())) {
            userInfoDto.setRegionName(addrMapper.selectNameByCode(user.getRegionCode()));
        }
        if (!StringUtils.isEmpty(user.getAddress())) {
            userInfoDto.setDetailAddress(EncrypDES.getInstance().setEncString(user.getAddress()));
        }
        if (!StringUtils.isEmpty(user.getRemark())) {
            userInfoDto.setRemark(user.getRemark());
        }
        return userInfoDto;
    }
}
